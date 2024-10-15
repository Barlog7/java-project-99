package hexlet.code.controller;

import hexlet.code.dto.TaskDTO;
import hexlet.code.dto.TaskCreateDTO;
import hexlet.code.dto.TaskUpdateDTO;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.mapper.TaskMapper;
import hexlet.code.model.Task;
import hexlet.code.repository.TaskRepository;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.repository.UserRepository;
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
    private TaskStatusRepository taskStatusRepository;

    @GetMapping("")
    public ResponseEntity<List<TaskDTO>> index() {
        var tasks = taskRepository.findAll();
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
        taskRepository.deleteById(id);
    }

    @PostMapping(path = "")
    @ResponseStatus(HttpStatus.CREATED)
    public TaskDTO create(@Valid @RequestBody TaskCreateDTO taskCreateDTO) {
        var task = taskMapper.map(taskCreateDTO);
        updateInTaskStatus(task);
        taskRepository.save(task);
        var taskDTO = taskMapper.mapTask(task);
        return taskDTO;
    }

    private void updateInTaskStatus(Task task) {
        var taskStatusGet = taskStatusRepository.findBySlug(task.getTaskStatus().getSlug()).get();
        task.setTaskStatus(taskStatusGet);
        taskStatusGet.addTask(task);
    }

    @PutMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public TaskDTO update(@PathVariable Long id, @Valid @RequestBody TaskUpdateDTO taskUpdateDTO) {
        var task =  taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task with id " + id + " not found"));
        //var checkObject = taskUpdateDTO.getAssigneeId();
        if (taskUpdateDTO.getAssigneeId() != null) {
            var idFind = taskUpdateDTO.getAssigneeId().get();
            var userNew = userRepository.findById(Long.valueOf(idFind)).get();
            task.setAssignee(userNew);
        }
        if (taskUpdateDTO.getStatus() != null) {
            var stsusFind = taskUpdateDTO.getStatus().get();
            var statusNew = taskStatusRepository.findBySlug(stsusFind).get();
            task.setTaskStatus(statusNew);
        }
        taskMapper.update(taskUpdateDTO, task);
        taskRepository.save(task);
        return taskMapper.mapTask(task);
    }


}
