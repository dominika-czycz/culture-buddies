package pl.coderslab.cultureBuddies.setup;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import pl.coderslab.cultureBuddies.buddies.BuddyConverter;
import pl.coderslab.cultureBuddies.city.CityConverter;

import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(SetUpController.class)
@WithAnonymousUser
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class SetUpControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private SetUpDatabaseService setUpDatabaseServiceMock;
    @MockBean
    private CityConverter cityConverter;
    @MockBean
    private BuddyConverter buddyConverter;

    @Test
    public void whenGetRestartDatabase_thenDatabaseIsRestartedAndRedirectHome() throws Exception {
        //when, then
        mockMvc.perform(get("/restoreDatabase"))
                .andExpect(status().is(302))
                .andExpect(redirectedUrl("/"));
        verify(setUpDatabaseServiceMock).restoreDatabase();
    }
}