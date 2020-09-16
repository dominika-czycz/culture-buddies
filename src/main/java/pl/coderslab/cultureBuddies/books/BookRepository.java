package pl.coderslab.cultureBuddies.books;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, Long> {
    @Query(nativeQuery = true, value = "SELECT b.* FROM books b JOIN author_book ab on b.id = ab.book_id JOIN buddies_books bb on b.id = bb.book_id where author_id = :authorId AND  bb.buddy_id=:buddyId")
    List<Book> findByAuthorIdAndBookId(Long authorId, Long buddyId);

    Optional<Book> findFirstByIdentifier(String isbn);
}
