package api.kitabu.uz.entity;

import api.kitabu.uz.enums.PostLikeStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "post_like", uniqueConstraints = @UniqueConstraint(columnNames = {"profile_id", "post_id"}))
public class PostLikeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "profile_id")
    private String profileId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id", updatable = false, insertable = false)
    private ProfileEntity profile;
    @Column(name = "post_id")
    private String postId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", updatable = false, insertable = false)
    private PostEntity post;
    @CreationTimestamp
    private LocalDateTime createdDate;
    @Enumerated(EnumType.STRING)
    private PostLikeStatus status;

}
