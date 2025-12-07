package api.kitabu.uz.service;

import api.kitabu.uz.telegramBot.dto.PostCreateBot;
import api.kitabu.uz.telegramBot.dto.PostFullData;
import api.kitabu.uz.dto.ApiResponse;
import api.kitabu.uz.dto.filter.post.FilterRequest;
import api.kitabu.uz.dto.filter.post.FilterResponse;
import api.kitabu.uz.dto.post.PostRequest;
import api.kitabu.uz.dto.post.PostResponse;
import api.kitabu.uz.dto.post.PostShortInfo;
import api.kitabu.uz.dto.profile.ProfileResponse;
import api.kitabu.uz.entity.PostEntity;
import api.kitabu.uz.entity.ViewEntity;
import api.kitabu.uz.enums.AppLanguage;
import api.kitabu.uz.enums.GeneralStatus;
import api.kitabu.uz.enums.Lang;
import api.kitabu.uz.enums.ProfileRole;
import api.kitabu.uz.exeptions.exceptionhandler.APIException;
import api.kitabu.uz.repository.PostRepository;
import api.kitabu.uz.repository.ViewsRepository;
import api.kitabu.uz.repository.custom.PostCustomRepository;
import api.kitabu.uz.telegramBot.service.TelegramBotService;
import api.kitabu.uz.usecases.PostUseCase;
import api.kitabu.uz.util.PageUtil;
import api.kitabu.uz.util.PostViewCookieUtil;
import api.kitabu.uz.util.SpringSecurityUtil;

import javax.servlet.http.HttpServletResponse;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import jakarta.servlet.http.HttpServletRequest;

/*
 * @author Raufov Ma`ruf
 * */
@Service
@Slf4j
@Transactional
public class PostService implements PostUseCase<PostRequest, PostResponse> {
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private ViewsRepository viewsRepository;
    @Autowired
    private PostAttachService postAttachService;
    @Autowired
    private RegionService regionService;
    @Autowired
    private PostCustomRepository postCustomRepository;
    @Autowired
    private PostGenreService postGenreService;
    @Autowired
    private AttachService attachService;
    @Autowired
    private PostLikeService postLikeService;
    @Autowired
    private ResourceBundleService resourceBundleService;
    private final TelegramBotService telegramBotService;

    public PostService(@Lazy TelegramBotService telegramBotService) {
        this.telegramBotService = telegramBotService;
    }


    @Override
    public ApiResponse<String> createPost(PostRequest postRequest, AppLanguage language) {
        var entity = PostEntity
                .builder()
                .title(postRequest.title())
                .authorName(postRequest.authorName())
                .description(postRequest.description())
                .exchangeType((postRequest.exchangeType()))
                .conditionType((postRequest.conditionType()))
                .bookLanguage((postRequest.bookLanguage()))
                .bookPrintType((postRequest.bookPrintType()))
                .longitude(postRequest.longitude())
                .latitude(postRequest.latitude())
                .price(postRequest.price())
                .marketPrice(postRequest.marketPrice())
                .status(GeneralStatus.ACTIVE)
//                .status(GeneralStatus.IN_REVIEW)
                .regionId(regionService.get(postRequest.regionId(), language).getId())
                .profileId(SpringSecurityUtil.getCurrentUserId())
                .build();
        //database save
        postRepository.save(entity);
        // create post attaches
        postAttachService.create(entity.getId(), postRequest.attachIdList());
        // create post genres
        postGenreService.create(entity.getId(), postRequest.genreIdList());
        //send telegram channel
        telegramBotService.sendToChannel(entity, postRequest.attachIdList());
        return new ApiResponse<>(200, false, entity.getId());
    }

    public ApiResponse<String> createPostForBot(PostCreateBot postRequest) {
        var entity = PostEntity
                .builder()
                .title(postRequest.getTitle())
                .authorName(postRequest.getAuthorName())
                .description(postRequest.getDescription())
                .exchangeType((postRequest.getExchangeType()))
                .conditionType((postRequest.getCondition()))
                .bookLanguage((postRequest.getLanguage()))
                .bookPrintType((postRequest.getBookPrintType()))
                .status(GeneralStatus.ACTIVE)
                .price(postRequest.getPrice())
                .marketPrice(postRequest.getMarketPrice())
                .regionId(postRequest.getRegionId())
                .profileId(postRequest.getProfileId())
                .build();
        //database save
        postRepository.save(entity);
        // create post attaches
        postAttachService.create(entity.getId(), postRequest.getImages());
        // create post genres
        postGenreService.create(entity.getId(), postRequest.getGenres());
        //send telegram channel
        telegramBotService.sendToChannel(entity, postRequest.getImages());
        return new ApiResponse<>(200, false, entity.getId());
    }

