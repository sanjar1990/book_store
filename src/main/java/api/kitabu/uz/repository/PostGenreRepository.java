package api.kitabu.uz.repository;

import api.kitabu.uz.entity.PostGenreEntity;
import api.kitabu.uz.mappers.GenreLangMapper;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

public interface PostGenreRepository extends JpaRepository<PostGenreEntity, String> {
    List<PostGenreEntity> findAllByPostIdAndVisible(String postId, Boolean visible);


    @Transactional
    @Modifying
    @Query("update PostGenreEntity  p set p.visible = ?1, p.deletedDate = ?2, p.deletedId = ?3 where  p.id = ?4")
    Integer deleteGenresById(Boolean visible, LocalDateTime deletedDate, String deletedId, String postId);

    @Query(value = """
               SELECT
                   pg.genre_id              as  id,
                   CASE ?1
                       WHEN 'uz' THEN g.title_uz
                       WHEN 'en' THEN g.title_en
                       ELSE g.title_ru
                       END            as name,
                   g.order_number     as orderNumber
               FROM post_genre pg 
               INNER JOIN genre g ON pg.genre_id = g.id
               WHERE pg.post_id = ?2 and pg.visible = ?3 order by g.order_number asc
            """, nativeQuery = true)
    List<GenreLangMapper> getPostGenreByLang(String lang, String postId, Boolean visible);
    @Transactional
    @Modifying
    @Query("update PostGenreEntity  p set p.visible = ?1, p.deletedDate = ?2, p.deletedId = ?3 where  p.postId = ?4")
    Integer deleteGenreByPost(Boolean visible, LocalDateTime deletedDate, String deletedId, String postId);

}
