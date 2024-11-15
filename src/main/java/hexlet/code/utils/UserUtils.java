package hexlet.code.utils;

import hexlet.code.model.User;
import hexlet.code.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class UserUtils {
    @Autowired
    private UserRepository userRepository;

    public User getCurrentUser() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }
        var email = authentication.getName();
        return userRepository.findByEmail(email).get();
    }

    public boolean isUserAllow(long userId) {
        var userConnectId = getCurrentUser().getId();
        return userConnectId == userId;
    }

    public boolean isUserAdmin() {
        var userConnectId = getCurrentUser().getId();
        var adminId = userRepository.findByEmail("hexlet@example.com").get().getId();
        if (getCurrentUser().getId() == adminId) {
            return true;
        }
        return false;
    }


}
