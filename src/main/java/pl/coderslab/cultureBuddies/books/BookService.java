package pl.coderslab.cultureBuddies.books;

import pl.coderslab.cultureBuddies.buddies.BuddyBook;
import pl.coderslab.cultureBuddies.exceptions.InvalidDataFromExternalRestApiException;
import pl.coderslab.cultureBuddies.exceptions.NotExistingRecordException;
import pl.coderslab.cultureBuddies.googleapis.restModel.BookFromGoogle;

import java.util.List;

public interface BookService {
    boolean addBookToBuddy(BookFromGoogle bookFromGoogle) throws InvalidDataFromExternalRestApiException, NotExistingRecordException;

    Book saveBook(BookFromGoogle bookFromGoogle) throws InvalidDataFromExternalRestApiException;

    List<Book> findBooksByAuthorAndUsername(String username, Long authorId) throws NotExistingRecordException;
    List<Book> findBooksByAuthorAndPrincipalUsername(Long authorId) throws NotExistingRecordException;
    List<BuddyBook> findBooksRateWhereAuthorIdAndBuddyUsername(Long authorId, String username) throws NotExistingRecordException;
    List<BuddyBook> findBooksRateOfPrincipalByAuthorId(Long authorId) throws NotExistingRecordException;
}
