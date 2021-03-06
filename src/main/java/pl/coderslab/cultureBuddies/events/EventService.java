package pl.coderslab.cultureBuddies.events;

import pl.coderslab.cultureBuddies.buddies.Buddy;
import pl.coderslab.cultureBuddies.exceptions.EmptyKeysException;
import pl.coderslab.cultureBuddies.exceptions.NotExistingRecordException;
import pl.coderslab.cultureBuddies.exceptions.RelationshipAlreadyCreatedException;

import java.util.List;

public interface EventService {
    List<Event> getEventsOfPrincipal() throws NotExistingRecordException;

    List<Event> getJoinedEvents() throws NotExistingRecordException;

    List<EventType> findAllEventsTypes();

    void save(Event event) throws NotExistingRecordException;

    List<Event> findAllOfBuddy(Buddy buddy);

    Event findEventByIdWithBuddies(Long eventId) throws NotExistingRecordException;

    void setExampleEvents() throws NotExistingRecordException;

    Event findEventById(Long eventId) throws NotExistingRecordException;

    void updateEvent(Event event) throws NotExistingRecordException;

    void remove(Long eventId) throws NotExistingRecordException;

    void leave(Long eventId) throws NotExistingRecordException;

    List<Event> findByUsernameTitleTypeIdOrCity(String username, String title, Long typeId, String city) throws EmptyKeysException, NotExistingRecordException;

    void joinEvent(Long eventId) throws NotExistingRecordException, RelationshipAlreadyCreatedException;

    List<Event> findRecentlyAddedByBuddies(int recentlyLimit) throws NotExistingRecordException;

    List<Event> findRecentEvents();

    List<Event> findRecentOfBuddy(Long id, int recentlyLimit);

}
