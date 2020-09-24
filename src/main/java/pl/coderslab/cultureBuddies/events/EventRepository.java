package pl.coderslab.cultureBuddies.events;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pl.coderslab.cultureBuddies.buddies.Buddy;

import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {
    @Query(value = "SELECT DISTINCT e FROM Event e JOIN FETCH e.buddies WHERE e.buddy =?1")
    List<Event> findAllByBuddy(Buddy buddy);

    @Query("SELECT DISTINCT event FROM Event event JOIN FETCH event.buddies b JOIN event.buddies b2 where b2 = ?1")
    List<Event> findAllByBuddiesContains(Buddy buddy);
}
