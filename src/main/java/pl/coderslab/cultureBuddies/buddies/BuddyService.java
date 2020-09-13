package pl.coderslab.cultureBuddies.buddies;

import org.springframework.web.multipart.MultipartFile;
import pl.coderslab.cultureBuddies.exceptions.NotExistingRecordException;

import java.io.IOException;

public interface BuddyService {
    boolean save(MultipartFile profilePicture, Buddy buddy) throws IOException;

    Buddy findByUsername(String username) throws NotExistingRecordException;

    String getPrincipalUsername();

    Buddy findBuddyByUsernameWithAuthors(String username) throws NotExistingRecordException;

    Buddy findAuthenticatedBuddyWithAuthors() throws NotExistingRecordException;

    Buddy findById(Long id) throws NotExistingRecordException;
}
