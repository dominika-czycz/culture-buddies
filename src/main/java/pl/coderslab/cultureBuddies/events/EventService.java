package pl.coderslab.cultureBuddies.events;

import pl.coderslab.cultureBuddies.exceptions.NotExistingRecordException;

import java.util.List;

public interface EventService {
    List<Event> getEventsOfPrincipal() throws NotExistingRecordException;

    List<Event> getJoinedEvents() throws NotExistingRecordException;

    List<EventType> findAllEventsTypes();

    void save(Event event);

    Event findEventById(Long eventId) throws NotExistingRecordException;

    void updateEvent(Event event) throws NotExistingRecordException;

    int countParticipants(Event event);

    void remove(Long eventId) throws NotExistingRecordException;
}
