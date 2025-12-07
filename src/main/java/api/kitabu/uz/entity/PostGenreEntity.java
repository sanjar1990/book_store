package api.kitabu.uz.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Table(name = "post_genre")
@Entity
public class PostGenreEntity extends BaseEntity {
    @Column(name = "post_id")
    private String postId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", updatable = false, insertable = false)
    private PostEntity post;

    @Column(name = "genre_id")
    private String genreId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "genre_id", updatable = false, insertable = false)
    private GenreEntity genre;
}
