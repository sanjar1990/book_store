package api.kitabu.uz.repository;

import api.kitabu.uz.entity.ProfileRoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

public interface ProfileRoleRepository extends JpaRepository<ProfileRoleEntity, String> {
    List<ProfileRoleEntity> findAllByProfileIdAndVisible(String profileId, Boolean visible);
    @Transactional
    @Modifying
    @Query("update ProfileRoleEntity  r set r.visible = ?1, r.deletedDate = ?2, r.deletedId = ?3 where  r.id = ?4")
    Integer deleteRoleById(Boolean visible, LocalDateTime deletedDate, String deletedId, String profileId);
    @Transactional
    @Modifying
    @Query("update ProfileRoleEntity  r set r.visible = ?1, r.deletedDate = ?2, r.deletedId = ?3 where  r.profileId = ?4")
    Integer deleteRolesByProfile(Boolean visible, LocalDateTime deletedDate, String deletedId, String profileId);
    @Transactional
    @Modifying
    @Query("update ProfileRoleEntity  r set r.visible = ?1 where  r.profileId = ?2")
    Integer updateVisible(Boolean visible,  String profileId);

    @Transactional
    @Modifying
    @Query("delete from ProfileRoleEntity pr where pr.profileId = ?1")
    void deleteProfile(String id);
}
