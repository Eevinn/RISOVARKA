package backend.model;

import jakarta.persistence.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "shapes")
public class Shape {
    @Id
    //@GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "shape", columnDefinition = "TEXT")
    private String shape;

    @ManyToOne
    @JoinColumn(name = "board_id", nullable = false)
    private Board board;
}