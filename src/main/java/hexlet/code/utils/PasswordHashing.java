package hexlet.code.utils;
import org.mindrot.jbcrypt.BCrypt;

public class PasswordHashing {
    public static String getHashPass(String password) {

        String salt = BCrypt.gensalt();
        String hashedPassword = BCrypt.hashpw(password, salt);

        return hashedPassword;
    }
}
