package pl.coderslab.cultureBuddies.buddies;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pl.coderslab.cultureBuddies.books.Book;

import java.util.List;
import java.util.Optional;

public interface BuddyBookRepository extends JpaRepository<BuddyBook, Long> {
    @Query(nativeQuery = true,
            value = "SELECT bb.* FROM buddies_books bb JOIN books b on bb.book_id = b.id JOIN buddies b2 on bb.buddy_id = b2.id " +
                    "JOIN author_book ab on b.id = ab.book_id where author_id=?1  and buddy_id=?2")
    List<BuddyBook> findBookRatingWhereAuthorIdAndBuddyId(Long authorId, Long buddyId);
    Optional<BuddyBook> findByBookAndBuddy(Book book, Buddy buddy);

}
