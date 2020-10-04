package pl.coderslab.cultureBuddies.buddies;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import pl.coderslab.cultureBuddies.author.Author;
import pl.coderslab.cultureBuddies.books.Book;
import pl.coderslab.cultureBuddies.buddyBook.BuddyBook;
import pl.coderslab.cultureBuddies.buddyBook.BuddyBookRepository;
import pl.coderslab.cultureBuddies.buddyBuddy.BuddyBuddyId;
import pl.coderslab.cultureBuddies.buddyBuddy.BuddyRelation;
import pl.coderslab.cultureBuddies.buddyBuddy.RelationStatus;
import pl.coderslab.cultureBuddies.events.Event;
import pl.coderslab.cultureBuddies.exceptions.EmptyKeysException;
import pl.coderslab.cultureBuddies.exceptions.NotExistingRecordException;
import pl.coderslab.cultureBuddies.exceptions.RelationshipAlreadyCreatedException;
import pl.coderslab.cultureBuddies.security.Role;
import pl.coderslab.cultureBuddies.security.RoleRepository;

import java.io.IOException;
import java.util.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@ExtendWith(SpringExtension.class)
class BuddyServiceTest {
    @MockBean
    private BuddyRepository buddyRepositoryMock;
    @MockBean
    private PictureService pictureServiceMock;
    @MockBean
    private RoleRepository roleRepositoryMock;
    @MockBean
    private BuddyBookRepository buddyBookRepositoryMock;
    @MockBean
    private RelationStatusRepository relationStatusRepositoryMock;
    @MockBean
    private BuddyRelationRepository buddyRelationRepositoryMock;
    @Autowired
    private BuddyService testObject;
    @Spy
    private Buddy buddySpy;
    @Spy
    private Book book;
    @Spy
    private BuddyBook buddyBook;

    private Buddy unsavedBuddy;
    private Buddy savedBuddy;
    private final List<Buddy> expectedBuddies = new ArrayList<>();
    private Buddy someBuddy;
    private final RelationStatus buddiesStatus = new RelationStatus(7L, "buddies");
    private final RelationStatus invitingStatus = new RelationStatus(5L, "inviting");
    private BuddyRelation someBuddyPrincipalRelation;
    private BuddyRelation principalSomeBuddyRelation;
    private final BuddyBuddyId principalSomeBuddyRelationId = new BuddyBuddyId();
    private final BuddyBuddyId someBuddyPrincipalRelationId = new BuddyBuddyId();


    @BeforeEach
    public void setup() {
        unsavedBuddy = Buddy.builder()
                .username("bestBuddy")
                .email("test@gmail.com")
                .name("Anna")
                .lastName("Kowalska")
                .password("annaKowalska")
                .books(new HashSet<>())
                .build();
        savedBuddy = unsavedBuddy.toBuilder()
                .id(10L).build();
        someBuddy = Buddy.builder()
                .username("someBuddy")
                .id(333L).build();
        expectedBuddies.add(someBuddy);

        when(relationStatusRepositoryMock.findFirstByName("inviting"))
                .thenReturn(Optional.of(invitingStatus));
        when(relationStatusRepositoryMock.findFirstByName("buddies"))
                .thenReturn(Optional.of(buddiesStatus));

        when(buddyRepositoryMock.save(unsavedBuddy)).thenReturn(savedBuddy);
        when(buddyRepositoryMock.findFirstByUsernameIgnoringCase("bestBuddy"))
                .thenReturn(Optional.ofNullable(savedBuddy));
        when(buddyRepositoryMock.findById(someBuddy.getId()))
                .thenReturn(Optional.ofNullable(someBuddy));


        someBuddyPrincipalRelationId.setBuddyId(someBuddy.getId());
        someBuddyPrincipalRelationId.setBuddyOfId(savedBuddy.getId());
        principalSomeBuddyRelationId.setBuddyId(savedBuddy.getId());
        principalSomeBuddyRelationId.setBuddyOfId(someBuddy.getId());
        someBuddyPrincipalRelation = BuddyRelation.builder()
                .id(someBuddyPrincipalRelationId)
                .status(buddiesStatus).build();
        principalSomeBuddyRelation = BuddyRelation.builder()
                .id(principalSomeBuddyRelationId)
                .status(buddiesStatus).build();

        when(buddyRelationRepositoryMock.findById(someBuddyPrincipalRelationId))
                .thenReturn(Optional.ofNullable(someBuddyPrincipalRelation));
        when(buddyRelationRepositoryMock.findById(principalSomeBuddyRelationId))
                .thenReturn(Optional.ofNullable(principalSomeBuddyRelation));
    }

