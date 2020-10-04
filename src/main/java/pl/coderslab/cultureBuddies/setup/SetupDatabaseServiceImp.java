package pl.coderslab.cultureBuddies.setup;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import pl.coderslab.cultureBuddies.author.AuthorRepository;
import pl.coderslab.cultureBuddies.books.Book;
import pl.coderslab.cultureBuddies.books.BookService;
import pl.coderslab.cultureBuddies.buddies.Buddy;
import pl.coderslab.cultureBuddies.buddies.BuddyRelationRepository;
import pl.coderslab.cultureBuddies.buddies.BuddyService;
import pl.coderslab.cultureBuddies.buddies.RelationStatusRepository;
import pl.coderslab.cultureBuddies.buddyBook.BuddyBook;
import pl.coderslab.cultureBuddies.buddyBook.BuddyBookRepository;
import pl.coderslab.cultureBuddies.buddyBuddy.RelationStatus;
import pl.coderslab.cultureBuddies.city.City;
import pl.coderslab.cultureBuddies.city.CityRepository;
import pl.coderslab.cultureBuddies.events.*;
import pl.coderslab.cultureBuddies.exceptions.InvalidDataFromExternalServiceException;
import pl.coderslab.cultureBuddies.exceptions.NotExistingRecordException;
import pl.coderslab.cultureBuddies.security.Role;
import pl.coderslab.cultureBuddies.security.RoleRepository;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class SetupDatabaseServiceImp implements SetUpDatabaseService {
    private final BuddyService buddyService;
    private final BuddyRelationRepository buddyRelationRepository;
    private final BookService bookService;
    private final BuddyBookRepository buddyBookRepository;
    private final AuthorRepository authorRepository;
    private final EventRepository eventRepository;
    private final CityRepository cityRepository;
    private final AddressRepository addressRepository;
    private final RoleRepository roleRepository;
    private final EventTypeRepository eventTypeRepository;
    private final RelationStatusRepository relationStatusRepository;
    private static final String RESOURCE_NAME = "static/pictures/buddyPictures";

    @Override
    public void setStartingData() throws InvalidDataFromExternalServiceException, IOException, NotExistingRecordException {
        setDataUnchangedByUsers();
        setExampleUsersData();
    }

    @Override
    public void restoreDatabase() throws InvalidDataFromExternalServiceException, IOException, NotExistingRecordException {
        cleanDatabase();
        setExampleUsersData();
    }

    private void setDataUnchangedByUsers() {
        setRole();
        setCities();
        setRelationStatus();
        setEventsTypes();
    }

    private void setExampleUsersData() throws InvalidDataFromExternalServiceException, IOException, NotExistingRecordException {
        setBooks();
        setBuddies();
        setBuddiesRelations();
        setBookRatings();
        setEvents();
    }

    private void cleanDatabase() {
        buddyBookRepository.deleteAll();
        buddyRelationRepository.deleteAll();
        bookService.deleteAll();
        buddyService.deleteAll();
        authorRepository.deleteAll();
        eventRepository.deleteAll();
    }

    private void setBookRatings() throws NotExistingRecordException {
        saveRating("annaKowal", "lolita",
                "Pushing the boundaries of what acceptable literature can actually be, Lolita is very much a piece of art. For many years I kept hearing about this book, the content sounding disturbing and perhaps even slightly fascinating. ",
                10);
        saveRating("Mazur", "lolita",
                "Sick, twisted and beautiful. Love this.",
                9);
        saveRating("annaKowal", "Ada or Ardor",
                "“Maybe the only thing that hints at a sense of Time is rhythm; not the recurrent beats of the rhythm but the gap between two such beats, the gray gap between black beats: the Tender Interval.” ",
                10);
        saveRating("koala", "Beloved",
                "Just love it ",
                10);
        saveRating("annaKowal", "Beloved",
                "The brutal truth, brilliantly written. A mother hanging from a tree, the vile debasement of a nursing mother, scars so deep from whipping that they make a design of a tree on a woman’s back.",
                9);
        saveRating("Mazur", "Big Breasts and Wide Hips",
                "I loved the homage to the female- mother, nurturer, life giver, sacrificial lamb. Yan is brilliant in his use of allegories in the tale of the Shangguan family (specifically Mother and Jintong) from a China (Motherland) violated by war, suffering famine.",
                10);
        saveRating("koala", "The Road",
                "The Road is a truly disturbing book; it is absorbing, mystifying and completely harrowing. Simply because it shows us how man could act given the right circumstances; it’s a terrifying concept because it could also be a true one.",
                10);
        saveRating("Mazur", "The Piano Teacher",
                "AMAZING THINGS: I can literally feel new wrinkles spreading across the surface of my brain when I read this guy. He's so wicked smart that there's no chance he's completely sane. His adjectives and descriptions are 100% PERFECT, and yet entirely nonsensic",
                10);
        saveRating("koala", "The Piano Teacher",
                "good", 7);
        saveRating("annaKowal", "The Piano Teacher",
                "Love it",
                10);
        saveRating("adamski", "The Piano Teacher",
                "This is one of my favorite books. I can't even describe how amazed I was when I finished this book. Jelinek moves the reader from character to character, rarely telling us who we inhabit, yet unlike so many other books that abuse this device, it works.",
                10);
        saveRating("adamski", "Mistrz i Małgorzata",
                "Stories, stories, all is stories: political stories, religious stories, scientific stories, even stories about stories. We live inside these stories. Like this one in The Master and Margarita. The story that we can more or less agree upon we call reality.", 9);
    }

    private void saveRating(String username, String bookTitle, String comment, int rate) throws NotExistingRecordException {
        final Buddy buddy = buddyService.findByUsername(username);
        Book book = bookService.findByTitle(bookTitle);
        final BuddyBook buddyBook = new BuddyBook();
        buddyBook.setComment(comment);
        buddyBook.setRate(rate);
        buddyBook.setBook(book);
        buddyBook.setBuddy(buddy);
        buddyBookRepository.save(buddyBook);
    }

    private void setEvents() throws NotExistingRecordException {
        final Address address0 = getAddress("Włodkowica", "8a");
        final Event event = getEvent(address0, LocalDate.now().plusMonths(1), "20:00",
                "Wine and Nabokov...",
                "annaKowal",
                "Nabokov's most controversial books and a lot of wine...",
                null,
                "literature");
        final Address address1 = getAddress("Kazimierza Wielkiego ", "19a");
        final Event event1 = getEvent(address1, LocalDate.now().plusMonths(2), "19:00",
                "Tarantino",
                "annaKowal",
                "A Tarantino movie marathon and lots of food after that...",
                "https://www.kinonh.pl/art.s?id=1484",
                "cinema");
        final Address address2 = getAddress("Wystawowa", "1");
        final Event event2 = getEvent(address2, LocalDate.now().plusDays(12), "21:00",
                "KULT",
                "Mazur",
                "Orange route of Kult ",
                "https://www.stodola.pl/en/events/kult-akustik-wroclaw-137605.html",
                "concert");
        final Event event3 = getEvent(address2, LocalDate.now().plusDays(23), "17:00",
                "Understand contemporary art",
                "Koala",
                "Understand contemporary art...sometimes it's hard, but it's worth trying",
                null,
                "museum");
        final Buddy koala = buddyService.findByUsername("koala");
        final Buddy mazur = buddyService.findByUsername("Mazur");
        event.addBuddy(koala);
        event.addBuddy(mazur);
        event1.addBuddy(koala);
        event2.addBuddy(koala);
        final List<Event> events = List.of(event, event1, event2, event3);
        eventRepository.saveAll(events);
    }

    private Address getAddress(String street, String number) {
        final Address address = Address.builder()
                .street(street)
                .city("Wrocław")
                .number(number).build();
        return addressRepository.save(address);
    }

    private Event getEvent(Address savedAddress, LocalDate plusMonths, String time, String title, String username, String desc, String link, String eventTypeName) throws NotExistingRecordException {
        final EventType eventType = eventTypeRepository.findFirstByName(eventTypeName);
        final Buddy buddyWithEvents = getBuddy(username);
        final Event event = new Event();
        event.setDate(plusMonths);
        event.setStringTime(time);
        event.setBuddy(buddyWithEvents);
        event.setTitle(title);
        event.setDescription(desc);
        event.setEventType(eventType);
        event.setLink(link);
        event.setAddress(savedAddress);
        return event;
    }

    private Buddy getBuddy(String username) throws NotExistingRecordException {
        final Buddy buddy = buddyService.findByUsername(username);
        return buddyService.getBuddyWithEvents(buddy);
    }

    private void setBuddies() throws IOException {
        saveBuddy("barbora-polednova-dY7Q-sl77L4-unsplash.jpg",
                "Anna", "Kowalska", "annaKowal");
        saveBuddy("eric-weber-nQOQVJW7EY8-unsplash.jpg",
                "Piotr", "Mazur", "Mazur");
        saveBuddy("gabriel-silverio-u3WmDyKGsrY-unsplash.jpg",
                "Ola", "Wojciechowska", "koala");
        saveBuddy("meric-tuna-lIvALCu6T8Q-unsplash.jpg",
                "Adam", "Adamski", "adamski");
    }

    private void setBuddiesRelations() throws NotExistingRecordException {
        final Long koalaId = buddyService.findByUsername("koala").getId();
        final Long mazurId = buddyService.findByUsername("Mazur").getId();
        final Long adamskiId = buddyService.findByUsername("adamski").getId();
        final Long annaKowalId = buddyService.findByUsername("annaKowal").getId();
        buddyService.inviteBuddy(koalaId, mazurId);
        buddyService.inviteBuddy(koalaId, annaKowalId);
        buddyService.inviteBuddy(mazurId, annaKowalId);
        buddyService.inviteBuddy(adamskiId, annaKowalId);
        buddyService.acceptBuddy(annaKowalId, koalaId);
        buddyService.acceptBuddy(annaKowalId, mazurId);
    }

    private void saveBuddy(String fileName, String name, String lastName, String userName) throws IOException {
        City city = cityRepository.findFirstByName("Wrocław");
        MultipartFile kowalFile = getMultipartFile(fileName);
        final Buddy annaKowal = Buddy.builder()
                .name(name)
                .lastName(lastName)
                .username(userName)
                .email("dominika.czycz@gmail.com")
                .password(userName)
                .city(city).build();
        buddyService.save(kowalFile, annaKowal);
    }

    private MultipartFile getMultipartFile(String fileName) throws IOException {
        final Path pathToPicture = getPathToPicture(fileName);
        final byte[] bytes = Files.readAllBytes(pathToPicture);
        return new MockMultipartFile(fileName, bytes);
    }

    private String getPathToDirectory() {
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(Objects.requireNonNull(classLoader.getResource(RESOURCE_NAME)).getFile());
        return file.getAbsolutePath();
    }

    private Path getPathToPicture(String fileName) {
        final String file = StringUtils.cleanPath(fileName);
        String dir = getPathToDirectory();
        return Paths.get(dir + "/" + file);
    }

    private void setBooks() throws InvalidDataFromExternalServiceException {
        final Book lolita = getBook("Lolita", "9780141391601", "http://books.google.com/books/content?id=S0lVyYcw8tsC&printsec=frontcover&img=1&zoom=1&edge=curl&source=gbs_api", "Vladimir Nabokov");
        final Book adaOrArdor = getBook("Ada or Ardor", "9780141911304", "http://books.google.com/books/content?id=SBQgSToNcUsC&printsec=frontcover&img=1&zoom=1&edge=curl&source=gbs_api", "Vladimir Nabokov");
        final Book beloved = getBook("Beloved", "030738862X", "http://books.google.com/books/content?id=sfmp6gjZGP8C&printsec=frontcover&img=1&zoom=1&source=gbs_api", "Toni Morrison");
        final Book theRepublicOfWine = getBook("The Republic of Wine", "9781743771884", "http://books.google.com/books/content?id=F8m_DAAAQBAJ&printsec=frontcover&img=1&zoom=1&source=gbs_api", "Mo Yan");
        final Book bigBreastsAndWideHips = getBook("Big Breasts and Wide Hips", "1559706724", "http://books.google.com/books/content?id=fe5wAkv1snoC&printsec=frontcover&img=1&zoom=1&edge=curl&source=gbs_api", "Mo Yan");
        final Book theRoad = getBook("The Road", "9781529014587", "http://books.google.com/books/content?id=VcZtDwAAQBAJ&printsec=frontcover&img=1&zoom=1&edge=curl&source=gbs_api", "Cormac McCarthy");
        final Book thePianoTeacher = getBook("The Piano Teacher", "9781847653062", "http://books.google.com/books/content?id=d_Ady-4CHRIC&printsec=frontcover&img=1&zoom=1&edge=curl&source=gbs_api", "Elfriede Jelinek");
        final Book masterAndMargaret = getBook("Mistrz i Małgorzata", "8373161503", null, "Michaił Bułkahow");
        final List<Book> books = List.of(lolita, adaOrArdor, beloved, theRepublicOfWine, bigBreastsAndWideHips,
                theRoad, thePianoTeacher, masterAndMargaret);
        bookService.saveAll(books);
    }

    private Book getBook(String lolita2, String s, String s2, String s3) {
        return Book.builder()
                .title(lolita2)
                .identifier(s)
                .thumbnailLink(s2)
                .authorsFullName(Collections.singletonList(s3))
                .build();
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
        Role UserRole = new Role();
        UserRole.setName("ROLE_USER");
        roleRepository.save(UserRole);
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
