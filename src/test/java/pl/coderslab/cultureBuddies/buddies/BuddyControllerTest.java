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

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(BuddyController.class)
@ExtendWith(SpringExtension.class)
@WithMockUser
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class BuddyControllerTest {
    @Autowired
    MockMvc mockMvc;
    @MockBean
    private BuddyService buddyServiceMock;
    @MockBean
    private EmailService emailServiceMock;
    @MockBean
    private CityRepository cityRepository;

    @Spy
    private Buddy buddy;
    private final String username = "testUsername";

    @Test
    public void whenAppUsernameUrl_thenBuddyWithProfileUrlFieldInModelAttributes_AndProfileView() throws Exception {
        //given
        buddy.setUsername(username);
        buddy.setPictureUrl(username + ".jpg");
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
}