package api.kitabu.uz.repository;

import api.kitabu.uz.dto.post.PostShortInfo;
import api.kitabu.uz.entity.PostEntity;
import api.kitabu.uz.enums.AppLanguage;
import api.kitabu.uz.enums.GeneralStatus;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<PostEntity, String> {
    @Query(value = "select * from post p where p.visible = true and p.status in ('ACTIVE','IN_REVIEW') and p.id = ?1", nativeQuery = true)
    Optional<PostEntity> findByIdAndVisibleTrue(String id);

    Optional<PostEntity> findByIdAndVisibleIsTrue(String id);

    @Query("from PostEntity p where p.status = 'ACTIVE' and p.id = ?1")
    Optional<PostEntity> findByStatusActive(String postId);

    @Transactional
    @Modifying
    @Query("update PostEntity p set p.likeCount = coalesce(p.likeCount,0) + 1 where p.id = ?1")
    void likeCountIncrement(String id);

    @Transactional
    @Modifying
    @Query("update PostEntity p set p.dislikeCount = coalesce(p.dislikeCount,0) + 1 where p.id = ?1")
    void dislikeCountIncrement(String id);

    @Transactional
    @Modifying
    @Query("update PostEntity p set p.likeCount = coalesce(p.likeCount,0) - 1 where p.id = ?1 and p.likeCount > 0")
    void likeCountDecrement(String id);

    @Transactional
    @Modifying
    @Query("update PostEntity p set p.dislikeCount = coalesce(p.dislikeCount,0) - 1 where p.id = ?1 and p.dislikeCount > 0")
    void dislikeCountDecrement(String id);

    @Transactional
    @Modifying
    @Query("update PostEntity p set p.viewCount = coalesce(p.viewCount,0) + 1 where p.id = ?1")
    void viewCount(String postId);

    @Query(value = "SELECT p.profile_id  as profileId,\n" +
                   "                   p.id                    as postId,\n" +
                   "                   p.title                 as title,\n" +
                   "                   p.exchange_type         as type,\n" +
                   "                   p.created_date          as createdDate,\n" +
                   "                   (SELECT string_agg(CASE :lang WHEN 'uz' THEN g.title_uz  WHEN 'en' THEN g.title_en  ELSE g.title_ru END, ', ')\n" +
                   "                                      FROM post_genre pg  INNER JOIN genre g ON g.id = pg.genre_id\n" +
                   "                                      WHERE pg.post_id = p.id) AS genreNames, p.author_name as authorName , \n" +
                   "                   (select pa.attach_id from post_attach pa  where pa.post_id = p.id limit 1) as attachId,\n" +
                   "                   (SELECT CASE :lang  WHEN 'uz' THEN name_uz WHEN 'en' THEN name_en ELSE name_ru END  FROM region as r where r.id = p.region_id) as regionName ," +
                   "                   p.status as status \n" +
                   "            FROM post p\n" +
                   "            WHERE p.visible = true and p.status in (:status,'IN_REVIEW') and p.profile_id = :profileId order by p.created_date desc ",
            countQuery = "select count(*) from post p WHERE p.visible = true and p.status = :status  and p.profile_id = :profileId ", nativeQuery = true)
    Page<PostShortInfo> profilePostList(@Param("profileId") String profileId,
                                        @Param("lang") String lang,
                                        @Param("status") String status,
                                        Pageable pageable);


    @Transactional
    @Modifying
    @Query(value = "UPDATE post \n" +
                   "SET status = \n" +
                   "    CASE status\n" +
                   "        WHEN 'NOT_ACTIVE' THEN 'ACTIVE'\n" +
                   "        WHEN 'ACTIVE' THEN 'NOT_ACTIVE'\n" +
                   "    END\n" +
                   "WHERE id = ?1", nativeQuery = true)
    void changeStatus(String postId);

    @Transactional
    @Modifying
    @Query("update PostEntity p set p.status = 'BLOCKED' where p.id = ?1")
    void changeStatusBlock(String postId);

    @Query("from PostEntity p where p.visible = true and p.status = 'ACTIVE'")
    List<PostEntity> findAllByVisibleIsTrue();

    @Query(value = "SELECT *\n" +
            "FROM post p\n" +
            "WHERE (CURRENT_DATE - p.created_date::DATE) >= 10\n" +
            "  AND p.status = 'ACTIVE'",nativeQuery = true)
    List<PostEntity> getInactivePosts();

    @Query(value = "SELECT (SELECT string_agg(\n" +
                   "               CASE ?1 WHEN 'uz' THEN g.title_uz\n" +
                   "                                           WHEN 'en' THEN g.title_en\n" +
                   "                                           ELSE g.title_ru END, ', ')\n" +
                   "                        FROM post_genre pg\n" +
                   "                        INNER JOIN genre g ON g.id = pg.genre_id\n" +
                   "                        WHERE pg.post_id = p.id) AS genre_names\n" +
                   "                        from  post p\n" +
                   "    INNER JOIN post_genre pg ON p.id = pg.post_id\n" +
                   "    INNER JOIN genre g ON g.id = pg.genre_id\n" +
                   "  where p.id = ?2 limit 1", nativeQuery = true)
    Optional<String> genreNamesStringAgg(String lang, String postId);


    @Transactional
    @Modifying
    @Query("update PostEntity p set p.status = ?1 where p.id = ?2")
    void changeStatusAdmin(GeneralStatus status, String postId);

    @Transactional
    @Modifying
    @Query(value = "UPDATE PostEntity AS p set p.visible = false WHERE p.id = ?1")
    void deleteByBot(String postId);

    @Query("from PostEntity where profileId = ?1 and visible = true")
    Optional<List<PostEntity>> getProfilePostList(String profileId);

    @Query(value = "select get_admin_dashboard_data()", nativeQuery = true)
    String getAdminDashboardData();
}
