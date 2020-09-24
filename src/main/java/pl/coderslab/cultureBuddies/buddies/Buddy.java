package pl.coderslab.cultureBuddies.buddies;

import lombok.*;
import pl.coderslab.cultureBuddies.books.Book;
import pl.coderslab.cultureBuddies.buddyBook.BuddyBook;
import pl.coderslab.cultureBuddies.buddyBuddy.BuddyRelation;
import pl.coderslab.cultureBuddies.city.City;
import pl.coderslab.cultureBuddies.security.Role;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
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
@ToString(exclude = {"roles", "books", "password", "buddies", "buddyOf"})
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

    @NotNull
    @ManyToOne
    private City city;

    @Column(nullable = false)
    private Boolean enabled;

    @ManyToMany(fetch = FetchType.EAGER)
    private Set<Role> roles = new HashSet<>();

    @OneToMany(
            mappedBy = "buddy",
            orphanRemoval = true)
    private Set<BuddyBook> books = new HashSet<>();

    @OneToMany(
            mappedBy = "buddy",
            orphanRemoval = true)
    private Set<BuddyRelation> buddies = new HashSet<>();

    @OneToMany(
            mappedBy = "buddyOf",
            orphanRemoval = true)
    private Set<BuddyRelation> buddyOf = new HashSet<>();

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

}
