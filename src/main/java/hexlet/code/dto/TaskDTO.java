package hexlet.code.dto;

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
    private Integer assignee_id;
    private String title;
    private String content;
    private String status;
    private Set<Long> taskLabelIds;
}
