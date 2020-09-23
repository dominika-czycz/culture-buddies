package pl.coderslab.cultureBuddies.events;

import lombok.*;
import org.hibernate.validator.constraints.URL;
import pl.coderslab.cultureBuddies.buddies.Buddy;

import javax.persistence.*;
import javax.validation.constraints.Future;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "events")
@Getter
@Setter
@EqualsAndHashCode(of = "id")
@ToString(exclude = "buddies")
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank
    @Column(nullable = false)
    private String title;
    private LocalDateTime added;
    @NotNull
    @Column(nullable = false)
    @Future
    private LocalDateTime date;
    @NotNull
    @ManyToOne
    @JoinColumn(nullable = false, name = "address_id")
    private Address address;
    @NotBlank
    @Column(nullable = false)
    private String description;
    @URL
    private String link;
    @ManyToOne
    @NotNull
    @JoinColumn(nullable = false, name = "event_type_id")
    private EventType eventType;
    @ManyToMany
    @JoinTable(name = "events_buddies",
            joinColumns = @JoinColumn(name = "event_id"),
            inverseJoinColumns = @JoinColumn(name = "buddy_id")
    )
    private Set<Buddy> buddies;
    @ManyToOne
    @JoinColumn(nullable = false, name = "buddy_id")
    @NotNull
    private Buddy buddy;

    @PrePersist
    public void prePersist() {
        added = LocalDateTime.now();
    }
}
