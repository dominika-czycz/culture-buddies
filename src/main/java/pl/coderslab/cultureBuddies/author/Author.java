package pl.coderslab.cultureBuddies.author;

import lombok.*;
import pl.coderslab.cultureBuddies.books.Book;
import pl.coderslab.cultureBuddies.buddies.Buddy;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "authors")
@Getter
@Setter
@EqualsAndHashCode(of = "id")
@ToString(exclude = {"books"})
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class Author {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank
    @Column(name = "first_name", nullable = false)
    private String firstName;
    @NotBlank
    @Column(name = "last_name", nullable = false)
    private String lastName;
    @ManyToMany(mappedBy = "authors")
    private Set<Book> books = new HashSet<>();
    @ManyToMany(mappedBy = "authors")
    private Set<Buddy> buddies = new HashSet<>();


    public String getFullName() {
        return firstName + " " + lastName;
    }

    public Author addBook(Book book) {
        if (books == null) {
            books = new HashSet<>();
        }
        books.add(book);
        return this;
    }
}
