package api.kitabu.uz.controller;

import api.kitabu.uz.dto.ApiResponse;
import api.kitabu.uz.dto.genre.GenreRequest;
import api.kitabu.uz.dto.genre.GenreResponse;
import api.kitabu.uz.dto.profile.ProfileRequest;
import api.kitabu.uz.dto.profile.ProfileRequestUpdate;
import api.kitabu.uz.dto.profile.ProfileResponse;
import api.kitabu.uz.dto.profile.ProfileResponseDetail;
import api.kitabu.uz.enums.AppLanguage;
import api.kitabu.uz.mappers.GenreLangMapper;
import api.kitabu.uz.usecases.GenreUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RestController
@Log4j2
@RequestMapping("/api/v1/genre")
public class GenreController {

    @Autowired
    private GenreUseCase<GenreRequest, GenreResponse> genreUseCase;
    @PostMapping("/create")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Create genre", description = "")
    public ResponseEntity<ApiResponse<GenreResponse>> create(@RequestBody GenreRequest genreRequest,
                                                             @RequestHeader(value = "Accept-Language",defaultValue = "uz")AppLanguage language) {
        log.info("Genre create: {}",genreRequest.titleUz(), genreRequest.titleRu(), genreRequest.titleEn());
        return ResponseEntity.ok(genreUseCase.create(genreRequest,language));
    }

    @PutMapping("/update/{genreId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Update Genre by Id", description = "")
    public ResponseEntity<ApiResponse<GenreResponse>> update(@RequestBody GenreRequest genreRequest,
                                                             @PathVariable String genreId,
                                                             @RequestHeader(value = "Accept-Language",defaultValue = "uz")AppLanguage language) {
        log.info("Genre update by id: {}",genreId);
        return ResponseEntity.ok(genreUseCase.update(genreRequest, genreId,language));
    }

    @DeleteMapping("/delete/by/{genreId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Delete Genre by Id", description = "")
    public ResponseEntity<ApiResponse<GenreResponse.GenreResponseFull> >  deleteById(@PathVariable String genreId,
                                                                                     @RequestHeader(value = "Accept-Language",defaultValue = "uz")AppLanguage language) {
        log.info("Delete genre by id: {}",genreId);
        return ResponseEntity.ok(genreUseCase.delete(genreId,language));
    }

    @GetMapping("/get/by/{genreId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Genre get By id", description = "")
    public ResponseEntity<ApiResponse<GenreResponse>>getById(@PathVariable String genreId,
                                                             @RequestHeader(value = "Accept-Language",defaultValue = "uz")AppLanguage language) {
        log.info("Get genre by id: {}",genreId);
        return ResponseEntity.ok(genreUseCase.getGenre(genreId,language));
    }

    @GetMapping("/get/all/by/lang")
    @Operation(summary = "Get All genre by Language" , description = "")
    public ResponseEntity<ApiResponse<List<GenreLangMapper>>> getAllByLang(@RequestHeader(value = "Accept-Language", defaultValue = "uz") AppLanguage lang) {
        log.info("Get genre list by lang: {}");
        System.out.println(lang);
        return ResponseEntity.ok(genreUseCase.getAllByLanguage(lang));
    }

    @GetMapping("/get/all")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Get All genre", description = "")
    public ResponseEntity<ApiResponse<List<GenreResponse>>> getAll() {
        log.info("Get genre list: {}");
        return ResponseEntity.ok(genreUseCase.getAll());
    }
}
