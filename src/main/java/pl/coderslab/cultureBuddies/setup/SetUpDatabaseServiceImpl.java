package pl.coderslab.cultureBuddies.setup;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.coderslab.cultureBuddies.author.AuthorRepository;
import pl.coderslab.cultureBuddies.books.BookService;
import pl.coderslab.cultureBuddies.buddies.BuddyRelationRepository;
import pl.coderslab.cultureBuddies.buddies.BuddyService;
import pl.coderslab.cultureBuddies.buddies.RelationStatusRepository;
import pl.coderslab.cultureBuddies.buddyBook.BuddyBookRepository;
import pl.coderslab.cultureBuddies.buddyBuddy.RelationStatus;
import pl.coderslab.cultureBuddies.city.City;
import pl.coderslab.cultureBuddies.city.CityRepository;
import pl.coderslab.cultureBuddies.events.EventRepository;
import pl.coderslab.cultureBuddies.events.EventService;
import pl.coderslab.cultureBuddies.events.EventType;
import pl.coderslab.cultureBuddies.events.EventTypeRepository;
import pl.coderslab.cultureBuddies.exceptions.InvalidDataFromExternalServiceException;
import pl.coderslab.cultureBuddies.exceptions.NotExistingRecordException;
import pl.coderslab.cultureBuddies.security.Role;
import pl.coderslab.cultureBuddies.security.RoleRepository;

import java.io.IOException;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class SetUpDatabaseServiceImpl implements SetUpDatabaseService {
    private final BuddyService buddyService;
    private final BuddyRelationRepository buddyRelationRepository;
    private final BookService bookService;
    private final BuddyBookRepository buddyBookRepository;
    private final AuthorRepository authorRepository;
    private final EventRepository eventRepository;
    private final CityRepository cityRepository;
    private final RoleRepository roleRepository;
    private final EventTypeRepository eventTypeRepository;
    private final EventService eventService;
    private final RelationStatusRepository relationStatusRepository;

    @Override
    public void setStartingData() throws InvalidDataFromExternalServiceException, IOException, NotExistingRecordException {
        log.info("Setting database...");
        log.info("Setting immutable data... ");
        setDataUnchangedByUsers();
        log.info("Setting example users' data...");
        setExampleUsersData();
        log.info("Database setting has been finished.");
    }

    @Override
    public void restoreDatabase() throws InvalidDataFromExternalServiceException, IOException, NotExistingRecordException {
        log.info("Restarting database...");
        log.info("Cleaning database...");
        cleanDatabase();
        log.info("Setting example users' data...");
        setExampleUsersData();
        log.info("Database restart has been finished");
    }

    @Override
    public void setDataUnchangedByUsers() {
        setRole();
        setCities();
        setRelationStatus();
        setEventsTypes();
    }

    @Override
    public void setExampleUsersData() throws InvalidDataFromExternalServiceException, IOException, NotExistingRecordException {
        bookService.setExampleBooks();
        buddyService.setExampleBuddies();
        buddyService.setBuddiesRelations();
        bookService.setExampleBookRatings();
        eventService.setExampleEvents();
    }

    @Override
    public void cleanDatabase() {
        buddyBookRepository.deleteAll();
        buddyRelationRepository.deleteAll();
        bookService.deleteAll();
        buddyService.deleteAll();
        authorRepository.deleteAll();
        eventRepository.deleteAll();
    }

    private void setEventsTypes() {
        final EventType concert = getEventType("concert");
        final EventType theatre = getEventType("theatre");
        final EventType literature = getEventType("literature");
        final EventType cinema = getEventType("cinema");
        final EventType museum = getEventType("museum");
        final EventType music = getEventType("music");
        final EventType other = getEventType("other");
        List<EventType> types = List.of(concert, theatre, literature, cinema, museum,
                music, other);
        eventTypeRepository.saveAll(types);
    }

    private EventType getEventType(String type) {
        final EventType eventType = new EventType();
        eventType.setName(type);
        return eventType;
    }

    private void setCities() {
        final City city = getCity("Wrocław");
        final City city2 = getCity("Warszawa");
        final City city3 = getCity("Kraków");
        final City city4 = getCity("Poznań");
        final City city5 = getCity("Łódź");
        final City city6 = getCity("Gdańsk");
        List<City> cities = List.of(city, city2, city3, city4, city5, city6);
        cityRepository.saveAll(cities);
    }

    private City getCity(String cityName) {
        final City city = new City();
        city.setName(cityName);
        return city;
    }

    private void setRole() {
        Role userRole = new Role();
        userRole.setName("ROLE_USER");
        roleRepository.save(userRole);
    }

    private void setRelationStatus() {
        final RelationStatus inviting = getStatus("inviting");
        final RelationStatus invited = getStatus("invited");
        final RelationStatus blocking = getStatus("blocking");
        final RelationStatus blocked = getStatus("blocked");
        final RelationStatus buddies = getStatus("buddies");
        final List<RelationStatus> statuses = List.of(inviting, invited, blocking, blocked, buddies);
        relationStatusRepository.saveAll(statuses);
    }

    private RelationStatus getStatus(String statusName) {
        final RelationStatus inviting = new RelationStatus();
        inviting.setName(statusName);
        return inviting;
    }
}
