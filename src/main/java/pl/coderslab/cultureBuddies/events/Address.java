package pl.coderslab.cultureBuddies.events;


import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "addresses")
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = {"id"})
@Getter
@Setter
@Builder
@ToString
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull
    @Column(nullable = false)
    private String city;
    @NotNull
    @Column(nullable = false)
    private String street;
    @NotNull
    @Column(nullable = false)
    private String number;
    private String flatNumber;
}
