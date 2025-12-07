package api.kitabu.uz.service;

import api.kitabu.uz.dto.ApiResponse;
import api.kitabu.uz.dto.comment.PostCommentRequest;
import api.kitabu.uz.dto.comment.PostCommentResponse;
import api.kitabu.uz.dto.filter.comment.CommentFilterRequest;
import api.kitabu.uz.dto.filter.comment.CommentFilterResponse;
import api.kitabu.uz.entity.PostCommentEntity;
import api.kitabu.uz.enums.AppLanguage;
import api.kitabu.uz.enums.Lang;
import api.kitabu.uz.exeptions.exceptionhandler.APIException;
import api.kitabu.uz.repository.PostCommentRepository;
import api.kitabu.uz.repository.custom.CommentCustomRepository;
import api.kitabu.uz.usecases.CommentUseCase;
import api.kitabu.uz.util.PageUtil;
import api.kitabu.uz.util.SpringSecurityUtil;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
/*
 * @author Abduhoshim
 * */

@Service
public class PostCommentService implements CommentUseCase<PostCommentRequest, PostCommentResponse> {
    @Autowired
    private PostCommentRepository commentRepository;
    @Autowired
    private PostService postService;
    @Autowired
    private ResourceBundleService resourceBundleService;
    @Autowired
    private ProfileService profileService;
    @Autowired
    private CommentCustomRepository commentCustomRepository;

    @Override
    public ApiResponse<PostCommentResponse> create(PostCommentRequest postCommentRequest, AppLanguage language) {
        PostCommentEntity entity = PostCommentEntity.builder()
                .content(postCommentRequest.content())
                .postId(postService.getPostId(postCommentRequest.postId(), language).getId())
                .profileId(SpringSecurityUtil.getCurrentUserId())
                .isRead(Boolean.FALSE)
                .build();
        commentRepository.save(entity);
        return new ApiResponse<>(200, false, mapToResponse().apply(entity));
    }

    @Override
    public ApiResponse<PostCommentResponse> getById(String id, AppLanguage language) {
        return new ApiResponse<>(200, false, mapToResponse().apply(get(id, language)));
    }

    @Override
    public ApiResponse<List<PostCommentResponse>> getAll() {
        return new ApiResponse<>(200, false, commentRepository.getAllByVisibleTrue().stream().map(mapToResponse()).toList());
    }

    @Transactional
    @Override
    public ApiResponse<Boolean> delete(String id, AppLanguage language) {
        boolean t = true;
        Optional<PostCommentEntity> optional = commentRepository.findById(id);
        if (optional.isEmpty()) {
            t = false;
            throw new APIException(resourceBundleService.getMessage("comment.not.found", language.name()));
        }
        PostCommentEntity entity = optional.get();
        entity.setVisible(false);
        commentRepository.save(entity);
        return new ApiResponse<>(200, false, t);
    }
    @Override
    public ApiResponse<PageImpl<PostCommentResponse>> pageable(String postId,int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size,Sort.by("createdDate").descending());
        Page<PostCommentEntity> pageObj = commentRepository.findAllByPostIdAndVisibleTrue(postId,pageable);
        List<PostCommentResponse> postCommentList = new ArrayList<>();
        pageObj.map(entity -> postCommentList.add(mapToResponse().apply(entity)));
        return new ApiResponse<>(200, false, new PageImpl<>(postCommentList, pageable, pageObj.getTotalElements()));
    } @Override
    public ApiResponse<PageImpl<PostCommentResponse>> pageableByProfileId(int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<PostCommentEntity> pageObj = commentRepository.getAllCommentByProfilePost(SpringSecurityUtil.getCurrentUserId(),pageable);
        List<PostCommentResponse> postCommentList = new ArrayList<>();
        pageObj.map(entity -> postCommentList.add(mapToResponse().apply(entity)));
        return new ApiResponse<>(200, false, new PageImpl<>(postCommentList,pageable, pageObj.getTotalElements()));
    }
    @Override
    public ApiResponse<Boolean> isRead(String id,Boolean isRead){
        return new ApiResponse<>(200,false, commentRepository.isReadById(id, isRead) == 1);
    }
    public PostCommentEntity get(String id, AppLanguage language) {
        return commentRepository
                .findById(id)
                .orElseThrow(() -> new APIException(resourceBundleService.getMessage("comment.not.found", language.name())));
    }
    private Function<PostCommentEntity, PostCommentResponse> mapToResponse() {
        return postCommentEntity -> PostCommentResponse.builder()
                .id(postCommentEntity.getId())
                .profile(profileService.getByIdComment(postCommentEntity.getProfileId()))
                .postId(postCommentEntity.getPostId())
                .postTitle(postService.getPostId(postCommentEntity.getPostId(),AppLanguage.en).getTitle())
                .content(postCommentEntity.getContent())
                .isRead(postCommentEntity.getIsRead())
                .visible(postCommentEntity.getVisible())
                .createdDate(postCommentEntity.getCreatedDate()).build();
    }
    @Override
    public ApiResponse<PageImpl<CommentFilterResponse>> filterPublic(CommentFilterRequest dto, Lang appLang, int page, int size) {
        var pageable = PageRequest.of(PageUtil.getPage(page), size, Sort.by("createdDate"));
        var filterResponse = commentCustomRepository.filter(dto, appLang, PageUtil.getPage(page), size, false);
        return new ApiResponse<>(200, false, new PageImpl<>(filterResponse.getList(), pageable, filterResponse.getTotalCount()));
    }

    @Override
    public ApiResponse<PageImpl<CommentFilterResponse>> filterAsAdmin(CommentFilterRequest dto, Lang appLang, int page, int size) {
        var pageable = PageRequest.of(PageUtil.getPage(page), size, Sort.by("createdDate"));
        var filterResponse = commentCustomRepository.filter(dto, appLang, PageUtil.getPage(page), size, true);
        return new ApiResponse<>(200, false, new PageImpl<>(filterResponse.getList(), pageable, filterResponse.getTotalCount()));
    }
}
