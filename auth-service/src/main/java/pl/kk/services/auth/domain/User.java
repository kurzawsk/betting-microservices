package pl.kk.services.auth.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.Size;
import java.util.Set;


@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "USER_")
public class User {

    @Id
    @Column(updatable = false, nullable = false)
    @Size(min = 3, max = 50)
    private String username;

    @Size(min = 3, max = 500)
    private String password;

    @Email
    @Size(min = 5, max = 50)
    private String email;

    private boolean activated;

    @Size(max = 100)
    @Column(name = "activationkey")
    private String activationKey;

    @Size(max = 100)
    @Column(name = "resetpasswordkey")
    private String resetPasswordKey;

    @ManyToMany
    @JoinTable(name = "user_authority", joinColumns = @JoinColumn(name = "username"), inverseJoinColumns = @JoinColumn(name = "authority"))
    private Set<Authority> authorities;

}
