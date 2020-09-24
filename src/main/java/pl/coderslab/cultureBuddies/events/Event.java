package pl.coderslab.cultureBuddies.events;

import lombok.*;
import org.hibernate.validator.constraints.URL;
import org.springframework.format.annotation.DateTimeFormat;
import pl.coderslab.cultureBuddies.buddies.Buddy;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.Future;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
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
    private LocalDateTime updated;
    @NotNull
    @Column(nullable = false)
    @Future
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;
    @NotBlank
    @Pattern(regexp = "^([0-1]?[0-9]|2[0-3]):[0-5][0-9]$")
    @Transient
    private String stringTime;
    @Column(nullable = false)
    private LocalTime time;
    @Valid
    @ManyToOne
    @NotNull
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
        time = LocalTime.parse(stringTime, DateTimeFormatter.ofPattern("HH:mm"));
        addBuddy(buddy);
    }

    @PreUpdate
    public void preUpdate() {
        updated = LocalDateTime.now();
        time = LocalTime.parse(stringTime, DateTimeFormatter.ofPattern("HH:mm"));
    }

    @PostLoad
    public void postLoad() {
        stringTime = time.toString();
    }

    public void addBuddy(Buddy buddy) {
        if (buddies == null) buddies = new HashSet<>();
        if (buddy != null) {
            buddies.add(buddy);
            buddy.getEvents().add(this);
        }
    }

    public void removeBuddy(Buddy buddy) {
        if (buddies == null) buddies = new HashSet<>();
        if (buddy != null) {
            buddies.remove(buddy);
            buddy.getEvents().remove(this);
        }


    }
}
