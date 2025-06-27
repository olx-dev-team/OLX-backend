package uz.pdp.backend.olxapp.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;
import org.hibernate.annotations.Check;
import org.hibernate.annotations.Checks;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import uz.pdp.backend.olxapp.entity.abstractEntity.LongIdAbstract;
import uz.pdp.backend.olxapp.enums.Role;

import java.util.Collection;
import java.util.List;

@Entity(name = "users")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SQLDelete(sql = "UPDATE users SET active = false WHERE id = ?")
@Checks({
        @Check(name = "check_email", constraints = "email LIKE '%@%'")
})
@SQLRestriction(value = "active=true")
@FieldNameConstants
public class User extends LongIdAbstract implements UserDetails {

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Favorites> favorites;

    @OneToMany(mappedBy = "receiver", fetch = FetchType.LAZY)
    private List<Notification> receivedNotifications;

    @OneToMany(mappedBy = "sender", fetch = FetchType.LAZY)
    private List<Notification> sentNotifications;

    @ManyToMany(fetch = FetchType.LAZY)
    private List<Product> products;

    @OneToMany(mappedBy = "userOne")
    private List<Chat> sentChats;

    @OneToMany(mappedBy = "userTwo")
    private List<Chat> receivedChats;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }
}