package pl.coderslab.cultureBuddies.buddies;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import pl.coderslab.cultureBuddies.books.Book;
import pl.coderslab.cultureBuddies.buddyBook.BuddyBook;
import pl.coderslab.cultureBuddies.buddyBook.BuddyBookRepository;
import pl.coderslab.cultureBuddies.buddyBuddy.BuddyBuddyId;
import pl.coderslab.cultureBuddies.buddyBuddy.BuddyRelation;
import pl.coderslab.cultureBuddies.buddyBuddy.RelationStatus;
import pl.coderslab.cultureBuddies.city.City;
import pl.coderslab.cultureBuddies.city.CityRepository;
import pl.coderslab.cultureBuddies.exceptions.EmptyKeysException;
import pl.coderslab.cultureBuddies.exceptions.NotExistingRecordException;
import pl.coderslab.cultureBuddies.exceptions.RelationshipAlreadyCreatedException;
import pl.coderslab.cultureBuddies.security.RoleRepository;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

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
    private final CityRepository cityRepository;
    private static final int FILE_MAX_SIZE = 1048576;

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

    @Transactional
    @Override
    public void updateProfilePicture(MultipartFile profilePicture) throws NotExistingRecordException, IOException {
        final Buddy principal = getPrincipal();
        pictureService.save(profilePicture, principal);
        buddyRepository.save(principal);
    }

    @Transactional
    @Override
    public void updatePassword(String password) throws NotExistingRecordException {
        final Buddy principal = getPrincipal();
        setPassword(principal, password);
        buddyRepository.save(principal);
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
        final RelationStatus buddiesStatus = getStatus("buddies");
        final List<Buddy> buddies = buddyRepository
                .findAllBuddiesOfBuddyWithIdWhereRelationId(principalId, buddiesStatus.getId());
        return getBuddiesWithConvertedPicture(buddies);
    }

    private List<Buddy> getBuddiesWithConvertedPicture(List<Buddy> buddies) {
        return buddies.stream().peek(this::setProfilePicture)
                .collect(Collectors.toList());
    }

    @Override
    public List<Buddy> getBuddiesInvitingPrincipal() throws NotExistingRecordException {
        final Long principalId = getPrincipal().getId();
        final RelationStatus invitingStatus = getStatus("inviting");
        final List<Buddy> buddies = buddyRepository
                .findAllBuddiesOfBuddyWithIdWhereRelationId(principalId, invitingStatus.getId());
        return getBuddiesWithConvertedPicture(buddies);
    }

    @Transactional
    @Override
    public List<Buddy> findByUsernameAndAuthors(String username, ArrayList<Long> authorsIds)
            throws EmptyKeysException, NotExistingRecordException {
        if ((authorsIds == null || authorsIds.isEmpty()) && (username == null || username.isBlank())) {
            throw new EmptyKeysException("At least one keyword cannot be empty!");
        }
        List<Buddy> results = findMatchingBuddies(username, authorsIds);
        if (results.isEmpty()) throw new NotExistingRecordException("Nothing matches to your search!");
        return getBuddiesWithConvertedPicture(results);
    }

    @Override
    @Transactional
    public void deleteBuddy(Long buddyId) throws NotExistingRecordException {
        final String message = "Relation does not exist";
        final Buddy deleting = getBuddy(buddyId);
        final Buddy principal = getPrincipal();
        final Optional<BuddyRelation> deletingPrincipalRel = getBuddyRelationFromDb(deleting, principal);
        final Optional<BuddyRelation> principalDeletingRel = getBuddyRelationFromDb(principal, deleting);
        final BuddyRelation deletingPrincipal = deletingPrincipalRel.orElseThrow(new NotExistingRecordException(message));
        final BuddyRelation principalDeleting = principalDeletingRel.orElseThrow(new NotExistingRecordException(message));
        buddyRelationRepository.delete(deletingPrincipal);
        buddyRelationRepository.delete(principalDeleting);
    }

    @Override
    public boolean isProperFileSize(MultipartFile profilePicture) {
        if (profilePicture == null) return true;
        if (profilePicture.getSize() > FILE_MAX_SIZE) {
            log.warn("Profile picture size {} over 10MB", profilePicture.getSize());
            return false;
        }
        return true;
    }

    @Override
    @Transactional
    public void block(Long buddyId) throws NotExistingRecordException {
        savePrincipalBuddyRelations(buddyId, "blocking", "blocked");
    }

    @Override
    @Transactional
    public void inviteBuddy(Long buddyId) throws NotExistingRecordException {
        savePrincipalBuddyRelations(buddyId, "inviting", "invited");
    }

    @Override
    @Transactional
    public void inviteBuddy(Long activeId, Long passiveId) throws NotExistingRecordException {
        saveBuddyBuddyRelations(activeId, passiveId, "inviting", "invited");
    }


    @Override
    @Transactional
    public void acceptBuddy(Long buddyId) throws NotExistingRecordException {
        savePrincipalBuddyRelations(buddyId, "buddies", "buddies");
    }

    @Override
    @Transactional
    public void acceptBuddy(Long activeId, Long passiveId) throws NotExistingRecordException {
        saveBuddyBuddyRelations(activeId, passiveId, "buddies", "buddies");
    }

    @Override
    public Buddy findById(Long buddyId) throws NotExistingRecordException {
        return buddyRepository.findById(buddyId)
                .orElseThrow(new NotExistingRecordException("Buddy with id " + buddyId + "does not exist!"));
    }

    @Override
    public RelationStatus getStatus(String relationName) throws NotExistingRecordException {
        return relationStatusRepository.findFirstByName(relationName)
                .orElseThrow(new NotExistingRecordException("Status " + relationName + " does not exist! Contact administrator!"));
    }

    @Override
    public Buddy getPrincipalWithEvents() throws NotExistingRecordException {
        final Buddy principal = getPrincipal();
        return getBuddyWithEvents(principal);
    }

    @Override
    public String getPicture(Buddy buddy) {
        return pictureService.getPicture(buddy);
    }

    @Override
    public void setProfilePicture(Buddy buddy) {
        buddy.setProfileImage(getPicture(buddy));
    }

    @Override
    public void deleteAll() {
        buddyRepository.deleteAll();
    }

    private void savePrincipalBuddyRelations(Long buddyId, String principalIsDoing, String buddyIsDone) throws NotExistingRecordException {
        final Buddy principal = getPrincipal();
        saveBuddyBuddyRelations(principal.getId(), buddyId, principalIsDoing, buddyIsDone);
    }

    private void saveBuddyBuddyRelations(Long actionBuddyId, Long passiveBuddyId, String activeIsDoing, String passiveIsDone) throws NotExistingRecordException {
        final Buddy passiveBuddy = getBuddy(passiveBuddyId);
        final Buddy actionBuddy = getBuddy(actionBuddyId);
        final BuddyRelation savedActiveRel = saveBuddyRelation(actionBuddy, passiveBuddy, activeIsDoing);
        final BuddyRelation savedPassiveRel = saveBuddyRelation(passiveBuddy, actionBuddy, passiveIsDone);
        log.debug("Entities {}, {} have been saved.", savedActiveRel, savedPassiveRel);
    }

    private Buddy getBuddy(Long buddyId) throws NotExistingRecordException {
        final Optional<Buddy> buddyToInvite = buddyRepository.findById(buddyId);
        return buddyToInvite.orElseThrow(new NotExistingRecordException("Buddy does not exist!"));
    }

    private BuddyRelation saveBuddyRelation(Buddy who, Buddy whom, String relationName) throws NotExistingRecordException {
        final RelationStatus relationStatus = getStatus(relationName);
        final Optional<BuddyRelation> relation = getBuddyRelationFromDb(who, whom);
        final BuddyRelation buddyRelation = relation.orElseGet(() -> createBuddyRelation(who, whom));
        buddyRelation.setStatus(relationStatus);
        return buddyRelationRepository.save(buddyRelation);
    }

    @Override
    public Buddy getBuddyWithEvents(Buddy buddy) throws NotExistingRecordException {
        final Optional<Buddy> buddyWithEvents = buddyRepository.findByIdWithEvents(buddy.getId());
        return buddyWithEvents.orElseThrow(
                new NotExistingRecordException("Buddy with id " + buddy.getId() + " does not exist!"));
    }

    @Override
    @Transactional
    public void setExampleBuddies() throws IOException {
        saveExampleBuddy("barbora-polednova-dY7Q-sl77L4-unsplash.jpg",
                "Anna", "Kowalska", "annaKowal");
        saveExampleBuddy("eric-weber-nQOQVJW7EY8-unsplash.jpg",
                "Piotr", "Mazur", "Mazur");
        saveExampleBuddy("gabriel-silverio-u3WmDyKGsrY-unsplash.jpg",
                "Ola", "Wojciechowska", "koala");
        saveExampleBuddy("meric-tuna-lIvALCu6T8Q-unsplash.jpg",
                "Adam", "Adamski", "adamski");
    }

    private void saveExampleBuddy(String fileName, String name, String lastName, String userName) throws IOException {
        City city = cityRepository.findFirstByName("Wrocław");
        MultipartFile kowalFile = getExampleMultipartFile(fileName);
        final Buddy annaKowal = Buddy.builder()
                .name(name)
                .lastName(lastName)
                .username(userName)
                .email("dominika.czycz@gmail.com")
                .password(userName)
                .city(city).build();
        save(kowalFile, annaKowal);
    }

    @Override
    @Transactional
    public void setBuddiesRelations() throws NotExistingRecordException {
        final Long koalaId = findByUsername("koala").getId();
        final Long mazurId = findByUsername("Mazur").getId();
        final Long adamskiId = findByUsername("adamski").getId();
        final Long annaKowalId = findByUsername("annaKowal").getId();
        inviteBuddy(koalaId, mazurId);
        inviteBuddy(koalaId, annaKowalId);
        inviteBuddy(mazurId, annaKowalId);
        inviteBuddy(adamskiId, annaKowalId);
        acceptBuddy(annaKowalId, koalaId);
        acceptBuddy(annaKowalId, mazurId);
    }

    private MultipartFile getExampleMultipartFile(String fileName) throws IOException {
        final Path pathToPicture = getPathToPicture(fileName);
        final byte[] bytes = Files.readAllBytes(pathToPicture);
        return new MockMultipartFile(fileName, bytes);
    }

    private String getPathToDirectory() {
        final String RESOURCE_NAME = "static/pictures/buddyPictures";
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(Objects.requireNonNull(classLoader.getResource(RESOURCE_NAME)).getFile());
        return file.getAbsolutePath();
    }

    private Path getPathToPicture(String fileName) {
        final String file = StringUtils.cleanPath(fileName);
        String dir = getPathToDirectory();
        return Paths.get(dir + "/" + file);
    }

    private Optional<BuddyRelation> getBuddyRelationFromDb(Buddy who, Buddy whom) {
        final BuddyBuddyId relationId = new BuddyBuddyId();
        relationId.setBuddyId(who.getId());
        relationId.setBuddyOfId(whom.getId());
        return buddyRelationRepository.findById(relationId);
    }

    private BuddyRelation createBuddyRelation(Buddy who, Buddy whom) {
        BuddyRelation buddyRelation;
        buddyRelation = new BuddyRelation();
        buddyRelation.setBuddy(who);
        buddyRelation.setBuddyOf(whom);
        return buddyRelation;
    }

    private List<Buddy> findMatchingBuddies(String username, ArrayList<Long> authorsIds) throws NotExistingRecordException {
        final Long principalId = getPrincipal().getId();
        if (authorsIds == null || authorsIds.isEmpty()) {
            return buddyRepository.findNewBuddiesByUsernameStartingWithAndIdNot(username, principalId);
        }
        if (username.isBlank()) {
            return buddyRepository.findNewBuddiesByAuthors(authorsIds, principalId);
        }
        return buddyRepository.findNewBuddiesByAuthorsAndUsernameLike(authorsIds, username, principalId);
    }

    private void setPassword(Buddy buddy, String password) {
        buddy.setPassword(passwordEncoder.encode(password));
    }

    private void prepareBuddy(MultipartFile profilePicture, Buddy buddy) throws IOException {
        pictureService.save(profilePicture, buddy);
        setPassword(buddy, buddy.getPassword());
        buddy.addRole(roleRepository.findFirstByNameIgnoringCase("ROLE_USER"));
    }

    private boolean isDuplicate(Buddy buddy) {
        final Optional<Buddy> existingBuddyDb = buddyRepository.findFirstByUsernameIgnoringCase(buddy.getUsername());
        return existingBuddyDb.isPresent();
    }
}
