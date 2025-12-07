package api.kitabu.uz.telegramBot.dto;

import api.kitabu.uz.enums.BookLanguage;
import api.kitabu.uz.enums.BookPrintType;
import api.kitabu.uz.enums.ConditionType;
import api.kitabu.uz.enums.ExchangeType;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class PostCreateBot {
    private String title;
    private String authorName;
    private String profileId;
    private String description;
    private Integer regionId;
    private ExchangeType exchangeType;
    private ConditionType condition;
    private BookLanguage language;
    private Double price;
    private Double marketPrice;
    private BookPrintType bookPrintType;
    private List<String> images = new ArrayList<>();
    private List<String> genres = new ArrayList<>();

    public void imagesAdd(String imageId){
        images.add(imageId);
    }

    public void genresAdd(String genreId) {
        genres.add(genreId);
    }
}
