package api.kitabu.uz.usecases;

import api.kitabu.uz.dto.ApiResponse;
import api.kitabu.uz.dto.genre.GenreResponse;
import api.kitabu.uz.enums.AppLanguage;
import api.kitabu.uz.mappers.GenreLangMapper;

import java.util.List;

public interface GenreUseCase<REQUEST, RESPONSE> {
    ApiResponse<RESPONSE> create(REQUEST genreRequest,AppLanguage language);
    ApiResponse<GenreResponse.GenreResponseFull> delete(String genreId,AppLanguage language);
    ApiResponse<RESPONSE> getGenre(String genreId,AppLanguage language);
    ApiResponse<RESPONSE> update(REQUEST genreRequest, String genreId,AppLanguage language);
    ApiResponse<List<GenreLangMapper>> getAllByLanguage(AppLanguage language);
    ApiResponse<List<GenreResponse>> getAll();
}
