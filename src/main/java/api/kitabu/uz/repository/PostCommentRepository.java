package api.kitabu.uz.repository;

import api.kitabu.uz.entity.PostCommentEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostCommentRepository extends CrudRepository<PostCommentEntity, String> {
    List<PostCommentEntity> getAllByVisibleTrue();
    Page<PostCommentEntity> findAllByPostIdAndVisibleTrue(String postId,Pageable pageable);
    @Query(value = "select *\n" +
            "from comment as c where c.post_id in (select p.id from post as p where p.profile_id = ?1 and p.status = 'ACTIVE') and c.is_read = FALSE order by c.created_date desc;",nativeQuery = true)
    Page<PostCommentEntity> getAllCommentByProfilePost(String profileId,Pageable pageable);
    @Transactional
    @Modifying
    @Query("update PostCommentEntity as c set c.isRead = ?2 where c.id = ?1")
    int isReadById(String id,Boolean read);
}
