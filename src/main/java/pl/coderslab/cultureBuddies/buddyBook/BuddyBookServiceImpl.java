package pl.coderslab.cultureBuddies.buddyBook;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.coderslab.cultureBuddies.buddies.Buddy;
import pl.coderslab.cultureBuddies.buddies.BuddyService;
import pl.coderslab.cultureBuddies.buddies.RelationStatusRepository;
import pl.coderslab.cultureBuddies.buddyBuddy.RelationStatus;
import pl.coderslab.cultureBuddies.exceptions.NotExistingRecordException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class BuddyBookServiceImpl implements BuddyBookService {
    private final BuddyBookRepository buddyBookRepository;
    private final BuddyService buddyService;
    private final RelationStatusRepository relationStatusRepository;

    @Override

    public List<BuddyBook> getRatingWhereAuthorIdAndBuddy(Long authorId, Buddy buddy) {
        return buddyBookRepository.findRatingWhereAuthorIdAndBuddyId(authorId, buddy.getId());
    }

    @Override
    public List<BuddyBook> findAllPrincipalBuddiesBookRatings(Long bookId) throws NotExistingRecordException {
        final Buddy principal = buddyService.getPrincipal();
        final RelationStatus buddies = buddyService.getStatusId("buddies");
        return buddyBookRepository.
                findALlPrincipalBuddiesBookRatings(bookId, principal.getId(), buddies.getId());
    }

    @Override
    public double countRating(List<BuddyBook> ratings) throws NotExistingRecordException {
        return ratings.stream()
                .mapToInt(BuddyBook::getRate)
                .average()
                .orElseThrow(new NotExistingRecordException("Ratings does not exist!"));
    }

    @Override
    @Transactional
    public List<BuddyBook> findRecentlyAddedByBuddies(int limit) throws NotExistingRecordException {
        final Buddy principal = buddyService.getPrincipal();
        RelationStatus relationStatus = relationStatusRepository.findFirstByName("buddies")
                .orElseThrow(new NotExistingRecordException("Relation status buddies does not exist"));
        final List<BuddyBook> buddyBooks = buddyBookRepository
                .findRecentlyAddedBuddyBookWithBookAndBuddiesLimit(principal.getId(), relationStatus.getId(), limit);
        return buddyBooks.stream()
                .peek(buddyBook -> Hibernate.initialize(buddyBook.getBook().getAuthors()))
                .collect(Collectors.toList());
    }

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

    @Override
    public BuddyBook findRelationWithPrincipalByBookId(long bookId) throws NotExistingRecordException {
        final Buddy principal = buddyService.getPrincipal();
        final BuddyBookId buddyBookId = new BuddyBookId();
        buddyBookId.setBuddyId(principal.getId());
        buddyBookId.setBookId(bookId);
        log.debug("Looking for buddy-book relation with id {}", buddyBookId);
        final Optional<BuddyBook> buddyBookRelation = buddyBookRepository.findById(buddyBookId);
        return buddyBookRelation.orElseThrow(new NotExistingRecordException("Something went wrong. You are not related to this book!"));
    }

    @Override
    @Transactional
    public void remove(Long bookId) throws NotExistingRecordException {
        final BuddyBook buddyBook = findRelationWithPrincipalByBookId(bookId);
        buddyBookRepository.delete(buddyBook);
        log.debug("Entity {}  has been deleted.", buddyBook);
    }
}
