package api.kitabu.uz.service;

import api.kitabu.uz.dto.ApiResponse;
import api.kitabu.uz.dto.region.RegionLangResponse;
import api.kitabu.uz.dto.region.RegionRequest;
import api.kitabu.uz.dto.region.RegionResponse;
import api.kitabu.uz.entity.RegionEntity;
import api.kitabu.uz.enums.AppLanguage;
import api.kitabu.uz.enums.Lang;
import api.kitabu.uz.exeptions.ItemNotFoundException;
import api.kitabu.uz.exeptions.exceptionhandler.APIException;
import api.kitabu.uz.mappers.RegionLangMapper;
import api.kitabu.uz.repository.RegionRepository;
import api.kitabu.uz.usecases.RegionUserCase;
import jakarta.transaction.Transactional;
import lombok.Builder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
/*
 * @author Abduhoshim
 * */

@Builder
@Service

public class RegionService implements RegionUserCase<RegionRequest, RegionResponse> {

    @Autowired
    private RegionRepository regionRepository;
    @Autowired
    private ResourceBundleService resourceBundleService;

//    @Autowired
//    private DistrictRepository districtRepository;


    public RegionEntity get(Integer id, AppLanguage language) {
        // log
        return regionRepository
                .findById(id)
                .orElseThrow(() -> new APIException(resourceBundleService.getMessage("this.region.not.exist", language.name())));
    }

    @Override
    public ApiResponse<RegionResponse> createRegion(RegionRequest regionRequest) {
        RegionEntity entity = RegionEntity.builder()
                .nameUz(regionRequest.nameUz())
                .nameRu(regionRequest.nameRu())
                .nameEn(regionRequest.nameEn())
                .visible(Boolean.TRUE)
                .build();
        regionRepository.save(entity);
        return new ApiResponse<>(200,false,mapToResponse().apply(entity));
    }

    @Override
    public ApiResponse<RegionResponse> updateRegion(RegionRequest regionRequest, Integer id,AppLanguage language) {
        RegionEntity entity = get(id,language);
        entity.setNameEn(regionRequest.nameUz());
        entity.setNameEn(regionRequest.nameRu());
        entity.setNameEn(regionRequest.nameEn());

        regionRepository.save(entity);
        return new ApiResponse<>(200,false,mapToResponse().apply(entity));
    }

    @Override
    public ApiResponse<RegionResponse> getRegionById(Integer id,AppLanguage language) {
        return new ApiResponse<>(200,false,mapToResponse().apply(get(id,language)));
    }
    @Transactional
    @Override
    public ApiResponse<Boolean> deleteRegion(Integer response,AppLanguage language) {
        Optional<RegionEntity> optional = regionRepository.findById(response);
        if (optional.isEmpty()) {
            return ApiResponse.bad(resourceBundleService.getMessage("this.region.not.exist", language.name()));
        }
        RegionEntity entity = optional.get();
        entity.setVisible(false);
        regionRepository.save(entity);
        return ApiResponse.ok();
    }
    @Override
    public List<RegionResponse> getAllRegion() {
        return regionRepository
                .getAllByVisibleIsTrueOrderByCreatedDateDesc()
                .stream().map(mapToResponse()).toList();
    }

    @Override
    public List<RegionLangResponse> getShortInfo(AppLanguage lang) {
        return regionRepository
                .getAllByVisibleIsTrueOrderByCreatedDateDesc()
                .stream()
                .map(regionEntity -> new RegionLangResponse(regionEntity.getId(), switch (lang) {
                    case en -> regionEntity.getNameEn();
                    case ru -> regionEntity.getNameRu();
                    case uz -> regionEntity.getNameUz();
                }))
                .collect(Collectors.toList());
    }

    private Function<RegionEntity, RegionResponse> mapToResponse() {
        return entity -> RegionResponse.builder()
                .id(entity.getId())
                .nameUz(entity.getNameUz())
                .nameRu(entity.getNameRu())
                .nameEn(entity.getNameEn())
                .createdDate(entity.getCreatedDate())
                .visible(entity.getVisible())
                .build();
    }
    @Override
    public RegionResponse getRegion(String stringCellValue) {
        String result = stringCellValue.toLowerCase().trim();
        List<RegionEntity> region = regionRepository.findAll();
        for (RegionEntity entity : region) {
            String name = entity.getNameUz().toLowerCase().trim();
            if (name.equals(result)) {
                return mapToResponse().apply(entity);
            }
        }
        return null;
    }
    @Override
    public ApiResponse<List<RegionLangMapper>> getAllByLanguage(AppLanguage language) {
        List<RegionLangMapper> regionByLang = regionRepository.getRegionByLang(language.name());
        if (regionByLang.isEmpty())
            return new ApiResponse<>(200, false);

        return new ApiResponse<>(200, false, regionByLang);
    }
    public RegionLangMapper getRegionLang(AppLanguage language, String postId) {
        var region = regionRepository.getRegion(language.name(),postId);
        if (region == null) throw new APIException("region not found", 404);
        return region;
    }
    public Integer getByNameUz(String nameUz){
        return regionRepository.getByNameUz(nameUz);
    }




}
