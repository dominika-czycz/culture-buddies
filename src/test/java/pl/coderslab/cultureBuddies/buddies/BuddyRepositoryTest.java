package pl.coderslab.cultureBuddies.buddies;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import pl.coderslab.cultureBuddies.security.Role;

import javax.validation.ConstraintViolationException;
import java.util.HashSet;
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
    public void whenSaveEmptyBuddy_thenConstraintViolationException() {
        //given
        final Buddy emptyBuddy = Buddy.builder().build();
        //when, then
        assertThrows(ConstraintViolationException.class, () -> testObject.save(emptyBuddy));
        assertNull(emptyBuddy.getId());
    }

    @Test
    public void whenEmailIsInvalid_thenConstraintViolationException() {
        //given
        final Buddy buddyWithInvalidEmail = validTestBuddy.toBuilder().email("la").build();
        //when, then
        assertThrows(ConstraintViolationException.class, () -> testObject.save(buddyWithInvalidEmail));
    }

    @Test
    public void whenSavingValidBuddy_thenSavedAndIdNotNull() {
        //when
        final Buddy save = testObject.save(validTestBuddy);
        //then
        assertNotNull(save.getId());
    }

    @Test
    public void whenSavingBuddyWithRoleThenRoleAdded() {
        //given
        final Role roleUser = Role.builder().id(1L).name("ROLE_USER").build();
        validTestBuddy.addRole(roleUser);
        //when
        final Buddy savedBuddy = testObject.save(validTestBuddy);
        //then
        assertThat(savedBuddy.getRoles(), is(validTestBuddy.getRoles()));
    }
}
