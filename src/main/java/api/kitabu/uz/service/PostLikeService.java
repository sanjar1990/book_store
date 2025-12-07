package api.kitabu.uz.service;

import api.kitabu.uz.dto.ApiResponse;
import api.kitabu.uz.dto.post.PostLikeRequest;
import api.kitabu.uz.dto.post.PostResponse;
import api.kitabu.uz.entity.PostLikeEntity;
import api.kitabu.uz.enums.AppLanguage;
import api.kitabu.uz.enums.PostLikeStatus;
import api.kitabu.uz.exeptions.exceptionhandler.APIException;
import api.kitabu.uz.repository.PostLikeRepository;
import api.kitabu.uz.repository.PostRepository;
import api.kitabu.uz.repository.ProfileRepository;
import api.kitabu.uz.usecases.PostLikeUseCase;
import api.kitabu.uz.util.SpringSecurityUtil;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/*
 * @author Raufov Ma`ruf
 * */

@Service
@Transactional
public class PostLikeService implements PostLikeUseCase<PostLikeRequest> {
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private PostLikeRepository postLikeRepository;
    @Autowired
    private ProfileRepository profileRepository;

    private final PostService postService;

    public PostLikeService(@Lazy PostService postService) {
        this.postService = postService;
    }

    @Autowired
    private ResourceBundleService resourceBundleService;

    @Override
    public ApiResponse<PostResponse.PostLikeAndDislike> create(PostLikeRequest requestCreate, AppLanguage language, String currentUserId) {
        check(requestCreate, language, currentUserId);
        var response = isPresent(requestCreate, currentUserId);
        if (response != null) {
            if (requestCreate.getStatus().equals(PostLikeStatus.LIKE)
                && response.getStatus().equals(PostLikeStatus.LIKE)) {
                delete(requestCreate, currentUserId);
                postRepository.likeCountDecrement(requestCreate.getPostId());
                return ApiResponse.ok(new PostResponse.PostLikeAndDislike(
                        postService.getPostId(requestCreate.getPostId(), language).getLikeCount(),
                        postService.getPostId(requestCreate.getPostId(), language).getDislikeCount()

                ));
            } else if (requestCreate.getStatus().equals(PostLikeStatus.DISLIKE)
                       && response.getStatus().equals(PostLikeStatus.DISLIKE)) {
                delete(requestCreate, currentUserId);
                postRepository.dislikeCountDecrement(requestCreate.getPostId());
                return ApiResponse.ok(new PostResponse.PostLikeAndDislike(
                        postService.getPostId(requestCreate.getPostId(), language).getLikeCount(),
                        postService.getPostId(requestCreate.getPostId(), language).getDislikeCount()

                ));
            } else if (requestCreate.getStatus().equals(PostLikeStatus.LIKE)) {
                if (response.getStatus().equals(PostLikeStatus.DISLIKE)) {
                    postLikeRepository.updateLike(currentUserId, requestCreate.getPostId());
                    postRepository.likeCountIncrement(requestCreate.getPostId());
                    postRepository.dislikeCountDecrement(requestCreate.getPostId());
                    return ApiResponse.ok(new PostResponse.PostLikeAndDislike(
                            postService.getPostId(requestCreate.getPostId(), language).getLikeCount(),
                            postService.getPostId(requestCreate.getPostId(), language).getDislikeCount()

                    ));
                }
            } else if (requestCreate.getStatus().equals(PostLikeStatus.DISLIKE)) {
                if (response.getStatus().equals(PostLikeStatus.LIKE)) {
                    postLikeRepository.updateDislike(currentUserId, requestCreate.getPostId());
                    postRepository.likeCountDecrement(requestCreate.getPostId());
                    postRepository.dislikeCountIncrement(requestCreate.getPostId());
                    return ApiResponse.ok(new PostResponse.PostLikeAndDislike(
                            postService.getPostId(requestCreate.getPostId(), language).getLikeCount(),
                            postService.getPostId(requestCreate.getPostId(), language).getDislikeCount()

                    ));
                }
            }
        } else {
            if (requestCreate.getStatus().equals(PostLikeStatus.LIKE)) {
                postRepository.likeCountIncrement(requestCreate.getPostId());
            } else {
                postRepository.dislikeCountIncrement(requestCreate.getPostId());
            }
            var entity = PostLikeEntity
                    .builder()
                    .profileId(SpringSecurityUtil.getCurrentUserId())
                    .postId(requestCreate.getPostId())
                    .status(requestCreate.getStatus())
                    .build();
            postLikeRepository.save(entity);
            return ApiResponse.ok(new PostResponse.PostLikeAndDislike(
                    postService.getPostId(requestCreate.getPostId(),language).getLikeCount(),
                    postService.getPostId(requestCreate.getPostId(),language).getDislikeCount()
            ));
        }
        return null;
    }

    private void check(PostLikeRequest requestCreate, AppLanguage language, String currentUserId) {
        if (!profileRepository.existsById(currentUserId))
            throw new APIException(resourceBundleService.getMessage("profile.not.found", language.name()), 404);
        if (!postRepository.existsById(requestCreate.getPostId()))
            throw new APIException(resourceBundleService.getMessage("profile.not.found", language.name()), 404);
    }

    private PostLikeEntity isPresent(PostLikeRequest requestCreate, String currentUserId) {
        return postLikeRepository
                .findByProfileIdAndPostId(currentUserId
                        , requestCreate.getPostId());
    }

    public ApiResponse<Boolean> delete(PostLikeRequest requestDelete, String currentUserId) {
        postLikeRepository.deleted(currentUserId, requestDelete.getPostId());
        return ApiResponse.ok();
    }

    public String getStatus(String postId, String profileId) {
        if (profileId == null || postId == null) {
            return null;
        }
        return postLikeRepository.findByPostId(postId, profileId).orElse(null);
    }

    public List<String> haveBeenLiked(String profileId, AppLanguage language) {
        var postIds = postLikeRepository.findByProfileId(profileId);
        if (postIds.isEmpty()) {
            return new ArrayList<>();
        }
        return postIds;
    }

    public List<String> haveBeenDisLiked(String profileId, AppLanguage language) {
        var postIds = postLikeRepository.findByProfileIdDisLiked(profileId);
        System.out.println(profileId);
        if (postIds.isEmpty()) {
            return new ArrayList<>();
        }
        return postIds;
    }
}
