package hexlet.code.specification;

import hexlet.code.dto.TaskParamDTO;
import hexlet.code.model.Task;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class TaskSpecification {
    public Specification<Task> build(TaskParamDTO param) {
        return withAssigneeId(param.getAssigneeId())
                .and(withTitleCont(param.getTitleCont()))
                .and(withStatus(param.getStatus()))
                .and(withLabelId(param.getLabelId()));
    }
    private Specification<Task> withAssigneeId(Long assigneeId) {
        return (root, query, cb) ->
                assigneeId == null ? cb.conjunction() : cb.equal(root.get("assignee").get("id"), assigneeId);
    }
    private Specification<Task> withTitleCont(String titleCont) {
        return (root, query, cb) -> titleCont == null ? cb.conjunction() : cb.equal(root.get("name"), titleCont);
    }
    private Specification<Task> withStatus(String status) {
        return (root, query, cb) ->
                status == null ? cb.conjunction() : cb.equal(root.get("taskStatus").get("slug"), status);
    }
    private Specification<Task> withLabelId(Long labelId) {
        return (root, query, cb) -> labelId == null
                ? cb.conjunction()
                : cb.equal(root.join("labelsUsed", JoinType.INNER).get("id"), labelId);
    }
}
