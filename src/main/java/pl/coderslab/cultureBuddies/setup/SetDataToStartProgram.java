package pl.coderslab.cultureBuddies.setup;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import pl.coderslab.cultureBuddies.exceptions.InvalidDataFromExternalServiceException;
import pl.coderslab.cultureBuddies.exceptions.NotExistingRecordException;

import java.io.IOException;

@Component
@Slf4j
@RequiredArgsConstructor
public class SetDataToStartProgram implements ApplicationRunner {

    private final SetupDatabaseServiceImp setupDatabaseService;

    @Override
    public void run(ApplicationArguments args) throws InvalidDataFromExternalServiceException, IOException, NotExistingRecordException {
        setupDatabaseService.setStartingData();
    }


}
