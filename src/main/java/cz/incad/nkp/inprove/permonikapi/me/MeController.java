package cz.incad.nkp.inprove.permonikapi.me;

import cz.incad.nkp.inprove.permonikapi.config.security.user.UserDelegate;
import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@AllArgsConstructor
public class MeController {


    @GetMapping("/me")
    public Me getCurrentUser(@AuthenticationPrincipal @Nullable UserDelegate userDetails) {

        if (userDetails == null) {
            return null;
        }

        Me me = new Me();

        me.setId(userDetails.getId());
        me.setName(userDetails.getUser().getFirstName() + " " + userDetails.getUser().getLastName());
        me.setEmail(userDetails.getUser().getEmail());
        me.setAuthorities(userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList()));
        me.setOwners(userDetails.getUser().getOwners());
        me.setEnabled(userDetails.isEnabled());
        me.setRole(userDetails.getUser().getRole());
        me.setUsername(userDetails.getUsername());
        me.setAccountNonExpired(userDetails.isAccountNonExpired());
        me.setAccountNonLocked(userDetails.isAccountNonLocked());
        me.setCredentialsNonExpired(userDetails.isCredentialsNonExpired());

        return me;
    }
}
