package pl.coderslab.cultureBuddies.author;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.validation.ConstraintViolationException;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class AuthorRepositoryTest {
    private Author validAuthor;
    @Autowired
    private TestEntityManager testEm;
    @Autowired
    private AuthorRepository testObject;

    @BeforeEach
    public void setUp() {
        validAuthor = Author.builder()
                .firstName("Jan")
                .lastName("Kowalski")
                .id(10L).build();
    }

    @Test
    public void whenSaveEmptyAuthor_thenConstraintViolationsException() {
        //given
        final Author emptyAuthor = new Author();
        //when, then
        assertThrows(ConstraintViolationException.class,
                () -> testObject.save(emptyAuthor));
        assertNull(emptyAuthor.getId());
    }

    @Test
    public void whenSavingValidAuthor_thenSavedAndIdNotNull() {
        //when
        final Author savedAuthor = testObject.save(validAuthor);
        //then
        assertNotNull(savedAuthor.getId());
    }


}