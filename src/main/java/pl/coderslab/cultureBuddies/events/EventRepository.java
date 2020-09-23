package pl.coderslab.cultureBuddies.events;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.coderslab.cultureBuddies.buddies.Buddy;

import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findAllByBuddyId(Long id);
    List<Event> findAllByBuddies(Buddy buddy);

}
