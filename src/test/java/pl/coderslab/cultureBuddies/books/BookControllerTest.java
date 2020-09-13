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
import pl.coderslab.cultureBuddies.buddies.Buddy;
import pl.coderslab.cultureBuddies.buddies.BuddyService;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(BookController.class)
@WithMockUser
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class BookControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private BuddyService buddyServiceMock;
    @MockBean
    private BookService bookServiceMock;
    @Spy
    private Buddy buddy;
    private Author author;

    @BeforeEach
    void setUp() {
        author = Author.builder()
                .id(12L)
                .firstName("Jan")
                .lastName("Kowalski").build();
    }

    @Test
    void whenAppMyBookUrl_thenMyBookView() throws Exception {
        final Author author2 = author.toBuilder().lastName("Nowak").build();
        buddy.addAuthor(author).addAuthor(author2);
        when(buddyServiceMock.findAuthenticatedBuddyWithAuthors()).thenReturn(buddy);
        mockMvc.perform(get("/app/myBooks"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("authors", buddy.getAuthors()))
                .andExpect(view().name("/books/myBooks"));
    }

    @Test
    @WithMockUser(username = "testBuddy")
    void whenAppMyBookAuthorUrl_thenMyBookAuthorView() throws Exception {
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