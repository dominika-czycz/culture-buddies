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
//@ActiveProfiles("test")
class BuddiesRepositoryTest {
    private Buddy validTestBuddy;
    @Autowired
    private TestEntityManager testEm;
    @Autowired
    private BuddiesRepository buddiesRepository;

    @BeforeEach
    public void beforeEachTests() {
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
        final Optional<Buddy> found = buddiesRepository.findByUsername(validTestBuddy.getUsername());
        //then
        assertThat(found.get().getUsername(), is(validTestBuddy.getUsername()));
    }

    @Test
    void whenSaveEmptyBuddy_thenConstraintViolationException() {
        //given
        final Buddy emptyBuddy = Buddy.builder().build();
        //when, then
        assertThrows(ConstraintViolationException.class, () -> buddiesRepository.save(emptyBuddy));
        assertNull(emptyBuddy.getId());
    }

    @Test
    void whenEmailIsInvalid_thenConstraintViolationException() {
        //given
        final Buddy buddyWithInvalidEmail = validTestBuddy.toBuilder().email("la").build();
        //when, then
        assertThrows(ConstraintViolationException.class, () -> buddiesRepository.save(buddyWithInvalidEmail));
    }

    @Test
    void whenSavingValidBuddy_thenSaved() {
        //when
        final Buddy save = buddiesRepository.save(validTestBuddy);
        //then
        assertNotNull(save.getId());
    }
}
