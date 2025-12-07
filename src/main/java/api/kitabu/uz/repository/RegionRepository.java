package api.kitabu.uz.repository;

import api.kitabu.uz.entity.RegionEntity;
import api.kitabu.uz.mappers.RegionLangMapper;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RegionRepository extends JpaRepository<RegionEntity,Integer> {
    List<RegionEntity> getAllByVisibleIsTrueOrderByCreatedDateDesc();
    @Query(value = """
               SELECT
                   r.id   as  id,
                   CASE ?1
                       WHEN 'uz' THEN r.name_uz
                       WHEN 'en' THEN r.name_en
                       ELSE r.name_ru
                       END           as name
               FROM
                   region r
               WHERE r.visible = true
            """, nativeQuery = true)
    List<RegionLangMapper> getRegionByLang(String lang);
    @Query(value = """
               SELECT
                   r.id   as  id,
                   CASE ?1
                       WHEN 'uz' THEN r.name_uz
                       WHEN 'en' THEN r.name_en
                       ELSE r.name_ru
                       END           as name
               FROM
                   region r 
               INNER JOIN post p on p.region_id = r.id
               WHERE r.visible = true and p.id = ?2
            """, nativeQuery = true)
    RegionLangMapper getRegion(String lang, String postId);

    @Query("select id from RegionEntity where nameUz = ?1 ")
    Integer getByNameUz(String nameUz);



}
