package pl.coderslab.cultureBuddies.buddies;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@SpringBootTest
@ExtendWith(SpringExtension.class)
class BuddyServiceTest {
    @MockBean
    private BuddyRepository buddyRepository;
    @MockBean
    private PictureService pictureService;
    @Autowired
    private BuddyService testObject;
    private Buddy unsavedBuddy;

    @BeforeEach
    public void beforeEachTests() {
        unsavedBuddy = Buddy.builder()
                .username("bestBuddy")
                .email("test@gmail.com")
                .name("Anna")
                .lastName("Kowalska")
                .password("annaKowalska")
                .city("Wrocław")
                .build();
    }

    @Test
    public void whenSavingBuddyAndPicture_thenBuddyAndPictureSaved() throws IOException {
        MockMultipartFile profilePicture = new MockMultipartFile("profilePicture", "myPicture.jpg", "image/jpeg", "some profile picture".getBytes());
        //when
        final boolean isSaved = testObject.save(profilePicture, unsavedBuddy);
        //then
        verify(buddyRepository, atLeastOnce()).save(unsavedBuddy);
        verify(pictureService, atLeastOnce()).save(profilePicture, unsavedBuddy);
        assertTrue(isSaved);
    }

    @Test
    public void whenSavingBuddyWithoutPicture_thenBuddySaved() throws IOException {
        //when
        final boolean isSaved = testObject.save(null, unsavedBuddy);
        //then
        verify(buddyRepository, atLeastOnce()).save(unsavedBuddy);
        verify(pictureService, atLeastOnce()).save(null, unsavedBuddy);
        assertTrue(isSaved);
    }

    @Test
    public void whenNonUniqueUsername_thenBuddyNotSaved() throws IOException {
        //given
        final Buddy nonUnique = unsavedBuddy.toBuilder().build();
        final Buddy savedBuddy = unsavedBuddy.toBuilder().id(10L).build();
        //when
        when(buddyRepository.save(unsavedBuddy)).thenReturn(savedBuddy);
        final boolean isSavedUniqueBuddy = testObject.save(null, unsavedBuddy);
        when(buddyRepository.findFirstByUsernameIgnoringCase(nonUnique.getUsername())).thenReturn(Optional.of(savedBuddy));
        final boolean isSavedDuplicatedBuddy = testObject.save(null, nonUnique);
        //then
        verify(buddyRepository, atMost(1)).save(unsavedBuddy);
        assertTrue(isSavedUniqueBuddy);
        assertFalse(isSavedDuplicatedBuddy);
    }

}