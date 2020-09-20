package pl.coderslab.cultureBuddies.googleapis;

import javassist.tools.web.BadHttpRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import pl.coderslab.cultureBuddies.exceptions.NotExistingRecordException;
import pl.coderslab.cultureBuddies.googleapis.restModel.BookFromGoogle;

import java.io.UnsupportedEncodingException;
import java.util.List;

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
    private String notExistingTitle;


    @BeforeEach
    public void setUp() {
        existingTitle = "Nineteen Eighty-Four";
        notExistingTitle = "868686868999999999999999999999999999999999968686868686876868686";
    }

    @Test
    public void givenBookTitle_whenLookingForBookWithTitle_thenBooksFound() throws NotExistingRecordException, BadHttpRequest, UnsupportedEncodingException {
        //when
        final List<BookFromGoogle> googleBooksListByTitle = testObj.getGoogleBooksList(existingTitle, "", 0);
        //then
        assertNotNull(googleBooksListByTitle);
        assertThat(googleBooksListByTitle.size(), not(0));
    }

    @Test
    public void givenNotExistingTitle_whenLookingForBook_thenNonExistingRecordException() {
        //when, then
        assertThrows(NotExistingRecordException.class,
                () -> testObj.getGoogleBooksList(notExistingTitle, "", 0));
    }

}