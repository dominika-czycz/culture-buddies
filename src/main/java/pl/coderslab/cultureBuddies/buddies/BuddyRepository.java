package pl.coderslab.cultureBuddies.buddies;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface BuddyRepository extends JpaRepository<Buddy, Long> {
    Optional<Buddy> findByUsername(String username);
}
