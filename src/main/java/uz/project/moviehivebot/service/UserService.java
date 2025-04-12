package uz.project.moviehivebot.service;

import jakarta.transaction.Transactional;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import uz.project.moviehivebot.entity.User;
import uz.project.moviehivebot.entity.enums.Role;
import uz.project.moviehivebot.entity.enums.Status;
import uz.project.moviehivebot.repository.UserRepository;

import java.util.List;

@RequiredArgsConstructor
@Component
public class UserService {

    private final UserRepository userRepository;

    @Transactional
    public void checkExistingUser(org.telegram.telegrambots.meta.api.objects.User user) {
        User u = userRepository.getUserById(user.getId());
        if (u == null) {
            User newUser = new User();
            newUser.setId(user.getId());
            newUser.setFirstName(user.getFirstName());
            newUser.setLastName(user.getLastName());
            newUser.setStatus(Status.ACTIVE.toString());
            newUser.setUsername(user.getUserName());
            newUser.setRole(String.valueOf(Role.USER));
            userRepository.save(newUser);
        }
    }

    public boolean checkIsAdmin(Long userId) {
        User user = userRepository.getUserById(userId);
        return user.getRole().equals(Role.SUPER_ADMIN.toString()) || user.getRole().equals(Role.ADMIN.toString());
    }

    public User getUserByChatId( String chatId) {
       return userRepository.getUserById(Long.valueOf(chatId));
    }

    public List<User> findByRole(String role) {
       return userRepository.getAllUsersByRoleAndStatus(role , Status.ACTIVE.toString());
    }

    public void save(User user) {
        userRepository.save(user);
    }

    public Boolean checkIsActive(Long chatId) {

        User user = userRepository.getUserById(chatId);

        if (user == null){
            return true;
        }

        return user.getStatus().equals(Status.ACTIVE.toString());

    }

    public List<User> getUsers() {
       return userRepository.getAllUsersByRoleAndStatus(Role.USER.name() , Status.ACTIVE.name());
    }
}
