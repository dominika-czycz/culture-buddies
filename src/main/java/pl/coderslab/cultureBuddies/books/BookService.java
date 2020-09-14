package pl.coderslab.cultureBuddies.books;

import pl.coderslab.cultureBuddies.buddies.BuddyBook;
import pl.coderslab.cultureBuddies.exceptions.NotExistingRecordException;

import java.util.List;

public interface BookService {
    List<Book> findBooksByAuthorAndUsername(String username, Long authorId) throws NotExistingRecordException;
    List<Book> findBooksByAuthorAndPrincipalUsername(Long authorId) throws NotExistingRecordException;
    List<BuddyBook> findBooksRateWhereAuthorIdAndBuddyUsername(Long authorId, String username) throws NotExistingRecordException;
    List<BuddyBook> findBooksRateOfPrincipalByAuthorId(Long authorId) throws NotExistingRecordException;
}
