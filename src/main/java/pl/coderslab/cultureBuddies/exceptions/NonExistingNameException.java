package pl.coderslab.cultureBuddies.exceptions;


import java.util.function.Supplier;

public class NonExistingNameException extends Exception implements Supplier<NonExistingNameException> {
    public NonExistingNameException(String message) {
        super(message);
    }


    @Override
    public NonExistingNameException get() {
        return this;
    }
}
