package pl.coderslab.cultureBuddies.buddies;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.coderslab.cultureBuddies.buddyBuddy.RelationStatus;

import java.util.Optional;

public interface RelationStatusRepository extends JpaRepository<RelationStatus, Long> {
    Optional<RelationStatus> findFirstByName(String statusName);
}
