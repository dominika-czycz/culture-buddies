package pl.coderslab.cultureBuddies.buddies;

import org.springframework.web.multipart.MultipartFile;
import pl.coderslab.cultureBuddies.books.Book;
import pl.coderslab.cultureBuddies.buddyBook.BuddyBook;
import pl.coderslab.cultureBuddies.exceptions.NotExistingRecordException;
import pl.coderslab.cultureBuddies.exceptions.RelationshipAlreadyCreatedException;

import java.io.IOException;

public interface BuddyService {
    BuddyBook addBookToPrincipalBuddy(Book book) throws NotExistingRecordException, RelationshipAlreadyCreatedException;

    boolean save(MultipartFile profilePicture, Buddy buddy) throws IOException;

    Buddy findByUsername(String username) throws NotExistingRecordException;

    Buddy findPrincipal() throws NotExistingRecordException;

    String getPrincipalUsername();

}
