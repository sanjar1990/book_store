package api.kitabu.uz.controller;


import api.kitabu.uz.dto.ApiResponse;
import api.kitabu.uz.dto.region.RegionLangResponse;
import api.kitabu.uz.dto.region.RegionRequest;
import api.kitabu.uz.dto.region.RegionResponse;
import api.kitabu.uz.enums.AppLanguage;
import api.kitabu.uz.enums.Lang;
import api.kitabu.uz.mappers.RegionLangMapper;
import api.kitabu.uz.usecases.RegionUserCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/region")
@Tag(name = "Region Api list", description = "Api list for regions")
@Slf4j
public class RegionController {

    @Autowired
    private RegionUserCase<RegionRequest, RegionResponse> regionUserCase;

    @PostMapping("/")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Create region", description = "This api used for region creation.")
    public ResponseEntity<ApiResponse<RegionResponse>> createRegion(@RequestBody RegionRequest request) {
        log.info("Create region: {}", request.nameUz());
        return ResponseEntity.ok(regionUserCase.createRegion(request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Delete region", description = "")
    public ResponseEntity<ApiResponse<Boolean>> delete(@PathVariable("id") Integer id,
                                                       @RequestHeader(value = "Accept-language", defaultValue = "uz") AppLanguage language) {
        log.info("Delete region by id: {}", id);
        return ResponseEntity.ok(regionUserCase.deleteRegion(id, language));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Update region", description = "")
    public ResponseEntity<ApiResponse<RegionResponse>> update(@PathVariable("id") Integer id, @RequestBody RegionRequest request,
                                                              @RequestHeader(value = "Accept-language", defaultValue = "uz") AppLanguage language) {
        log.info("Update region by id: {}", id);
        return ResponseEntity.ok(regionUserCase.updateRegion(request, id, language));
    }

    @GetMapping(value = "")
    @Operation(summary = "Get region list for admin", description = "")
    public ResponseEntity<ApiResponse<List<RegionResponse>>> getAll() {
        log.info("Get region List: {}", regionUserCase.getAllRegion().size());
        return ResponseEntity.ok(new ApiResponse<>(200, false, regionUserCase.getAllRegion()));
    }

    @GetMapping(value = "/{id}")
    @Operation(summary = "Get region by id", description = "")
    public ResponseEntity<ApiResponse<RegionResponse>> getById(@PathVariable("id") Integer id,
                                                               @RequestHeader(value = "Accept-language", defaultValue = "uz") AppLanguage language) {
        log.info("Get redion by id: {}", id);
        return ResponseEntity.ok(regionUserCase.getRegionById(id, language));
    }

    @GetMapping(value = "/by-lang")
    @Operation(summary = "Get region list  by lang", description = "")
    public ResponseEntity<ApiResponse<List<RegionLangResponse>>> getAllByLang(@RequestHeader(value = "Accept-language", defaultValue = "uz") AppLanguage language) {
        log.info("Get region by lang: {}", language);
        return ResponseEntity.ok(new ApiResponse<>(200, false, regionUserCase.getShortInfo(language)));
    }
}


