package pl.coderslab.cultureBuddies.events;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.coderslab.cultureBuddies.buddies.Buddy;
import pl.coderslab.cultureBuddies.buddies.BuddyService;
import pl.coderslab.cultureBuddies.exceptions.NotExistingRecordException;

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

    @Override
    public void save(Event event) {
        final Address address = event.getAddress();
        final Optional<Address> addressFromDb = addressRepository.findFirstByCityAndStreetAndNumberAndFlatNumber(
                address.getCity(), address.getStreet(), address.getNumber(), address.getFlatNumber());
        final Address addressToAdd = addressFromDb.orElseGet(() -> addressRepository.save(address));
        event.setAddress(addressToAdd);
        final Event saved = eventRepository.save(event);
        log.debug("Entity {} has been saved ", saved);
    }

}
