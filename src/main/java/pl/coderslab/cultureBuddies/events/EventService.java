package pl.coderslab.cultureBuddies.events;

import pl.coderslab.cultureBuddies.author.Author;
import pl.coderslab.cultureBuddies.exceptions.NotExistingRecordException;

import java.util.List;

public interface EventService {
    List<Event> getEventsOfPrincipal() throws NotExistingRecordException;

    List<Event> getJoinedEvents() throws NotExistingRecordException;

    List<EventType> findAllEventsTypes();
}
