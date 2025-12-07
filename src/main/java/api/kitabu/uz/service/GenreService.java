package api.kitabu.uz.service;

import api.kitabu.uz.dto.ApiResponse;
import api.kitabu.uz.dto.genre.GenreRequest;
import api.kitabu.uz.dto.genre.GenreResponse;
import api.kitabu.uz.entity.GenreEntity;
import api.kitabu.uz.enums.AppLanguage;
import api.kitabu.uz.exeptions.exceptionhandler.APIException;
import api.kitabu.uz.mappers.GenreLangMapper;
import api.kitabu.uz.repository.GenreRepository;
import api.kitabu.uz.usecases.GenreUseCase;
import api.kitabu.uz.util.SpringSecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@Service
@Transactional
public class GenreService implements GenreUseCase<GenreRequest, GenreResponse> {
    @Autowired
    private GenreRepository genreRepository;
    @Autowired
    private ResourceBundleService resourceBundleService;

    @Override
    public ApiResponse<GenreResponse> create(GenreRequest genreRequest,AppLanguage language) {
        Optional<GenreEntity> byOrderNumberAndVisible = genreRepository.findByOrderNumberAndVisible(genreRequest.orderNumber(), true);
        if (byOrderNumberAndVisible.isPresent())
            throw new APIException(resourceBundleService.getMessage("this.order.number.already.taken",language.name()));
        var entity = GenreEntity
                .builder()
                .titleUz(genreRequest.titleUz())
                .titleRu(genreRequest.titleRu())
                .titleEn(genreRequest.titleEn())
                .orderNumber(genreRequest.orderNumber())
                .build();
        genreRepository.save(entity);
        return new ApiResponse<>(200, false, mapToResponse().apply(entity));
    }

    @Override
    public ApiResponse<GenreResponse.GenreResponseFull> delete(String genreId,AppLanguage language) {
        Optional<GenreEntity> byIdAndVisible = genreRepository.findByIdAndVisible(genreId, true);
        if (byIdAndVisible.isEmpty())
            return new ApiResponse<>(resourceBundleService.getMessage("no.such.a.genre.id", language.name()), 400, true);
        GenreEntity genreEntity = byIdAndVisible.get();
        genreEntity.setDeletedDate(LocalDateTime.now());
        genreEntity.setDeletedId(SpringSecurityUtil.getCurrentUserId());
        genreEntity.setVisible(false);
        genreRepository.save(genreEntity);
        return new ApiResponse<>(200, false, mapToResponseFull().apply(genreEntity));
    }

    @Override
    public ApiResponse<GenreResponse> getGenre(String genreId,AppLanguage language) {
        Optional<GenreEntity> byIdAndVisible = genreRepository.findByIdAndVisible(genreId, true);
        if (byIdAndVisible.isEmpty())
            return new ApiResponse<>(resourceBundleService.getMessage("no.such.a.genre.id", language.name()), 400, true);
        GenreEntity genreEntity = byIdAndVisible.get();
        return new ApiResponse<>(200, false, mapToResponse().apply(genreEntity));

    }

    @Override
    public ApiResponse<GenreResponse> update(GenreRequest genreRequest, String genreId,AppLanguage language) {
        Optional<GenreEntity> byIdAndVisible = genreRepository.findByIdAndVisible(genreId, true);
        if (byIdAndVisible.isEmpty())
            return new ApiResponse<>(resourceBundleService.getMessage("no.such.a.genre.id", language.name()), 400, true);

        GenreEntity genreEntity = byIdAndVisible.get();
        genreEntity.setTitleUz(genreRequest.titleUz());
        genreEntity.setTitleRu(genreRequest.titleRu());
        genreEntity.setTitleEn(genreRequest.titleEn());
        genreEntity.setOrderNumber(genreRequest.orderNumber());
        genreRepository.save(genreEntity);
        return new ApiResponse<>(200, false, mapToResponse().apply(genreEntity));

    }



    @Override
    public ApiResponse<List<GenreLangMapper>> getAllByLanguage(AppLanguage language) {
        List<GenreLangMapper> genreByLang = genreRepository.getGenreByLang(language.name());
        if (genreByLang.isEmpty())
            return new ApiResponse<>(200, false);

        return new ApiResponse<>(200, false, genreByLang);
    }

    @Override
    public ApiResponse<List<GenreResponse>> getAll() {
        List<GenreEntity> allByVisible = genreRepository.findAllByVisibleOrderByOrderNumberAsc(true);
        if (allByVisible.isEmpty())
            return new ApiResponse<>(200, false);

        return new ApiResponse<>(200, false, allByVisible.stream().map(mapToResponse()).toList());
    }

    private Function<GenreEntity, GenreResponse> mapToResponse() {
        return genreEntity -> GenreResponse
                .builder()
                .id(genreEntity.getId())
                .titleUz(genreEntity.getTitleUz())
                .titleRu(genreEntity.getTitleRu())
                .titleEn(genreEntity.getTitleEn())
                .orderNumber(genreEntity.getOrderNumber())
                .createdDate(genreEntity.getCreatedDate())
                .build();
    }

    private Function<GenreEntity, GenreResponse.GenreResponseFull> mapToResponseFull() {
        return genreEntity -> GenreResponse.GenreResponseFull
                .builder()
                .id(genreEntity.getId())
                .titleUz(genreEntity.getTitleUz())
                .titleRu(genreEntity.getTitleRu())
                .titleEn(genreEntity.getTitleEn())
                .orderNumber(genreEntity.getOrderNumber())
                .createdDate(genreEntity.getCreatedDate())
                .deletedDate(genreEntity.getDeletedDate())
                .deletedId(genreEntity.getDeletedId())
                .visible(genreEntity.getVisible())
                .build();
    }

    public String getByName(String nameUz){
        return genreRepository.getByName(nameUz);
    }
}
