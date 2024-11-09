package Back.Paint.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonView;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Document
public class Canvas{
    @Id
    private String id;
    private String type;
    private String version;
    private String originX;
    private String originY;
    private int left;
    private int top;
    private int width;
    private int height;
    private String fill;
    private String stroke;
    private int strokeWidth;
    private Object strokeDashArray;
    private String strokeLineCap;
    private int strokeDashOffset;
    private String strokeLineJoin;
    private boolean strokeUniform;
    private int strokeMiterLimit;
    private double scaleX;
    private double scaleY;
    private double angle;
    private boolean flipX;
    private boolean flipY;
    private double opacity;
    private Object shadow;
    private boolean visible;
    private String backgroundColor;
    private String fillRule;
    private String paintFirst;
    private String globalCompositeOperation;
    private double skewX;
    private double skewY;
}
