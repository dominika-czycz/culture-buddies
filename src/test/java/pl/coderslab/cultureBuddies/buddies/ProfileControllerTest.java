package pl.coderslab.cultureBuddies.buddies;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import pl.coderslab.cultureBuddies.city.CityRepository;
import pl.coderslab.cultureBuddies.email.EmailService;
import pl.coderslab.cultureBuddies.events.EventService;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(ProfileController.class)
@ExtendWith(SpringExtension.class)
@WithMockUser
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ProfileControllerTest {
    @Autowired
    MockMvc mockMvc;
    @MockBean
    private BuddyService buddyServiceMock;
    @MockBean
    private EmailService emailServiceMock;
    @MockBean
    private CityRepository cityRepository;
    @MockBean
    private BuddyRepository buddyRepository;
    @MockBean
    private EventService eventService;

    @Spy
    private Buddy buddy;
    private final String username = "testUsername";

    @Test
    public void whenAppUsernameUrl_thenBuddyWithProfileUrlFieldInModelAttributes_AndProfileView() throws Exception {
        //given
        buddy.setUsername(username);
        buddy.setEmail("test@test");
        when(buddyServiceMock.findByUsername(username)).thenReturn(buddy);

        //when
        mockMvc.perform(get("/app/board/" + username))
                .andExpect(status().isOk())
                .andExpect(view().name("buddy/profile"))
                .andExpect(model().attribute("buddy", buddy));
        //then
        verify(buddyServiceMock).findByUsername(username);
    }

    @Test
    public void whenAppProfileUrl_thenRedirectAppUsernameUrl() throws Exception {
        when(buddyServiceMock.getPrincipalUsername()).thenReturn("testUsername");
        mockMvc.perform(get("/app/board/profile"))
                .andExpect(status().is(302))
                .andExpect(redirectedUrl("/app/board/" + username));
    }

    @Test
    public void whenGetChangePassword_thenChangePasswordView() throws Exception {
        mockMvc.perform(get("/app/board/changePassword"))
                .andExpect(status().isOk())
                .andExpect(view().name("/buddy/changePassword"));
    }

    @Test
    public void givenTheSamePasswordAndRepeatedPassword_whenPostChangePasswordUrl_thenPasswordIsUpdated() throws Exception {
        final String password = "newPassword";
        final String repeatedPassword = "newPassword";
        mockMvc.perform(post("/app/board/changePassword").with(csrf())
                .param("password", password)
                .param("repeatedPassword", repeatedPassword))
                .andExpect(status().is(302))
                .andExpect(redirectedUrl("/app/board/profile/"));
        verify(buddyServiceMock).updatePassword(password);
    }

}