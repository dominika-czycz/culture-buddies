package pl.coderslab.cultureBuddies.buddies;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

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
    @Email
    private String email;
    private String pictureUrl;
}
