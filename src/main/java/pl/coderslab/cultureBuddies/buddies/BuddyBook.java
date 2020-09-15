package pl.coderslab.cultureBuddies.buddies;

import lombok.*;
import org.hibernate.validator.constraints.Range;
import org.springframework.stereotype.Service;
import pl.coderslab.cultureBuddies.books.Book;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "buddies_books")
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = {"id"})
@Getter
@Setter
@Builder
public class BuddyBook {
    @EmbeddedId
    private BuddyBookId id = new BuddyBookId();

    @ManyToOne
    @MapsId("buddyId")
    private Buddy buddy;

    @ManyToOne
    @MapsId("bookId")
    private Book book;
    private String comment;
    @Range(min = 0, max = 10)
    private Integer rate;

    public BuddyBook(Buddy buddy, Book book) {
        this.buddy = buddy;
        this.book = book;
    }

}

