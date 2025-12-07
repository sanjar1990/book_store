package api.kitabu.uz.repository;

import api.kitabu.uz.entity.AttachEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface AttachRepository extends JpaRepository<AttachEntity,String> {


    Page<AttachEntity> findAll(Pageable pageable);

    Optional<AttachEntity> findByIdAndVisibleTrue(String attachId);

    @Transactional
    @Modifying
    @Query("update  AttachEntity a set a.visible = false  where a.id = ?1")
    void deleteByIdAttach(String attachId);

    @Transactional
    @Modifying
    @Query("update  AttachEntity a set a.visible = false  where a.id = ?1")
    void deleteByIdResized(String attachId);
}
