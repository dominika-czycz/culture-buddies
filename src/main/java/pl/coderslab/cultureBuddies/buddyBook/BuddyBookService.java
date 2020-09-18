package pl.coderslab.cultureBuddies.buddyBook;

import pl.coderslab.cultureBuddies.exceptions.NotExistingRecordException;

public interface BuddyBookService {
    void updateBuddyBook(BuddyBook buddyBook) throws NotExistingRecordException;
}
