package pl.coderslab.cultureBuddies.setup;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import pl.coderslab.cultureBuddies.exceptions.InvalidDataFromExternalServiceException;
import pl.coderslab.cultureBuddies.exceptions.NotExistingRecordException;

import java.io.IOException;

@Controller
@Slf4j
@RequiredArgsConstructor
public class SetUpController {
    private final SetUpDatabaseService setupDatabaseService;

    @RequestMapping("/restoreDatabase")
    public String restartDbToDefault() throws InvalidDataFromExternalServiceException, IOException, NotExistingRecordException {
        setupDatabaseService.restoreDatabase();
        return "redirect:/";
    }
}
