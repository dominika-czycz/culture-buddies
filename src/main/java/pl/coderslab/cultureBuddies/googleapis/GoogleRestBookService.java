package pl.coderslab.cultureBuddies.googleapis;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import pl.coderslab.cultureBuddies.googleapis.restModel.GoogleLibrarySearchResults;

@Service
@Slf4j
@RequiredArgsConstructor
public class GoogleRestBookService implements RestBooksService<GoogleLibrarySearchResults> {
    private final RestTemplate template;

    public GoogleLibrarySearchResults getSearchResults(String title) {
        String uri = "https://www.googleapis.com/books/v1/volumes?q=title:" + title;
        return template.getForObject(uri, GoogleLibrarySearchResults.class);
    }
}
