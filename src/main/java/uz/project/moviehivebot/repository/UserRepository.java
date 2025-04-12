package uz.project.moviehivebot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.project.moviehivebot.entity.User;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    User getUserById(Long id);
    List<User> getAllUsersByRoleAndStatus( String role , String status);
}
