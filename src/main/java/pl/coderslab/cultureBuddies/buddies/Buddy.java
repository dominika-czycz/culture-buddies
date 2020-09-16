package pl.coderslab.cultureBuddies.buddies;

import lombok.*;
import pl.coderslab.cultureBuddies.author.Author;
import pl.coderslab.cultureBuddies.books.Book;
import pl.coderslab.cultureBuddies.security.Role;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

@Entity
@Table(name = "buddies")
@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
@Builder(toBuilder = true)
@EqualsAndHashCode(of = "id")
@ToString(exclude = {"roles", "authors", "books"})
public class Buddy {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String name;

    @NotBlank
    @Column(name = "last_name", nullable = false)
    private String lastName;

    @NotBlank
    @Column(nullable = false)
    private String username;

    @NotBlank
    @Column(nullable = false)
    private String password;

    @NotBlank
    @Email
    @Column(nullable = false)
    private String email;

    private String pictureUrl;

    @NotBlank
    private String city;

    @Column(nullable = false)
    private Boolean enabled;

    @ManyToMany(fetch = FetchType.EAGER)
    private Set<Role> roles = new HashSet<>();

    @ManyToMany
    @JoinTable(name = "buddies_authors",
            joinColumns = @JoinColumn(name = "buddy_id"),
            inverseJoinColumns= @JoinColumn(name = "author_id")
    )
    private Set<Author> authors = new HashSet<>();

    @OneToMany(
            mappedBy = "buddy",
            orphanRemoval = true)
    private Set<BuddyBook> books = new HashSet<>();

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

    public BuddyBook addBook(Book book) {
        BuddyBook buddyBook = new BuddyBook(this, book);
        books.add(buddyBook);
        book.getBuddies().add(buddyBook);
        return buddyBook;
    }

    public Buddy removeBook(Book book) {
        for (Iterator<BuddyBook> iterator = books.iterator(); iterator.hasNext(); ) {
            final BuddyBook buddyBook = iterator.next();
            if (buddyBook.getBuddy().equals(this) && buddyBook.getBook().equals(book)) {
                iterator.remove();
                buddyBook.getBook().getBuddies().remove(buddyBook);
                buddyBook.setBook(null);
                buddyBook.setBuddy(null);
            }
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
}
