package pl.coderslab.cultureBuddies.buddies;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;
@ExtendWith(SpringExtension.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class BuddyBookRepositoryTest {
    @Autowired
    private BuddyBookRepository testObject;
    @Autowired
    private TestEntityManager testEm;

    public void givenBuddyIdAndAuthorId_whenLookingForOpinion_ThenOpinionFound(){

    
    }


}