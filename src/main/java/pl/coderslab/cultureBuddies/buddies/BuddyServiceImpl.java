package pl.coderslab.cultureBuddies.buddies;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import pl.coderslab.cultureBuddies.books.Book;
import pl.coderslab.cultureBuddies.buddyBook.BuddyBook;
import pl.coderslab.cultureBuddies.buddyBook.BuddyBookRepository;
import pl.coderslab.cultureBuddies.buddyBuddy.BuddyBuddyId;
import pl.coderslab.cultureBuddies.buddyBuddy.BuddyRelation;
import pl.coderslab.cultureBuddies.buddyBuddy.RelationStatus;
import pl.coderslab.cultureBuddies.exceptions.EmptyKeysException;
import pl.coderslab.cultureBuddies.exceptions.NotExistingRecordException;
import pl.coderslab.cultureBuddies.exceptions.RelationshipAlreadyCreatedException;
import pl.coderslab.cultureBuddies.security.RoleRepository;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class BuddyServiceImpl implements BuddyService {
    private final BuddyRepository buddyRepository;
    private final PictureService pictureService;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final BuddyBookRepository buddyBookRepository;
    private final BuddyRelationRepository buddyRelationRepository;
    private final RelationStatusRepository relationStatusRepository;

    @Transactional
    @Override
    public BuddyBook addBookToPrincipalBuddy(Book book) throws NotExistingRecordException, RelationshipAlreadyCreatedException {
        final Buddy buddy = findByUsername(getPrincipalUsername());
        if (buddyBookRepository.findByBookAndBuddy(book, buddy).isPresent()) {
            throw new RelationshipAlreadyCreatedException("The book already exists in your collection");
        }
        return saveBuddyBook(book, buddy);
    }

    private BuddyBook saveBuddyBook(Book book, Buddy buddy) {
        final BuddyBook buddyBook = buddy.addBook(book);
        final BuddyBook saved = buddyBookRepository.save(buddyBook);
        log.debug("Entity {} has been saved.", saved);
        return saved;
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
    public Buddy getPrincipal() throws NotExistingRecordException {
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

    @Override
    public List<Buddy> getBuddiesOfPrincipal() throws NotExistingRecordException {
        final Long principalId = getPrincipal().getId();
        return buddyRepository.findAllBuddiesOfBuddyWithIdWhereRelationIs(principalId, "buddies");
    }

    @Override
    public List<Buddy> getBuddiesInvitingPrincipal() throws NotExistingRecordException {
        final Long principalId = getPrincipal().getId();
        return buddyRepository.findAllBuddiesOfBuddyWithIdWhereRelationIs(principalId, "inviting");
    }

    @Transactional
    @Override
    public List<Buddy> findByUsernameAndAuthors(String username, List<Integer> authorsIds) throws EmptyKeysException, NotExistingRecordException {
        if (authorsIds == null || authorsIds.isEmpty()
                && (username == null || username.isBlank())) {
            throw new EmptyKeysException("At least one keyword cannot be empty!");
        }
        List<Buddy> results = findMatchingBuddies(username, authorsIds);
        if (results.isEmpty()) throw new NotExistingRecordException("Nothing matches to your search!");
        return results;
    }

    @Override
    public void inviteBuddy(Long buddyId) throws NotExistingRecordException {
        final Optional<Buddy> buddyToInvite = buddyRepository.findById(buddyId);
        final Buddy invited = buddyToInvite.orElseThrow(new NotExistingRecordException("Buddy does not exist!"));
        final Buddy principal = getPrincipal();
        final BuddyRelation savedInviting = saveBuddyRelation(principal, invited, "inviting");
        final BuddyRelation savedInvited = saveBuddyRelation(invited, principal, "invited");
        log.debug("Entities {}, {} have been saved.", savedInviting, savedInvited);

    }

    @Override
    public void acceptBuddy(Long buddyId) throws NotExistingRecordException {
        final Optional<Buddy> buddyToInvite = buddyRepository.findById(buddyId);
        final Buddy inviting = buddyToInvite.orElseThrow(new NotExistingRecordException("Buddy does not exist!"));
        final Buddy principal = getPrincipal();
        final BuddyRelation savedInviting = saveBuddyRelation(inviting, principal, "buddies");
        final BuddyRelation savedInvited = saveBuddyRelation(principal, inviting, "buddies");
        log.debug("Entities {}, {} have been saved.", savedInviting, savedInvited);
    }

    private BuddyRelation saveBuddyRelation(Buddy who, Buddy whom, String relationName) throws NotExistingRecordException {
        final RelationStatus relationStatus = relationStatusRepository.findFirstByName(relationName)
                .orElseThrow(new NotExistingRecordException("Status" + relationName + "does not exist! Contact administrator!"));
        final BuddyBuddyId relationId = new BuddyBuddyId();
        relationId.setBuddyId(who.getId());
        relationId.setBuddyOfId(whom.getId());
        final BuddyRelation buddyRelation = buddyRelationRepository.findById(relationId)
                .orElseGet(() -> getBuddyRelation(who, whom));
        buddyRelation.setStatus(relationStatus);
        return buddyRelationRepository.save(buddyRelation);
    }

    private BuddyRelation getBuddyRelation(Buddy who, Buddy whom) {
        BuddyRelation buddyRelation;
        buddyRelation = new BuddyRelation();
        buddyRelation.setBuddy(who);
        buddyRelation.setBuddyOf(whom);
        return buddyRelation;
    }

    private List<Buddy> findMatchingBuddies(String username, List<Integer> authorsIds) throws NotExistingRecordException {
        final Long principalId = getPrincipal().getId();
        if (authorsIds.isEmpty()) {
            return buddyRepository.findNewBuddiesByUsernameStartingWithAndIdNot(username, principalId);
        }
        if (username.isBlank()) {
            return buddyRepository.findNewBuddiesByAuthors(authorsIds, principalId);
        }
        return buddyRepository.findNewBuddiesByAuthorsAndUsernameLike(authorsIds, username, principalId);
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
