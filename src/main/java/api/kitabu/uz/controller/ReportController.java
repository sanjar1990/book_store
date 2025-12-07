package api.kitabu.uz.controller;

import api.kitabu.uz.dto.ApiResponse;
import api.kitabu.uz.dto.genre.GenreRequest;
import api.kitabu.uz.dto.genre.GenreResponse;
import api.kitabu.uz.dto.report.ReportRequest;
import api.kitabu.uz.dto.report.ReportResponse;
import api.kitabu.uz.enums.AppLanguage;
import api.kitabu.uz.mappers.GenreLangMapper;
import api.kitabu.uz.service.ReportService;
import api.kitabu.uz.usecases.GenreUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RestController
@RequiredArgsConstructor
@Log4j2
@RequestMapping("/api/v1/report")
public class ReportController {

    private final ReportService reportService;
    @PostMapping("/create")
    @PreAuthorize("hasRole('ROLE_USER')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Create report", description = "")
    public ResponseEntity<ApiResponse<ReportResponse>> create(@RequestBody ReportRequest reportRequest,
                                                              @RequestHeader(value = "Accept-Language",defaultValue = "uz")AppLanguage language) {
        log.info("Report create: {}",reportRequest);
        return ResponseEntity.ok(reportService.create(reportRequest,language));
    }
}
