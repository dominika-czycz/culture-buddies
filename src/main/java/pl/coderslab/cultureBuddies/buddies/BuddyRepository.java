package pl.coderslab.cultureBuddies.buddies;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BuddyRepository extends JpaRepository<Buddy, Long> {
    Optional<Buddy> findFirstByUsernameIgnoringCase(String username);

    @Query("SELECT DISTINCT b FROM Buddy b LEFT JOIN FETCH b.authors WHERE  b.username =?1")
    Optional<Buddy> findFirstByUsernameWithAuthors(String username);

}