    @Transactional
    @Override
    public ApiResponse<?> updatePost(PostRequest postRequest, String postId, AppLanguage language) {
        var post = getPostId(postId, language);
        if (!post.getProfileId().equals(SpringSecurityUtil.getCurrentUserId())) {
            throw new APIException(resourceBundleService.getMessage("this.post.not.belongs.to.you", language.name()), 403);
        }
        post.setTitle(postRequest.title());
        post.setDescription(postRequest.description());
        post.setAuthorName(postRequest.authorName());
        post.setExchangeType(postRequest.exchangeType());
        post.setConditionType(postRequest.conditionType());
        post.setBookLanguage(postRequest.bookLanguage());
        post.setBookPrintType(postRequest.bookPrintType());
        post.setLongitude(postRequest.longitude());
        post.setLatitude(postRequest.latitude());
        post.setPrice(postRequest.price());
        post.setRegionId(postRequest.regionId());
//        post.setStatus(GeneralStatus.IN_REVIEW);
        post.setStatus(GeneralStatus.ACTIVE);
        post.setMarketPrice(postRequest.marketPrice());
        post.setDeletedId(SpringSecurityUtil.getCurrentUserId());
        postRepository.save(post);
        // update genres
        postGenreService.merge(postId, postRequest.genreIdList());
        // update attaches
        postAttachService.merge(post.getId(), postRequest.attachIdList());
        return new ApiResponse<>(200, false, post.getId());
    }

    @Override
    public ApiResponse<?> deletePost(String postId, AppLanguage language) {
        var post = postRepository.findByIdAndVisibleIsTrue(postId);
        if (post.isEmpty()) throw new APIException("Post does not exist", 404);

        // owner or admin can delete post
        if (SpringSecurityUtil.containsRole(ProfileRole.ROLE_ADMIN) ||
                post.get().getProfileId().equals(SpringSecurityUtil.getCurrentUserId())) {
            post.get().setVisible(Boolean.FALSE);
            //    post.get().setStatus(GeneralStatus.NOT_ACTIVE);
            post.get().setDeletedDate(LocalDateTime.now());
            post.get().setDeletedId(SpringSecurityUtil.getCurrentUserId());
            postRepository.save(post.get());
            // delete attaches for post
            postAttachService.deleteAllAttachesByPostId(postId);
            // delete genres for post
            postGenreService.deleteAllByPostId(postId);
            return ApiResponse.ok();
        } else {
            throw new APIException(resourceBundleService.getMessage("this.post.not.belongs.to.you", language.name()), 403);
        }
    }

    public void deletePostByBot(String postId) {
        postRepository.deleteByBot(postId);
    }

    @Override
    public void deletePostAdmin(String postId, AppLanguage language) {
        var post = postRepository.findByIdAndVisibleIsTrue(postId);
        if (post.isEmpty()) throw new APIException("Post does not exist", 404);
        if (SpringSecurityUtil.containsRole(ProfileRole.ROLE_ADMIN)) {
            post.get().setVisible(Boolean.FALSE);
            post.get().setStatus(GeneralStatus.NOT_ACTIVE);
            post.get().setDeletedDate(LocalDateTime.now());
            post.get().setDeletedId(SpringSecurityUtil.getCurrentUserId());
            postRepository.save(post.get());
            postAttachService.deleteAllAttachesByPostId(postId);
            postGenreService.deleteAllByPostId(postId);
        } else {
            throw new APIException(resourceBundleService.getMessage("this.post.not.belongs.to.you", language.name()), 403);
        }
    }

    @Override
    public ApiResponse<PostResponse.PostResponseByLang> getPost(String postId, AppLanguage lang, String profileId, HttpServletRequest request) {
        increaseViewCount(postId, request);
        PostEntity post = getPostId(postId, lang);
        if (!post.getStatus().equals(GeneralStatus.ACTIVE) && !post.getStatus().equals(GeneralStatus.IN_REVIEW)) { // if status not active
            if (!post.getProfileId().equals(profileId)) { // profile should be owner
                throw new APIException("Post Not Found", 404);
            }
        }
        return new ApiResponse<>(200, false, mapToFullInfoByLang(lang, postId, profileId).apply(post));
    }

   /* public PostEntity incrementViewCount(String postId, HttpServletRequest request, HttpServletResponse response) {
        if (PostViewCookieUtil.hasViewedPost(request, postId)) {
            return postRepository.findById(postId)
                    .orElseThrow(() -> new RuntimeException("Post topilmadi"));
        }

        PostEntity post = postRepository.findByIdAndVisibleIsTrue(postId)
                .orElseThrow(() -> new RuntimeException("Post topilmadi"));

        post.setViewCount(post.getViewCount() + 1);
        postRepository.save(post);

        PostViewCookieUtil.addViewedPostCookie(response, postId);

        return post;
    }*/

