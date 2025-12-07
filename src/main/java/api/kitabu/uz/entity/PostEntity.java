package api.kitabu.uz.entity;

import api.kitabu.uz.enums.*;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "post")
@Entity
public class PostEntity extends BaseEntity {
    @Column(name = "title", columnDefinition = "text")
    private String title;

    @Column(name = "description", columnDefinition = "text")
    private String description;

    @Column(name = "author_name")
    private String authorName;

    @Enumerated(EnumType.STRING)
    @Column(name = "exchange_type")
    private ExchangeType exchangeType;

    @Enumerated(EnumType.STRING)
    @Column(name = "condition_type")
    private ConditionType conditionType;

    @Enumerated(EnumType.STRING)
    @Column(name = "book_language")
    private BookLanguage bookLanguage;

    @Enumerated(EnumType.STRING)
    @Column(name = "book_print_type")
    private BookPrintType bookPrintType;

    @Column(name = "latitude")
    private Double latitude;
    @Column(name = "longitude")
    private Double longitude;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private GeneralStatus status;
    @Builder.Default
    @Column(name = "like_count")
    private Integer likeCount = 0;
    @Column(name = "dislike_count")
    @Builder.Default
    private Integer dislikeCount = 0;
    @Builder.Default
    @Column(name = "view_count")
    private Integer viewCount = 0;

    @Column(name = "price")
    private Double price;

    @Column(name = "market_price")
    private Double marketPrice;

    @Column(name = "profile_id")
    private String profileId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id", updatable = false, insertable = false)
    private ProfileEntity profile;

    @Column(name = "region_id")
    private Integer regionId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "region_id", updatable = false, insertable = false)
    private RegionEntity region;
}
