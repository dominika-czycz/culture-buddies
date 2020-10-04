package pl.coderslab.cultureBuddies.buddies;

import org.springframework.web.multipart.MultipartFile;
import pl.coderslab.cultureBuddies.books.Book;
import pl.coderslab.cultureBuddies.buddyBook.BuddyBook;
import pl.coderslab.cultureBuddies.buddyBuddy.RelationStatus;
import pl.coderslab.cultureBuddies.exceptions.EmptyKeysException;
import pl.coderslab.cultureBuddies.exceptions.NotExistingRecordException;
import pl.coderslab.cultureBuddies.exceptions.RelationshipAlreadyCreatedException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public interface BuddyService {
    BuddyBook addBookToPrincipalBuddy(Book book) throws NotExistingRecordException, RelationshipAlreadyCreatedException;

    boolean save(MultipartFile profilePicture, Buddy buddy) throws IOException;

    void updateProfilePicture(MultipartFile profilePicture) throws NotExistingRecordException, IOException;

    void updatePassword(String password) throws NotExistingRecordException;

    Buddy findByUsername(String username) throws NotExistingRecordException;

    Buddy getPrincipal() throws NotExistingRecordException;

    String getPrincipalUsername();

    List<Buddy> getBuddiesOfPrincipal() throws NotExistingRecordException;

    List<Buddy> getBuddiesInvitingPrincipal() throws NotExistingRecordException;

    List<Buddy> findByUsernameAndAuthors(String username, ArrayList<Long> authors) throws EmptyKeysException, NotExistingRecordException;

    void inviteBuddy(Long buddyId) throws NotExistingRecordException;

    void inviteBuddy(Long activeId, Long passiveId) throws NotExistingRecordException;

    void acceptBuddy(Long buddyId) throws NotExistingRecordException;

    void deleteBuddy(Long buddyId) throws NotExistingRecordException;

    void acceptBuddy(Long activeId, Long passiveId) throws NotExistingRecordException;

    Buddy findById(Long buddyId) throws NotExistingRecordException;

    boolean isProperFileSize(MultipartFile profilePicture);

    void block(Long buddyId) throws NotExistingRecordException;

    RelationStatus getStatus(String relationName) throws NotExistingRecordException;

    Buddy getPrincipalWithEvents() throws NotExistingRecordException;

    String getPicture(Buddy buddy);

    void setProfilePicture(Buddy buddy);

    void deleteAll();
}