    private void increaseViewCount(String postId, HttpServletRequest request) {
        log.info("ip address 2: ******   " + getUserIP(request)); // 185.213.229.9

        var optional = viewsRepository.findByPostIdAndUserIpAddress(postId, getUserIP(request));
        if (optional.isEmpty()) {
            var viewEntity = ViewEntity
                    .builder()
                    .userIpAddress(getUserIP(request))
                    .postId(postId)
                    .build();
            viewsRepository.save(viewEntity);
            postRepository.viewCount(postId);
        }
    }

    public String getUserIP(HttpServletRequest request) {
        String ipAddress = request.getHeader("X-Forwarded-For");
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getRemoteAddr();
        }
        return ipAddress;
    }

    public PostEntity getPostId(String postId, AppLanguage language) {
        return postRepository
                .findByIdAndVisibleTrue(postId)
                .orElseThrow(() -> new APIException(resourceBundleService.getMessage("post.not.found", language.name()), 404));
    }

    private Function<PostEntity, PostResponse.PostResponseByLang> mapToFullInfoByLang(AppLanguage language, String postId, String profileId) {
        return postEntity -> PostResponse.PostResponseByLang.builder()
                .id(postEntity.getId())
                .bookLanguage(postEntity.getBookLanguage())
                .bookPrintType(postEntity.getBookPrintType())
                .conditionType(postEntity.getConditionType())
                .exchangeType(postEntity.getExchangeType())
                .latitude(postEntity.getLatitude())
                .longitude(postEntity.getLongitude())
                .title(postEntity.getTitle())
                .authorName(postEntity.getAuthorName())
                .likeCount(postEntity.getLikeCount())
                .dislikeCount(postEntity.getDislikeCount())
                .createdDate(postEntity.getCreatedDate())
                .status(postEntity.getStatus())
                .price(postEntity.getPrice())
                .marketPrice(postEntity.getMarketPrice())
                .description(postEntity.getDescription())
                .likeStatus(postLikeService.getStatus(postEntity.getId(), profileId))
                .profile(new ProfileResponse.ProfileResponseShort(
                        postEntity.getProfile().getId(),
                        postEntity.getProfile().getName(),
                        postEntity.getProfile().getSurname(),
                        postEntity.getProfile().getPhone(),
                        attachService.asUrlString(postEntity.getProfile().getPhotoId())
                ))
                .viewCount(postEntity.getViewCount())
                .regionShort(regionService.getRegionLang(language, postId))
                .attachList(postAttachService.getPostAttachList(postEntity.getId())) // set attach
                .genreList(postGenreService.getAllGenrePostsByPostIdAndLanguage(postEntity.getId(), language)) // set postAttaches
                .build();
    }

    @Override
    public ApiResponse<PageImpl<PostResponse.PostShortInfoDTO>> getProfilePostList(String profileId, int page, int size, GeneralStatus status, Lang lang) {
        PageRequest paging = PageRequest.of(PageUtil.getPage(page), size);
        Page<PostShortInfo> pageObj = postRepository.profilePostList(SpringSecurityUtil.getCurrentUserId(),
                lang.name(), status.name(), paging);
        return getPageApiResponse(paging, pageObj);
    }

    @Override
    public ApiResponse<PageImpl<FilterResponse>> filterPublic(FilterRequest dto, Lang appLang, int page, int size) {
        var pageable = PageRequest.of(PageUtil.getPage(page), size, Sort.by("createdDate"));
        var filterResponse = postCustomRepository.filter(dto, appLang, page - 1, size, false);
        return new ApiResponse<>(200, false, new PageImpl<>(filterResponse.getList(), pageable, filterResponse.getTotalCount()));
    }

    @Override
    public ApiResponse<PageImpl<FilterResponse>> filterAsAdmin(FilterRequest dto, Lang appLang, int page, int size) {
        var pageable = PageRequest.of(PageUtil.getPage(page), size, Sort.by("createdDate"));
        var filterResponse = postCustomRepository.filter(dto, appLang, page - 1, size, true);
        return new ApiResponse<>(200, false, new PageImpl<>(filterResponse.getList(), pageable, filterResponse.getTotalCount()));
    }

    @Override
    public ApiResponse<?> changeStatus(String postId, AppLanguage appLang) {
        var post = getPostId(postId, appLang);
        postRepository.changeStatus(post.getId());
        return ApiResponse.ok();
    }

    @Override
    public ApiResponse<?> changeStatusAdmin(String postId, GeneralStatus status, AppLanguage appLang) {
        var post = postRepository.findById(postId);
        if (post.isEmpty()) throw new APIException("post Not found");
        postRepository.changeStatusAdmin(status, postId);
        return ApiResponse.ok();
    }

    @Override
    public ApiResponse<List<PostResponse.PostShortInfoLike>> haveBeenLiked(String currentUserId, AppLanguage appLang) {
        var response = postLikeService
                .haveBeenLiked(currentUserId, appLang).stream()
                .map(postRepository::findByStatusActive) // Stream<Optional<PostEntity>>
                .flatMap(Optional::stream) // Stream<PostEntity>
                .map(postEntity -> new PostResponse.PostShortInfoLike(
                        postEntity.getId(),
                        postAttachService.getPostAttachLimitOne(postEntity.getId()),
                        postEntity.getTitle(),
                        regionService.getRegionLang(appLang, postEntity.getId()),
                        postEntity.getCreatedDate(),
                        postEntity.getExchangeType(),
                        genreNamesStringAgg(postEntity.getId(), appLang)))
                .collect(Collectors.toList());

        return ApiResponse.ok(response);
    }

    @Override
    public ApiResponse<List<PostResponse.PostShortInfoLike>> haveBeenDisLiked(String currentUserId, AppLanguage appLang) {
        var response = postLikeService
                .haveBeenDisLiked(currentUserId, appLang).stream()
                .map(postRepository::findByStatusActive) // Stream<Optional<PostEntity>>
                .flatMap(Optional::stream) // Stream<PostEntity>
                .map(postEntity -> new PostResponse.PostShortInfoLike(
                        postEntity.getId(),
                        postAttachService.getPostAttachLimitOne(postEntity.getId()),
                        postEntity.getTitle(),
                        regionService.getRegionLang(appLang, postEntity.getId()),
                        postEntity.getCreatedDate(),
                        postEntity.getExchangeType(),
                        genreNamesStringAgg(postEntity.getId(), appLang)))
                .collect(Collectors.toList());

        return ApiResponse.ok(response);
    }

    @Override
    public ApiResponse<PageImpl<PostResponse.PostShortInfoDTO>> getLastTenPostsProfile(String postId, int page,
                                                                                       int size,
                                                                                       GeneralStatus status,
                                                                                       AppLanguage lang) {
        PageRequest paging = PageRequest.of(page - 1, size);
        Page<PostShortInfo> pageObj = postRepository.profilePostList(getPostId(postId, lang).getProfileId(),
                lang.name(), status.name(), paging);
        return getPageApiResponse(paging, pageObj);
    }

    public List<PostEntity> getAllPosts() {
        return postRepository.findAllByVisibleIsTrue();
    }

    public List<PostEntity> getInactivePosts() {
        return postRepository.getInactivePosts();
    }

    public void blockPost(PostEntity post) {
        postRepository.changeStatusBlock(post.getId());
    }

    private ApiResponse<PageImpl<PostResponse.PostShortInfoDTO>> getPageApiResponse(PageRequest paging, Page<PostShortInfo> pageObj) {
        List<PostResponse.PostShortInfoDTO> dtoList = new LinkedList<>();
        for (PostShortInfo post : pageObj) {
            dtoList.add(new PostResponse.PostShortInfoDTO(
                    post.getPostId(),
                    attachService.toDTO(post.getAttachId()),
                    post.getTitle(),
                    post.getRegionName(),
                    post.getCreatedDate(),
                    post.getType(),
                    post.getStatus(),
                    post.getGenreNames()
            ));
        }
        return new ApiResponse<>(200, false, new PageImpl<>(dtoList, paging, pageObj.getTotalElements()));
    }

    public String genreNamesStringAgg(String postId, AppLanguage appLang) {
        var genreNamesResponse = postRepository.genreNamesStringAgg(appLang.name(), postId);
        if (genreNamesResponse.isEmpty()) {
            throw new APIException("Genre Names String Agg not found");
        }
        return genreNamesResponse.get();
    }

    public PostFullData getProfilePosts(String profileId) {
        var profilePostList = postRepository.getProfilePostList(profileId);
        PostFullData fullData = null;
        if (profilePostList.isPresent()) {
            fullData = new PostFullData();
            Map<String, List<String>> images = new HashMap<>();
            fullData.setPostEntityList(profilePostList.get());
            for (PostEntity postEntity : profilePostList.get()) {
                images.put(postEntity.getId(), postAttachService.getPostImageIdList(postEntity.getId()));
            }
            fullData.setImages(images);
            return fullData;
        }
        return null;
    }
}
