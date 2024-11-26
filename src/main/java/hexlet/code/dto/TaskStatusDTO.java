package hexlet.code.dto;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;


import java.util.Date;

@Getter
@Setter
public class TaskStatusDTO {
    private Long id;
    private String name;
    private String slug;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date createdAt;
}
