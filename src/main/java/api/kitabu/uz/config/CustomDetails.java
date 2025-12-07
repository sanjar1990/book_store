package api.kitabu.uz.config;


import api.kitabu.uz.entity.ProfileEntity;
import api.kitabu.uz.enums.ProfileRole;
import api.kitabu.uz.service.ProfileRoleService;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

@Getter
public class CustomDetails implements UserDetails {
    private ProfileEntity profile;
    private List<SimpleGrantedAuthority> roleList = new LinkedList<>();
    @Autowired
    private ProfileRoleService profileRoleService;


    public CustomDetails(ProfileEntity profile, List<ProfileRole> roles) {
        this.profile = profile;
        roles.forEach(role -> {
            this.roleList.add(new SimpleGrantedAuthority(role.name()));
        });

    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roleList;
    }

    @Override
    public String getPassword() {
        return profile.getPassword();
    }

    @Override
    public String getUsername() {
        return profile.getPhone();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }


}
