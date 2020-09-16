package pl.coderslab.cultureBuddies.buddies;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import pl.coderslab.cultureBuddies.author.Author;
import pl.coderslab.cultureBuddies.books.Book;
import pl.coderslab.cultureBuddies.exceptions.NotExistingRecordException;
import pl.coderslab.cultureBuddies.security.RoleRepository;

import java.io.IOException;
import java.util.Optional;
import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
public class BuddyServiceImpl implements BuddyService {
    private final BuddyRepository buddyRepository;
    private final PictureService pictureService;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final BuddyBookRepository buddyBookRepository;

    @Transactional
    @Override
    public boolean addBook(Book book) throws NotExistingRecordException {
        final Buddy buddy = findByUsername(getPrincipalUsername());
        if (buddyBookRepository.findByBookAndBuddy(book, buddy).isPresent()) {
            return false;
        }
        final BuddyBook buddyBook = buddy.addBook(book);
        addAuthors(book, buddy);
        buddyBookRepository.save(buddyBook);
        return true;
    }

    private void addAuthors(Book book, Buddy buddy) {
        final Set<Author> authors = book.getAuthors();
        if (authors != null) {
            for (Author author : authors) {
                buddy.addAuthor(author);
            }
        }
    }

    @Transactional
    @Override
    public boolean save(MultipartFile profilePicture, Buddy buddy) throws IOException {
        if (isDuplicate(buddy)) {
            log.warn("Not unique username: {}", buddy.getUsername());
            return false;
        }
        prepareBuddy(profilePicture, buddy);
        final Buddy savedBuddy = buddyRepository.save(buddy);
        log.debug("Entity {} has been saved", savedBuddy);
        return true;
    }

    @Override
    public Buddy findByUsername(String username) throws NotExistingRecordException {
        return buddyRepository.findFirstByUsernameIgnoringCase(username)
                .orElseThrow(new NotExistingRecordException("Buddy with username " + username + " does not exist in database"));
    }

    @Override
    public Buddy findPrincipal() throws NotExistingRecordException {
        final String principalUsername = getPrincipalUsername();
        final Optional<Buddy> principal = buddyRepository.findFirstByUsernameIgnoringCase(principalUsername);
        return principal.orElseThrow(new NotExistingRecordException("Buddy with username " + principalUsername
                + " does not exist"));
    }

    @Override
    public String getPrincipalUsername() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username;
        if (principal instanceof UserDetails) {
            username = ((UserDetails) principal).getUsername();
        } else {
            username = principal.toString();
        }
        return username;
    }

    private void prepareBuddy(MultipartFile profilePicture, Buddy buddy) throws IOException {
        pictureService.save(profilePicture, buddy);
        buddy.setPassword(passwordEncoder.encode(buddy.getPassword()));
        buddy.addRole(roleRepository.findFirstByNameIgnoringCase("ROLE_USER"));
    }

    private boolean isDuplicate(Buddy buddy) {
        final Optional<Buddy> existingBuddyDb = buddyRepository.findFirstByUsernameIgnoringCase(buddy.getUsername());
        return existingBuddyDb.isPresent();
    }
}
