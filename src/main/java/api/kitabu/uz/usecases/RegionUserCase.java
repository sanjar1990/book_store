package api.kitabu.uz.usecases;

import api.kitabu.uz.dto.ApiResponse;
import api.kitabu.uz.dto.region.RegionLangResponse;
import api.kitabu.uz.enums.AppLanguage;
import api.kitabu.uz.enums.Lang;
import api.kitabu.uz.mappers.RegionLangMapper;

import java.util.List;

public interface RegionUserCase <REQUEST,RESPONSE>{
    ApiResponse<RESPONSE> createRegion(REQUEST request);
    ApiResponse<RESPONSE> updateRegion(REQUEST request,Integer id,AppLanguage language);
    ApiResponse<RESPONSE> getRegionById(Integer id, AppLanguage language);
    ApiResponse<Boolean> deleteRegion(Integer response,AppLanguage language);
    List<RESPONSE> getAllRegion();
    List<RegionLangResponse> getShortInfo(AppLanguage lang);
    RESPONSE getRegion(String stringCellValue);
    ApiResponse<List<RegionLangMapper>> getAllByLanguage(AppLanguage language);

}
