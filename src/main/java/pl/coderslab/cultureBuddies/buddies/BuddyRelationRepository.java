package pl.coderslab.cultureBuddies.buddies;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.coderslab.cultureBuddies.buddyBuddy.BuddyBuddyId;
import pl.coderslab.cultureBuddies.buddyBuddy.BuddyRelation;

public interface BuddyRelationRepository extends JpaRepository<BuddyRelation, BuddyBuddyId> {

}
