package pl.coderslab.cultureBuddies.buddyBook;

import pl.coderslab.cultureBuddies.buddies.Buddy;
import pl.coderslab.cultureBuddies.exceptions.NotExistingRecordException;

import java.util.List;

public interface BuddyBookService {
    void updateBuddyBook(BuddyBook buddyBook) throws NotExistingRecordException;


    BuddyBook findRelationWithPrincipalByBookId(long bookId) throws NotExistingRecordException;

    void remove(Long bookId) throws NotExistingRecordException;

    List<BuddyBook> getRatingWhereAuthorIdAndBuddy(Long authorId, Buddy buddy);

}
