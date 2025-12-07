package api.kitabu.uz.repository;

import api.kitabu.uz.entity.GenreEntity;
import api.kitabu.uz.mappers.GenreLangMapper;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface GenreRepository extends JpaRepository<GenreEntity, String> {
    Optional<GenreEntity> findByIdAndVisible(String genreId, Boolean visible);
    List<GenreEntity> findAllByVisibleOrderByOrderNumberAsc(Boolean visible);
    Optional<GenreEntity> findByOrderNumberAndVisible(Integer orderNumber, Boolean visible);
    @Query(value = """
               SELECT
                   g.id              as  id,
                   CASE ?1
                       WHEN 'uz' THEN g.title_uz
                       WHEN 'en' THEN g.title_en
                       ELSE g.title_ru
                       END           as name,
                   g.created_date    as createdDate,
                   g.order_number    as orderNumber
               FROM
                   genre g
               WHERE g.visible = true order by g.order_number asc
            """, nativeQuery = true)
    List<GenreLangMapper> getGenreByLang(String lang);
    @Transactional
    @Modifying
    @Query("update GenreEntity g set g.titleUz = ?1, g.titleRu = ?2, g.titleEn = ?3, g.orderNumber = ?4 where g.id=?5")
    int update(String titleUz, String titleRu, String titleEn, Integer orderNumber, String genreId);

    @Query("select id from GenreEntity where titleUz = ?1 and visible = true ")
    String getByName(String nameUz);


}
