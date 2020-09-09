package pl.coderslab.cultureBuddies;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import pl.coderslab.cultureBuddies.buddies.Buddy;
import pl.coderslab.cultureBuddies.buddies.BuddyService;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@ExtendWith(SpringExtension.class)
@WebMvcTest(RegisterController.class)
class RegisterControllerTest {
    private Buddy unsavedBuddy;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private BuddyService buddyService;


    @BeforeEach
    public void beforeEachTests() {
        unsavedBuddy = Buddy.builder()
                .username("bestBuddy")
                .email("test@gmail.com")
                .name("Anna")
                .lastName("Kowalska")
                .password("annaKowalska")
                .city("Wrocław")
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
        mockMvc.perform(post("/register")
                .param("name", unsavedBuddy.getName())
                .param("email", unsavedBuddy.getEmail())
                .param("lastName", unsavedBuddy.getLastName())
                .param("city", unsavedBuddy.getCity())
                .param("username", unsavedBuddy.getUsername())
                .param("password", unsavedBuddy.getPassword()))
                .andExpect(redirectedUrl("/app/" + unsavedBuddy.getUsername()));
        verify(buddyService, atLeastOnce()).save(null, unsavedBuddy);
    }

    @Test
    public void whenPostEmptyBuddy_thenValidationFails() throws Exception {
        //given
        final Buddy emptyBuddy = new Buddy();
        //when, then
        mockMvc.perform(post("/register"))
                .andExpect(status().isOk())
                .andExpect(model().errorCount(6))
                .andExpect(model().attributeHasFieldErrors("buddy",
                        "name", "lastName", "email", "username",
                        "password", "city"));
        verify(buddyService, atMost(0)).save(null, emptyBuddy);
    }

    @Test
    public void whenPostBuddyWithInvalidEmail_thenValidationFails() throws Exception {
        //given
        final Buddy unsavedWithInvalidEmail = unsavedBuddy.toBuilder().email("nonExistingEmail").build();
        //when, then
        mockMvc.perform(post("/register")
                .param("name", unsavedWithInvalidEmail.getName())
                .param("email", unsavedWithInvalidEmail.getEmail())
                .param("lastName", unsavedWithInvalidEmail.getLastName())
                .param("city", unsavedWithInvalidEmail.getCity())
                .param("username", unsavedWithInvalidEmail.getUsername())
                .param("password", unsavedWithInvalidEmail.getPassword()))
                .andExpect(status().isOk())
                .andExpect(model().errorCount(1))
                .andExpect(model().attributeHasFieldErrors("buddy",
                        "email"));
        verify(buddyService, atMost(0)).save(null, unsavedWithInvalidEmail);
    }

    @Test
    public void whenPostProfilePicture_thenBuddyWithProfilePictureSaved() throws Exception {
        //given
        MockMultipartFile profilePicture = new MockMultipartFile("profilePicture", "myPicture.jpg", "image/jpeg", "some profile picture".getBytes());
        //when, then
        mockMvc.perform(multipart("/register")
                .file(profilePicture)
                .param("name", unsavedBuddy.getName())
                .param("email", unsavedBuddy.getEmail())
                .param("lastName", unsavedBuddy.getLastName())
                .param("city", unsavedBuddy.getCity())
                .param("username", unsavedBuddy.getUsername())
                .param("password", unsavedBuddy.getPassword()))
                .andExpect(redirectedUrl("/app/" + unsavedBuddy.getUsername()));
        verify(buddyService, atLeastOnce()).save(profilePicture, unsavedBuddy);
    }
}