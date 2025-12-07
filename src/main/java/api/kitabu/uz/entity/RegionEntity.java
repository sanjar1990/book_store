package api.kitabu.uz.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "region")
public class RegionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column
    private  String nameUz;
    @Column
    private String nameRu;
    @Column
    private String nameEn;
    @Column(name = "visible")
    private Boolean visible;
    @CreationTimestamp
    @Column(name = "created_date")
    private LocalDateTime createdDate;
}
