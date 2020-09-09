package pl.coderslab.cultureBuddies.buddies;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.validation.ConstraintViolationException;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class BuddyRepositoryTest {
    private Buddy validTestBuddy;
    @Autowired
    private TestEntityManager testEm;
    @Autowired
    private BuddyRepository testObject;

    @BeforeEach
    public void setup() {
        validTestBuddy = Buddy.builder()
                .username("bestBuddy")
                .email("test@gmail.com")
                .name("Anna")
                .lastName("Kowalska")
                .password("annaKowalska")
                .city("Wrocław")
                .build();
    }

    @Test
    public void whenFindByUsername_thenReturnBuddy() {
        //given
        testEm.persist(validTestBuddy);
        testEm.flush();
        //when
        final Optional<Buddy> found = testObject.findFirstByUsernameIgnoringCase(validTestBuddy.getUsername());
        //then
        assertThat(found.get().getUsername(), is(validTestBuddy.getUsername()));
    }

    @Test
    void whenSaveEmptyBuddy_thenConstraintViolationException() {
        //given
        final Buddy emptyBuddy = Buddy.builder().build();
        //when, then
        assertThrows(ConstraintViolationException.class, () -> testObject.save(emptyBuddy));
        assertNull(emptyBuddy.getId());
    }

    @Test
    void whenEmailIsInvalid_thenConstraintViolationException() {
        //given
        final Buddy buddyWithInvalidEmail = validTestBuddy.toBuilder().email("la").build();
        //when, then
        assertThrows(ConstraintViolationException.class, () -> testObject.save(buddyWithInvalidEmail));
    }

    @Test
    void whenSavingValidBuddy_thenSavedAndIdNotNull() {
        //when
        final Buddy save = testObject.save(validTestBuddy);
        //then
        assertNotNull(save.getId());
    }
}
