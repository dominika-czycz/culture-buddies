package pl.coderslab.cultureBuddies.googleapis;

import pl.coderslab.cultureBuddies.googleapis.restModel.GoogleLibrarySearchResults;
import pl.coderslab.cultureBuddies.googleapis.restModel.RestLibrarySearchResults;

public interface RestBooksService<T extends RestLibrarySearchResults> {
    public T getSearchResults(String title);
}
