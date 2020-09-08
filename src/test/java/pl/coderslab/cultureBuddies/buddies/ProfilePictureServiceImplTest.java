package pl.coderslab.cultureBuddies.buddies;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@SpringBootTest
class ProfilePictureServiceImplTest {
    @Autowired
    PictureService testObj;

    @Test
    public void whenPictureSaved_thenPicturedUrlSavedToBuddy() throws IOException {
        //given
        Buddy buddy = Buddy.builder()
                .username("bestBuddy")
                .email("test@gmail.com")
                .name("Anna")
                .lastName("Kowalska")
                .password("annaKowalska")
                .city("Wrocław")
                .build();
        MockMultipartFile mockMultipartFile = new MockMultipartFile(
                "picture", "profilePic.jpg", "text/jpeg", "test file".getBytes());
        //when
        testObj.savePicture(mockMultipartFile, buddy);
        //then
        assertThat(buddy.getPictureUrl(), is(mockMultipartFile.getOriginalFilename()));
    }


}