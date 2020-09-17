package pl.coderslab.cultureBuddies.googleapis;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import pl.coderslab.cultureBuddies.exceptions.NotExistingRecordException;
import pl.coderslab.cultureBuddies.googleapis.restModel.BookFromGoogle;
import pl.coderslab.cultureBuddies.googleapis.restModel.LibrarySearchResults;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class GoogleRestBookService implements RestBooksService {
    private final RestTemplate template;

    @Override
    public List<BookFromGoogle> getGoogleBooksListByTitle(String title) throws NotExistingRecordException {
        final LibrarySearchResults searchResults = getSearchResultsByTitle(title);
        String message = "No match to the title was found.";
        if (searchResults == null || searchResults.getItems() == null) {
            throw new NotExistingRecordException(message);
        }
        final List<BookFromGoogle> booksList = Arrays.stream(searchResults.getItems()).collect(Collectors.toList());
        final List<BookFromGoogle> checkedList = checkBooks(booksList);
        if (checkedList.isEmpty()) {
            throw new NotExistingRecordException(message);
        }
        return checkedList;
    }

    @Override
    public BookFromGoogle getGoogleBookByIdentifierOrTitle(String isbn, String title) throws NotExistingRecordException {
        final LibrarySearchResults resultFromGoogle = getSearchResultsByIsbn(isbn);
        String message = "Book does not exist in google book service";
        BookFromGoogle foundBook;
        if (resultFromGoogle == null || resultFromGoogle.getItems() == null || resultFromGoogle.getItems().length == 0) {
            final LibrarySearchResults resultsByTitle = getSearchResultsByTitle(title);
            if (resultsByTitle != null && resultsByTitle.getItems() != null) {
                final List<BookFromGoogle> booksFromGoogle = Arrays.asList(resultsByTitle.getItems());
                final List<BookFromGoogle> booksList = checkBooks(booksFromGoogle);
                log.debug("List size {}.", booksList.size());
                final Optional<BookFromGoogle> bookFromList = booksList.stream()
                        .filter(b -> b.getVolumeInfo().getIndustryIdentifiers()[0].getIdentifier().equals(isbn))
                        .findFirst();
                foundBook = bookFromList.orElseThrow(new NotExistingRecordException(message));
            } else {
                throw new NotExistingRecordException(message);
            }
        } else {
            foundBook = resultFromGoogle.getItems()[0];
        }
        return foundBook;
    }

    private LibrarySearchResults getSearchResultsByTitle(String title) {
        String uri = "https://www.googleapis.com/books/v1/volumes?orderBy=relevance&printType=books&q=title:"
                + URLEncoder.encode(title, StandardCharsets.UTF_8);
        return template.getForObject(uri, LibrarySearchResults.class);
    }

    private LibrarySearchResults getSearchResultsByIsbn(String isbn) {
        String uri = "https://www.googleapis.com/books/v1/volumes?orderBy=relevance&printType=books&q=isbn:"
                + URLEncoder.encode(isbn, StandardCharsets.UTF_8);
        return template.getForObject(uri, LibrarySearchResults.class);
    }

    private List<BookFromGoogle> checkBooks(List<BookFromGoogle> booksList) {
        log.info("Checking books list from google...");
        return booksList.stream()
                .filter(b -> b.getVolumeInfo() != null)
                .filter(b -> b.getVolumeInfo().getIndustryIdentifiers() != null)
                .filter(b -> b.getVolumeInfo().getIndustryIdentifiers()[0] != null)
                .filter(b -> b.getVolumeInfo().getIndustryIdentifiers()[0].getIdentifier() != null)
                .filter(b -> b.getVolumeInfo().getAuthors() != null && b.getVolumeInfo().getAuthors().length > 0)
                .collect(Collectors.toList());
    }

}
