package pl.coderslab.cultureBuddies.buddyBook;

import pl.coderslab.cultureBuddies.exceptions.NotExistingRecordException;

public interface BuddyBookService {
    void updateBuddyBook(BuddyBook buddyBook) throws NotExistingRecordException;


    BuddyBook findRelationWithPrincipalByBookId(long bookId) throws NotExistingRecordException;

    void remove(Long bookId) throws NotExistingRecordException;
}
