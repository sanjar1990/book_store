package api.kitabu.uz.service;

import api.kitabu.uz.dto.ApiResponse;
import api.kitabu.uz.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class DashboardService {
    private final PostRepository postRepository;
    public ApiResponse<String> getAdminDashboardData() {
      return ApiResponse.ok(postRepository.getAdminDashboardData());
    }
}