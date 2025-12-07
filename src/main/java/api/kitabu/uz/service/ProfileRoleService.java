package api.kitabu.uz.service;

import api.kitabu.uz.entity.ProfileRoleEntity;
import api.kitabu.uz.enums.ProfileRole;
import api.kitabu.uz.repository.ProfileRepository;
import api.kitabu.uz.repository.ProfileRoleRepository;
import api.kitabu.uz.util.SpringSecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

@Service
public class ProfileRoleService {
    @Autowired
    private ProfileRoleRepository profileRoleRepository;


    public void merge(String profileId, List<ProfileRole> roles) {
        List<ProfileRoleEntity> oldRoleList = profileRoleRepository.findAllByProfileIdAndVisible(profileId,true);
        // create new role
        for (ProfileRole role : roles) {
            if (!containsRole(oldRoleList, role)) {
                create(profileId, role);
            }
        }
        // remove role
        for (ProfileRoleEntity entity : oldRoleList) {
            if (!roles.contains(entity.getRole())) {
                profileRoleRepository.deleteRoleById(false,LocalDateTime.now(), SpringSecurityUtil.getCurrentUserId(),entity.getId());
            }
        }
    }

    public void create(String profileId, ProfileRole role) {
        ProfileRoleEntity profileRoleEntity = new ProfileRoleEntity();
        profileRoleEntity.setRole(role);
        profileRoleEntity.setProfileId(profileId);
        profileRoleRepository.save(profileRoleEntity);
    }

    public void createForNotFullyReg(String profileId, ProfileRole role) {
        ProfileRoleEntity profileRoleEntity = new ProfileRoleEntity();
        profileRoleEntity.setProfileId(profileId);
        profileRoleEntity.setRole(role);
        profileRoleEntity.setVisible(true);
        profileRoleRepository.save(profileRoleEntity);
    }

    public void updateVisible(String profileId, Boolean visible) {
        profileRoleRepository.updateVisible(visible, profileId);
    }


    private boolean containsRole(List<ProfileRoleEntity> oldRoleList, ProfileRole role) {
        for (ProfileRoleEntity profileRole : oldRoleList) {
            if (profileRole.getRole().equals(role)) {
                return true;
            }
        }
        return false;
    }


    public void deleteAllByProfileId(String profileId){
        profileRoleRepository.deleteRolesByProfile(false,LocalDateTime.now(),SpringSecurityUtil.getCurrentUserId(),profileId);

    }


    public List<ProfileRole> getByProfileId(String id) {
        List<ProfileRoleEntity> allEntity = profileRoleRepository.findAllByProfileIdAndVisible(id, true);
        List<ProfileRole> roles = new LinkedList<>();
        allEntity.forEach(entity->{roles.add(entity.getRole());} );
        return roles;
    }

    public void deleteProfile(String id) {
        profileRoleRepository.deleteProfile(id);
    }
}
