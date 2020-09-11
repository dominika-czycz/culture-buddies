package pl.coderslab.cultureBuddies.buddies;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
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
    private static final String RESOURCE_NAME = "static/pictures/buddyPictures";

    @Override
    public boolean save(MultipartFile profilePicture, Buddy buddy) throws IOException {
        if (profilePicture == null || profilePicture.isEmpty()) {
            return false;
        }
        String fileName = savePicture(profilePicture, buddy);
        buddy.setPictureUrl(fileName);
        return true;
    }

    private String savePicture(MultipartFile profilePicture, Buddy buddy) throws IOException {
        log.info("Preparing to save profile picture...");
        final String extension = FilenameUtils.getExtension(profilePicture.getOriginalFilename());
        Path pathToPicture = getPathToPicture(buddy, extension);
        log.debug("Profile picture path: {}", pathToPicture);
        log.info("Saving profile picture...");
        Files.copy(profilePicture.getInputStream(), pathToPicture, StandardCopyOption.REPLACE_EXISTING);
        String fileName = getFileName(buddy, extension);
        log.debug("Picture {} has been saved", fileName);
        return fileName;
    }

    private Path getPathToPicture(Buddy buddy, String extension) {
        String dir = getPathToDirectory();
        String fileName = getFileName(buddy, extension);
        return Paths.get(dir + "/" + fileName);
    }

    private String getFileName(Buddy buddy, String extension) {
        return StringUtils.cleanPath(buddy.getUsername() + "." + extension);
    }

    private String getPathToDirectory() {
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(Objects.requireNonNull(classLoader.getResource(RESOURCE_NAME)).getFile());
        return file.getAbsolutePath();
    }
}
