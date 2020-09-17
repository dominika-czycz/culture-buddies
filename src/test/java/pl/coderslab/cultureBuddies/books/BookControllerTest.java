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
import pl.coderslab.cultureBuddies.author.AuthorService;
import pl.coderslab.cultureBuddies.buddies.Buddy;
import pl.coderslab.cultureBuddies.buddies.BuddyBook;
import pl.coderslab.cultureBuddies.exceptions.RelationshipAlreadyCreatedException;
import pl.coderslab.cultureBuddies.googleapis.RestBooksService;
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
    private AuthorService authorServiceMock;
    @MockBean
    private BookService bookServiceMock;
    @MockBean
    private RestBooksService restBooksServiceMock;
    @Spy
    private Buddy buddy;
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
    public void givenBook_whenPostToAdd_thenBookIsAddedToBuddy() throws Exception {
        when(bookServiceMock.addBookToBuddy(book)).thenReturn(buddyBook);
        mockMvc.perform(post("/app/myBooks/add").with(csrf())
                .flashAttr("book", book))
                .andExpect(status().isOk())
                .andExpect(view().name("/books/rate"));
        verify(bookServiceMock).addBookToBuddy(book);
    }

    //TODO
    @Test
    public void givenAlreadyAddedBook_whenPostToAdd_thenBookNotAdded() throws Exception {
        when(bookServiceMock.addBookToBuddy(book)).thenThrow(RelationshipAlreadyCreatedException.class);
        mockMvc.perform(post("/app/myBooks/add").with(csrf())
                .flashAttr("book", book))
                .andExpect(status().isOk())
                .andExpect(view().name("/errors/error"));
    }

    @Test
    public void givenTitle_whenAppMyBookAddPlusTitleUrl_thenAddViewAndModelWithGoogleBooksList() throws Exception {
        //given
        final List<BookFromGoogle> googleList = Collections.singletonList(bookFromGoogle);
        when(restBooksServiceMock.getGoogleBooksListByTitle(title)).thenReturn(googleList);
        //when, then
        mockMvc.perform(get("/app/myBooks/add?title=" + title))
                .andExpect(status().isOk())
                .andExpect(model().attribute("gBooks", googleList))
                .andExpect(view().name("/books/add"));
    }

    @Test
    public void whenAppMyBooksUrl_thenMyBookView() throws Exception {
        //given
        final Author author2 = author.toBuilder().lastName("Nowak").build();
        buddy.addAuthor(author).addAuthor(author2);
        final List<Author> authors = Arrays.asList(author, author2);
        when(authorServiceMock.getOrderedAuthorsListOfPrincipalUser()).thenReturn(authors);
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
        final List<Book> books = Arrays.asList(book, book2);
        author.addBook(book)
                .addBook(book2);
        when(bookServiceMock.findBooksByAuthorAndPrincipalUsername(author.getId())).thenReturn(books);

        //when, then
        mockMvc.perform(get("/app/myBooks/" + author.getId()))
                .andExpect(status().isOk())
                .andExpect(model().attribute("books", books))
                .andExpect(view().name("/books/author"));
    }
}