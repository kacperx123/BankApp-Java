package mvcsecuritytest.entity.embedded;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@EqualsAndHashCode
@Embeddable
@NoArgsConstructor
public class AuthorityId implements Serializable {
    private String username;
    private String authority;
}

