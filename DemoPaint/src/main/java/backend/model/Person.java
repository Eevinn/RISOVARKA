package backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "app_user")
public class Person implements Serializable {
    private static final long serialVersionUID = 2L;

    @Id
    @Column(name = "person_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username")
    @NotBlank(message = "Имя пользователя не может быть пустым")
    private String username;

    @Column(name = "last_login")
    private LocalDateTime lastLogin;


    // Связь с таблицей login_timestamps
    @OneToMany(mappedBy = "person", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<LoginTimestamp> loginTimes = new ArrayList<>();

    @Column(name = "role")
    private String role = "ROLE_USER";

    @NotEmpty(message = "Пароль не должен быть пустым")
    @Column(name = "password")
    private String password;

    @Column(name = "created_at", nullable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

}
