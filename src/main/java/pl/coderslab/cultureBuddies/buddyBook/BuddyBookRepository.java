package pl.coderslab.cultureBuddies.buddyBook;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pl.coderslab.cultureBuddies.books.Book;
import pl.coderslab.cultureBuddies.buddies.Buddy;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

public interface BuddyBookRepository extends JpaRepository<BuddyBook, Long> {
    @Query(nativeQuery = true,
            value = "SELECT bb.* FROM buddies_books bb JOIN books b on bb.book_id = b.id JOIN buddies b2 on bb.buddy_id = b2.id " +
                    "JOIN author_book ab on b.id = ab.book_id where author_id=?1  and buddy_id=?2")
    List<BuddyBook> findRatingWhereAuthorIdAndBuddyId(Long authorId, Long buddyId);

    Optional<BuddyBook> findById(BuddyBookId buddyBookId);

    Optional<BuddyBook> findByBookAndBuddy(Book book, Buddy buddy);

    @Query(nativeQuery = true,
            value = "SELECT bb.*, b3.* FROM buddies_books bb " +
                    "JOIN buddies b2 on bb.buddy_id = b2.id " +
                    "JOIN buddies_relations br on b2.id = br.buddy_id " +
                    "JOIN buddies b3 ON b3.id = br.buddy_of_id  " +
                    "WHERE bb.book_id = ?1 AND " +
                    "br.buddy_of_id = ?2 AND " +
                    "br.status_id = ?3")
    @Transactional
    List<BuddyBook> findALlPrincipalBuddiesBookRatings(Long bookId, Long principalId, Long statusId);

    @Query(nativeQuery = true,
            value = "SELECT DISTINCT bb.*, b3.*, a.* FROM buddies_books bb " +
                    "JOIN books on bb.book_id = books.id JOIN author_book ab on books.id = ab.book_id " +
                    "JOIN authors a on ab.author_id = a.id " +
                    "JOIN buddies b2 on bb.buddy_id = b2.id " +
                    "JOIN buddies_relations br on b2.id = br.buddy_id " +
                    "JOIN buddies b3 ON b3.id = br.buddy_of_id  " +
                    "WHERE   " +
                    "br.buddy_of_id = ?1 " +
                    "AND " +
                    "br.status_id = ?2 " +
                    "ORDER BY bb.added DESC LIMIT ?3")
    List<BuddyBook> findRecentlyAddedBuddyBookWithBookAndBuddiesLimitTo(Long buddyId, Long statusId, int limit);

}
