package api.kitabu.uz.controller;

import api.kitabu.uz.dto.ApiResponse;
import api.kitabu.uz.dto.KeyValueDTO;
import api.kitabu.uz.dto.auth.AuthorizationDTO;
import api.kitabu.uz.dto.auth.AuthorizationResponseDTO;
import api.kitabu.uz.enums.AppLanguage;
import api.kitabu.uz.enums.BookLanguage;
import api.kitabu.uz.enums.Lang;
import api.kitabu.uz.service.BookLanguageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/book-language")
@Tag(name = "Book language controller", description = "")
public class BookLanguageController {
    @Autowired
    private BookLanguageService bookLanguageService;

    @GetMapping("")
    @Operation(summary = "Get book language", description = "")
    public ResponseEntity<ApiResponse<List<KeyValueDTO>>> bookLanguage(@RequestHeader(value = "Accept-Language", defaultValue = "uz") AppLanguage lang) {
        return ResponseEntity.ok(ApiResponse.ok(bookLanguageService.getByLanguage(lang)));
    }
}
