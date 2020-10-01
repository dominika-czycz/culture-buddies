package pl.coderslab.cultureBuddies.googleapis.restModel;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class IndustryIdentifier {
    private String type;
    private String identifier;

}
