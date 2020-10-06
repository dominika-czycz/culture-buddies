package pl.coderslab.cultureBuddies.buddies;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;
import java.util.Base64;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.atMost;
import static org.mockito.Mockito.verify;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
class ProfilePictureServiceTest {
    @Autowired
    private PictureService testObj;
    @Spy
    private Buddy buddySpy;

    @BeforeEach
    public void setup() {
        buddySpy.setUsername("bestBuddy");
        buddySpy.setEmail("test@gmail.com");
        buddySpy.setName("Anna");
        buddySpy.setLastName("Kowalska");
        buddySpy.setPassword("annaKowalska");
    }

    @Test
    public void whenSavePicture_thenPictureIsSetToBuddy() throws IOException {
        //given
        MockMultipartFile mockProfilePicture = new MockMultipartFile(
                "picture", "profilePic.jpg", "text/jpeg", "test file".getBytes());
        //when
        testObj.save(mockProfilePicture, buddySpy);
        //then
        verify(buddySpy).setPicture(mockProfilePicture.getBytes());
    }

    @Test
    public void whenPictureIsNull_thenPictureIsNotSetToBuddy() throws IOException {
        //when
        testObj.save(null, buddySpy);
        //then
        verify(buddySpy, atMost(0)).setPicture(null);
    }

    @Test
    public void givenBuddyWithPicture_whenGetPictureBytesFromBuddy_thenBase64StringIsReturned() {
        //given
        byte[] picture = "a nice profile picture".getBytes();
        buddySpy.setPicture(picture);
        final String expectedString = Base64.getEncoder().encodeToString(picture);
        //when
        final String actualString = testObj.getPicture(buddySpy);
        //then
        assertThat(actualString, is(expectedString));
    }

}