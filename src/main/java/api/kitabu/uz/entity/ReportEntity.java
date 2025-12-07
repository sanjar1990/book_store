package api.kitabu.uz.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "report")
public class ReportEntity {
    @UuidGenerator
    @Id
    private String id;

    @Column(name = "title")
    private String title;

    @Column(name = "post_id")
    private String postId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", updatable = false, insertable = false)
    private PostEntity post;

    @Column(name = "profile_id")
    private String profileId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id", updatable = false, insertable = false)
    private ProfileEntity profile;
}
