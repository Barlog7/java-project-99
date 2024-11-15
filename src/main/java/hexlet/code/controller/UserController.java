package hexlet.code.controller;

import hexlet.code.dto.UserCreateDTO;
import hexlet.code.dto.UserDTO;
import hexlet.code.dto.UserUpdateDTO;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.mapper.JsonNullableMapper;
import hexlet.code.mapper.UserMapper;
//import hexlet.code.model.User;
import hexlet.code.repository.UserRepository;
import hexlet.code.utils.PasswordHashing;
//import hexlet.code.utils.UserUtils;
import hexlet.code.utils.UserUtils;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
//import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
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
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private JsonNullableMapper jsonNullableMapper;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserUtils userUtils;
    @GetMapping("")
    public ResponseEntity<List<UserDTO>> index() {
        var users = userRepository.findAll();
        var usersDTO = users.stream()
                .map(userMapper::mapModel)
                .toList();
        //return usersDTO;
        return ResponseEntity.ok()
                .header("X-Total-Count", String.valueOf(usersDTO.size()))
                .body(usersDTO);
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
    @PreAuthorize("@userUtils.isUserAllow(#id)")
    public void delete(@PathVariable Long id) {
/*        User UserEnter =  userUtils.getCurrentUser();
        var UniqeName = UserEnter.getUsername();*/
        //var userUtils = new UserUtils();
        //var userConnect = userUtils.getCurrentUser();
/*        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return;
        }
        var email = authentication.getName();*/
/*        var userUtils = new UserUtils();
        var userConnect = userUtils.getCurrentUser();*/
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        var email = authentication.getName();
        var user = userRepository.findById(id).get();
        if (!email.equals(user.getUsername())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
            //return;
        }
        if (user.getTasks().isEmpty()) {
            userRepository.deleteById(id);
        }

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
    @PreAuthorize("@userUtils.isUserAllow(#id) || @userUtils.isUserAdmin()")
    //@PreAuthorize(value = "@userRepository.findById(#id).getEmail() == authentication.name")
    public UserDTO update(@PathVariable Long id, @Valid @RequestBody UserUpdateDTO productData) {
        var user =  userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User with id " + id + " not found"));
        userMapper.update(productData, user);
        if (jsonNullableMapper.isPresent(productData.getPassword())) {
            //if (productData.getPassword().isPresent()) {
            var passHash = PasswordHashing.getHashPass(productData.getPassword().get());
            user.setPassword(passHash);
        }
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        var email = authentication.getName();
        //userRepository.findByEmail("hexlet@example.com").get()..
        var userCheck = userRepository.findById(id).get();
        if (!email.equals(userCheck.getUsername()) && !userUtils.isUserAdmin()) {
            //user =  userRepository.findById(id).get();

            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        userRepository.save(user);
        return userMapper.mapModel(user);
    }
}

