package api.kitabu.uz.entity;

import api.kitabu.uz.dto.FileResponse;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name = "attach")
@Entity
public class AttachEntity {
    @Id
    private String id;
    @Column
    private String compressedId;
    @Column
    private String filename;
    @Column
    private String extension;
    @Column
    private String path;
    @Column
    private Long size;
    @Column
    private Boolean visible;
    @Column
    @CreationTimestamp
    private LocalDateTime createdDate;

    public FileResponse mapToResponse() {
        return FileResponse.builder()
                .id(this.id)
                .size(this.size)
                .build();
    }
}
