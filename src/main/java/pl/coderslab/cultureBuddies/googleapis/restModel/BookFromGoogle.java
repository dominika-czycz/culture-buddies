package pl.coderslab.cultureBuddies.googleapis.restModel;

import lombok.Data;

@Data
public class BookFromGoogle {
    private String id;
    private String title;
    private String author;
    private VolumeInfo volumeInfo;
}
