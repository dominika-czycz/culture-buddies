package pl.coderslab.cultureBuddies.googleapis.restModel;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VolumeInfo {
    private String title;
    private String[] authors;
    private String description;
    private ImageLinks imageLinks;
    private IndustryIdentifier[] industryIdentifiers;

}
