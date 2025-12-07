package api.kitabu.uz.dto.post;

import api.kitabu.uz.dto.FileResponse;
import api.kitabu.uz.dto.profile.ProfileResponse;
import api.kitabu.uz.enums.*;
import api.kitabu.uz.mappers.GenreLangMapper;
import api.kitabu.uz.mappers.RegionLangMapper;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record PostResponse(
        String id,
        Boolean visible,
        LocalDateTime createdDate,
        LocalDateTime deletedDate,
        String deletedId,
        Double price,
        Double marketPrice,
        String title,
        String authorName,
        String description,
        ExchangeType exchangeType,
        ConditionType conditionType,
        BookLanguage bookLanguage,
        BookPrintType bookPrintType,
        Double latitude,
        Double longitude,
        GeneralStatus status,
        Integer likeCount,
        Integer dislikeCount,
        Integer viewCount,
        String profileId,
        Integer regionId,
        ProfileResponse profile,
        List<FileResponse> attachList,
        List<PostGenreDTO> postGenreList

) {

    public record PostLikeAndDislike(
           Integer likeCount,
           Integer dislikeCount

            ) {
    }
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record PostShortInfoLike(
            String postId,
            FileResponse attachFindFirst,
            String title,
            RegionLangMapper regionName,
            LocalDateTime createdDate,
            ExchangeType type,
            String genreNames) {
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record PostShortInfoDTO(
            String postId,
            FileResponse attach,
            String title,
            String regionName,
            LocalDateTime createdDate,
            ExchangeType type,
            GeneralStatus status,
            String genreNames) {
    }

    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record PostResponseByLang(
            String id,
            Boolean visible,
            LocalDateTime createdDate,
            LocalDateTime deletedDate,
            String deletedId,
            String title,
            String authorName,
            String description,
            ExchangeType exchangeType,
            ConditionType conditionType,
            BookLanguage bookLanguage,
            BookPrintType bookPrintType,
            Double latitude,
            Double longitude,
            GeneralStatus status,
            Integer likeCount,
            Integer dislikeCount,
            Integer viewCount,
            Double price,
            Double marketPrice,
            RegionLangMapper regionShort,
            String likeStatus,
            ProfileResponse.ProfileResponseShort profile,
            List<FileResponse> attachList,
            List<GenreLangMapper> genreList
    ) {
    }
}
