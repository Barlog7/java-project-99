package hexlet.code.controller;

import hexlet.code.dto.LabelDTO;
import hexlet.code.dto.LabelCreateDTO;
import hexlet.code.dto.LabelUpdateDTO;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.mapper.LabelMapper;
import hexlet.code.repository.LabelRepository;
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
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/labels")
public class LabelController {

    @Autowired
    private LabelRepository labelRepository;

    @Autowired
    LabelMapper labelMapper;

    @GetMapping("")
    public ResponseEntity<List<LabelDTO>> index() {
        var labels = labelRepository.findAll();
        var labelsDTO = labels.stream().map(labelMapper::mapModelToDTO).toList();
        return ResponseEntity.ok()
                .header("X-Total-Count", String.valueOf(labels.size()))
                .body(labelsDTO);
    }

    @GetMapping("/{id}")
    public LabelDTO show(@PathVariable long id) {
        var label =  labelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Label with id " + id + " not found"));
        var labelDTO = labelMapper.mapModelToDTO(label);
        return labelDTO;
    }

    @DeleteMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        var label = labelRepository.findById(id).get();

        if (labelRepository.findById(id).get().getTasks().isEmpty()) {
            labelRepository.deleteById(id);
        } else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
    }

    @PostMapping(path = "")
    @ResponseStatus(HttpStatus.CREATED)
    public LabelDTO create(@Valid @RequestBody LabelCreateDTO labelCreateDTO) {
        var label = labelMapper.mapCreateDTOtoModel(labelCreateDTO);
        labelRepository.save(label);
        var taskDTO = labelMapper.mapModelToDTO(label);
        return taskDTO;
    }

    @PutMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public LabelDTO update(@PathVariable Long id, @Valid @RequestBody LabelUpdateDTO dto) {
        var label =  labelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Label with id " + id + " not found"));
        labelMapper.update(dto, label);
        labelRepository.save(label);
        return labelMapper.mapModelToDTO(label);
    }
}
