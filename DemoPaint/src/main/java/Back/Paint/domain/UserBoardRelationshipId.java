package Back.Paint.domain;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Embeddable
@Data
@EqualsAndHashCode
public class UserBoardRelationshipId implements Serializable {
    private Long idBoard;
    private Long idUser;
}
