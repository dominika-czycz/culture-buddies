package pl.coderslab.cultureBuddies.author;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pl.coderslab.cultureBuddies.buddies.Buddy;

import java.util.List;
import java.util.Optional;

public interface AuthorRepository extends JpaRepository<Author, Long> {
    Optional<Author> findFirstByFirstNameAndLastName(String firstName, String lastName);
    @Query (nativeQuery = true,
    value = "SELECT DISTINCT a.* FROM authors a JOIN author_book ab on a.id = ab.author_id JOIN buddies_books bb on ab.book_id = bb.book_id where buddy_id =?1 " +
            "ORDER BY a.last_name ")
    List<Author> findByBuddiesOrderByLastName(Long buddyId);
}
