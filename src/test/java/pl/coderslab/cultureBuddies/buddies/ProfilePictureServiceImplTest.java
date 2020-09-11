package pl.coderslab.cultureBuddies.buddies;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ExtendWith(SpringExtension.class)
class ProfilePictureServiceTest {
    @Autowired
    private PictureService testObj;
    private Buddy buddyToSave;

    @BeforeEach
    public void setup() {
        buddyToSave = Buddy.builder()
                .username("bestBuddy")
                .email("test@gmail.com")
                .name("Anna")
                .lastName("Kowalska")
                .password("annaKowalska")
                .city("Wrocław")
                .build();
    }

    @Test
    public void whenPictureSaved_thenPicturedUrlSavedToBuddy() throws IOException {
        //given
        MockMultipartFile mockProfilePicture = new MockMultipartFile(
                "picture", "profilePic.jpg", "text/jpeg", "test file".getBytes());
        //when
        final boolean isSaved = testObj.save(mockProfilePicture, buddyToSave);
        //then
        assertTrue(isSaved);
        assertNotNull(buddyToSave.getPictureUrl());
    }

    @Test
    public void whenPictureIsNull_thenPictureNotSaved() throws IOException {
        //when
        final boolean isSaved = testObj.save(null, buddyToSave);
        //then
        assertFalse(isSaved);
        assertNull(buddyToSave.getPictureUrl());
    }

    @Test
    public void whenPictureIsEmpty_thenPictureNotSaved() throws IOException {
        //given
        final MockMultipartFile mockEmptyMultipartFile = new MockMultipartFile("empty", (byte[]) null);
        //when
        final boolean isSaved = testObj.save(mockEmptyMultipartFile, buddyToSave);
        //then
        assertFalse(isSaved);
        assertNull(buddyToSave.getPictureUrl());
    }
}