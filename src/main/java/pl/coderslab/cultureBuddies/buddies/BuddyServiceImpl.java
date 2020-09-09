package pl.coderslab.cultureBuddies.buddies;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class BuddyServiceImpl implements BuddyService {
    private final BuddyRepository buddyRepository;
    private final PictureService pictureService;

    @Override
    public boolean save(MultipartFile profilePicture, Buddy buddy) throws IOException {
        if (isDuplicate(buddy)) return false;

        pictureService.save(profilePicture, buddy);

        final Buddy savedBuddy = buddyRepository.save(buddy);
        log.debug("Entity {} been saved", savedBuddy);
        return true;
    }

    private boolean isDuplicate(Buddy buddy) {
        final Optional<Buddy> existingBuddyDb = buddyRepository.findFirstByUsernameIgnoringCase(buddy.getUsername());
        log.warn("Not unique username: {}", buddy.getUsername());
        return existingBuddyDb.isPresent();
    }
}
