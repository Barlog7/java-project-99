package hexlet.code.controller;

import hexlet.code.dto.UserCreateDTO;
import hexlet.code.dto.UserDTO;
import hexlet.code.dto.UserUpdateDTO;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.mapper.JsonNullableMapper;
import hexlet.code.mapper.UserMapper;
import hexlet.code.repository.UserRepository;
import hexlet.code.utils.PasswordHashing;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private JsonNullableMapper jsonNullableMapper;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserMapper userMapper;
    @GetMapping("")
    public List<UserDTO> index() {
        var users = userRepository.findAll();
        var usersDTO = users.stream()
                .map(userMapper::mapModel)
                .toList();
        return usersDTO;
    }

    @GetMapping("/{id}")
    public UserDTO show(@PathVariable long id) {
        var user =  userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User with id " + id + " not found"));
        var userDTO = userMapper.mapModel(user);
        return userDTO;
    }

    @DeleteMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        userRepository.deleteById(id);
    }

    @PostMapping(path = "")
    @ResponseStatus(HttpStatus.CREATED)
    public UserDTO create(@Valid @RequestBody UserCreateDTO userCreateDTO) {
        var user = userMapper.map(userCreateDTO);
        // шифруем пароль
        var pass = user.getPassword();
        var passHash = PasswordHashing.getHashPass(pass);
        user.setPassword(passHash);
        userRepository.save(user);
        var userDTO = userMapper.mapModel(user);
        return userDTO;
    }

    @PutMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public UserDTO update(@PathVariable Long id, @Valid @RequestBody UserUpdateDTO productData) {
        var user =  userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User with id " + id + " not found"));
        userMapper.update(productData, user);
        if ( jsonNullableMapper.isPresent( productData.getPassword() ) ) {
            //if (productData.getPassword().isPresent()) {
            var passHash = PasswordHashing.getHashPass(productData.getPassword().get());
            user.setPassword(passHash);
        }
        userRepository.save(user);
        return userMapper.mapModel(user);
    }
}

