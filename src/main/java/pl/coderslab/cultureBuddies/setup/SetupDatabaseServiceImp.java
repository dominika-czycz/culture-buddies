package pl.coderslab.cultureBuddies.setup;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;
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
import pl.coderslab.cultureBuddies.events.EventRepository;
import pl.coderslab.cultureBuddies.exceptions.InvalidDataFromExternalServiceException;
import pl.coderslab.cultureBuddies.exceptions.NotExistingRecordException;
import pl.coderslab.cultureBuddies.security.Role;
import pl.coderslab.cultureBuddies.security.RoleRepository;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
public class SetupDatabaseServiceImp {
    private final BuddyService buddyService;
    private final BuddyRelationRepository buddyRelationRepository;
    private final BookService bookService;
    private final BuddyBookRepository buddyBookRepository;
    private final AuthorRepository authorRepository;
    private final EventRepository eventRepository;
    private final CityRepository cityRepository;
    private final RoleRepository roleRepository;
    private final RelationStatusRepository relationStatusRepository;
    private static final String RESOURCE_NAME = "static/pictures/buddyPictures";

    public void setStartingData() throws InvalidDataFromExternalServiceException, IOException, NotExistingRecordException {
        setDataUnchangedByUsers();
        setExampleUsersData();
    }

    public void restoreDatabase() throws InvalidDataFromExternalServiceException, IOException, NotExistingRecordException {
        cleanDatabase();
        setExampleUsersData();
    }

    private void setDataUnchangedByUsers() {
        setRole();
        setCities();
        setRelationStatus();
    }

    private void setExampleUsersData() throws InvalidDataFromExternalServiceException, IOException, NotExistingRecordException {
        setBooks();
        setBuddies();
        setBuddiesRelations();
        setBookRatings();
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
                "Stories, stories, all is stories: political stories, religious stories, scientific stories, even stories about stories. We live inside these stories. Like this one in The Master and Margarita. The story that we can more or less agree upon we call reality.",9 );
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
        final Book lolita = Book.builder()
                .title("Lolita")
                .identifier("9780141391601")
                .thumbnailLink("http://books.google.com/books/content?id=S0lVyYcw8tsC&printsec=frontcover&img=1&zoom=1&edge=curl&source=gbs_api")
                .authorsFullName(List.of("Michaił Bułhakow"))
                .build();
        final Book adaOrArdor = Book.builder()
                .title("Ada or Ardor")
                .identifier("9780141911304")
                .thumbnailLink("http://books.google.com/books/content?id=SBQgSToNcUsC&printsec=frontcover&img=1&zoom=1&edge=curl&source=gbs_api")
                .authorsFullName(List.of("Michaił Bułhakow"))
                .build();
        final Book beloved = Book.builder()
                .title("Beloved")
                .identifier("030738862X")
                .thumbnailLink("http://books.google.com/books/content?id=sfmp6gjZGP8C&printsec=frontcover&img=1&zoom=1&source=gbs_api")
                .authorsFullName(List.of("Toni Morrison"))
                .build();
        final Book theRepublicOfWine = Book.builder()
                .title("The Republic of Wine")
                .identifier("9781743771884")
                .thumbnailLink("http://books.google.com/books/content?id=F8m_DAAAQBAJ&printsec=frontcover&img=1&zoom=1&source=gbs_api")
                .authorsFullName(Collections.singletonList("Mo Yan"))
                .build();
        final Book bigBreastsAndWideHips = Book.builder()
                .title("Big Breasts and Wide Hips")
                .identifier("1559706724")
                .thumbnailLink("http://books.google.com/books/content?id=fe5wAkv1snoC&printsec=frontcover&img=1&zoom=1&edge=curl&source=gbs_api")
                .authorsFullName(Collections.singletonList("Mo Yan"))
                .build();
        final Book theRoad = Book.builder()
                .title("The Road")
                .identifier("9781529014587")
                .thumbnailLink("http://books.google.com/books/content?id=VcZtDwAAQBAJ&printsec=frontcover&img=1&zoom=1&edge=curl&source=gbs_api")
                .authorsFullName(Collections.singletonList("Cormac McCarthy"))
                .build();
        final Book thePianoTeacher = Book.builder()
                .title("The Piano Teacher")
                .identifier("9781847653062")
                .thumbnailLink("http://books.google.com/books/content?id=d_Ady-4CHRIC&printsec=frontcover&img=1&zoom=1&edge=curl&source=gbs_api")
                .authorsFullName(Collections.singletonList("Elfriede Jelinek"))
                .build();
        final Book mistrzIMalgorzata = Book.builder()
                .title("Mistrz i Małgorzata")
                .identifier("8373161503")
                .authorsFullName(Collections.singletonList("Michaił Bułkahow"))
                .build();
        final List<Book> books = List.of(lolita, adaOrArdor, beloved, theRepublicOfWine, bigBreastsAndWideHips,
                theRoad, thePianoTeacher, mistrzIMalgorzata);
        bookService.saveAll(books);
    }

    private void setCities() {
        final City city = new City();
        city.setName("Wrocław");
        final City city2 = new City();
        city2.setName("Warszawa");
        final City city3 = new City();
        city3.setName("Kraków");
        final City city4 = new City();
        city4.setName("Poznań");
        final City city5 = new City();
        city5.setName("Łódź");
        final City city6 = new City();
        city6.setName("Gdańsk");
        List<City> cities = List.of(city, city2, city3, city4, city5, city6);
        cityRepository.saveAll(cities);
    }

    private void setRole() {
        Role UserRole = new Role();
        UserRole.setName("ROLE_USER");
        roleRepository.save(UserRole);
    }

    private void setRelationStatus() {
        final RelationStatus inviting = new RelationStatus();
        inviting.setName("inviting");
        final RelationStatus invited = new RelationStatus();
        invited.setName("invited");
        final RelationStatus blocking = new RelationStatus();
        blocking.setName("blocking");
        final RelationStatus blocked = new RelationStatus();
        blocked.setName("blocked");
        final RelationStatus buddies = new RelationStatus();
        buddies.setName("buddies");
        final List<RelationStatus> statuses = List.of(inviting, invited, blocking, blocked, buddies);
        relationStatusRepository.saveAll(statuses);
    }
}
