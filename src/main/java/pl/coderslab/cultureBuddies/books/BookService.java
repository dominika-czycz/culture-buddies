package pl.coderslab.cultureBuddies.books;

import javassist.tools.web.BadHttpRequest;
import pl.coderslab.cultureBuddies.author.Author;
import pl.coderslab.cultureBuddies.buddyBook.BuddyBook;
import pl.coderslab.cultureBuddies.exceptions.InvalidDataFromExternalRestApiException;
import pl.coderslab.cultureBuddies.exceptions.NotExistingRecordException;
import pl.coderslab.cultureBuddies.exceptions.RelationshipAlreadyCreatedException;
import pl.coderslab.cultureBuddies.googleapis.restModel.BookFromGoogle;

import java.util.List;

public interface BookService {
    int getMaxResultsPage(String title, String author) throws NotExistingRecordException;

    List<BookFromGoogle> getBooksFromExternalApi(String title, String author, int pageNo) throws NotExistingRecordException, BadHttpRequest;

    Author getAuthorById(Long authorId) throws NotExistingRecordException;

    BuddyBook addBookToBuddy(Book book) throws InvalidDataFromExternalRestApiException, NotExistingRecordException, RelationshipAlreadyCreatedException;

    List<BuddyBook> findBooksRateOfPrincipalByAuthorId(Long authorId) throws NotExistingRecordException;

    Book findById(Long id) throws NotExistingRecordException;

    Book findByIdWithAuthors(Long bookId);

    List<Author> getBooksAuthorsOfPrincipal() throws NotExistingRecordException;

    List<Author> getBooksAuthorsOfBuddy(Long buddyId);

    List<BuddyBook> findBooksRateOfBuddyByAuthorId(Long buddyId, Long authorId) throws NotExistingRecordException;

}
