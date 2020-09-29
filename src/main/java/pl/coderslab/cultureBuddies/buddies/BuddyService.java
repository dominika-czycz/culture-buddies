package pl.coderslab.cultureBuddies.buddies;

import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;
import pl.coderslab.cultureBuddies.books.Book;
import pl.coderslab.cultureBuddies.buddyBook.BuddyBook;
import pl.coderslab.cultureBuddies.buddyBuddy.RelationStatus;
import pl.coderslab.cultureBuddies.events.Event;
import pl.coderslab.cultureBuddies.exceptions.EmptyKeysException;
import pl.coderslab.cultureBuddies.exceptions.NotExistingRecordException;
import pl.coderslab.cultureBuddies.exceptions.RelationshipAlreadyCreatedException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public interface BuddyService {
    BuddyBook addBookToPrincipalBuddy(Book book) throws NotExistingRecordException, RelationshipAlreadyCreatedException;

    boolean save(MultipartFile profilePicture, Buddy buddy) throws IOException;

    Buddy findByUsername(String username) throws NotExistingRecordException;

    Buddy getPrincipal() throws NotExistingRecordException;

    String getPrincipalUsername();

    List<Buddy> getBuddiesOfPrincipal() throws NotExistingRecordException;

    List<Buddy> getBuddiesInvitingPrincipal() throws NotExistingRecordException;

    List<Buddy> findByUsernameAndAuthors(String username, ArrayList<Long> authors) throws EmptyKeysException, NotExistingRecordException;

    void inviteBuddy(Long buddyId) throws NotExistingRecordException;

    void acceptBuddy(Long buddyId) throws NotExistingRecordException;

    void deleteBuddy(Long buddyId) throws NotExistingRecordException;

    Buddy findById(Long buddyId) throws NotExistingRecordException;

    void block(Long buddyId) throws NotExistingRecordException;

    RelationStatus getStatusId(String relationName) throws NotExistingRecordException;

    int countParticipants(Event event);

    void removeEvent(Buddy principal) throws NotExistingRecordException;

    Buddy getPrincipalWithEvents() throws NotExistingRecordException;

}
