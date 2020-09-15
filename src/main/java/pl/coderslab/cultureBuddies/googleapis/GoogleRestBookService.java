package pl.coderslab.cultureBuddies.googleapis;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import pl.coderslab.cultureBuddies.exceptions.InvalidDataFromExternalRestApiException;
import pl.coderslab.cultureBuddies.exceptions.NotExistingRecordException;
import pl.coderslab.cultureBuddies.googleapis.restModel.BookFromGoogle;
import pl.coderslab.cultureBuddies.googleapis.restModel.LibrarySearchResults;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class GoogleRestBookService implements RestBooksService {
    private final RestTemplate template;

    @Override
    public List<BookFromGoogle> getGoogleBooksListByTitle(String title) throws NotExistingRecordException {
        final LibrarySearchResults searchResults = getSearchResultsByTitle(title);
        final List<BookFromGoogle> booksList = Arrays.stream(searchResults.getItems()).collect(Collectors.toList());
        if (booksList.isEmpty()) {
            throw new NotExistingRecordException("No match to the title was found.");
        }
        return booksList;
    }

    @Override
    public BookFromGoogle getGoogleBookByIsbn(String isbn) throws NotExistingRecordException, InvalidDataFromExternalRestApiException {
        final LibrarySearchResults resultFromGoogle = getSearchResultsByIsbn(isbn);
        if(resultFromGoogle==null){
            throw new InvalidDataFromExternalRestApiException("External api error! ");
        }
        final BookFromGoogle bookFromGoogle = resultFromGoogle.getItems()[0];
        if (bookFromGoogle == null) {
            throw new NotExistingRecordException("Book does not exist in google book service");
        }
        return bookFromGoogle;
    }

    public LibrarySearchResults getSearchResultsByTitle(String title) {
        String uri = "https://www.googleapis.com/books/v1/volumes?orderBy=relevance&printType=books&q=title:" + title;
        return template.getForObject(uri, LibrarySearchResults.class);
    }

    private LibrarySearchResults getSearchResultsByIsbn(String isbn) {
        String uri = "https://www.googleapis.com/books/v1/volumes?orderBy=relevance&printType=books&q=isbn:" + isbn;
        return template.getForObject(uri, LibrarySearchResults.class);
    }


}
