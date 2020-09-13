package pl.coderslab.cultureBuddies.buddies;

import lombok.*;
import pl.coderslab.cultureBuddies.author.Author;
import pl.coderslab.cultureBuddies.books.Book;
import pl.coderslab.cultureBuddies.security.Role;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "buddies")
@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
@Builder(toBuilder = true)
@EqualsAndHashCode(of = "id")
@ToString

public class Buddy {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank
    private String name;
    @NotBlank
    @Column(name = "last_name")
    private String lastName;
    @NotBlank
    private String username;
    @NotBlank
    private String password;
    @NotBlank
    @Email
    private String email;
    private String pictureUrl;
    @NotBlank
    private String city;
    private Boolean enabled;
    @ManyToMany(fetch = FetchType.EAGER)
    private Set<Role> roles = new HashSet<>();
    @ManyToMany
    private Set<Author> authors = new HashSet<>();
    @ManyToMany
    @JoinTable(name = "buddy_books",
            joinColumns = @JoinColumn(name = "buddy_id"),
            inverseJoinColumns = @JoinColumn(name = "book_id")
    )
    private Set<Book> books = new HashSet<>();

    @PrePersist
    public void prePersist() {
        enabled = true;
    }

    public void addRole(Role role) {
        if (roles == null) {
            roles = new HashSet<>();
        }
        if (role != null) {
            roles.add(role);
        }
    }

    public Buddy addBook(Book book) {
        if (books == null) {
            books = new HashSet<>();
        }
        if (book != null) {
            books.add(book);
        }
        return this;
    }

    public Buddy addAuthor(Author author) {
        if (authors == null) {
            authors = new HashSet<>();
        }
        if (author != null) {
            authors.add(author);
        }
        return this;
    }


    public boolean removeBook(Book book) {
        if (books != null && book != null) {
            return books.remove(book);
        }
        return false;
    }
}
