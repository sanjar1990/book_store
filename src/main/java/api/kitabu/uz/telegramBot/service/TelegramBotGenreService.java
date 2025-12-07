package api.kitabu.uz.telegramBot.service;

import api.kitabu.uz.enums.AppLanguage;
import api.kitabu.uz.mappers.GenreLangMapper;
import api.kitabu.uz.service.PostGenreService;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class TelegramBotGenreService {

    private static PostGenreService postGenreService;

    public TelegramBotGenreService(PostGenreService postGenreService) {
        TelegramBotGenreService.postGenreService = postGenreService;
    }

    public static String getGenre(String postId) {
        return postGenreService.getAllGenrePostsByPostIdAndLanguage(
                        postId, AppLanguage.uz)
                .stream()
                .map(GenreLangMapper::getName)
                .collect(Collectors.joining(","));
    }
}
