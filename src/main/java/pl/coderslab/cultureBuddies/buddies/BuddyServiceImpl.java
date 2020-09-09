package pl.coderslab.cultureBuddies.buddies;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@Slf4j
@RequiredArgsConstructor
public class BuddyServiceImpl implements BuddyService {
    private final BuddyRepository buddyRepository;
    private final PictureService pictureService;

    @Override
    public void save(MultipartFile profilePicture, Buddy buddy) throws IOException {
        if (!(profilePicture == null)) {
            pictureService.savePicture(profilePicture, buddy);
        }
        final Buddy savedBuddy = buddyRepository.save(buddy);
        log.debug("Entity {} been saved", savedBuddy);
    }

}
