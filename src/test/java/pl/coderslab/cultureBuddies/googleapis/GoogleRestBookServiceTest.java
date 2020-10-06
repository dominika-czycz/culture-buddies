package pl.coderslab.cultureBuddies.googleapis;

import javassist.tools.web.BadHttpRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.RestTemplate;
import pl.coderslab.cultureBuddies.exceptions.NotExistingRecordException;
import pl.coderslab.cultureBuddies.googleapis.restModel.BookFromGoogle;
import pl.coderslab.cultureBuddies.googleapis.restModel.NumberOfResults;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Stream;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@SpringBootTest
@ExtendWith(SpringExtension.class)
class GoogleRestBookServiceTest {
    @Autowired
    RestBooksService testObj;


    private static final String EXISTING_TITLE = "Nineteen Eighty-Four";
    private static final String NOT_EXISTING_TITLE = "868686868999999999999999999999999999999999968686868686876868686";
    private static final Integer RESULTS_ON_PAGE = 10;


    @Test
    void givenBookTitle_whenLookingForBookWithTitle_thenBooksAreFound() throws NotExistingRecordException, BadHttpRequest {
        //when
        final List<BookFromGoogle> googleBooksListByTitle = testObj.getGoogleBooksList(EXISTING_TITLE, "", 0);

        //then
        assertNotNull(googleBooksListByTitle);
        assertThat(googleBooksListByTitle.size(), not(0));
    }

    @Test
    void givenNotExistingTitle_whenLookingForBook_thenNonExistingRecordExceptionIsThrown() {
        //when, then
        assertThrows(NotExistingRecordException.class,
                () -> testObj.getGoogleBooksList(NOT_EXISTING_TITLE, "", 0));
    }

    @ParameterizedTest
    @MethodSource("prepareDataForCountingMaxPage")
    void givenTitleAndAuthor_whenSearchingMaxNumResultsPage_thenMaxNumIsFound(
            int totalItems, int expected)
            throws NotExistingRecordException {
        //given
        final RestTemplate restTemplateMock = Mockito.mock(RestTemplate.class);
        final GoogleRestBookService testObj = new GoogleRestBookService(restTemplateMock);
        String authorName = "Kowalski";
        final NumberOfResults numberOfResults = new NumberOfResults();
        numberOfResults.setTotalItems(totalItems);
        final String uri = createUri(authorName);
        when(restTemplateMock.getForObject(uri, NumberOfResults.class)).thenReturn(numberOfResults);
        //when
        final int maxPage = testObj.countMaxPage(EXISTING_TITLE, authorName);
        //then
        assertThat(maxPage, is(expected));
    }

    private static Stream<Arguments> prepareDataForCountingMaxPage() {
        return Stream.of(
                Arguments.of(1, 0),
                Arguments.of(100, 9),
                Arguments.of(101, 10));
    }

    private String createUri(String author) {
        final String normalizedTitle = LettersUtils.replaceSpecialLetters(GoogleRestBookServiceTest.EXISTING_TITLE);
        final String normalizedAuthor = LettersUtils.replaceSpecialLetters(author);
        return "https://www.googleapis.com/books/v1/volumes?orderBy=relevance&" +
                "startIndex=" + 0 + "&maxResults=" + RESULTS_ON_PAGE + "&printType=books&q="
                + URLEncoder.encode(normalizedTitle, StandardCharsets.UTF_8)
                + "+inauthor:" + URLEncoder.encode(normalizedAuthor, StandardCharsets.UTF_8);
    }


}