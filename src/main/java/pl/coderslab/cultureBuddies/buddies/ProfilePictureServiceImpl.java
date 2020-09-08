package pl.coderslab.cultureBuddies.buddies;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProfilePictureServiceImpl implements PictureService {
    @Override
    public String savePicture(MultipartFile profilePicture, Buddy buddy) throws IOException {
        log.info("Saving profile picture");

        String dir = getPathToDirectory();
        String originalFilename = StringUtils.cleanPath(Objects.requireNonNull(profilePicture.getOriginalFilename()));
        Path path = Paths.get(dir + "/" + originalFilename);
        Files.copy(profilePicture.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

        buddy.setPictureUrl(originalFilename);
        log.debug("Picture {} saved", originalFilename);
        return originalFilename;
    }

    private String getPathToDirectory() {
        ClassLoader classLoader = getClass().getClassLoader();
        String resourceName = "buddyPictures";
        File file = new File(Objects.requireNonNull(classLoader.getResource(resourceName)).getFile());
        return file.getAbsolutePath();
    }
}
