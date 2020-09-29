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
import pl.coderslab.cultureBuddies.buddyBuddy.RelationStatus;
import pl.coderslab.cultureBuddies.exceptions.NotExistingRecordException;
import pl.coderslab.cultureBuddies.exceptions.RelationshipAlreadyCreatedException;
import pl.coderslab.cultureBuddies.security.Role;
import pl.coderslab.cultureBuddies.security.RoleRepository;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

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
        when(buddyRepositoryMock.save(unsavedBuddy)).thenReturn(savedBuddy);
        when(buddyRepositoryMock.findFirstByUsernameIgnoringCase("bestBuddy")).thenReturn(Optional.ofNullable(savedBuddy));
    }

    @Test
    @WithMockUser("bestBuddy")
    public void givenBookAlreadyAssignedToPrincipal_whenAddingBookToPrincipal_thenRelationAlreadyCreatedException() throws NotExistingRecordException, RelationshipAlreadyCreatedException {
        //given
        when(buddyRepositoryMock.findFirstByUsernameIgnoringCase("bestBuddy"))
                .thenReturn(Optional.ofNullable(savedBuddy));
        when(buddyBookRepositoryMock.findByBookAndBuddy(book, savedBuddy))
                .thenReturn(Optional.of(buddyBook));
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
        //when
        final boolean isSaved = testObject.save(profilePicture, unsavedBuddy);
        //then
        verify(buddyRepositoryMock, atLeastOnce()).save(unsavedBuddy);
        verify(pictureServiceMock, atLeastOnce()).save(profilePicture, unsavedBuddy);
        assertTrue(isSaved);
    }

    @Test
    public void whenSavingBuddyWithoutPicture_thenBuddySaved() throws IOException {
        //when
        final boolean isSaved = testObject.save(null, unsavedBuddy);
        //then
        verify(buddyRepositoryMock, atLeastOnce()).save(unsavedBuddy);
        verify(pictureServiceMock, atLeastOnce()).save(null, unsavedBuddy);
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
        verify(buddyRepositoryMock, atLeastOnce()).save(buddySpy);
        verify(buddySpy, atLeastOnce()).addRole(roleUser);
        assertTrue(isSaved);
    }

    @Test
    public void whenNonUniqueUsername_thenBuddyNotSaved() throws IOException {
        //given
        final Buddy nonUnique = unsavedBuddy.toBuilder().build();
        //when
        final boolean isSavedUniqueBuddy = testObject.save(null, unsavedBuddy);
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
        when(buddyRepositoryMock.findFirstByUsernameIgnoringCase(savedBuddy.getUsername()))
                .thenReturn(Optional.ofNullable(savedBuddy));
        final Buddy foundBuddy = testObject.findByUsername(savedBuddy.getUsername());
        assertThat(foundBuddy, is(savedBuddy));
        verify(buddyRepositoryMock, atLeastOnce()).findFirstByUsernameIgnoringCase(savedBuddy.getUsername());
    }

    @Test
    public void whenBuddyNotFound_thenThrowsNonExistingNameException() {
        //when
        when(buddyRepositoryMock.findFirstByUsernameIgnoringCase(savedBuddy.getUsername()))
                .thenReturn(Optional.empty());
        //then
        assertThrows(NotExistingRecordException.class,
                () -> testObject.findByUsername(savedBuddy.getUsername()));
    }

    @Test
    @WithMockUser(username = "testUsername")
    public void givenPrincipalUsername_whenGettingUsername_thenUsernameGot() {
        //given
        String excepted = "testUsername";
        //when
        final String principalUsername = testObject.getPrincipalUsername();
        //then
        assertThat(principalUsername, is(excepted));
    }

    @Test
    @WithMockUser(username = "bestBuddy")
    public void whenGettingBuddiesOfPrincipal_thenBuddiesGot() throws NotExistingRecordException {
        RelationStatus buddiesStatus = new RelationStatus(7L, "buddies");
        final List<Buddy> buddies = List.of(this.buddySpy);
        when(relationStatusRepositoryMock.findFirstByName("buddies"))
                .thenReturn(Optional.of(buddiesStatus));
        when(buddyRepositoryMock.findAllBuddiesOfBuddyWithIdWhereRelationId(savedBuddy.getId(), buddiesStatus.getId()))
                .thenReturn(buddies);
        final List<Buddy> principalBuddies = testObject.getBuddiesOfPrincipal();
        assertThat(principalBuddies, is(buddies));
    }
}