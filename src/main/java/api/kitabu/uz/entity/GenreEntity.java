package api.kitabu.uz.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

@Getter
@Setter
@Table(name = "genre")
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GenreEntity extends BaseEntity { // janr
    @Column(name = "title_uz")
    private String titleUz;
    @Column(name = "title_en")
    private String titleEn;
    @Column(name = "title_ru")
    private String titleRu;
    @Column(name = "order_number")
    private Integer orderNumber;
}
