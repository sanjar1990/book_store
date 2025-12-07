package api.kitabu.uz.repository;

import api.kitabu.uz.entity.AttachEntity;
import api.kitabu.uz.entity.PostAttachEntity;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PostAttachRepository extends JpaRepository<PostAttachEntity, String> {
    @Query("select  p.attachId from PostAttachEntity  p where p.postId =?1")
    List<String> finAllByPostId(String postId);

    @Query(value = "select  p.attach_id from post_attach  p where p.post_id =?1 limit 1", nativeQuery = true)
    String finAllByPostIdLimit(String postId);

    @Transactional
    @Modifying
    @Query("delete from PostAttachEntity where postId =?1 and attachId =?2")
    void delete(String postId, String attachId);

    @Query(value = "select  p.attachId from PostAttachEntity as p")
    List<String> findAllByPostId(String profileId);

    @Transactional
    @Modifying
    @Query("update PostAttachEntity  p set p.visible = ?1, p.deletedDate = ?2, p.deletedId = ?3 where  p.postId = ?4")
    Integer deleteAttachesByPost(Boolean visible, LocalDateTime deletedDate, String deletedId, String postId);

    @Transactional
    @Modifying
    @Query("delete PostAttachEntity p where p.attachId = ?1")
    void deleteAttachesByAttachId(String attachId);
}
