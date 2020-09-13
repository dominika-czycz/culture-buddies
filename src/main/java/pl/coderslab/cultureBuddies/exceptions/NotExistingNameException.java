package pl.coderslab.cultureBuddies.exceptions;


import java.util.function.Supplier;

public class NotExistingNameException extends Exception implements Supplier<NotExistingNameException> {
    public NotExistingNameException(String message) {
        super(message);
    }


    @Override
    public NotExistingNameException get() {
        return this;
    }
}
