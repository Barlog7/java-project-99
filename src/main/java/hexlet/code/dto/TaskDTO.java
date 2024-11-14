package hexlet.code.dto;

//import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

//import java.time.Instant;
import java.util.Date;
import java.util.Set;


@Getter
@Setter
public class TaskDTO {
    private Long id;
    private Integer index;
    //@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createdAt;
    @JsonProperty("assignee_id")
    private Integer assigneeid;
    private String title;
    private String content;
    private String status;
    private Set<Long> taskLabelIds;
}
