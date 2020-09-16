package pl.coderslab.cultureBuddies.author;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.coderslab.cultureBuddies.buddies.Buddy;
import pl.coderslab.cultureBuddies.buddies.BuddyService;
import pl.coderslab.cultureBuddies.exceptions.NotExistingRecordException;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthorServiceImpl implements AuthorService {
    private final AuthorRepository authorRepository;
    private final BuddyService buddyService;

    @Override
    public List<Author> getOrderedAuthorsListOfPrincipalUser() throws NotExistingRecordException {
        final Buddy principal = buddyService.findPrincipal();
        return authorRepository.findByBuddiesOrderByLastName(principal);
    }
}
