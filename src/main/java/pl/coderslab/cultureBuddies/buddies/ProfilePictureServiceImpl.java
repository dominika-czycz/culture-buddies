package pl.coderslab.cultureBuddies.buddies;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProfilePictureServiceImpl implements PictureService {

    @Override
    public void save(MultipartFile profilePicture, Buddy buddy) throws IOException {
        log.info("Preparing to save profile picture...");
        if (profilePicture != null && !profilePicture.isEmpty()) {
            buddy.setPicture(profilePicture.getBytes());
            log.debug("New profile picture has been set to buddy {}", buddy);
        }else{
            log.info("Profile picture is (null or empty.)");
        }
    }

    @Override
    public String getPicture(Buddy buddy) {
        final byte[] file = buddy.getPicture();
        String picture = "";
        if (file != null && file.length > 0) {
            picture = Base64.getEncoder().encodeToString(file);
        }
        return picture;
    }
}
