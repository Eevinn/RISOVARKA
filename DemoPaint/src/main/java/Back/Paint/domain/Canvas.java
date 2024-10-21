package Back.Paint.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonView;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Data
@Entity
@Table
public class Canvas{
    @Id
    private String id;
    @Column(name="lefter")
    private int lefter;
    @Column(name = "toper")
    private int toper;
    private int width;
    private int height;
    private String fill;
    private Integer strokeWidth;
    private String stroke;
    private String strokeDashArray;
    private String strokeLineCap;
    private double strokeDashOffset;
    private String strokeLineJoin;
    private boolean strokeUniform;
    private double strokeMiterLimit;
    private double scaleX;
    private double scaleY;
    private double angle;
    private boolean flipX;
    private boolean flipY;
    private double opacity;
    private String shadow;
    private boolean visible;
    private String backgroundColor;
    private String fillRule;
    private String paintFirst;
    private String globalCompositeOperation;
    private double skewX;
    private double skewY;

}
