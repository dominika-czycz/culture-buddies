package pl.coderslab.cultureBuddies.exceptions;

import java.util.function.Supplier;

public class RelationshipAlreadyCreatedException extends Exception implements Supplier<RelationshipAlreadyCreatedException> {
    public RelationshipAlreadyCreatedException(String message) {
        super(message);
    }

    @Override
    public RelationshipAlreadyCreatedException get() {
        return this;
    }
}
