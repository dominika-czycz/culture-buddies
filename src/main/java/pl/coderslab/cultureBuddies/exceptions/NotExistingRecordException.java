package pl.coderslab.cultureBuddies.exceptions;


import java.util.function.Supplier;

public class NotExistingRecordException extends Exception implements Supplier<NotExistingRecordException> {
    public NotExistingRecordException(String message) {
        super(message);
    }


    @Override
    public NotExistingRecordException get() {
        return this;
    }
}
