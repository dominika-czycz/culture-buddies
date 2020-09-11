package pl.coderslab.cultureBuddies.buddies;

import org.springframework.web.multipart.MultipartFile;
import pl.coderslab.cultureBuddies.exceptions.NonExistingNameException;

import java.io.IOException;

public interface BuddyService {
    boolean save(MultipartFile profilePicture, Buddy buddy) throws IOException;

    Buddy findByUsername(String username) throws NonExistingNameException;
}
