package pl.coderslab.cultureBuddies.buddyBook;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import pl.coderslab.cultureBuddies.books.Book;
import pl.coderslab.cultureBuddies.buddies.Buddy;
import pl.coderslab.cultureBuddies.buddies.BuddyService;
import pl.coderslab.cultureBuddies.buddies.RelationStatusRepository;
import pl.coderslab.cultureBuddies.buddyBuddy.RelationStatus;
import pl.coderslab.cultureBuddies.exceptions.NotExistingRecordException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
class BuddyBookServiceTest {
    @Autowired
    private BuddyBookService testObject;
    @MockBean
    private BuddyBookRepository buddyBookRepositoryMock;
    @MockBean
    private BuddyService buddyServiceMock;
    @MockBean
    private RelationStatusRepository relationStatusRepositoryMock;
    private Buddy principal;
    private Book book;
    private BuddyBookId buddyBookId;
    private BuddyBook savedBuddyBook;
    private final Long buddiesStatusId = 7L;
    private final Long bookId = 3134L;
    private final Long principalId = 44323421L;
    private final List<BuddyBook> expectedRatings = new ArrayList<>();

    @BeforeEach
    public void setUp() throws NotExistingRecordException {
        principal = Buddy.builder()
                .username("BestBuddy")
                .id(principalId)
                .build();
        book = Book.builder()
                .title("title")
                .id(bookId)
                .build();
        buddyBookId =
                new BuddyBookId();
        buddyBookId.setBuddyId(principalId);
        buddyBookId.setBookId(bookId);
        BuddyBook validBuddyBook = BuddyBook.builder()
                .bookId(bookId)
                .buddyId(principalId)
                .buddy(principal)
                .book(book)
                .rate(8)
                .build();
        savedBuddyBook = validBuddyBook.toBuilder()
                .id(buddyBookId)
                .build();
        expectedRatings.add(savedBuddyBook);

        when(buddyBookRepositoryMock.findById(buddyBookId))
                .thenReturn(Optional.ofNullable(savedBuddyBook));
        when(buddyServiceMock.getPrincipal()).thenReturn(principal);
        final RelationStatus buddiesStatus = new RelationStatus(buddiesStatusId, "buddies");
        when(buddyServiceMock.getStatus("buddies")).thenReturn(buddiesStatus);
    }

    @Test
    public void givenValidBuddyBookWithUpdatedRating_whenUpdatingBuddyBook_thenBeingSaved() throws NotExistingRecordException {
        savedBuddyBook.setRate(10);
        //when
        testObject.updateBuddyBook(savedBuddyBook);
        //then
        verify(buddyBookRepositoryMock).save(savedBuddyBook);
    }

    @Test
    public void givenBookId_whenSearchingForBookPrincipalRelation_thenItIsBeingSearched() throws NotExistingRecordException {
        //when
        testObject.findRelationWithPrincipalByBookId(book.getId());
        //then
        verify(buddyBookRepositoryMock).findById(buddyBookId);
    }

    @Test
    public void givenBookId_whenRemovingBook_thenIsBeingRemoved() throws NotExistingRecordException {
        //when
        testObject.remove(book.getId());
        //then
        verify(buddyBookRepositoryMock).delete(savedBuddyBook);
    }

    @Test
    public void givenAuthorIdAndBookId_whenGetRatingsWhereAuthorIdAndBuddy_thenRatingsAreGotten() {
        //given
        long authorId = 32432432L;
        final Long buddyId = principal.getId();
        when(buddyBookRepositoryMock.findRatingWhereAuthorIdAndBuddyId(authorId, buddyId))
                .thenReturn(expectedRatings);
        //when
        final List<BuddyBook> actualRatings = testObject.getRatingsWhereAuthorIdAndBuddy(authorId, principal);
        //then
        verify(buddyBookRepositoryMock).findRatingWhereAuthorIdAndBuddyId(authorId, buddyId);
        assertThat(actualRatings, is(expectedRatings));
    }

    @Test
    public void givenBookId_whenSearchForPrincipalBuddiesBookRatings_thenBuddiesBookRatingsAreSearched() throws NotExistingRecordException {
        //given
        when(buddyBookRepositoryMock.findALlPrincipalBuddiesBookRatings(bookId, principalId, buddiesStatusId))
                .thenReturn(expectedRatings);
        //when
        final List<BuddyBook> actual = testObject.findAllPrincipalBuddiesBookRatings(bookId);
        //then
        verify(buddyBookRepositoryMock).findALlPrincipalBuddiesBookRatings(bookId, principalId, buddiesStatusId);
        assertThat(actual, is(expectedRatings));
    }

    @ParameterizedTest
    @MethodSource("ratingsAndAverageRatings")
    public void givenRatings_whenCountAverageRatings_thenCorrectResultIsCalculated(List<BuddyBook> ratings,
                                                                                   double expectedResult) throws NotExistingRecordException {
        final double actualResult = testObject.countRating(ratings);
        assertEquals(actualResult, expectedResult, 0.01);
    }

    private static Stream<Arguments> ratingsAndAverageRatings() {
        final BuddyBook r0 = BuddyBook.builder().rate(0).build();
        final BuddyBook r1 = BuddyBook.builder().rate(1).build();
        final BuddyBook r2 = BuddyBook.builder().rate(2).build();
        final BuddyBook r3 = BuddyBook.builder().rate(3).build();
        final BuddyBook r4 = BuddyBook.builder().rate(4).build();
        final BuddyBook r10 = BuddyBook.builder().rate(10).build();

        return Stream.of(
                Arguments.of(List.of(r0, r0, r0), 0.00),
                Arguments.of(List.of(r0, r0, r10), 3.33),
                Arguments.of(List.of(r1, r2, r3, r4), 2.50)
        );
    }

    @Test
    public void givenLimit_whenSearchRecentRatingsAddedByBuddies_thenRecentRatingsAreSearched() throws NotExistingRecordException {
        //given
        int limit = 10;
        when(buddyBookRepositoryMock
                .findRecentlyAddedBuddyBookWithBookAndBuddiesLimitTo(principalId, buddiesStatusId, limit))
                .thenReturn(expectedRatings);
        final RelationStatus buddiesStatus = new RelationStatus(buddiesStatusId, "buddies");
        when(relationStatusRepositoryMock.findFirstByName("buddies")).thenReturn(Optional.of(buddiesStatus));
        //when
        final List<BuddyBook> actualRatings = testObject.findRecentlyAddedByBuddies(limit);
        //then
        verify(buddyBookRepositoryMock)
                .findRecentlyAddedBuddyBookWithBookAndBuddiesLimitTo(principalId, buddiesStatusId, limit);
        assertThat(actualRatings, is(expectedRatings));
    }


}