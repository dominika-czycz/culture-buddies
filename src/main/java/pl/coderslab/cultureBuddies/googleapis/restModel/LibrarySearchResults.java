package pl.coderslab.cultureBuddies.googleapis.restModel;

import lombok.Data;

@Data
public class LibrarySearchResults {
    private BookFromGoogle[] items;
}
