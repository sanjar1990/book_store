package api.kitabu.uz.usecases;

import api.kitabu.uz.dto.ApiResponse;
import api.kitabu.uz.enums.AppLanguage;
import org.springframework.data.domain.PageImpl;

import java.util.List;

public interface FileUseCase<REQUEST, RESPONSE> {
    ApiResponse<RESPONSE> uploadFile(REQUEST fileRequest, AppLanguage language);
    ApiResponse<?> delete(String fileName, AppLanguage language);
    ApiResponse<RESPONSE> getFile(String fileId, AppLanguage language);
    byte[] open(String fileName);
    ApiResponse<PageImpl<RESPONSE>> getListOfAttaches(int page,int size);
    ApiResponse<?> deletePublic(String fileId, AppLanguage language);
}
