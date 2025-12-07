package api.kitabu.uz.controller;

import api.kitabu.uz.dto.ApiResponse;
import api.kitabu.uz.dto.filter.post.FilterRequest;
import api.kitabu.uz.dto.filter.post.FilterResponse;
import api.kitabu.uz.dto.post.PostRequest;
import api.kitabu.uz.dto.post.PostResponse;
import api.kitabu.uz.enums.AppLanguage;
import api.kitabu.uz.enums.GeneralStatus;
import api.kitabu.uz.enums.Lang;
import api.kitabu.uz.service.PostService;
import api.kitabu.uz.usecases.PostUseCase;
import api.kitabu.uz.util.SpringSecurityUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/post")
@Slf4j
@Tag(name = "Post Api list")
public class PostController {
    @Autowired
    private PostUseCase<PostRequest, PostResponse> postUseCase;
    @Autowired
    private PostService postService;

    @PostMapping("")
    @PreAuthorize("hasRole('ROLE_USER')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Create post", description = "")
    public ResponseEntity<ApiResponse<String>> createPost(@Valid @RequestBody PostRequest postRequest,
                                                          @RequestHeader(value = "Accept-language", defaultValue = "uz") AppLanguage language) {
        log.info("Create post: {}", postRequest.title());
        return ResponseEntity.ok(postUseCase.createPost(postRequest, language));
    }

