package api.kitabu.uz.usecases;

import api.kitabu.uz.dto.ApiResponse;
import api.kitabu.uz.dto.filter.comment.CommentFilterRequest;
import api.kitabu.uz.dto.filter.comment.CommentFilterResponse;
import api.kitabu.uz.enums.AppLanguage;
import api.kitabu.uz.enums.Lang;
import org.springframework.data.domain.PageImpl;

import java.util.List;

public interface CommentUseCase<REQUEST, RESPONSE> {
    ApiResponse<RESPONSE> create(REQUEST request, AppLanguage language);
    ApiResponse<RESPONSE> getById(String id, AppLanguage language);
    ApiResponse<Boolean> delete(String id, AppLanguage language);
    ApiResponse<List<RESPONSE>> getAll();
    ApiResponse<PageImpl<RESPONSE>> pageable(String postId,int page,int size);
    ApiResponse<PageImpl<RESPONSE>> pageableByProfileId(int page,int size);
    ApiResponse<Boolean> isRead(String id, Boolean isRead);
    ApiResponse<PageImpl<CommentFilterResponse>> filterPublic(CommentFilterRequest filterRequest,Lang appLang, int page, int size);
    ApiResponse<PageImpl<CommentFilterResponse>> filterAsAdmin(CommentFilterRequest filterRequest,Lang appLang, int page, int size);
}
