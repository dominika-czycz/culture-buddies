package pl.coderslab.cultureBuddies.buddies;

import org.springframework.web.multipart.MultipartFile;
import pl.coderslab.cultureBuddies.books.Book;
import pl.coderslab.cultureBuddies.exceptions.NotExistingRecordException;

import java.io.IOException;

public interface BuddyService {
    boolean addBook(Book book) throws NotExistingRecordException;

    boolean save(MultipartFile profilePicture, Buddy buddy) throws IOException;

    Buddy findByUsername(String username) throws NotExistingRecordException;

    Buddy findPrincipal() throws NotExistingRecordException;

    String getPrincipalUsername();

}