    @Test
    @WithMockUser("bestBuddy")
    public void givenBookAlreadyAssignedToPrincipal_whenAddingBookToPrincipal_thenRelationAlreadyCreatedException() {
        //given
        when(buddyBookRepositoryMock.findByBookAndBuddy(book, savedBuddy))
                .thenReturn(Optional.of(buddyBook));
        //when, then
        assertThrows(RelationshipAlreadyCreatedException.class,
                () -> testObject.addBookToPrincipalBuddy(book));
    }

    @Test
    @WithMockUser("bestBuddy")
    public void givenBookAndBuddy_whenBookAddedToBuddy_thenBuddyBookRelationCreated() throws NotExistingRecordException, RelationshipAlreadyCreatedException {
        //given
        final Author author = Author.builder().id(10L).lastName("Kowalski").build();
        final Book book = Book.builder().title("new Book").identifier("ISBN").authors(Set.of(author)).build();
        buddySpy.setUsername("bestBuddy");
        when(buddyBookRepositoryMock.findByBookAndBuddy(book, buddySpy)).thenReturn(Optional.empty());
        when(buddyRepositoryMock.findFirstByUsernameIgnoringCase(buddySpy.getUsername())).thenReturn(Optional.of(buddySpy));
        final BuddyBook buddyBook = BuddyBook.builder().book(book).buddy(buddySpy).build();
        when(buddySpy.addBook(book)).thenReturn(buddyBook);
        when(buddyBookRepositoryMock.save(buddyBook)).thenReturn(buddyBook);
        //when
        final BuddyBook createdBuddyBook = testObject.addBookToPrincipalBuddy(book);
        //then
        verify(buddyBookRepositoryMock).save(buddyBook);
        assertNotNull(createdBuddyBook);
    }

    @Test
    public void whenSavingBuddyAndPicture_thenBuddyAndPictureSaved() throws IOException {
        MockMultipartFile profilePicture = new MockMultipartFile("profilePicture", "myPicture.jpg", "image/jpeg", "some profile picture".getBytes());
        Buddy buddyWithPicture = unsavedBuddy.toBuilder().username("Some buddy").build();
        //when
        final boolean isSaved = testObject.save(profilePicture, buddyWithPicture);
        //then
        verify(buddyRepositoryMock).save(buddyWithPicture);
        verify(pictureServiceMock).save(profilePicture, buddyWithPicture);
        assertTrue(isSaved);
    }

    @Test
    public void whenSavingBuddyWithoutPicture_thenBuddySaved() throws IOException {
        //when
        Buddy buddyWithoutPicture = unsavedBuddy.toBuilder().username("buddy without picture").build();
        final boolean isSaved = testObject.save(null, buddyWithoutPicture);
        //then
        verify(buddyRepositoryMock).save(buddyWithoutPicture);
        verify(pictureServiceMock).save(null, buddyWithoutPicture);
        assertTrue(isSaved);
    }

    @Test
    public void whenSavingBuddyWithRole_thenBuddySaved() throws IOException {
        //given
        final Role roleUser = Role.builder().id(10L).name("ROLE_USER").build();
        buddySpy.setName("Spy");
        buddySpy.setUsername("SpyUsername");
        buddySpy.setPassword("password");
        buddySpy.setEmail("spy@spy");
        buddySpy.setLastName("SpyName");
        final Buddy savedSpy = buddySpy.toBuilder().id(10L).build();
        savedSpy.addRole(roleUser);
        //when
        when(roleRepositoryMock.findFirstByNameIgnoringCase("ROLE_USER")).thenReturn(roleUser);
        when(buddyRepositoryMock.save(buddySpy)).thenReturn(savedSpy);
        final boolean isSaved = testObject.save(null, buddySpy);
        //then
        verify(buddyRepositoryMock).save(buddySpy);
        verify(buddySpy).addRole(roleUser);
        assertTrue(isSaved);
    }