    @GetMapping("/public/{id}")
    @Operation(summary = "Get post by id as not authorized user", description = "Can get only active posts")
    public ResponseEntity<ApiResponse<PostResponse.PostResponseByLang>> getPost(@PathVariable("id") String postId,
                                                                                @RequestHeader(value = "Accept-Language", defaultValue = "uz") AppLanguage lang,
                                                                                @RequestHeader(value = "X-MAC-Address", required = false, defaultValue = "") String macAddress,
                                                                                HttpServletRequest request) {
        log.info("Get post by id: {}", postId);
        log.info("Received MAC Address: " + macAddress);
        return ResponseEntity.ok(postUseCase.getPost(postId, lang, null, request));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_USER')")
    @Operation(summary = "Get post by id as authorized user", description = "Ca get active and own not active posts")
    public ResponseEntity<ApiResponse<PostResponse.PostResponseByLang>> getPostAsAuthorizedUser(@PathVariable("id") String postId,
                                                                                                @RequestHeader(value = "Accept-Language", defaultValue = "uz") AppLanguage lang,
                                                                                                HttpServletRequest request) {
        log.info("Get post by id: {}", postId);
        return ResponseEntity.ok(postUseCase.getPost(postId, lang, SpringSecurityUtil.getCurrentUserId(), request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_USER','ROLE_ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Delete post by id", description = "")
    public ResponseEntity<ApiResponse<?>> deletePost(@PathVariable("id") String postId,
                                                     @RequestHeader(value = "Accept-language", defaultValue = "uz") AppLanguage language) {
        log.info("Delete post by id: {}", postId);
        postUseCase.deletePost(postId, language);
        return ResponseEntity.ok().build(); // TODO
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAnyRole('ROLE_USER','ROLE_ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Delete post by id for frontend", description = "")
    public ResponseEntity<ApiResponse<?>> deletePostByFront(@PathVariable("id") String postId,
                                                            @RequestHeader(value = "Accept-language", defaultValue = "uz") AppLanguage language) {
        log.info("Delete post by id frontend: {}", postId);
        postUseCase.deletePost(postId, language);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/delete_admin/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Delete admin post by id ", description = "")
    public ResponseEntity<ApiResponse<?>> deleteAdmin(@PathVariable("id") String postId,
                                                      @RequestHeader(value = "Accept-language", defaultValue = "uz") AppLanguage language) {
        log.info("delete by admin {} %s".formatted(postId));
        postUseCase.deletePostAdmin(postId, language);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_USER')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Update Post by Id", description = "")
    public ResponseEntity<ApiResponse<?>> updatePost(@PathVariable("id") String postId,
                                                     @Valid @RequestBody PostRequest postRequest,
                                                     @RequestHeader(value = "Accept-language", defaultValue = "uz") AppLanguage language) {
        log.info("Update Post by id: {}", postId);
        return ResponseEntity.ok(postUseCase.updatePost(postRequest, postId, language));
    }

    @GetMapping("/profile")
    @PreAuthorize("hasRole('ROLE_USER')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Get profile post List", description = "")
    public ResponseEntity<ApiResponse<PageImpl<PostResponse.PostShortInfoDTO>>> profilePostList(@RequestParam(value = "page", defaultValue = "1") int page,
                                                                                                @RequestParam(value = "size", defaultValue = "10") int size,
                                                                                                @RequestParam(value = "status", defaultValue = "ACTIVE") GeneralStatus status,
                                                                                                @RequestHeader(value = "Accept-Language", defaultValue = "uz") Lang lang) {
        log.info("Get profile post list");
        return ResponseEntity.ok(postService.getProfilePostList(SpringSecurityUtil.getCurrentUserId(), page, size, status, lang));
    }

    @GetMapping("/public/profilePosts/{id}")
    @Operation(summary = "Get Last Ten  posts Profile", description = "")
    public ResponseEntity<ApiResponse<PageImpl<PostResponse.PostShortInfoDTO>>> getLastTenPostsProfile(@PathVariable("id") String postId, @RequestParam(value = "page", defaultValue = "1") int page,
                                                                                                       @RequestParam(value = "size", defaultValue = "10") int size,
                                                                                                       @RequestParam(value = "status", defaultValue = "ACTIVE") GeneralStatus status,
                                                                                                       @RequestHeader(value = "Accept-Language", defaultValue = "uz") AppLanguage lang) {
        log.info("Get Last Ten  posts Profile");
        return ResponseEntity.ok(postService.getLastTenPostsProfile(postId, page, size, status, lang));
    }

    @PostMapping("/public/filter")
    @Operation(summary = "Filter post List", description = "")
    public ResponseEntity<ApiResponse<PageImpl<FilterResponse>>> filterPublic(@RequestBody FilterRequest filterRequest,
                                                                              @RequestHeader(value = "Accept-Language", defaultValue = "en") Lang appLang,
                                                                              @RequestParam(value = "page", defaultValue = "1") int page,
                                                                              @RequestParam(value = "size", defaultValue = "10") int size) {
        var filterResponse = postUseCase.filterPublic(filterRequest, appLang, page, size);
        return ResponseEntity.ok(filterResponse);
    }


    @PostMapping("/filter_admin")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @Operation(summary = "Filter post list as admin", description = "")
    public ResponseEntity<ApiResponse<PageImpl<FilterResponse>>> filterAsAdmin(@RequestBody FilterRequest filterRequest,
                                                                               @RequestHeader(value = "Accept-Language", defaultValue = "en") Lang appLang,
                                                                               @RequestParam(value = "page", defaultValue = "1") int page,
                                                                               @RequestParam(value = "size", defaultValue = "10") int size) {
        var filterResponse = postUseCase.filterAsAdmin(filterRequest, appLang, page, size);
        return ResponseEntity.ok(filterResponse);
    }

    @PutMapping("/status/{post_id}")
    @PreAuthorize("hasAnyRole('ROLE_USER')")
    @Operation(summary = "User change post  status", description = "")
    public ResponseEntity<ApiResponse<?>> postChangeStatus(@PathVariable("post_id") String postId,
                                                           @RequestHeader(value = "Accept-Language", defaultValue = "en") AppLanguage appLang) {
        var response = postUseCase.changeStatus(postId, appLang);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/status_admin")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @Operation(summary = "Admin  change  post  status", description = "")
    public ResponseEntity<ApiResponse<?>> postChangeStatusAdmin(@RequestParam String postId, @RequestParam GeneralStatus status,
                                                                @RequestHeader(value = "Accept-Language", defaultValue = "en") AppLanguage appLang) {
        var response = postUseCase.changeStatusAdmin(postId, status, appLang);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/liked")
    @PreAuthorize("hasAnyRole('ROLE_USER')")
    @Operation(summary = "Profile that returns posts that have been liked", description = "")
    public ResponseEntity<ApiResponse<List<PostResponse.PostShortInfoLike>>> haveBeenLiked(@RequestHeader(value = "Accept-Language", defaultValue = "en") AppLanguage appLang) {
        var response = postUseCase.haveBeenLiked(SpringSecurityUtil.getCurrentUserId(), appLang);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/dis_liked")
    @PreAuthorize("hasAnyRole('ROLE_USER')")
    @Operation(summary = "Profile that returns posts that have been dis liked", description = "")
    public ResponseEntity<ApiResponse<List<PostResponse.PostShortInfoLike>>> haveBeenDisLiked(@RequestHeader(value = "Accept-Language", defaultValue = "en") AppLanguage appLang) {
        var response = postUseCase.haveBeenDisLiked(SpringSecurityUtil.getCurrentUserId(), appLang);
        return ResponseEntity.ok(response);
    }

   /* @GetMapping("/public/test/{id}")
    @Operation(summary = "Get post by id as not authorized user", description = "Can get only active posts")
    public ResponseEntity<?> getPostTest(@PathVariable("id") String postId,
                                                                                HttpServletRequest request, HttpServletResponse response,
                                                                                @RequestHeader(value = "Accept-Language", defaultValue = "uz") AppLanguage lang) {
        log.info("Get post by id: {}", postId);
        return ResponseEntity.ok(postService.incrementViewCount(postId,request,response));
    }
*/
}
