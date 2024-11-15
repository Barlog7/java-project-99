package hexlet.code.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import org.openapitools.jackson.nullable.JsonNullable;

import java.util.Set;


@Getter
@Setter
public class TaskCreateDTO {

    //private Integer index;
    private JsonNullable<Integer> index;
    @JsonProperty("assignee_id")
    private JsonNullable<Integer> assigneeid;
    //private Integer assigneeid;
    private String title;
    private JsonNullable<String> content;
    //private String content;
    private JsonNullable<String> status;
    //private String status;
    private Set<Long> taskLabelIds;

}
