package pl.coderslab.cultureBuddies.googleapis;

import javassist.tools.web.BadHttpRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import pl.coderslab.cultureBuddies.exceptions.NotExistingRecordException;
import pl.coderslab.cultureBuddies.googleapis.restModel.BookFromGoogle;
import pl.coderslab.cultureBuddies.googleapis.restModel.LibrarySearchResults;
import pl.coderslab.cultureBuddies.googleapis.restModel.NumberOfResults;

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
    private static final Integer RESULTS_ON_PAGE = 10;

    @Override
    public List<BookFromGoogle> getGoogleBooksList(String title, String author, Integer pageNo)
            throws NotExistingRecordException, BadHttpRequest {
        if (pageNo == null || pageNo < 0) throw new BadHttpRequest();
        final Optional<LibrarySearchResults> rawSearchResults =
                getSearchResultsByAuthorAndTitle(author, title, pageNo);
        final LibrarySearchResults searchResults = rawSearchResults.orElseThrow(new NotExistingRecordException("Nothing matches your search"));
        return prepareResultsList(searchResults);
    }

    private Optional<LibrarySearchResults> getSearchResultsByAuthorAndTitle(String author, String title, Integer pageNo) {
        final int startIndex = pageNo * RESULTS_ON_PAGE;
        final String uri = createUri(title, author, startIndex);
        return Optional.ofNullable(template.getForObject(uri, LibrarySearchResults.class));
    }

    private String createUri(String title, String author, int startIndex) {
        final String normalizedTitle = LettersUtils.replaceSpecialLetters(title);
        final String normalizedAuthor = LettersUtils.replaceSpecialLetters(author);
        final String uri = "https://www.googleapis.com/books/v1/volumes?orderBy=relevance&" +
                "startIndex=" + startIndex + "&maxResults=" + RESULTS_ON_PAGE + "&printType=books&q="
                + URLEncoder.encode(normalizedTitle, StandardCharsets.UTF_8)
                + "+inauthor:" + URLEncoder.encode(normalizedAuthor, StandardCharsets.UTF_8);
        log.debug("Generated Google api uri {}", uri);
        return uri;
    }

    @Override
    public int countMaxPage(String title, String author) throws NotExistingRecordException {
        final String uri = createUri(title, author, 0);
        final NumberOfResults resultsNumber = template.getForObject(uri, NumberOfResults.class);
        if (resultsNumber == null || resultsNumber.getTotalItems() == 0) {
            throw new NotExistingRecordException("Nothing matches your search");
        }
        final Integer totalItems = resultsNumber.getTotalItems();
        final int maxPageNum = totalItems / RESULTS_ON_PAGE;
        final int maxPage = (totalItems % RESULTS_ON_PAGE == 0) ? maxPageNum - 1 : maxPageNum;
        log.debug("total items: {}, max page:  {}",totalItems, maxPage);
        return maxPage;
    }

    private List<BookFromGoogle> prepareResultsList(LibrarySearchResults searchResults) throws NotExistingRecordException {
        String message = "External Api error.";
        if (searchResults == null || searchResults.getItems() == null || searchResults.getItems().length == 0)
            throw new NotExistingRecordException(message);
        final List<BookFromGoogle> booksList = Arrays.stream(searchResults.getItems()).collect(Collectors.toList());
        final List<BookFromGoogle> checkedList = checkBooks(booksList);
        if (checkedList.isEmpty()) throw new NotExistingRecordException(message);
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
}
