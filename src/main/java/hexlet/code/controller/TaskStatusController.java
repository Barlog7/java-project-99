package hexlet.code.controller;

import hexlet.code.dto.TaskStatusDTO;
import hexlet.code.dto.TaskStatusCreateDTO;
import hexlet.code.dto.TaskStatusUpdateDTO;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.mapper.TaskStatusMaper;
import hexlet.code.repository.TaskStatusRepository;
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
@RequestMapping("/api/task_statuses")
public class TaskStatusController {
    @Autowired
    private TaskStatusRepository taskStatusRepository;
    @Autowired
    private TaskStatusMaper taskStatusMaper;

    @GetMapping("")
    public ResponseEntity<List<TaskStatusDTO>> index() {
        var tasks = taskStatusRepository.findAll();
        var taskStatusDTO = tasks.stream()
                .map(taskStatusMaper::mapTaskStatus)
                .toList();
        return ResponseEntity.ok()
                .header("X-Total-Count", String.valueOf(taskStatusDTO.size()))
                .body(taskStatusDTO);
        //return taskDTO;
    }
    @GetMapping("/{id}")
    public TaskStatusDTO show(@PathVariable long id) {
        var task =  taskStatusRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("TaskStatus with id " + id + " not found"));
        var taskStatusDTO = taskStatusMaper.mapTaskStatus(task);
        return taskStatusDTO;
    }
    @DeleteMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        taskStatusRepository.deleteById(id);
    }

    @PostMapping(path = "")
    @ResponseStatus(HttpStatus.CREATED)
    public TaskStatusDTO create(@Valid @RequestBody TaskStatusCreateDTO taskStatusCreateDTO) {
        var task = taskStatusMaper.map(taskStatusCreateDTO);
        taskStatusRepository.save(task);
        var taskDTO = taskStatusMaper.mapTaskStatus(task);
        return taskDTO;
    }
    @PutMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public TaskStatusDTO update(@PathVariable Long id, @Valid @RequestBody TaskStatusUpdateDTO taskStatusUpdateDTO) {
        var task =  taskStatusRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("TaskStatus with id " + id + " not found"));
        taskStatusMaper.update(taskStatusUpdateDTO, task);
        taskStatusRepository.save(task);
        return taskStatusMaper.mapTaskStatus(task);
    }
}
