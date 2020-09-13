package pl.coderslab.cultureBuddies.books;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.coderslab.cultureBuddies.author.Author;
import pl.coderslab.cultureBuddies.author.AuthorRepository;
import pl.coderslab.cultureBuddies.buddies.Buddy;
import pl.coderslab.cultureBuddies.buddies.BuddyBook;
import pl.coderslab.cultureBuddies.buddies.BuddyBookRepository;
import pl.coderslab.cultureBuddies.buddies.BuddyService;
import pl.coderslab.cultureBuddies.exceptions.NotExistingRecordException;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {
    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;
    private final BuddyService buddyService;
    private final BuddyBookRepository buddyBookRepository;

    @Override
    public List<Book> findBooksByAuthorAndUsername(String username, Long authorId) throws NotExistingRecordException {
        final Buddy buddy = buddyService.findByUsername(username);
        checkIfAuthorExists(authorId);
        return bookRepository.findByAuthorIdAndBookId(authorId, buddy.getId());
    }

    @Override
    public List<Book> findBooksByAuthorAndPrincipalUsername(Long authorId) throws NotExistingRecordException {
        final String username = buddyService.getPrincipalUsername();
        return findBooksByAuthorAndUsername(username, authorId);
    }

    @Override
    public List<BuddyBook> findBooksRateWhereAuthorIdAndBuddyId(Long authorId, String username) throws NotExistingRecordException {
        checkIfAuthorExists(authorId);
        final Buddy buddy = buddyService.findByUsername(username);
        return buddyBookRepository.findBooksWhereAuthorIdAndBuddyId(authorId, buddy.getId());
    }


    @Override
    public List<BuddyBook> findBooksRateOfPrincipalByAuthorId(Long authorId) throws NotExistingRecordException {
        final String principalUsername = buddyService.getPrincipalUsername();
        return findBooksRateWhereAuthorIdAndBuddyId(authorId, principalUsername);
    }

    private void checkIfAuthorExists(Long authorId) throws NotExistingRecordException {
        final Optional<Author> author = authorRepository.findById(authorId);
        if (author.isEmpty()) {
            throw new NotExistingRecordException("Author with id " + authorId + " does not exist");
        }
    }

}
