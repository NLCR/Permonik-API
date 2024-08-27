package cz.incad.nkp.inprove.permonikapi.me;

import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@Setter
@Getter
public class Me {

    private String id;
    private String name;
    private String email;
    private List<String> authorities;
    private List<String> owners;
    private Boolean enabled;
    private String username;
    private String role;
    private Boolean accountNonExpired;
    private Boolean accountNonLocked;
    private Boolean credentialsNonExpired;
}
