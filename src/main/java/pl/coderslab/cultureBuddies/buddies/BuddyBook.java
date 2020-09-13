package pl.coderslab.cultureBuddies.buddies;

import lombok.*;
import org.hibernate.validator.constraints.Range;
import pl.coderslab.cultureBuddies.books.Book;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "buddies_books")
@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
@Builder(toBuilder = true)
@EqualsAndHashCode(of = "id")
@ToString
public class BuddyBook {
    @EmbeddedId
    private BuddyBookId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("buddyId")
    private Buddy buddy;

    @ManyToOne
    @MapsId("bookId")
    private Book book;
    private String comment;
    @NotNull
    @Range(min = 0, max = 10)
    private Integer rate;

    public BuddyBook(Buddy buddy, Book book) {
        this.buddy = buddy;
        this.book = book;
    }

}

