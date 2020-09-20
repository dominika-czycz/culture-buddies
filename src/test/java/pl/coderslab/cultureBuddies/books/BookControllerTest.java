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
import pl.coderslab.cultureBuddies.buddyBook.BuddyBookService;
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
    private BuddyBookService buddyBookService;
    @Spy
    private Book book;
    @Spy
    private BuddyBook buddyBook;

    private Author author;
    private BookFromGoogle bookFromGoogle;
    private final String title = "title";

    @BeforeEach
    void setUp() {
        author = Author.builder()
                .id(12L)
                .firstName("Jan")
                .lastName("Kowalski").build();
        final IndustryIdentifier industryIdentifier = new IndustryIdentifier();
        industryIdentifier.setIdentifier("8374955422");

        final IndustryIdentifier[] industryIdentifiers = {industryIdentifier};
        final VolumeInfo volumeInfo = VolumeInfo.builder()
                .industryIdentifiers(industryIdentifiers).title(title)
                .build();
        bookFromGoogle = BookFromGoogle.builder()
                .volumeInfo(volumeInfo).build();
    }

    @Test
    public void givenValidBuddyBookWithRating_whenPostToRate_thenBuddyBookIsUpdated() throws Exception {
        mockMvc.perform(post("/app/myBooks/rate").with(csrf())
                .flashAttr("buddyBook", buddyBook))
                .andExpect(status().is(302))
                .andExpect(redirectedUrl("/app/myBooks"));
    }

    @Test
    public void givenBook_whenPostToAdd_thenBookIsAddedToBuddy() throws Exception {
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
    public void givenAlreadyAddedBook_whenPostToAdd_thenBookNotAdded() throws Exception {
        //given
        when(bookServiceMock.addBookToBuddy(book)).thenThrow(RelationshipAlreadyCreatedException.class);
        //when, then
        mockMvc.perform(post("/app/myBooks/add").with(csrf())
                .flashAttr("book", book))
                .andExpect(status().isOk())
                .andExpect(view().name("/errors/error"));
    }

    @Test
    public void givenTitle_whenAppMyBookAddUrl_thenAddViewAndModelWithGoogleBooksList() throws Exception {
        //given
        String author = "";
        int maxPage = 100;
        final List<BookFromGoogle> googleList = Collections.singletonList(bookFromGoogle);
        when(bookServiceMock.getBooksFromExternalApi(title, author, 0)).thenReturn(googleList);
        //when, then
        mockMvc.perform(get("/app/myBooks/search/0")
                .param("title", title)
                .param("author", author)
                .param("maxPage", String.valueOf(maxPage)))
                .andExpect(status().isOk())
                .andExpect(model().attribute("gBooks", googleList))
                .andExpect(model().attribute("pageNo", 0))
                .andExpect(model().attribute("title", title))
                .andExpect(model().attribute("author", author))
                .andExpect(model().attribute("book", new Book()))
                .andExpect(view().name("/books/add"));
    }

    @Test
    public void whenAppMyBooksUrl_thenMyBookView() throws Exception {
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
    public void whenAppMyBookAuthorUrl_thenMyBookAuthorView() throws Exception {
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
}