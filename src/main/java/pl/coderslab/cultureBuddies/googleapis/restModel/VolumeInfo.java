package pl.coderslab.cultureBuddies.googleapis.restModel;

import lombok.Data;

@Data
public class VolumeInfo {
    private String title;
    private String[] authors;
    private String description;
    private ImageLinks imageLinks;

}
