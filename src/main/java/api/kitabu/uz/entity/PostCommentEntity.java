package api.kitabu.uz.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Setter
@Getter
@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "comment")
public class PostCommentEntity extends BaseEntity {
    @Column(name = "content",columnDefinition = "TEXT")
    private String content;
    @Column
    private String postId;
    @Builder.Default
    @Column
    private Boolean isRead = Boolean.FALSE;
    @Column(name = "profile_Id")
    private String  profileId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_Id",insertable = false,updatable = false)
    private ProfileEntity profile;

}
