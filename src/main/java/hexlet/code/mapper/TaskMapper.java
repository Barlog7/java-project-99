package hexlet.code.mapper;

import hexlet.code.dto.TaskCreateDTO;
import hexlet.code.dto.TaskDTO;
import hexlet.code.dto.TaskUpdateDTO;
import hexlet.code.model.Label;
import hexlet.code.model.Task;
import org.mapstruct.*;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(
        uses = { JsonNullableMapper.class, ReferenceMapper.class },
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public abstract class TaskMapper {

    @Mapping(target = "id", source = "id")
    @Mapping(target = "assignee_id", source = "assignee.id")
    @Mapping(target = "title", source = "name")
    @Mapping(target = "content", source = "description")
    @Mapping(target = "status", source = "taskStatus.slug")
    //@Mapping(target = "labelsUsed", source = "taskStatus.slug")
    //@Mapping(target = "taskLabelIds", expression = "java(labelsToLabelIds(task.getLabels()))")
    @Mapping(source = "labelsUsed", target = "taskLabelIds", qualifiedByName = "labelTaskToDto")
    public abstract TaskDTO mapTask(Task task);

    @Mapping(target = "name", source = "title")
    @Mapping(target = "assignee", source = "assignee_id")
    @Mapping(target = "description", source = "content")
    @Mapping(target = "taskStatus.slug", source = "status")
    public abstract Task map(TaskCreateDTO dto);

    @Mapping(target = "name", source = "title")
    @Mapping(target = "assignee.id", source = "assignee_id")
    @Mapping(target = "description", source = "content")
    @Mapping(target = "taskStatus.slug", source = "status")
    public abstract Task update(TaskUpdateDTO dto, @MappingTarget Task task);

    @Named("labelTaskToDto")
    public static Set<Long> labelTaskToDto(Set<Label> labelsUsed) {
        var list = labelsUsed.stream().map((v -> v.getId())).toList();
        Set<Long> result = new HashSet<Long>(list);
        return result;
    }

/*    @Named("dtoToTask")
    public static Set<Long> labelTaskToDto(Set<Long> dto) {
        var list = dto.stream().map((v -> v.getId())).toList();
        Set<Long> result = new HashSet<Long>(list);
        return result;
    }*/
}
