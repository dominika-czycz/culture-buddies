package pl.coderslab.cultureBuddies.events;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.coderslab.cultureBuddies.buddies.Buddy;
import pl.coderslab.cultureBuddies.buddies.BuddyService;
import pl.coderslab.cultureBuddies.exceptions.EmptyKeysException;
import pl.coderslab.cultureBuddies.exceptions.NotExistingRecordException;
import pl.coderslab.cultureBuddies.exceptions.RelationshipAlreadyCreatedException;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final EventTypeRepository eventTypeRepository;
    private final BuddyService buddyService;
    private final AddressRepository addressRepository;

    @Override
    public List<Event> getEventsOfPrincipal() throws NotExistingRecordException {
        final Buddy principal = buddyService.getPrincipal();
        return eventRepository.findAllByBuddy(principal);
    }

    @Override
    public List<Event> getJoinedEvents() throws NotExistingRecordException {
        final Buddy principal = buddyService.getPrincipal();
        return eventRepository.findAllByBuddiesContains(principal);
    }

    @Override
    public List<EventType> findAllEventsTypes() {
        return eventTypeRepository.findAll();
    }

    @Override
    @Transactional
    public void save(Event event) throws NotExistingRecordException {
        saveAddress(event);
        final Buddy buddy = buddyService.getPrincipalWithEvents();
        event.setBuddy(buddy);
        final Event saved = eventRepository.save(event);
        log.debug("Entity {} has been saved ", saved);
    }

    @Override
    @Transactional
    public void updateEvent(Event event) throws NotExistingRecordException {
        final Event toUpdate = findEventById(event.getId());
        saveAddress(event);
        log.debug("Updating entity {}...", toUpdate);
        toUpdate.setTitle(event.getTitle());
        toUpdate.setDescription(event.getDescription());
        toUpdate.setEventType(event.getEventType());
        toUpdate.setLink(event.getLink());
        toUpdate.setAddress(event.getAddress());
        toUpdate.setStringTime(event.getStringTime());
        toUpdate.setDate(event.getDate());
        final Event updated = eventRepository.save(toUpdate);
        log.debug("Entity {} has been updated ", updated);
    }

    @Override
    public int countParticipants(Event event) {
        return buddyService.countParticipants(event);
    }

    @Override
    public void remove(Long eventId) throws NotExistingRecordException {
        final Event event = findEventById(eventId);
        eventRepository.delete(event);
    }

    @Override
    public void leave(Long eventId) throws NotExistingRecordException {
        final Optional<Event> eventWithBuddies = eventRepository.findEventWithBuddies(eventId);
        final Event event = eventWithBuddies.orElseThrow(new NotExistingRecordException("Event with id " + eventId + " does not exist!"));
        final Buddy principal = buddyService.getPrincipalWithEvents();
        event.removeBuddy(principal);
        eventRepository.save(event);
    }

    @Override
    public List<Event> findByUsernameTitleTypeIdOrCity(String username, String title, Long typeId, String city)
            throws EmptyKeysException, NotExistingRecordException {
        if ((username == null || username.isBlank()) && (title == null || title.isBlank())
                && typeId == null && (city == null || city.isBlank())) {
            throw new EmptyKeysException("At least one keyword cannot be empty!");
        }
        List<Event> results = findMatchingEvents(username, title, typeId, city);
        if (results.isEmpty()) throw new NotExistingRecordException("Nothing matches to your search!");
        return results;
    }

    @Override
    public void joinEvent(Long eventId) throws NotExistingRecordException, RelationshipAlreadyCreatedException {
        final Buddy principal = buddyService.getPrincipal();
        Optional<Event> alreadyJoined = eventRepository.findEventByBuddies(principal, eventId);
        if (alreadyJoined.isPresent()) {
            throw new RelationshipAlreadyCreatedException("You have already joined the event");
        }
        final Event event = findEventByIdWithBuddies(eventId);
        final Buddy principal2 = buddyService.getPrincipalWithEvents();
        event.addBuddy(principal2);
        eventRepository.save(event);
    }

    @Override
    public Event findEventByIdWithBuddies(Long eventId) throws NotExistingRecordException {
        Optional<Event> event = eventRepository.findEventWithBuddies(eventId);
        return event.orElseThrow(new NotExistingRecordException("Event wiht id " + eventId + " does not exist"));
    }

    @Override
    public Event findEventById(Long eventId) throws NotExistingRecordException {
        final Optional<Event> event = eventRepository.findById(eventId);
        return event.orElseThrow(new NotExistingRecordException("Event with id " + eventId + " does not exist"));
    }

    private List<Event> findMatchingEvents(String username, String title, Long typeId, String city) {
        String keyUsername = (username == null) ? "" : username;
        String keyTitle = (title == null) ? "" : title;
        String keyCity = (city == null) ? "" : city;
        if (typeId == null) {
            return eventRepository.findByUsernameTitleAndCity(keyUsername, keyTitle, keyCity);
        }
        return eventRepository.findByUsernameTitleCityAndTypeId(keyUsername, keyTitle, keyCity, typeId);
    }

    private void saveAddress(Event event) {
        final Address address = event.getAddress();
        final Optional<Address> addressFromDb = addressRepository.findFirstByCityAndStreetAndNumberAndFlatNumber(
                address.getCity(), address.getStreet(), address.getNumber(), address.getFlatNumber());
        final Address addressToAdd = addressFromDb.orElseGet(() -> addressRepository.save(address));
        event.setAddress(addressToAdd);
    }
}
