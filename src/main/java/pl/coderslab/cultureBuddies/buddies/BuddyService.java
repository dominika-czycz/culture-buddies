package pl.coderslab.cultureBuddies.buddies;

import org.springframework.web.multipart.MultipartFile;
import pl.coderslab.cultureBuddies.exceptions.NotExistingNameException;

import java.io.IOException;

public interface BuddyService {
    boolean save(MultipartFile profilePicture, Buddy buddy) throws IOException;

    Buddy findByUsername(String username) throws NotExistingNameException;

    String getPrincipalUsername();

    Buddy findBuddyByUsernameWithAuthors(String username) throws NotExistingNameException;

    Buddy findAuthenticatedBuddyWithAuthors() throws NotExistingNameException;
}
