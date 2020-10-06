package pl.coderslab.cultureBuddies.buddies;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import pl.coderslab.cultureBuddies.city.City;
import pl.coderslab.cultureBuddies.security.Role;
import pl.coderslab.cultureBuddies.setup.SetUpDatabaseService;

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
    @MockBean
    private SetUpDatabaseService setUpDatabaseServiceMock;

    @BeforeEach
    public void setup() {
        final City city = new City();
        city.setName("Wrocław");
        final City savedCity = testEm.persist(city);
        validTestBuddy = Buddy.builder()
                .username("bestBuddy")
                .email("test@gmail.com")
                .name("Anna")
                .city(savedCity)
                .lastName("Kowalska")
                .password("annaKowalska")
                .books(new HashSet<>())
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

    @Test
    @Transactional
    public void givenBuddyWithAuthors_whenSeekingBuddyWithAuthors_thenBuddyWithAuthorsFound() {
        //given
        testEm.persist(validTestBuddy);
        testEm.flush();
        testEm.clear();
        //when
        final Buddy buddyFromDb = testObject.findFirstByUsernameIgnoringCase(validTestBuddy.getUsername()).get();
        //then
        assertThat(buddyFromDb, is(validTestBuddy));
    }
}
