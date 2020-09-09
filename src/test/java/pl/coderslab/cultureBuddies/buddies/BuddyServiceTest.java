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

import static org.mockito.Mockito.*;

@SpringBootTest
@ExtendWith(SpringExtension.class)
class BuddyServiceTest {
    @MockBean
    private BuddyRepository buddyRepository;
    @MockBean
    private PictureService pictureService;
    @Autowired
    BuddyService testObject;
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
        testObject.save(profilePicture, unsavedBuddy);
        //then
        verify(buddyRepository, atLeastOnce()).save(unsavedBuddy);
        verify(pictureService, atLeastOnce()).savePicture(profilePicture, unsavedBuddy);
    }

    @Test
    public void whenSavingBuddyWithoutPicture_thenBuddySaved() throws IOException {
        //when
        testObject.save(null, unsavedBuddy);
        //then
        verify(buddyRepository, atLeastOnce()).save(unsavedBuddy);
        verify(pictureService, atMost(0)).savePicture(null, unsavedBuddy);
    }

}