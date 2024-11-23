package Back.Paint.domain;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonView;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Data
@Entity
@Table
@EqualsAndHashCode(of = {"id"})
public class Board {

    @JsonView(Views.Id.class)
    @Id
    @Column(columnDefinition = "VARCHAR(255)")
    private Long id;

    @JsonView(Views.IdName.class)
    @Column(columnDefinition = "VARCHAR(255000) DEFAULT 'Default Name'")
    private String text;
    @JsonView(Views.FullMessage.class)

    private String Name;
    @OneToMany(mappedBy = "id.idBoard", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<UserBoardRelationship> userBoardRelationships;
    @Column(updatable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonView(Views.FullMessage.class)
    private LocalDateTime creationDate;


}
