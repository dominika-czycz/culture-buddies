package pl.coderslab.cultureBuddies.email;

import javax.mail.MessagingException;

public interface EmailService {
    void sendHTMLEmail(String name, String email) throws MessagingException;
}
