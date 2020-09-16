package pl.coderslab.cultureBuddies.googleapis;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import pl.coderslab.cultureBuddies.exceptions.NotExistingRecordException;
import pl.coderslab.cultureBuddies.googleapis.restModel.BookFromGoogle;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ExtendWith(SpringExtension.class)
class GoogleRestBookServiceTest {
    @Autowired
    RestBooksService testObj;
    private String existingTitle;
    private String existingIsbn;
    private String notExistingTitle;


    @BeforeEach
    public void setUp() {
        existingTitle = "Nineteen Eighty-Four";
        existingIsbn = "9781849433495";
        notExistingTitle = "868686868999999999999999999999999999999999968686868686876868686";
    }

    @Test
    public void givenBookTitle_whenLookingForBookWithTitle_thenBooksFound() throws NotExistingRecordException {
        //when
        final List<BookFromGoogle> googleBooksListByTitle = testObj.getGoogleBooksListByTitle(existingTitle);
        //then
        assertNotNull(googleBooksListByTitle);
        assertThat(googleBooksListByTitle.size(), not(0));
    }

    @Test
    public void givenNotExistingTitle_whenLookingForBook_thenNonExistingRecordException() {
        //when, then
        assertThrows(NotExistingRecordException.class,
                () -> testObj.getGoogleBooksListByTitle(notExistingTitle));
    }

    @Test
    public void givenExistingIsbn_whenLookingForBook_thenBookFound() throws NotExistingRecordException {
        //when
        final BookFromGoogle book = testObj.getGoogleBookByIdentifierOrTitle(existingIsbn, existingTitle);
        final String actual = book.getVolumeInfo().getIndustryIdentifiers()[0].getIdentifier();
        //then
        assertThat(actual, is(existingIsbn));
    }
    @Test
    public void givenNonExistingIsbnAndNonExistingTitle_whenLookingForBook_thenNonExistingRecordException() throws NotExistingRecordException {
        String notExistingIsbn = "NotExistingISBN";
        assertThrows(NotExistingRecordException.class,
                () -> testObj.getGoogleBookByIdentifierOrTitle(notExistingIsbn, notExistingTitle));
    }



}