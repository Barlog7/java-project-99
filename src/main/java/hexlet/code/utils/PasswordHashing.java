package hexlet.code.utils;
import org.mindrot.jbcrypt.BCrypt;

public class PasswordHashing {
    public static String getHashPass(String password) {
        //String password = "password123";

        // Генерируем соль и хэш-значение пароля
        String salt = BCrypt.gensalt();
        String hashedPassword = BCrypt.hashpw(password, salt);

        // Проверяем пароль
       /* if (BCrypt.checkpw(password, hashedPassword)) {
            System.out.println("Пароль верный");
        } else {
            System.out.println("Пароль неверный");
        }*/
        return hashedPassword;
        //return password;
    }
}