    @Test
    public void whenNonUniqueUsername_thenBuddyNotSaved() throws IOException {
        //given
        Buddy uniqueBuddy = unsavedBuddy.toBuilder().username("uniqueUsername").build();
        final Buddy nonUnique = uniqueBuddy.toBuilder().build();
        //when
        final boolean isSavedUniqueBuddy = testObject.save(null, uniqueBuddy);
        when(buddyRepositoryMock.findFirstByUsernameIgnoringCase(nonUnique.getUsername())).thenReturn(Optional.of(savedBuddy));
        final boolean isSavedDuplicatedBuddy = testObject.save(null, nonUnique);
        //then
        verify(buddyRepositoryMock, atMost(1)).save(unsavedBuddy);
        assertTrue(isSavedUniqueBuddy);
        assertFalse(isSavedDuplicatedBuddy);
    }

    @Test
    public void whenFindingBuddyByUsername_thenBuddyFound() throws NotExistingRecordException {
        //when
        final Buddy foundBuddy = testObject.findByUsername(savedBuddy.getUsername());
        //then
        assertThat(foundBuddy, is(savedBuddy));
        verify(buddyRepositoryMock).findFirstByUsernameIgnoringCase(savedBuddy.getUsername());
    }

    @Test
    public void givenNotExistingUsername_whenBuddyNotFound_thenThrowsNonExistingNameException() {
        //given
        String notExisting = "not existing buddy";
        //when, then
        assertThrows(NotExistingRecordException.class,
                () -> testObject.findByUsername(notExisting));
    }

    @Test
    @WithMockUser(username = "bestBuddy")
    public void givenPrincipalUsername_whenGettingUsername_thenUsernameGot() {
        //given
        String excepted = "bestBuddy";
        //when
        final String principalUsername = testObject.getPrincipalUsername();
        //then
        assertThat(principalUsername, is(excepted));
    }

    @Test
    @WithMockUser(username = "bestBuddy")
    public void whenGettingBuddiesOfPrincipal_thenBuddiesGot() throws NotExistingRecordException {
        //given

        when(buddyRepositoryMock.findAllBuddiesOfBuddyWithIdWhereRelationId(savedBuddy.getId(), buddiesStatus.getId()))
                .thenReturn(expectedBuddies);
        //when
        final List<Buddy> principalBuddies = testObject.getBuddiesOfPrincipal();
        //then
        assertThat(principalBuddies, is(expectedBuddies));
    }

    @Test
    @WithMockUser(username = "bestBuddy")
    public void whenGetBuddiesInvitingPrincipal_thenBuddiesAreGotten() throws NotExistingRecordException {
        //given

        when(buddyRepositoryMock.findAllBuddiesOfBuddyWithIdWhereRelationId(savedBuddy.getId(), invitingStatus.getId()))
                .thenReturn(expectedBuddies);
        //when
        final List<Buddy> buddiesInvitingPrincipal = testObject.getBuddiesInvitingPrincipal();
        //then
        verify(buddyRepositoryMock)
                .findAllBuddiesOfBuddyWithIdWhereRelationId(savedBuddy.getId(), invitingStatus.getId());
        assertThat(buddiesInvitingPrincipal, is(expectedBuddies));
    }

    @Test
    @WithMockUser(username = "bestBuddy")
    public void givenUsernameAndAuthorsIds_whenSearchForBuddies_thenBuddiesAreSearched() throws EmptyKeysException, NotExistingRecordException {
        //given
        String username = "someBuddy";
        final ArrayList<Long> authorsIds = new ArrayList<>(List.of(1L, 2L));
        when(buddyRepositoryMock.findNewBuddiesByAuthorsAndUsernameLike(authorsIds, username, savedBuddy.getId()))
                .thenReturn(expectedBuddies);
        //when
        final List<Buddy> actual = testObject.findByUsernameAndAuthors(username, authorsIds);
        //then
        verify(buddyRepositoryMock)
                .findNewBuddiesByAuthorsAndUsernameLike(authorsIds, username, savedBuddy.getId());

        assertThat(actual, is(expectedBuddies));
    }

    @Test
    @WithMockUser(username = "bestBuddy")
    public void notGivenAnyKeysToSearch_whenSearchForBuddies_thenEmptyKeysException() {
        //when, then
        assertThrows(EmptyKeysException.class,
                () -> testObject.findByUsernameAndAuthors(null, null));
    }

    @Test
    @WithMockUser(username = "bestBuddy")
    public void givenBuddyId_whenDeleteBuddy_thenBuddyIsDeleted() throws NotExistingRecordException {
        //when
        testObject.deleteBuddy(someBuddy.getId());
        //then
        verify(buddyRelationRepositoryMock).delete(Objects.requireNonNull(someBuddyPrincipalRelation));
        verify(buddyRelationRepositoryMock).delete(Objects.requireNonNull(principalSomeBuddyRelation));
    }

