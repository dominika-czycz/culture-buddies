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
import pl.coderslab.cultureBuddies.exceptions.NotExistingNameException;
import pl.coderslab.cultureBuddies.security.Role;
import pl.coderslab.cultureBuddies.security.RoleRepository;

import java.io.IOException;
import java.util.Optional;

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
    @Autowired
    private BuddyService testObject;
    @Spy
    private Buddy buddySpy;

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
                .city("Wrocław")
                .build();
        savedBuddy = unsavedBuddy.toBuilder()
                .id(10L).build();
        when(buddyRepositoryMock.save(unsavedBuddy)).thenReturn(savedBuddy);
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
        buddySpy.setCity("Wrocław");
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
    public void whenFindingBuddyByUsername_thenBuddyFound() throws NotExistingNameException {
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
        assertThrows(NotExistingNameException.class,
                () -> testObject.findByUsername(savedBuddy.getUsername()));
    }

    @Test
    @WithMockUser(username = "testUsername")
    public void givenUsername_whenGettingUsername_thenUsernameGot(){
        //given
        String excepted = "testUsername";
        //when
        final String principalUsername = testObject.getPrincipalUsername();
        //then
        assertThat(principalUsername, is(excepted));
    }
}