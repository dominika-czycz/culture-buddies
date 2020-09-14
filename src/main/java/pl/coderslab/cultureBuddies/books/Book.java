package pl.coderslab.cultureBuddies.books;

import lombok.*;
import pl.coderslab.cultureBuddies.author.Author;
import pl.coderslab.cultureBuddies.buddies.BuddyBook;

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
@ToString(exclude = {"authors", "buddies"})
@Builder
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank
    @Column(nullable = false)
    private String title;
    @NotEmpty
    @ManyToMany(mappedBy = "books")
    private Set<Author> authors = new HashSet<>();
    @OneToMany(mappedBy = "book",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private Set<BuddyBook> buddies = new HashSet<>();

    public void addAuthor(Author author) {
        if (author != null) {
            authors.add(author);
        }
    }
}
