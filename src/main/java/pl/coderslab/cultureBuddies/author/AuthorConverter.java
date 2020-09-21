package pl.coderslab.cultureBuddies.author;

import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import pl.coderslab.cultureBuddies.exceptions.NotExistingRecordException;

import java.util.Optional;

@Component
public class AuthorConverter implements Converter<String, Author> {
    private AuthorRepository authorRepository;


    @SneakyThrows
    @Override
    public Author convert(String s) {
        final long id = Long.parseLong(s);
        final Optional<Author> author = authorRepository.findById(id);
        if (author.isEmpty()) {
            throw new NotExistingRecordException("Author does not exist");
        }
        return author.get();
    }

    @Autowired
    public void setAuthorDao(AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
    }
}
