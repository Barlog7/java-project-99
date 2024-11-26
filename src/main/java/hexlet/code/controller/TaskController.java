package hexlet.code.controller;

import hexlet.code.dto.TaskDTO;
import hexlet.code.dto.TaskCreateDTO;
import hexlet.code.dto.TaskParamDTO;
import hexlet.code.dto.TaskUpdateDTO;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.mapper.JsonNullableMapper;
import hexlet.code.mapper.TaskMapper;
import hexlet.code.model.Label;
import hexlet.code.model.Task;
import hexlet.code.repository.LabelRepository;
import hexlet.code.repository.TaskRepository;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.repository.UserRepository;
import hexlet.code.specification.TaskSpecification;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {
    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private TaskMapper taskMapper;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private LabelRepository labelRepository;
    @Autowired
    private TaskStatusRepository taskStatusRepository;
    @Autowired
    private JsonNullableMapper jsonNullableMapper;
    @Autowired
    private TaskSpecification taskSpecification;

    @GetMapping("")
    public ResponseEntity<List<TaskDTO>> index(TaskParamDTO param) {
        var spec = taskSpecification.build(param);
        var tasks = taskRepository.findAll(spec);
        var taskDTO = tasks.stream()
                .map(taskMapper::mapTask)
                .toList();
        //return taskDTO;
        return ResponseEntity.ok()
                        .header("X-Total-Count", String.valueOf(tasks.size()))
                        .body(taskDTO);
    }

    @GetMapping("/{id}")
    public TaskDTO show(@PathVariable long id) {
        var task =  taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task with id " + id + " not found"));
        var taskDTO = taskMapper.mapTask(task);
        return taskDTO;
    }

    @DeleteMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        var task = taskRepository.findById(id).get();
        var user = taskRepository.findById(id).get().getAssignee();
        var status = taskRepository.findById(id).get().getTaskStatus();
        if (user != null) {
            user.removeTask(task);
        }
        if (status != null) {
            status.removeTask(task);
        }
        taskRepository.deleteById(id);
    }

    @PostMapping(path = "")
    @ResponseStatus(HttpStatus.CREATED)
    public TaskDTO create(@Valid @RequestBody TaskCreateDTO taskCreateDTO) {
        var task = taskMapper.map(taskCreateDTO);
        updateInTaskStatus(task);

        if (jsonNullableMapper.isPresent(taskCreateDTO.getAssigneeid())) {
            if (taskCreateDTO.getAssigneeid().get() != null) {
                var user = userRepository.findById(Long.valueOf(taskCreateDTO.getAssigneeid().get())).get();
                task.setAssignee(user);
            }
        }

        createLabels(task, taskCreateDTO);
        taskRepository.save(task);
        var taskDTO = taskMapper.mapTask(task);
        return taskDTO;
    }

    private void updateInTaskStatus(Task task) {
        var taskStatusGet = taskStatusRepository.findBySlug(task.getTaskStatus().getSlug()).get();
        task.setTaskStatus(taskStatusGet);
        taskStatusGet.addTask(task);
    }
    private void createLabels(Task task, TaskCreateDTO taskCreateDTO) {
        List<Label> labels = null;
        if (taskCreateDTO.getTaskLabelIds() != null) {
            labels = labelRepository.findAllById(taskCreateDTO.getTaskLabelIds().get());
        }
        task.setLabelsUsed(labels != null ? new HashSet<>(labels) : new HashSet<>());
    }

    @PutMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public TaskDTO update(@PathVariable Long id, @Valid @RequestBody TaskUpdateDTO taskUpdateDTO) {
        var task =  taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task with id " + id + " not found"));

        var userNew = task.getAssignee();
        if (jsonNullableMapper.isPresent(taskUpdateDTO.getAssigneeid())) {
            var idFind = taskUpdateDTO.getAssigneeid().get();
            userNew = userRepository.findById(Long.valueOf(idFind)).get();
        }

        var statusNew = task.getTaskStatus();
        if (jsonNullableMapper.isPresent(taskUpdateDTO.getStatus())) {
            var stsusFind = taskUpdateDTO.getStatus().get();
            statusNew = taskStatusRepository.findBySlug(stsusFind).get();
        }

        var labelUsed = task.getLabelsUsed();
        List<Label> labels = new ArrayList<>(task.getLabelsUsed());

        if (jsonNullableMapper.isPresent(taskUpdateDTO.getTaskLabelIds())) {

            if (taskUpdateDTO.getTaskLabelIds().get() != null) {
                labels = labelRepository.findAllById(taskUpdateDTO.getTaskLabelIds().get());
            }
            task.setLabelsUsed(labels != null ? new HashSet<>(labels) : new HashSet<>());
        }
        taskMapper.update(taskUpdateDTO, task);

        task.setAssignee(userNew);
        task.setTaskStatus(statusNew);
        task.setLabelsUsed(labels != null ? new HashSet<>(labels) : new HashSet<>());
        taskRepository.save(task);
        var returnDto = taskMapper.mapTask(task);
        return returnDto;
    }
}
