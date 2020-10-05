package pl.coderslab.cultureBuddies.setup;

import pl.coderslab.cultureBuddies.exceptions.InvalidDataFromExternalServiceException;
import pl.coderslab.cultureBuddies.exceptions.NotExistingRecordException;

import java.io.IOException;

public interface SetUpDatabaseService {
    void setStartingData() throws InvalidDataFromExternalServiceException, IOException, NotExistingRecordException;

    void restoreDatabase() throws InvalidDataFromExternalServiceException, IOException, NotExistingRecordException;

    void setDataUnchangedByUsers();

    void setExampleUsersData() throws InvalidDataFromExternalServiceException, IOException, NotExistingRecordException;

    void cleanDatabase();
}
