package pl.coderslab.cultureBuddies.buddies;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;

import java.util.Optional;


public class BuddyConverter implements Converter<String, Buddy> {
    private BuddyRepository buddyRepository;

    @Autowired
    public void setBuddyRepository(BuddyRepository buddyRepository) {
        this.buddyRepository = buddyRepository;
    }

    @Override
    public Buddy convert(String s) {
        final long id = Long.parseLong(s);
        final Optional<Buddy> buddy = buddyRepository.findById(id);
        if (buddy.isEmpty()) {
            throw new IllegalStateException("Buddy does not exist");
        }
        return buddy.get();
    }
}
