package pl.coderslab.cultureBuddies.buddies;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.util.NumberUtils;

import javax.validation.constraints.NotNull;
import java.util.Optional;


public class BuddyConverter implements Converter<String, Buddy> {
    private BuddyRepository buddyRepository;

    @Autowired
    public void setBuddyRepository(BuddyRepository buddyRepository) {
        this.buddyRepository = buddyRepository;
    }

    @Override
    public Buddy convert(String s) {
        final long id = NumberUtils.parseNumber(s, Long.class);
        final Optional<Buddy> buddy = buddyRepository.findById(id);
        if (buddy.isEmpty()) {
            throw new IllegalStateException("Buddy does not exist");
        }
        return buddy.get();
    }
}
