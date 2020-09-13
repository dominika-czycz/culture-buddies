package pl.coderslab.cultureBuddies.books;

import pl.coderslab.cultureBuddies.exceptions.NotExistingNameException;

import java.util.List;

public interface BookService {
    List<Book> findBooksByAuthorAndUsername(String username, Long authorId) throws NotExistingNameException;
    List<Book> findBooksByAuthorAndPrincipalUsername(Long authorId) throws  NotExistingNameException;
}
