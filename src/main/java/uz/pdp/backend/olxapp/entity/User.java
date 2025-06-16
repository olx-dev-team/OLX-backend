package uz.pdp.backend.olxapp.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import uz.pdp.backend.olxapp.entity.abstractEntity.LongIdAbstract;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Entity(name = "users")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class User extends LongIdAbstract implements UserDetails{

    private String firstName;

    private String lastName;

    private String username;

    private String password;

    private String email;

    private String phoneNumber;

    @ManyToOne
    @JoinColumn(name = "role_id")
    private Role role;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Favorites> favorites;

    @OneToMany(mappedBy = "receiver", fetch = FetchType.LAZY)
    private List<Notification> receivedNotifications;

    @OneToMany(mappedBy = "sender", fetch = FetchType.LAZY)
    private List<Notification> sentNotifications;

    @ManyToMany(fetch = FetchType.LAZY)
    private List<Product> products;

    @OneToMany(mappedBy = "sender")
    private List<Chat> sentChats;

    @OneToMany(mappedBy = "receiver")
    private List<Chat> receivedChats;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return role.getPermissions().stream()
                .map(permission -> new SimpleGrantedAuthority(permission.name()))
                .collect(Collectors.toList());

    }
}
