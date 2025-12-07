package api.kitabu.uz.controller;

import api.kitabu.uz.dto.ApiResponse;
import api.kitabu.uz.dto.FileResponse;
import api.kitabu.uz.enums.AppLanguage;
import api.kitabu.uz.usecases.FileUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@RestController
@Slf4j
@RequestMapping("/api/v1/attach")
@Tag(name = "Attach Api list", description = "Api list for attach")
public class AttachController {

    private final FileUseCase<MultipartFile, FileResponse> fileUseCase;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//    @PreAuthorize("hasRole('ROLE_USER')")
//    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Upload image", description = "")
    public ResponseEntity<ApiResponse<FileResponse>> uploadFile(
            @RequestParam("file") MultipartFile multipartFile,
            @RequestHeader(value = "Accept-Language", defaultValue = "uz") AppLanguage language) {
        log.info("Image upload: {}", multipartFile.getName());
        var fileResponse = fileUseCase.uploadFile(multipartFile, language);
        return ResponseEntity.ok(fileResponse);
    }

    @GetMapping("/{imageId}")
    @Operation(summary = "Get image By Id", description = "")
    public ResponseEntity<ApiResponse<FileResponse>> getFile(@PathVariable String imageId,
                                                             @RequestHeader(value = "Accept-Language", defaultValue = "uz") AppLanguage language) {
        log.info("Get Image by Id: {}", imageId);
        System.out.println("fileId = " + imageId);
        var fileResponse = fileUseCase.getFile(imageId, language);
        return ResponseEntity.ok(fileResponse);
    }

    @DeleteMapping("/{fileId}")
    @Operation(summary = "Delete image By fileId", description = "")
    @PreAuthorize("hasRole('ROLE_USER')")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<ApiResponse<?>> deleteFile(@PathVariable String fileId,
                                                     @RequestHeader(value = "Accept-Language", defaultValue = "uz") AppLanguage language) {
        log.info("Delete image by name: {}", fileId);
        var response = fileUseCase.delete(fileId, language);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete/{fileId}")
    @Operation(summary = "Delete image By fileId", description = "")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<ApiResponse<?>> deleteFilePublic(@PathVariable String fileId,
                                                     @RequestHeader(value = "Accept-Language", defaultValue = "uz") AppLanguage language) {
        log.info("Delete image by id: {}", fileId);
        var response = fileUseCase.deletePublic(fileId, language);
        return ResponseEntity.ok(response);
    }

    @GetMapping(value = "/open/{fileId}", produces = MediaType.IMAGE_PNG_VALUE)
    @Operation(summary = "Open image by name", description = "")
    public byte[] openImage(@PathVariable String fileId) {
        log.info("Open image by name: {}", fileId);
        return fileUseCase.open(fileId);
    }
    @GetMapping(path = "/listOfAttaches")
    @Operation(summary = "Open image by name", description = "")
    public ResponseEntity<ApiResponse<PageImpl<FileResponse>>> getListOfAttaches(@RequestParam(value = "page", defaultValue = "1") int page,
                                                                                 @RequestParam(value = "size", defaultValue = "10") int size){
        log.info("getListOfAttaches");
        return ResponseEntity.ok(fileUseCase.getListOfAttaches(page,size));
    }

}
