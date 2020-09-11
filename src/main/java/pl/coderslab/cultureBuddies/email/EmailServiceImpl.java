package pl.coderslab.cultureBuddies.email;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {
    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;

    @Override
    public void sendHTMLEmail(String name, String email) throws MessagingException {
        final Context thymeleafContext = new Context();
        thymeleafContext.setVariable("name", name);
        final String emailText = templateEngine.process("/email/email.html", thymeleafContext);

        final MimeMessage mimeMessage = mailSender.createMimeMessage();
        final MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

        helper.setFrom("noreplyDominika@gmail.com");
        helper.setTo(email);
        helper.setSubject("Welcome Buddy!");
        helper.setText(emailText, true);
        ClassPathResource image = new ClassPathResource("/static/pictures/logo1.png");
        helper.addInline("logo", image);
        mailSender.send(mimeMessage);
    }
}
