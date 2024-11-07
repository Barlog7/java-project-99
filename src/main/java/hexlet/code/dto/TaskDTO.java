package hexlet.code.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.Set;


@Getter
@Setter
public class TaskDTO {
    private Long id;
    private Integer index;
    private Instant createdAt;
    @JsonProperty("assignee_id")
    private Integer assigneeid;
    private String title;
    private String content;
    private String status;
    private Set<Long> taskLabelIds;
}
