package pl.coderslab.cultureBuddies.books;

import lombok.*;
import pl.coderslab.cultureBuddies.author.Author;
import pl.coderslab.cultureBuddies.buddies.Buddy;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "books")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
@ToString(exclude = "authors")
@Builder
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank
    private String title;
    @NotEmpty
    @ManyToMany(mappedBy = "books")
    private Set<Author> authors = new HashSet<>();
    @ManyToMany(mappedBy = "books")
    private Set<Buddy> buddies = new HashSet<>();

    public void addAuthor(Author author) {
        if (authors == null) {
            authors = new HashSet<>();
        }
        if (author != null) {
            authors.add(author);
        }
    }
}
