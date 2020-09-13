package pl.coderslab.cultureBuddies.books;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import pl.coderslab.cultureBuddies.author.Author;

import javax.validation.ConstraintViolationException;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class BookRepositoryTest {
    private Book validBook;
    @Autowired
    private TestEntityManager testEm;
    @Autowired
    private BookRepository testObject;

    @BeforeEach
    public void setup() {
      Author  author = Author.builder()
                .firstName("Jan")
                .lastName("Kowalski")
                .id(10L).build();
        validBook = Book.builder()
                .title("Novel").build();
        validBook.addAuthor(author);
    }

    @Test
    public void whenSaveEmptyBook_thenConstraintViolationsException() {
        //given
        final Book emptyBook = Book.builder().build();
        //when, then
        assertThrows(ConstraintViolationException.class,
                () -> testObject.save(emptyBook));
        assertNull(emptyBook.getId());
    }

    @Test
    public void whenSavingValidBook_thenSavedAndIdNotNull() {
        //when
        final Book savedBook = testObject.save(validBook);
        //then
        assertNotNull(savedBook.getId());
    }
    /* Author author = Author.builder()
                .id(12L)
                .firstName("Jan")
                .lastName("Kowalski").build();
        final Book book = Book.builder().title("Book").id(10L).build();
        final Book book2 = Book.builder().title("Book 2").id(11L).build();
        final List<Book> books = Arrays.asList(book, book2);
        author.addBook(book).addBook(book2);
        buddy.setUsername("testBuddy");
        buddy.addBook(book).addBook(book2);
        buddy.addAuthor(author);*/
}