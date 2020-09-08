package pl.coderslab.cultureBuddies.buddies;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface PictureService {
    public String savePicture(MultipartFile profilePicture, Buddy buddy) throws IOException;
}
