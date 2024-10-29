package hexlet.code.component;

import hexlet.code.model.Label;
import hexlet.code.model.TaskStatus;
import hexlet.code.model.User;
import hexlet.code.repository.LabelRepository;
import hexlet.code.repository.TaskRepository;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.repository.UserRepository;
import hexlet.code.utils.PasswordHashing;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class DataInitializer implements ApplicationRunner {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TaskStatusRepository taskStatusRepository;
    @Autowired
    private LabelRepository labelRepository;
    @Autowired
    private TaskRepository taskRepository;
    @Override
    public void run(ApplicationArguments args) throws Exception {
        taskRepository.deleteAll();
        userRepository.deleteAll();
        taskStatusRepository.deleteAll();
        labelRepository.deleteAll();
        User defaultUser = userRepository.findByEmail("hexlet@example.com").orElse(null);
        if (defaultUser != null) {
            return;
        }
        var email = "hexlet@example.com";
        var userData = new User();
        userData.setEmail(email);
        var password = "qwerty";
        var passHash = PasswordHashing.getHashPass(password);
        userData.setPassword(passHash);
        userRepository.save(userData);

        var slag = "draft";
        var name = "Draft";
        var taskData = new TaskStatus();
        taskData.setName(name);
        taskData.setSlug(slag);
        taskStatusRepository.save(taskData);

        slag = "to_review";
        name = "To review";
        taskData = new TaskStatus();
        taskData.setName(name);
        taskData.setSlug(slag);
        taskStatusRepository.save(taskData);

        slag = "to_be_fixed";
        name = "To be fixed";
        taskData = new TaskStatus();
        taskData.setName(name);
        taskData.setSlug(slag);
        taskStatusRepository.save(taskData);

        slag = "to_publish";
        name = "To publish";
        taskData = new TaskStatus();
        taskData.setName(name);
        taskData.setSlug(slag);
        taskStatusRepository.save(taskData);

        slag = "published";
        name = "Published";
        taskData = new TaskStatus();
        taskData.setName(name);
        taskData.setSlug(slag);
        taskStatusRepository.save(taskData);

        var label = new Label();
        var nameLabel = "feature";
        label.setName(nameLabel);
        labelRepository.save(label);

        label = new Label();
        nameLabel = "bug";
        label.setName(nameLabel);
        labelRepository.save(label);
    }
}
