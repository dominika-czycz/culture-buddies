package pl.coderslab.cultureBuddies.googleapis;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import pl.coderslab.cultureBuddies.exceptions.NotExistingRecordException;
import pl.coderslab.cultureBuddies.googleapis.restModel.BookFromGoogle;
import pl.coderslab.cultureBuddies.googleapis.restModel.LibrarySearchResults;

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
        final List<BookFromGoogle> booksList = Arrays.stream(searchResults.getItems()).collect(Collectors.toList());
        final List<BookFromGoogle> checkedList = checkBooks(booksList);
        if (checkedList.isEmpty()) {
            throw new NotExistingRecordException("No match to the title was found.");
        }
        return checkedList;
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

    @Override
    public BookFromGoogle getGoogleBookByIsbnOrTitle(String isbn, String title) throws NotExistingRecordException {
        final LibrarySearchResults resultFromGoogle = getSearchResultsByIsbn(isbn);
        BookFromGoogle foundBook;
        if (resultFromGoogle == null || resultFromGoogle.getItems() == null || resultFromGoogle.getItems().length == 0) {
            final List<BookFromGoogle> booksFromGoogle = Arrays.asList(getSearchResultsByTitle(title).getItems());
            final List<BookFromGoogle> booksList = checkBooks(booksFromGoogle);
            log.debug("List size {}.",booksList.size());
            final Optional<BookFromGoogle> bookFromList = booksList.stream()
                    .filter(b -> b.getVolumeInfo().getIndustryIdentifiers()[0].getIdentifier().equals(isbn))
                    .findFirst();
            foundBook = bookFromList.orElseThrow(new NotExistingRecordException("Book does not exist in google book service"));
        } else {
            foundBook = resultFromGoogle.getItems()[0];
        }
        return foundBook;
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
