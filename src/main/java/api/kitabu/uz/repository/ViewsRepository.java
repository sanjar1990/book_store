package api.kitabu.uz.repository;

import api.kitabu.uz.entity.ViewEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ViewsRepository extends JpaRepository<ViewEntity,Integer> {
    Optional<ViewEntity> findByPostIdAndUserIpAddress(String postId,String userIpAddress);
}
