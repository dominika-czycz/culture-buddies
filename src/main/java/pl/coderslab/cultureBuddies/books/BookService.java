package pl.coderslab.cultureBuddies.books;

import pl.coderslab.cultureBuddies.buddyBook.BuddyBook;
import pl.coderslab.cultureBuddies.exceptions.InvalidDataFromExternalRestApiException;
import pl.coderslab.cultureBuddies.exceptions.NotExistingRecordException;
import pl.coderslab.cultureBuddies.exceptions.RelationshipAlreadyCreatedException;

import java.util.List;

public interface BookService {
    BuddyBook addBookToBuddy(Book book) throws InvalidDataFromExternalRestApiException, NotExistingRecordException, RelationshipAlreadyCreatedException;

    List<Book> findBooksByAuthorAndUsername(String username, Long authorId) throws NotExistingRecordException;

    List<Book> findBooksByAuthorAndPrincipalUsername(Long authorId) throws NotExistingRecordException;

    List<BuddyBook> findBooksRateWhereAuthorIdAndBuddyUsername(Long authorId, String username) throws NotExistingRecordException;

    List<BuddyBook> findBooksRateOfPrincipalByAuthorId(Long authorId) throws NotExistingRecordException;

    Book findById(Long id) throws NotExistingRecordException;

    Book findByIdWithAuthors(Long bookId);
}