    @Test
    @WithMockUser(username = "bestBuddy")
    public void givenBuddyId_whenBlockBuddy_thenBuddyIsBlocked() throws NotExistingRecordException {
        //given
        RelationStatus blockingStatus = new RelationStatus();
        blockingStatus.setId(1L);
        blockingStatus.setName("blocking");
        RelationStatus blockedStatus = new RelationStatus();
        blockedStatus.setId(2L);
        blockedStatus.setName("blocked");
        principalSomeBuddyRelation.setStatus(blockingStatus);
        someBuddyPrincipalRelation.setStatus(blockedStatus);
        when(relationStatusRepositoryMock.findFirstByName("blocking"))
                .thenReturn(Optional.of(blockingStatus));
        when(relationStatusRepositoryMock.findFirstByName("blocked"))
                .thenReturn(Optional.of(blockedStatus));
        when(buddyRelationRepositoryMock.save(principalSomeBuddyRelation))
                .thenReturn(principalSomeBuddyRelation);
        when(buddyRelationRepositoryMock.save(someBuddyPrincipalRelation))
                .thenReturn(someBuddyPrincipalRelation);
        //when
        testObject.block(someBuddy.getId());
        //then
        verify(buddyRelationRepositoryMock).save(principalSomeBuddyRelation);
        verify(buddyRelationRepositoryMock).save(someBuddyPrincipalRelation);
    }

    @Test
    @WithMockUser(username = "bestBuddy")
    public void givenBuddyId_whenInviteBuddy_thenBuddyIsInvited() throws NotExistingRecordException {
        //given
        final RelationStatus invitedStatus = new RelationStatus(6L, "invited");
        when(relationStatusRepositoryMock.findFirstByName("invited"))
                .thenReturn(Optional.of(invitedStatus));
        principalSomeBuddyRelation.setStatus(invitingStatus);
        someBuddyPrincipalRelation.setStatus(invitedStatus);

        when(buddyRelationRepositoryMock.save(principalSomeBuddyRelation))
                .thenReturn(principalSomeBuddyRelation);
        when(buddyRelationRepositoryMock.save(someBuddyPrincipalRelation))
                .thenReturn(someBuddyPrincipalRelation);
        //when
        testObject.inviteBuddy(someBuddy.getId());
        //then
        verify(buddyRelationRepositoryMock).save(principalSomeBuddyRelation);
        verify(buddyRelationRepositoryMock).save(someBuddyPrincipalRelation);
    }

    @Test
    @WithMockUser(username = "bestBuddy")
    public void givenBuddyId_whenAcceptBuddy_thenBuddiesRelationIsSaved() throws NotExistingRecordException {
        //given
        principalSomeBuddyRelation.setStatus(buddiesStatus);
        someBuddyPrincipalRelation.setStatus(buddiesStatus);

        when(buddyRelationRepositoryMock.save(principalSomeBuddyRelation))
                .thenReturn(principalSomeBuddyRelation);
        when(buddyRelationRepositoryMock.save(someBuddyPrincipalRelation))
                .thenReturn(someBuddyPrincipalRelation);
        //when
        testObject.acceptBuddy(someBuddy.getId());
        //then
        verify(buddyRelationRepositoryMock).save(principalSomeBuddyRelation);
        verify(buddyRelationRepositoryMock).save(someBuddyPrincipalRelation);
    }

    @Test
    public void givenBuddyId_whenFindById_thenBuddyIsSearched() throws NotExistingRecordException {
        Long buddyId = someBuddy.getId();
        final Buddy actual = testObject.findById(buddyId);
        verify(buddyRepositoryMock).findById(buddyId);
        assertThat(actual, is(someBuddy));
    }

    @Test
    @WithMockUser(username = "bestBuddy")
    public void whenGetPrincipalWithEvents_thenPrincipalWithEventsIsSearched() throws NotExistingRecordException {
        savedBuddy.setEvents(Set.of(new Event()));
        when(buddyRepositoryMock.findByIdWithEvents(savedBuddy.getId()))
                .thenReturn(Optional.ofNullable(savedBuddy));
        final Buddy actual = testObject.getPrincipalWithEvents();
        verify(buddyRepositoryMock).findByIdWithEvents(savedBuddy.getId());
        assertThat(actual, is(savedBuddy));
    }
}























