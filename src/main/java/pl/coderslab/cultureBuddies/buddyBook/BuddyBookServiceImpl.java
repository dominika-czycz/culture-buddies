package pl.coderslab.cultureBuddies.buddyBook;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.coderslab.cultureBuddies.exceptions.NotExistingRecordException;

import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class BuddyBookServiceImpl implements BuddyBookService {
    private final BuddyBookRepository buddyBookRepository;

    @Override
    public void updateBuddyBook(BuddyBook buddyBook) throws NotExistingRecordException {
        log.debug("Preparing to update entity {}", buddyBook);
        final BuddyBookId buddyBookId = new BuddyBookId();
        buddyBookId.setBookId(buddyBook.getBookId());
        buddyBookId.setBuddyId(buddyBook.getBuddyId());
        final Optional<BuddyBook> toUpdate = buddyBookRepository.findById(buddyBookId);
        final BuddyBook updating = toUpdate
                .orElseThrow(new NotExistingRecordException("Something went wrong! You are not assigned to this book."));
        log.debug("Updating entity {}...", updating);
        updating.setComment(buddyBook.getComment());
        updating.setRate(buddyBook.getRate());
        final BuddyBook updated = buddyBookRepository.save(updating);
        log.debug("Entity {} has been updated", updated);
    }
}
