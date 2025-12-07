package api.kitabu.uz.controller;

import api.kitabu.uz.dto.ApiResponse;
import api.kitabu.uz.dto.comment.PostCommentRequest;
import api.kitabu.uz.dto.comment.PostCommentResponse;
import api.kitabu.uz.dto.filter.comment.CommentFilterRequest;
import api.kitabu.uz.dto.filter.comment.CommentFilterResponse;
import api.kitabu.uz.enums.AppLanguage;
import api.kitabu.uz.enums.Lang;
import api.kitabu.uz.usecases.CommentUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
@Slf4j
@RestController
@RequestMapping(value = "/api/v1/comment")
@Tag(name = "Comment Api List",description = "List of API for Commnet")
public class CommentController {

    @Autowired
    private CommentUseCase<PostCommentRequest,PostCommentResponse> commentUseCase;

    @PostMapping(value = "" )
    @PreAuthorize("hasRole('ROLE_USER')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Create comment")
    public ResponseEntity<ApiResponse<PostCommentResponse>> create(@RequestBody PostCommentRequest request,
                                                                   @RequestHeader(value = "Accept-language",defaultValue = "uz")AppLanguage language){
        log.info("PostComment: {}",request.postId());
        return ResponseEntity.ok(commentUseCase.create(request,language));
    }
    @GetMapping(value = "/{id}")
    @PreAuthorize("hasRole('ROLE_USER')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Get Comment ById")
    public ResponseEntity<ApiResponse<PostCommentResponse>> getById(@PathVariable("id") String id,
                                                                    @RequestHeader(value = "Accept-language",defaultValue = "uz")AppLanguage language){
        log.info("Get Comment by id: {}",id);
        return ResponseEntity.ok(commentUseCase.getById(id,language));
    }
    @DeleteMapping(value = "/{id}")
    @PreAuthorize("hasRole('ROLE_USER')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Delete Commnet By Id")
    public ResponseEntity<ApiResponse<Boolean>> delete(@PathVariable("id") String id,
                                                       @RequestHeader(value = "Accept-language",defaultValue = "uz")AppLanguage language){
        log.info("Delete comment by id: {}",id);
        return ResponseEntity.ok(commentUseCase.delete(id,language));
    }
    @GetMapping(value = "/pagination/{id}")
    @Operation(summary = "Post comment pagination")
    public ResponseEntity<ApiResponse<PageImpl<PostCommentResponse>>> pagination(@PathVariable("id")String postId,@RequestParam(value = "page",defaultValue = "1") int page,
                                                           @RequestParam(value = "size",defaultValue = "10") int size){
        log.info("Comment paging: {},{}",page,size);
        return ResponseEntity.ok(commentUseCase.pageable(postId,page,size));
    }
    @GetMapping(value = "/paginationByProfileId")
    @Operation(summary = "Pagination of Comment by ProfileId")
    public ResponseEntity<ApiResponse<PageImpl<PostCommentResponse>>> paginationByProfileId(@RequestParam(value = "page",defaultValue = "1") int page,
                                                           @RequestParam(value = "size",defaultValue = "10") int size){
        log.info("Comment paging: {},{}",page,size);
        return ResponseEntity.ok(commentUseCase.pageableByProfileId(page,size));
    }
    @PutMapping(value = "/isReadByCommentId/{id}")
    @Operation(summary = "Reading status",description = "")
    @PreAuthorize("hasRole('ROLE_USER')")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<ApiResponse<Boolean>> isRead(@PathVariable("id")String id,@RequestParam Boolean isRead) {
        return ResponseEntity.ok(commentUseCase.isRead(id, isRead));
    }
    /*@PostMapping("/public/filter")
    @Operation(summary = "Filter comment list as user", description = "")
    public ResponseEntity<ApiResponse<PageImpl<CommentFilterResponse>>> filterPublic(@RequestBody CommentFilterRequest filterRequest,
                                                                                     @RequestHeader(value = "Accept-Language", defaultValue = "en") Lang appLang,
                                                                                     @RequestParam(value = "page", defaultValue = "1") int page,
                                                                                     @RequestParam(value = "size", defaultValue = "10") int size) {
        var filterResponse = commentUseCase.filterPublic(filterRequest, appLang, page, size);
        return ResponseEntity.ok(filterResponse);
    }*/
    @PostMapping("/filter_admin")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Filter comment list as admin", description = "")
    public ResponseEntity<ApiResponse<PageImpl<CommentFilterResponse>>> filterAsAdmin(@RequestBody CommentFilterRequest filterRequest,
                                                                               @RequestHeader(value = "Accept-Language", defaultValue = "en") Lang appLang,
                                                                               @RequestParam(value = "page", defaultValue = "1") int page,
                                                                               @RequestParam(value = "size", defaultValue = "10") int size) {
        var filterResponse = commentUseCase.filterAsAdmin(filterRequest, appLang, page, size);
        return ResponseEntity.ok(filterResponse);
    }
}
