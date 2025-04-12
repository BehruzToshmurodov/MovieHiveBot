package uz.project.moviehivebot.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User {

    @Id
    private Long id;
    private String firstName;
    private String lastName;
    private String username;
    private String status;
    private String role;

    private String state;

}
