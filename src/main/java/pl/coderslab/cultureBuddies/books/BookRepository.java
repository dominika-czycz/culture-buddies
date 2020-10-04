package pl.coderslab.cultureBuddies.books;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, Long> {
    Optional<Book> findFirstByIdentifier(String isbn);

    @Query(value = "SELECT b FROM Book b JOIN FETCH b.authors where b.id = ?1")
    Book findByIdWithAuthors(Long bookId);

    Optional<Book> findFirstByTitle(String title);
}
