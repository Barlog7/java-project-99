package hexlet.code.dto;

import lombok.Getter;
import lombok.Setter;
import org.openapitools.jackson.nullable.JsonNullable;

@Getter
@Setter
public class TaskUpdateDTO {
    private JsonNullable<Integer> index;
    private JsonNullable<Integer> assigneeId;
    private JsonNullable<String> title;
    private JsonNullable<String> content;
    private JsonNullable<String> status;

}
