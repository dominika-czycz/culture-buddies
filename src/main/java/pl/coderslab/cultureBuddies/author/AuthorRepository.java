package pl.coderslab.cultureBuddies.author;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.coderslab.cultureBuddies.buddies.Buddy;

import java.util.List;
import java.util.Optional;

public interface AuthorRepository extends JpaRepository<Author, Long> {
    Optional<Author> findFirstByFirstNameAndLastName(String firstName, String lastName);
    List<Author> findByBuddiesOrderByLastName(Buddy buddy);
}
