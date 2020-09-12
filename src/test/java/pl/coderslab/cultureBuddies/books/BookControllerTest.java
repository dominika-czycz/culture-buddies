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
    private BuddyService buddyService;
    @Spy
    private Buddy buddy;

    @BeforeEach
    void setUp() {
    }

    @Test
    void whenAppMyBookUrl_thenMyBookView() throws Exception {
        final Author author = Author.builder().firstName("Jan").lastName("Kowalski").build();
        final Author author2 = author.toBuilder().lastName("Nowak").build();
        buddy.addAuthor(author).addAuthor(author2);
        when(buddyService.findAuthenticatedBuddyWithAuthors()).thenReturn(buddy);
        mockMvc.perform(get("/app/myBooks"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("authors", buddy.getAuthors()))
                .andExpect(view().name("/books/myBooks"));
    }
}