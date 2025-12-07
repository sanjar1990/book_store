package api.kitabu.uz.service;

import api.kitabu.uz.dto.KeyValueDTO;
import api.kitabu.uz.enums.AppLanguage;
import api.kitabu.uz.enums.BookLanguage;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;

@Service
public class BookLanguageService {
    private List<KeyValueDTO> uzValueList = new LinkedList<>();
    private List<KeyValueDTO> enValueList = new LinkedList<>();
    private List<KeyValueDTO> ruValueList = new LinkedList<>();

    public BookLanguageService() {
        for (BookLanguage bl : BookLanguage.values()) {
            uzValueList.add(new KeyValueDTO(bl.name(), bl.getNameUz()));
            ruValueList.add(new KeyValueDTO(bl.name(), bl.getNameRu()));
            enValueList.add(new KeyValueDTO(bl.name(), bl.getNameEn()));
        }
    }

    public List<KeyValueDTO> getByLanguage(AppLanguage appLanguage) {
        return switch (appLanguage) {
            case uz -> uzValueList;
            case en -> enValueList;
            default -> ruValueList;
        };
    }
}
