package pl.coderslab.cultureBuddies.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import pl.coderslab.cultureBuddies.buddies.Buddy;
import pl.coderslab.cultureBuddies.buddies.BuddyRepository;
import pl.coderslab.cultureBuddies.buddies.BuddyService;
import pl.coderslab.cultureBuddies.city.City;
import pl.coderslab.cultureBuddies.city.CityRepository;
import pl.coderslab.cultureBuddies.email.EmailService;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(RegisterController.class)
@WithAnonymousUser
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class RegisterControllerTest {
    private Buddy unsavedBuddy;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private CityRepository cityRepository;
    @MockBean
    private BuddyRepository buddyRepository;
    @MockBean
    private BuddyService buddyServiceMock;
    @MockBean
    private EmailService emailServiceMock;


    @BeforeEach
    public void setUp() {
        unsavedBuddy = Buddy.builder()
                .username("bestBuddy")
                .email("test@gmail.com")
                .name("Anna")
                .city(new City(1L, "Wrocław"))
                .lastName("Kowalska")
                .password("annaKowalska")
                .build();
    }

    @Test
    public void whenRegisterUrl_thenRegisterView() throws Exception {
        //when, then
        mockMvc.perform(get("/register"))
                .andExpect(status().isOk())
                .andExpect(view().name("register"))
                .andExpect(model().attribute("buddy", new Buddy()));
    }

    @Test
    public void whenPostValidBuddy_thenSaved() throws Exception {
        //when, then
        when(buddyServiceMock.save(null, unsavedBuddy)).thenReturn(true);
        mockMvc.perform(post("/register").with(csrf())
                .flashAttr("buddy", unsavedBuddy)
                .param("repeatedPassword", unsavedBuddy.getPassword()))
                .andExpect(redirectedUrl("/"));
        verify(buddyServiceMock).save(null, unsavedBuddy);
        verify(emailServiceMock).sendHTMLEmail(unsavedBuddy.getName(), unsavedBuddy.getEmail());

    }

    @Test
    public void whenPostEmptyBuddy_thenValidationFails() throws Exception {
        //given
        final Buddy emptyBuddy = new Buddy();
        //when, then
        mockMvc.perform(post("/register").with(csrf())
                .param("repeatedPassword", "password")
                .flashAttr("buddy", emptyBuddy))
                .andExpect(status().isOk())
                .andExpect(model().errorCount(6))
                .andExpect(model().attributeHasFieldErrors("buddy",
                        "name", "lastName", "email", "username",
                        "password", "city"));
        verify(buddyServiceMock, atMost(0)).save(null, emptyBuddy);
        verify(emailServiceMock, atMost(0)).sendHTMLEmail(emptyBuddy.getName(), emptyBuddy.getEmail());

    }

    @Test
    public void whenPostBuddyWithInvalidEmail_thenValidationFails() throws Exception {
        //given
        final Buddy unsavedWithInvalidEmail = unsavedBuddy.toBuilder().email("nonExistingEmail").build();
        //when, then
        mockMvc.perform(post("/register").with(csrf())
                .flashAttr("buddy", unsavedWithInvalidEmail)
                .param("repeatedPassword", unsavedWithInvalidEmail.getPassword()))
                .andExpect(status().isOk())
                .andExpect(model().errorCount(1))
                .andExpect(model().attributeHasFieldErrors("buddy",
                        "email"));
        verify(buddyServiceMock, atMost(0)).save(null, unsavedWithInvalidEmail);
        verify(emailServiceMock, atMost(0)).sendHTMLEmail(unsavedBuddy.getName(), unsavedBuddy.getEmail());
    }

    @Test
    public void whenPostProfilePicture_thenBuddyWithProfilePictureSaved() throws Exception {
        //given
        MockMultipartFile profilePicture = new MockMultipartFile("profilePicture", "myPicture.jpg", "image/jpeg", "some profile picture".getBytes());
        when(buddyServiceMock.save(profilePicture, unsavedBuddy)).thenReturn(true);
        //when, then
        mockMvc.perform(multipart("/register")
                .file(profilePicture).with(csrf())
                .flashAttr("buddy", unsavedBuddy)
                .param("repeatedPassword", unsavedBuddy.getPassword()))
                .andExpect(redirectedUrl("/"));
        verify(buddyServiceMock).save(profilePicture, unsavedBuddy);
        verify(emailServiceMock).sendHTMLEmail(unsavedBuddy.getName(), unsavedBuddy.getEmail());
    }

    @Test
    public void whenNotUniqueUsername_thenErrorMessageInModel() throws Exception {
        String notUniqueUsername = "notUniqueUsername";
        final Buddy notUniqueBuddy = unsavedBuddy.toBuilder().username(notUniqueUsername).build();
        when(buddyServiceMock.save(null, notUniqueBuddy)).thenReturn(false);
        mockMvc.perform(post("/register").with(csrf())
                .flashAttr("buddy", notUniqueBuddy)
                .param("repeatedPassword", notUniqueBuddy.getPassword()))
                .andExpect(status().isOk())
                .andExpect(view().name("register"))
                .andExpect(model().attribute("errorMessage", "Username is not unique."));
        verify(emailServiceMock, atMost(0)).sendHTMLEmail(unsavedBuddy.getName(), unsavedBuddy.getEmail());

    }
}