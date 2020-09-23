package pl.coderslab.cultureBuddies.events;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;

@Entity
@Table(name = "event_types")
@Getter
@Setter
@EqualsAndHashCode(of = "id")
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class EventType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank
    @Column(nullable = false)
    private String name;
}
