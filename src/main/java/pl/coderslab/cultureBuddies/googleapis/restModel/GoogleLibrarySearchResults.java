package pl.coderslab.cultureBuddies.googleapis.restModel;

import lombok.Data;

@Data
public class GoogleLibrarySearchResults implements  RestLibrarySearchResults {
    private BookFromGoogle[] items;
}
