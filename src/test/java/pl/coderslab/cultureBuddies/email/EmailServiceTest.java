package pl.coderslab.cultureBuddies.email;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import pl.coderslab.cultureBuddies.buddies.Buddy;

@SpringBootTest
@ExtendWith(SpringExtension.class)
class EmailServiceTest {
    @Autowired
    private EmailService emailService;
    @Spy
    private Buddy buddy;

    @Test
    public void whenMailSend_ThenNoErrors() throws Exception {
        buddy.setEmail("dominika.czycz@gmail.com");
        buddy.setName("test");
        emailService.sendHTMLEmail(buddy.getName(), buddy.getEmail());
    }

}