package api.kitabu.uz.dto.filter;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class FilterResultDTO<RESULT> {
    private List<RESULT> list;
    private Long totalCount;

}
