package api.kitabu.uz.usecases;

import api.kitabu.uz.dto.ApiResponse;
import api.kitabu.uz.dto.post.PostLikeRequest;
import api.kitabu.uz.dto.post.PostResponse;
import api.kitabu.uz.enums.AppLanguage;

public interface PostLikeUseCase<REQUEST> {
    ApiResponse<PostResponse.PostLikeAndDislike> create(REQUEST requestCreate, AppLanguage language, String currentUserId);
}
