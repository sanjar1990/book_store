package api.kitabu.uz.usecases;

import api.kitabu.uz.dto.ApiResponse;
import api.kitabu.uz.dto.filter.post.FilterRequest;
import api.kitabu.uz.dto.filter.post.FilterResponse;
import api.kitabu.uz.dto.post.PostResponse;
import api.kitabu.uz.enums.AppLanguage;
import api.kitabu.uz.enums.GeneralStatus;
import api.kitabu.uz.enums.Lang;
import org.springframework.data.domain.PageImpl;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

public interface PostUseCase<REQUEST, RESPONSE> {
    ApiResponse<String > createPost(REQUEST requestPost,AppLanguage language);

    ApiResponse<?> deletePost(String postId,AppLanguage language);

    ApiResponse<?> updatePost(REQUEST requestPost, String postId, AppLanguage language);
    ApiResponse<PostResponse.PostResponseByLang> getPost(String postId, AppLanguage lang, String profileId, HttpServletRequest request);

    ApiResponse<PageImpl<PostResponse.PostShortInfoDTO>> getProfilePostList(String profileId, int page, int size, GeneralStatus status, Lang lang);

    ApiResponse<PageImpl<FilterResponse>> filterPublic(FilterRequest filterRequest,
                                          Lang appLang, int page, int size);

    ApiResponse<PageImpl<FilterResponse>> filterAsAdmin(FilterRequest filterRequest,
                                           Lang appLang, int page, int size);
    ApiResponse<?> changeStatus(String postId, AppLanguage appLang);

    ApiResponse<?> changeStatusAdmin(String postId, GeneralStatus status, AppLanguage appLang);

    ApiResponse<PageImpl<PostResponse.PostShortInfoDTO>> getLastTenPostsProfile(String postId, int page, int size, GeneralStatus status, AppLanguage lang);

    ApiResponse<List<PostResponse.PostShortInfoLike>> haveBeenLiked(String currentUserId, AppLanguage appLang);

    ApiResponse<List<PostResponse.PostShortInfoLike>> haveBeenDisLiked(String currentUserId, AppLanguage appLang);

    void deletePostAdmin(String postId, AppLanguage language);
}
