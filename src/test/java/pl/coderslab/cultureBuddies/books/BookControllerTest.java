package pl.coderslab.cultureBuddies.books;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import pl.coderslab.cultureBuddies.author.Author;
import pl.coderslab.cultureBuddies.buddyBook.BuddyBook;
import pl.coderslab.cultureBuddies.buddyBook.BuddyBookId;
import pl.coderslab.cultureBuddies.buddyBook.BuddyBookService;
import pl.coderslab.cultureBuddies.city.CityRepository;
import pl.coderslab.cultureBuddies.exceptions.NotExistingRecordException;
import pl.coderslab.cultureBuddies.exceptions.RelationshipAlreadyCreatedException;
import pl.coderslab.cultureBuddies.googleapis.restModel.BookFromGoogle;
import pl.coderslab.cultureBuddies.googleapis.restModel.IndustryIdentifier;
import pl.coderslab.cultureBuddies.googleapis.restModel.VolumeInfo;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(BookController.class)
@WithMockUser
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class BookControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private BookService bookServiceMock;
    @MockBean
    private BuddyBookService buddyBookServiceMock;
    @MockBean
    private CityRepository cityRepository;

    @Spy
    private Book book;
    @Spy
    private BuddyBook buddyBook;

    private Author author;
    private static final String TITLE = "title";
    private static final String AUTHOR_NAME = "Kowalski";
    private static final int MAX_PAGE = 100;
    private List<BookFromGoogle> googleList;
    private static final long BOOK_ID = 20L;
    private static final long AUTHOR_ID = 233L;
    private static final long BUDDY_ID = 2111L;

    @BeforeEach
    void setUp() throws NotExistingRecordException {
        author = Author.builder()
                .id(AUTHOR_ID)
                .firstName("Jan")
                .lastName(AUTHOR_NAME).build();
        final IndustryIdentifier industryIdentifier = new IndustryIdentifier();
        industryIdentifier.setIdentifier("8374955422");
        final IndustryIdentifier[] industryIdentifiers = {industryIdentifier};
        final VolumeInfo volumeInfo = VolumeInfo.builder()
                .industryIdentifiers(industryIdentifiers).title(TITLE)
                .build();
        BookFromGoogle bookFromGoogle = BookFromGoogle.builder()
                .volumeInfo(volumeInfo).build();
        googleList = Collections.singletonList(bookFromGoogle);
        book.setId(BOOK_ID);
        author.setId(AUTHOR_ID);
        final BuddyBookId buddyBookId = new BuddyBookId();
        buddyBookId.setBookId(BOOK_ID);
        buddyBookId.setBuddyId(BUDDY_ID);
        buddyBook.setId(buddyBookId);
        when(buddyBookServiceMock.findRelationWithPrincipalByBookId(BOOK_ID)).thenReturn(buddyBook);
        when(bookServiceMock.findById(BOOK_ID)).thenReturn(book);
        when(bookServiceMock.findByIdWithAuthors(BOOK_ID)).thenReturn(book);
    }


    @Test
    public void givenValidBuddyBookWithRating_whenPostRateUrl_thenBuddyBookIsBeingUpdated() throws Exception {
        //when
        mockMvc.perform(post("/app/myBooks/rate").with(csrf())
                .flashAttr("buddyBook", buddyBook))
                .andExpect(status().is(302))
                .andExpect(redirectedUrl("/app/myBooks"));
        verify(buddyBookServiceMock).updateBuddyBook(buddyBook);
    }

    @Test
    public void givenBook_whenPostAdd_thenBookIsBeingAddedToBuddy() throws Exception {
        //given
        when(bookServiceMock.addBookToBuddy(book)).thenReturn(buddyBook);
        //when, then
        mockMvc.perform(post("/app/myBooks/add").with(csrf())
                .flashAttr("book", book))
                .andExpect(status().isOk())
                .andExpect(model().attribute("buddyBookWithId", buddyBook))
                .andExpect(model().attribute("buddyBook", new BuddyBook()))
                .andExpect(model().attribute("book", book))
                .andExpect(view().name("/books/rate"));
        verify(bookServiceMock).addBookToBuddy(book);
    }

    @Test
    public void givenAlreadyAddedBook_whenPostToAdd_thenErrorPage() throws Exception {
        //given
        when(bookServiceMock.addBookToBuddy(book)).thenThrow(RelationshipAlreadyCreatedException.class);
        //when, then
        mockMvc.perform(post("/app/myBooks/add").with(csrf())
                .flashAttr("book", book))
                .andExpect(status().isOk())
                .andExpect(view().name("/errors/error"));
    }

    @Test
    public void givenTitleAuthorAndPageNo_whenGetSearchUrl_thenAddPageBeingPrepared() throws Exception {
        //given
        int pageNo = 1;
        when(bookServiceMock.getBooksFromExternalApi(TITLE, AUTHOR_NAME, pageNo)).thenReturn(googleList);
        //when, then
        mockMvc.perform(get("/app/myBooks/search/" + pageNo)
                .param("title", TITLE)
                .param("author", AUTHOR_NAME)
                .param("maxPage", String.valueOf(MAX_PAGE)))
                .andExpect(status().isOk())
                .andExpect(model().attribute("gBooks", googleList))
                .andExpect(model().attribute("pageNo", pageNo))
                .andExpect(model().attribute("title", TITLE))
                .andExpect(model().attribute("author", AUTHOR_NAME))
                .andExpect(model().attribute("book", new Book()))
                .andExpect(view().name("/books/add"));
    }

    @Test
    public void givenTitleAuthorName_whenPostSearchUrl_thenAddPageBeingPrepared() throws Exception {
        //given
        int pageNo = 0;
        when(bookServiceMock.getMaxResultsPage(TITLE, AUTHOR_NAME)).thenReturn(MAX_PAGE);
        when(bookServiceMock.getBooksFromExternalApi(TITLE, AUTHOR_NAME, pageNo))
                .thenReturn(googleList);
        //when, then
        mockMvc.perform(post("/app/myBooks/search").with(csrf())
                .param("title", TITLE)
                .param("author", AUTHOR_NAME))
                .andExpect(status().isOk())
                .andExpect(model().attribute("gBooks", googleList))
                .andExpect(model().attribute("pageNo", pageNo))
                .andExpect(model().attribute("title", TITLE))
                .andExpect(model().attribute("author", AUTHOR_NAME))
                .andExpect(model().attribute("book", new Book()))
                .andExpect(view().name("/books/add"));
    }


    @Test
    public void whenGetMyBooksUrl_thenMyBookPageBeingPrepared() throws Exception {
        //given
        final Author author2 = author.toBuilder().lastName("Nowak").build();
        final List<Author> authors = Arrays.asList(author, author2);
        when(bookServiceMock.getBooksAuthorsOfPrincipal()).thenReturn(authors);
        //when, then
        mockMvc.perform(get("/app/myBooks"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("authors", authors))
                .andExpect(view().name("/books/myBooks"));
    }

    @Test
    @WithMockUser(username = "testBuddy")
    public void whenGetAuthorUrl_thenMyBookAuthorPageBeingPrepared() throws Exception {
        //given
        final Book book = Book.builder().title("Book").id(10L).build();
        final Book book2 = Book.builder().title("Book 2").id(11L).build();
        author.addBook(book).addBook(book2);
        when(bookServiceMock.getAuthorById(author.getId())).thenReturn(author);
        //when, then
        mockMvc.perform(get("/app/myBooks/" + author.getId()))
                .andExpect(status().isOk())
                .andExpect(model().attribute("author", author))
                .andExpect(view().name("/books/author"));
    }

    @Test
    @WithMockUser(username = "testBuddy")
    public void givenBookIdAndAuthorId_whenGetDeleteUrl_thenDeletePageBeingPrepared() throws Exception {

        mockMvc.perform(get("/app/myBooks/delete/" + BOOK_ID)
                .param("authorId", String.valueOf(AUTHOR_ID)))
                .andExpect(status().isOk())
                .andExpect(model().attribute("authorId", AUTHOR_ID))
                .andExpect(model().attribute("book", book))
                .andExpect(model().attribute("buddyBook", buddyBook))
                .andExpect(view().name("/books/delete"));
    }

    @Test
    public void givenBookId_whenPostDeleteUrl_thenBuddyBookRelationBeingDeleted() throws Exception {
        mockMvc.perform(post("/app/myBooks/delete").with(csrf())
                .param("bookId", String.valueOf(BOOK_ID)))
                .andExpect(status().is(302))
                .andExpect(redirectedUrl("/app/myBooks"));
        verify(buddyBookServiceMock).remove(BOOK_ID);
    }

    @Test
    public void givenBookId_whenGetEditUrl_thenEditPageBeingPrepared() throws Exception {
        mockMvc.perform(get("/app/myBooks/edit/" + BOOK_ID)
                .param("authorId", String.valueOf(AUTHOR_ID)))
                .andExpect(status().isOk())
                .andExpect(model().attribute("authorId", AUTHOR_ID))
                .andExpect(model().attribute("book", book))
                .andExpect(model().attribute("buddyBook", buddyBook))
                .andExpect(view().name("/books/edit"));

    }

    @Test
    public void givenValidBuddyBook_whenPostEditUrl_thenBuddyBookBeingUpdated() throws Exception {
        buddyBook.setRate(9);
        mockMvc.perform(post("/app/myBooks/edit").with(csrf())
                .flashAttr("buddyBook", buddyBook)
                .param("authorId", String.valueOf(AUTHOR_ID)))
                .andExpect(status().is(302))
                .andExpect(redirectedUrl("/app/myBooks/" + AUTHOR_ID));
        verify(buddyBookServiceMock).updateBuddyBook(buddyBook);
    }

}















