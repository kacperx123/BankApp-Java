package mvcsecuritytest.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mvcsecuritytest.entity.embedded.AuthorityId;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "authorities")
public class Authority {

    @EmbeddedId
    private AuthorityId authorityId;

    @ManyToOne
    @MapsId("username")
    @JoinColumn(name = "username")
    private UserEntity user;

}