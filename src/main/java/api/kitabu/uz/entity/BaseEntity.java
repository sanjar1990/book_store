package api.kitabu.uz.entity;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@MappedSuperclass
public class BaseEntity {
    @Id
    @GeneratedValue(generator = "uuid-hibernate-generator")
    @GenericGenerator(name = "uuid-hibernate-generator", strategy = "org.hibernate.id.UUIDGenerator")
    private String id;
    @Column(name = "visible")
    private Boolean visible = Boolean.TRUE;
    @CreationTimestamp
    @Column(name = "created_date")
    private LocalDateTime createdDate;
    @Column(name = "deleted_date")
    private LocalDateTime deletedDate;
    @Column(name = "deletedId")
    private String deletedId;
}
