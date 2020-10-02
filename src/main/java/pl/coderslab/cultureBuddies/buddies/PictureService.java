package pl.coderslab.cultureBuddies.buddies;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface PictureService {
    void save(MultipartFile profilePicture, Buddy buddy) throws IOException;

    String getPicture(Buddy buddy);
}
