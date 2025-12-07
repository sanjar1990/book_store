package api.kitabu.uz.repository;

import api.kitabu.uz.entity.SmsHistoryEntity;
import api.kitabu.uz.enums.SmsStatus;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

public interface SmsHistoryRepository extends CrudRepository<SmsHistoryEntity, String> {
    Long countByPhoneAndCreatedDateBetween(String phone, LocalDateTime toTime, LocalDateTime fromTime);
    Optional<SmsHistoryEntity> findTopByPhoneAndVisibleOrderByCreatedDateDesc(String phone,Boolean visible);
    @Modifying
    @Transactional
    @Query("update SmsHistoryEntity as s set s.status = ?2 where s.id = ?1")
    void updateStatus(String id, SmsStatus status);



}
