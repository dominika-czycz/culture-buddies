package pl.coderslab.cultureBuddies.events;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.coderslab.cultureBuddies.buddies.Buddy;
import pl.coderslab.cultureBuddies.buddies.BuddyService;
import pl.coderslab.cultureBuddies.exceptions.NotExistingRecordException;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final EventTypeRepository eventTypeRepository;
    private final BuddyService buddyService;

    @Override
    public List<Event> getEventsOfPrincipal() throws NotExistingRecordException {
        final Buddy principal = buddyService.getPrincipal();
        return eventRepository.findAllByBuddyId(principal.getId());
    }

    @Override
    public List<Event> getJoinedEvents() throws NotExistingRecordException {
        final Buddy principal = buddyService.getPrincipal();
        return eventRepository.findAllByBuddies(principal);
    }

    @Override
    public List<EventType> findAllEventsTypes() {
        return eventTypeRepository.findAll();
    }

}
