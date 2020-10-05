package pl.coderslab.cultureBuddies.email;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.mail.Session;
import javax.mail.internet.MimeMessage;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
class EmailServiceTest {

    @Autowired
    private EmailService emailService;
    @MockBean
    private JavaMailSender mailSender;

    @Test
    public void whenMailSend_ThenNoErrors() throws Exception {
        //given
        String email = "email@test";
        String username = "BestBuddy";
        MimeMessage mimeMessage = new MimeMessage((Session) null);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        //when
        emailService.sendHTMLEmail(username, email);
        //then
        verify(mailSender).send(mimeMessage);
    }

}