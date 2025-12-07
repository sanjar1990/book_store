package api.kitabu.uz.entity;

import api.kitabu.uz.enums.AppLanguage;
import api.kitabu.uz.enums.GeneralStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Table(name = "profile")
@Entity
public class ProfileEntity extends BaseEntity {
    @Column
    private String name;
    @Column
    private String surname;
    @Column
    private String phone;
    @Column
    private String tempPhone;
    @Column
    private String password;
    @Column(name = "app_language")
    private AppLanguage appLanguage;

    @Enumerated(EnumType.STRING)
    private GeneralStatus status;
    @CreationTimestamp
    private LocalDateTime createdDate;

    @Column(name = "photo_id")
    private String photoId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "photo_id", updatable = false, insertable = false)
    private AttachEntity attach;

    @OneToMany(mappedBy = "profile")
    private List<PostCommentEntity> commentEntityList;
    @Column
    private String signature;

    private Boolean visible = Boolean.TRUE;
}
