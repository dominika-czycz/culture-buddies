package pl.coderslab.cultureBuddies.googleapis;

import pl.coderslab.cultureBuddies.exceptions.InvalidDataFromExternalRestApiException;
import pl.coderslab.cultureBuddies.exceptions.NotExistingRecordException;
import pl.coderslab.cultureBuddies.googleapis.restModel.BookFromGoogle;

import java.util.List;

public interface RestBooksService {
    List<BookFromGoogle> getGoogleBooksListByTitle(String title) throws NotExistingRecordException;

    BookFromGoogle getGoogleBookByIsbnOrTitle(String isbn, String title) throws NotExistingRecordException;
}
