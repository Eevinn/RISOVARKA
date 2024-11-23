package Back.Paint.domain;

import jakarta.persistence.*;
import lombok.*;

@Data
@Entity
@Table(name = "user_board_relationship")
@EqualsAndHashCode(of = {"id"})
public class UserBoardRelationship {

    @EmbeddedId
    private UserBoardRelationshipId id;

    private Boolean accessModifier;

    // Геттеры и сеттеры для idBoard и idUser
    public Long getIdBoard() {
        return id.getIdBoard();
    }

    public void setIdBoard(Long idBoard) {
        id.setIdBoard(idBoard);
    }

    public Long getIdUser() {
        return id.getIdUser();
    }

    public void setIdUser(Long idUser) {
        id.setIdUser(idUser);
    }
}
