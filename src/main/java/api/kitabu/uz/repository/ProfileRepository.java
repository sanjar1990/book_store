package api.kitabu.uz.repository;


import api.kitabu.uz.dto.profile.ProfileResponse;
import api.kitabu.uz.entity.ProfileEntity;
import api.kitabu.uz.enums.GeneralStatus;
import api.kitabu.uz.mappers.ProfileMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProfileRepository extends JpaRepository<ProfileEntity, String> {
    Optional<ProfileEntity> findByPhoneAndVisible(String phone, Boolean visible);

    Optional<ProfileEntity> findByIdAndVisible(String id, Boolean visible);

    Optional<ProfileEntity> findByPhoneAndPasswordAndVisible(String phone, String pswd, Boolean visible);

    List<ProfileEntity> findAllByVisible(Boolean visible);

    @Transactional
    @Modifying
    @Query("update ProfileEntity p set p.name = ?1, p.surname = ?2,  p.status = ?3,  p.password = ?4 where  p.id = ?5")
    Integer update(String name, String surname, GeneralStatus status, String password, String id);

    @Transactional
    @Modifying
    @Query("update ProfileEntity  p set p.photoId = ?1 where p.id = ?2")
    Integer updatePhoto(String photoId, String profileId);

    @Transactional
    @Modifying
    @Query("update ProfileEntity  p set p.visible = ?1, p.deletedDate = ?2, p.deletedId = ?3 where  p.id = ?4")
    Integer delete(Boolean visible, LocalDateTime deletedDate, String deletedId, String profileId);

    @Transactional
    @Modifying
    @Query("update ProfileEntity  p set p.visible = ?1, p.deletedDate = ?2 where  p.id = ?3")
    Integer deleteNotActiveUser(Boolean visible, LocalDateTime deletedDate, String profileId);

    @Modifying
    @Transactional
    @Query("update ProfileEntity set status = ?2 where  id = ?1")
    void updateStatus(String id, GeneralStatus active);

    @Transactional
    @Modifying
    @Query("update ProfileEntity set password =:nPswd where id =:id")
    int updatePassword(@Param("id") String id, @Param("nPswd") String nPswd);

    @Transactional
    @Modifying
    @Query("update ProfileEntity set tempPhone = :newPhone  where id=:id")
    void changeNewPhone(@Param("id") String id,
                        @Param("newPhone") String newPhone);

    @Transactional
    @Modifying
    @Query("update ProfileEntity set phone = :phone where id=:id")
    int changePhone(@Param("id") String id,
                    @Param("phone") String phone);

    @Transactional
    @Modifying
    @Query("update ProfileEntity as p set p.name =:name,p.surname =:surname where p.id =:id")
    int updateDeteil(String name, String surname, String id);

    @Transactional
    @Modifying
    @Query("update ProfileEntity as p set p.password =:newPassword where p.id =:id and p.password =:oldPassword")
    int updatePassword(String newPassword, String oldPassword, String id);


    @Query(value = "select p.id as id,p.name as name," +
            " p.surname as surname ,p.phone as phone , " +
            "p.password as password ,p.photo_id as photoId , " +
            "p.status as status , p.created_date as createdDate from profile p", nativeQuery = true)
    Page<Object[]> getAllPagination(PageRequest paging);


    @Transactional
    @Modifying
    @Query("update ProfileEntity  set status = 'NOT_ACTIVE' where id = ?1")
    int currentProfileDelete(String currentUserId);

    @Transactional
    @Modifying
    @Query("update ProfileEntity set visible = false where id = ?1")
    void deleteProfile(String id);
}
