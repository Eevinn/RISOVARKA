package Back.Paint.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "app_user")
//@EqualsAndHashCode(of = {"id"})
public class Person {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotEmpty(message = "Логин должен быть заполнен")
    @Column(name = "username", unique = true, nullable = false)
    private String username;

    @NotEmpty(message = "Пароль не должен быть пустым")
    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "role", nullable = false)
    private String role = "ROLE_USER";

//    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
//    @JoinColumn(name = "user_id")
//    private List<Board> userBoards;

//    private String role;
//    @OneToMany(mappedBy = "id.idUser", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
//    private List<UserBoardRelationship> userBoardRelationships;

//    @Column(updatable = false)
//    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
//    private LocalDateTime creationDate;
}
