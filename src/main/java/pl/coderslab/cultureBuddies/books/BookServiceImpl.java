package pl.coderslab.cultureBuddies.books;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.coderslab.cultureBuddies.author.Author;
import pl.coderslab.cultureBuddies.author.AuthorRepository;
import pl.coderslab.cultureBuddies.buddies.Buddy;
import pl.coderslab.cultureBuddies.buddies.BuddyService;
import pl.coderslab.cultureBuddies.exceptions.NotExistingNameException;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {
    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;
    private final BuddyService buddyService;

    @Override
    public List<Book> findBooksByAuthorAndUsername(String username, Long authorId) throws NotExistingNameException {
        final Buddy buddy = buddyService.findByUsername(username);
        final Author author = authorRepository.findById(authorId)
                .orElseThrow(new NotExistingNameException("Author with id " + authorId + " does not exist"));
        return bookRepository.findByAuthorsAndBuddies(author, buddy);
    }

    @Override
    public List<Book> findBooksByAuthorAndPrincipalUsername(Long authorId) throws NotExistingNameException {
        final String username = buddyService.getPrincipalUsername();
        return findBooksByAuthorAndUsername(username, authorId);
    }

}
