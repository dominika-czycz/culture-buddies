package pl.coderslab.cultureBuddies.books;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.coderslab.cultureBuddies.author.Author;
import pl.coderslab.cultureBuddies.buddies.Buddy;

import java.util.List;

public interface BookRepository extends JpaRepository<Book, Long> {
    List<Book> findByAuthorsAndBuddies(Author author, Buddy buddy);
}
