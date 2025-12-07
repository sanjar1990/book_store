package api.kitabu.uz.repository;

import api.kitabu.uz.entity.PostLikeEntity;
import api.kitabu.uz.enums.PostLikeStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;


@Repository
public interface PostLikeRepository extends JpaRepository<PostLikeEntity, Integer> {
    PostLikeEntity findByProfileIdAndPostId(String profileId, String postId);

    @Transactional
    @Modifying
    @Query("update PostLikeEntity p set p.status = 'LIKE' where p.profileId = ?1 AND p.postId = ?2")
    void updateLike(String profileId, String postId);

    @Transactional
    @Modifying
    @Query("update PostLikeEntity p set p.status = 'DISLIKE' where p.profileId = ?1 AND p.postId = ?2")
    void updateDislike(String profileId, String postId);

    @Transactional
    @Modifying
    @Query("delete from PostLikeEntity p where p.profileId = ?1 and p.postId = ?2")
    void deleted(String profileId, String postId);

    @Query(value = "select pl.status from post_like pl where pl.post_id = ?1 and pl.profile_id = ?2", nativeQuery = true)
    Optional<String> findByPostId(String postId, String profileId);

    @Query("select p.postId from PostLikeEntity p where p.status = 'LIKE' and p.profileId = ?1")
    List<String> findByProfileId(String profileId);

    @Query("select p.postId from PostLikeEntity p where p.status = 'DISLIKE' and p.profileId = ?1")
    List<String> findByProfileIdDisLiked(String profileId);
}
