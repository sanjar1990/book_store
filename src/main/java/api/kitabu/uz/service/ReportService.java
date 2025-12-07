package api.kitabu.uz.service;

import api.kitabu.uz.dto.ApiResponse;
import api.kitabu.uz.dto.report.ReportRequest;
import api.kitabu.uz.dto.report.ReportResponse;
import api.kitabu.uz.entity.ReportEntity;
import api.kitabu.uz.enums.AppLanguage;
import api.kitabu.uz.repository.ReportRepository;
import api.kitabu.uz.util.SpringSecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class ReportService {
    private final ReportRepository reportRepository;

    public ApiResponse<ReportResponse> create(ReportRequest reportRequest, AppLanguage language) {
        var entity = new ReportEntity();
        entity.setProfileId(SpringSecurityUtil.getCurrentUserId());
        entity.setPostId(reportRequest.postId());
        entity.setTitle(reportRequest.title());
        reportRepository.save(entity);
        return ApiResponse.ok(mapToResponse().apply(entity));
    }

    private Function<ReportEntity, ReportResponse> mapToResponse() {
        return reportEntity -> new ReportResponse(reportEntity.getTitle(), reportEntity.getPostId(), reportEntity.getProfileId());
    }
}
