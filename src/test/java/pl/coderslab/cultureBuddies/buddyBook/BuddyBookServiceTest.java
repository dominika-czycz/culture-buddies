package pl.coderslab.cultureBuddies.buddyBook;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import pl.coderslab.cultureBuddies.books.Book;
import pl.coderslab.cultureBuddies.buddies.Buddy;
import pl.coderslab.cultureBuddies.buddies.BuddyService;
import pl.coderslab.cultureBuddies.exceptions.NotExistingRecordException;

import java.util.Optional;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
class BuddyBookServiceTest {
    @Autowired
    private BuddyBookService testObject;
    @MockBean
    private BuddyBookRepository buddyBookRepositoryMock;
    @MockBean
    private BuddyService buddyServiceMock;
    private BuddyBook validBuddyBook;
    private Buddy buddy;
    private Book book;
    private BuddyBookId buddyBookId;

    @BeforeEach
    public void setUp() throws NotExistingRecordException {
        buddy = Buddy.builder()
                .username("testBuddy")
                .id(10L)
                .build();
        book = Book.builder()
                .title("title")
                .id(2222L)
                .build();
        buddyBookId =
                new BuddyBookId();
        buddyBookId.setBuddyId(buddy.getId());
        buddyBookId.setBookId(book.getId());
        validBuddyBook = BuddyBook.builder()
                .id(buddyBookId)
                .bookId(book.getId())
                .buddyId(buddy.getId())
                .buddy(buddy)
                .book(book)
                .rate(8)
                .build();
        when(buddyBookRepositoryMock.findById(buddyBookId))
                .thenReturn(Optional.ofNullable(validBuddyBook));
        when(buddyServiceMock.getPrincipal()).thenReturn(buddy);
    }

    @Test
    public void givenValidBuddyBook_whenUpdatingBuddyBook_thenBeingSaved() throws NotExistingRecordException {
        //when
        testObject.updateBuddyBook(validBuddyBook);
        //then
        verify(buddyBookRepositoryMock).save(validBuddyBook);
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
        verify(buddyBookRepositoryMock).delete(validBuddyBook);

    }

}