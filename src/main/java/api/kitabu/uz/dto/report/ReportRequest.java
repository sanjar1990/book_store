package api.kitabu.uz.dto.report;

import jakarta.validation.constraints.NotNull;

public record ReportRequest(
        @NotNull
        String title,
        @NotNull
        String postId
) {
}
