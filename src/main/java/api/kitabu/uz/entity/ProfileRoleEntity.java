package api.kitabu.uz.entity;

import api.kitabu.uz.enums.ProfileRole;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Table(name = "profile_role")
@Entity
public class ProfileRoleEntity extends BaseEntity {
    @Column(name = "profile_id")
    private String profileId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id", updatable = false, insertable = false)
    private ProfileEntity profile;
    @Enumerated(EnumType.STRING)
    private ProfileRole role;
}
