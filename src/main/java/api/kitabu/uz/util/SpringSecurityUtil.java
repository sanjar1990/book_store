package api.kitabu.uz.util;

import api.kitabu.uz.config.CustomDetails;
import api.kitabu.uz.entity.ProfileEntity;
import api.kitabu.uz.enums.ProfileRole;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SpringSecurityUtil {

    public static ProfileEntity getCurrentEntity() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomDetails user = (CustomDetails) authentication.getPrincipal();
        // System.out.println(user.getUsername());
        //Collection<GrantedAuthority> roles = (Collection<GrantedAuthority>) user.getAuthorities();
        // Collection<? extends GrantedAuthority> authorities = user.getAuthorities();
        return user.getProfile();
    }

    public static String getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomDetails user = (CustomDetails) authentication.getPrincipal();
        return user.getProfile().getId();
    }

    public static boolean containsRole(ProfileRole requiredRole) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomDetails user = (CustomDetails) authentication.getPrincipal();
        return user.getAuthorities().stream().anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals(requiredRole.name()));
    }
}
