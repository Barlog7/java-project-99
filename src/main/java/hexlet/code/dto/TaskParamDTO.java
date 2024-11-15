package hexlet.code.dto;

import lombok.Getter;
import lombok.Setter;
//import org.openapitools.jackson.nullable.JsonNullable;

@Setter
@Getter
public class TaskParamDTO {
    private String titleCont;
    private Long assigneeId;
    private String status;
    private Long labelId;
    //JsonNullable<String>
    //private JsonNullable<String> titleCont;
    //private JsonNullable<Long> assigneeId;
    //private JsonNullable<String> status;
    //private JsonNullable<Long> labelId;
}
