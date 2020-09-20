package pl.coderslab.cultureBuddies.author;

import pl.coderslab.cultureBuddies.exceptions.NotExistingRecordException;

import java.util.List;

public interface AuthorService {
    List<Author> getOrderedAuthorsListOfPrincipalUser() throws NotExistingRecordException;

    Author findById(Long authorId) throws NotExistingRecordException;

    Author saveIfNotExistYet(String firstName, String lastName);

    boolean checkIfAuthorExists(Long authorId) throws NotExistingRecordException;
}
