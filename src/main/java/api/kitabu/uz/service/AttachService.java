package api.kitabu.uz.service;

import api.kitabu.uz.dto.ApiResponse;
import api.kitabu.uz.dto.FileResponse;
import api.kitabu.uz.entity.AttachEntity;
import api.kitabu.uz.enums.AppLanguage;
import api.kitabu.uz.exeptions.exceptionhandler.APIException;
import api.kitabu.uz.repository.AttachRepository;
import api.kitabu.uz.usecases.FileUseCase;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.List;
import java.util.function.Function;

import static java.time.LocalDateTime.now;
/*
 * @author Raufov Ma`ruf
 * */

@Service
@RequiredArgsConstructor
@Transactional
public class AttachService implements FileUseCase<MultipartFile, FileResponse> {
    @Value("${attach.open.url}")
    private String fileUrl;
    @Autowired
    private ResourceBundleMessageSource messageSource;

    @Autowired
    private PostAttachService postAttachService;

    private final AttachRepository attachRepository;

    @Override
    public ApiResponse<FileResponse> uploadFile(MultipartFile fileRequest, AppLanguage language) {
        String fileUploadDir = "attach/" + getYearMonthDay();
        String extension = getExtension(Objects.requireNonNull(fileRequest.getOriginalFilename()));
        String uuid = UUID.randomUUID().toString();
        String fileId = uuid + "." + extension;
        String compressedId = "resized-" + uuid + "." + extension;
        var directory = new File(fileUploadDir);
        if (!directory.exists()) directory.mkdirs();
        try {
            Path path = Paths.get(fileUploadDir + fileId);
            Files.write(path, fileRequest.getBytes());

            var fileEntity = AttachEntity.builder()
                    .id(fileId)
                    .size(fileRequest.getSize())
                    .filename(fileRequest.getOriginalFilename())
                    .extension(extension)
                    .visible(Boolean.TRUE)
                    .path(fileUploadDir)
                    .compressedId(compressedId)
                    .build();

            attachRepository.save(fileEntity);

            // compressed file db save
            compressedFile(fileRequest, fileUploadDir, compressedId, extension, 500, 800);

            return new ApiResponse<>(200, false, mapToFileResponse().apply(fileEntity));
        } catch (IOException e) {
            throw new APIException(messageSource.getMessage("file.is.invalid", null, new Locale(language.name())), 404);
        }
    }

    private void compressedFile(MultipartFile fileRequest, String fileUploadDir, String compressedId, String extension, int newWidth, int newHeight) throws IOException {
        BufferedImage originalImage = ImageIO.read(fileRequest.getInputStream());
        BufferedImage resizedImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = resizedImage.createGraphics();
        g.drawImage(originalImage, 0, 0, newWidth, newHeight, null);
        g.dispose();

        String outputFilePath = fileUploadDir + compressedId;
        ImageIO.write(resizedImage, "jpg", new File(outputFilePath));

        long reducedFileSize = (long) (fileRequest.getSize() * 0.5);

        var entity = AttachEntity.builder()
                .id(compressedId)
                .size(reducedFileSize)
                .visible(Boolean.TRUE)
                .filename(fileRequest.getOriginalFilename())
                .extension(extension)
                .path(fileUploadDir)
                .build();

        attachRepository.save(entity);
    }


    private Function<AttachEntity, FileResponse> mapToFileResponse() {
        return file -> FileResponse.builder()
                .id(file.getId())
                .size(file.getSize())
                .createdDate(file.getCreatedDate())
                .url(fileUrl + file.getId())
                .extension(file.getExtension())
                .filename(file.getFilename())
                .build();
    }


    public FileResponse toDTO(String attachId) {
        return attachId == null ? null : FileResponse.builder()
                .id(attachId)
                .url(fileUrl + attachId)
                .build();
    }

    public FileResponse toDTOFilter(String attachId) {
        return attachId == null ? null : FileResponse.builder()
                .id(attachId)
                .url(fileUrl + "resized-" + attachId)
                .build();
    }

    public String asUrlString(String attachId) {
        if (attachId == null) {
            return null;
        }
        return fileUrl + attachId;
    }
    public List<String> asUrlStringMore(List<String> attachIds) {
        List<String> list = new ArrayList<>();
        if (attachIds == null) {
            return null;
        }
        for (String attachId : attachIds) {
            list.add(fileUrl + attachId);
            }
        return list;
    }

    @Override
    public ApiResponse<?> delete(String fileId, AppLanguage language) {
        var fileEntity = attachRepository.findById(fileId).orElseThrow(() -> new APIException("Photo id null"));
        try {
            if (Files.deleteIfExists(new File(fileEntity.getPath() + fileId).toPath())) {
                attachRepository.delete(fileEntity);
                return ApiResponse.ok(true);
            }
        } catch (IOException e) {
            throw new APIException(messageSource.getMessage("file.does.not.exist", null, new Locale(language.name())), 404);
        }
        return null;
    }

    @Override
    public ApiResponse<FileResponse> getFile(String fileId, AppLanguage language) {
        return new ApiResponse<>(200, false, attachRepository
                .findById(fileId)
                .map(mapToFileResponse())
                .orElseThrow(() -> new APIException(messageSource.getMessage("item.not.found", null, new Locale(language.name())), 403)));
    }

    @Override
    public byte[] open(String fileId) {
        var fileEntity = attachRepository.findByIdAndVisibleTrue(fileId).orElseThrow(() -> new APIException(messageSource.getMessage("item.not.found", null, new Locale(AppLanguage.en.name())), 404));
        try {
            return Files.readAllBytes(new File(fileEntity.getPath() + fileId).toPath());
        } catch (IOException e) {
            throw new APIException(e.getMessage(), 400);
        }
    }

    @Override
    public ApiResponse<PageImpl<FileResponse>> getListOfAttaches(int page,int size) {
        var pageable = PageRequest.of(page-1, size);
        var pageObj = attachRepository.findAll(pageable);
        return new ApiResponse<>(200, false,new PageImpl<>(pageObj
                .map(attachEntity -> mapToFileResponse()
                        .apply(attachEntity))
                .toList(),pageable,pageObj.getTotalElements()));
    }

    @Override
    public ApiResponse<?> deletePublic(String fileId, AppLanguage language) {
        var fileEntity = attachRepository.findById(fileId).orElseThrow(() -> new APIException("Photo id null"));
        postAttachService.deletePostAttachByAttachId(fileEntity.getId());
        try {
            if (Files.deleteIfExists(new File(fileEntity.getPath() + fileId).toPath())
                && Files.deleteIfExists(new File(fileEntity.getPath() + "resized-" + fileId).toPath())) {
                attachRepository.deleteByIdAttach(fileEntity.getId());
                attachRepository.deleteByIdResized("resized-" + fileEntity.getId());
                return ApiResponse.ok(true);
            }
        } catch (IOException e) {
            throw new APIException(messageSource.getMessage("file.does.not.exist", null, new Locale(language.name())), 404);
        }
        return null;
    }

    private String getYearMonthDay() {
        return String.format("%s/%s/%s/", now().getYear(), now().getMonthValue(), now().getDayOfMonth());
    }

    public AttachEntity getId(String attachId, AppLanguage language) {
        return attachRepository
                .findById(attachId)
                .orElseThrow(() -> new APIException(messageSource.getMessage("item.not.found", null, new Locale(language.name())), 404));
    }

    public String getExtension(String fileName) { // mp3/jpg/npg/mp4.....
        int lastIndex = fileName.lastIndexOf(".");
        return fileName.substring(lastIndex + 1);
    }
}