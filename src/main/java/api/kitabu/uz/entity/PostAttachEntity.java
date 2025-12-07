package api.kitabu.uz.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Table(name = "post_attach")
@Entity
public class PostAttachEntity extends BaseEntity {
    @Column(name = "post_id")
    private String postId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", updatable = false, insertable = false)
    private PostEntity post;

    @Column(name = "attach_id")
    private String attachId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attach_id", updatable = false, insertable = false)
    private AttachEntity attach;
}
