package api.kitabu.uz.config;


import api.kitabu.uz.exeptions.ProfileException;
import api.kitabu.uz.repository.ProfileRepository;
import api.kitabu.uz.service.ProfileRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomDetailService implements UserDetailsService {
    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private ProfileRoleService profileRoleService;

    @Override
    public UserDetails loadUserByUsername(String phone) throws UsernameNotFoundException {
        var optionalOfUser = profileRepository.findByPhoneAndVisible(phone, true);
        if (optionalOfUser.isEmpty()) {
            throw new ProfileException("Profile not found!");
        }

        return new CustomDetails(optionalOfUser.get(),profileRoleService.getByProfileId(optionalOfUser.get().getId()));
    }


}
