package pl.coderslab.cultureBuddies.author;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AuthorRepository extends JpaRepository<Author, Long> {
    Optional<Author> findFirstByFirstNameAndLastName(String firstName, String lastName);
}
