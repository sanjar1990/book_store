package api.kitabu.uz.telegramBot.service;

import api.kitabu.uz.entity.PostEntity;
import api.kitabu.uz.enums.*;
import api.kitabu.uz.service.AttachService;
import api.kitabu.uz.service.PostGenreService;
import api.kitabu.uz.service.RegionService;
import api.kitabu.uz.telegramBot.TelegramBotsApi;
import api.kitabu.uz.util.DetailUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMediaGroup;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.media.InputMedia;
import org.telegram.telegrambots.meta.api.objects.media.InputMediaPhoto;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

@Service
public class TelegramBotService {

    private final TelegramBotsApi telegramBotsApi;
    private final AttachService attachService;
    private final RegionService regionService;
    private final PostGenreService postGenreService;

    public TelegramBotService(@Lazy TelegramBotsApi telegramBotsApi, AttachService attachService, RegionService regionService, PostGenreService postGenreService) {
        this.telegramBotsApi = telegramBotsApi;
        this.attachService = attachService;
        this.regionService = regionService;
        this.postGenreService = postGenreService;
    }

    public void sendToChannel(PostEntity postEntity, List<String> attachIds) {
        String genre = TelegramBotGenreService.getGenre(postEntity.getId());

        StringBuilder exchangeType;
        exchangeType = new StringBuilder();

        if (postEntity.getExchangeType().equals(ExchangeType.EXCHANGE)) {
            exchangeType.append("\uD83D\uDD04 <b>Turkumi: <i>").append("Almashish").append("</i></b>\n");
        } else if (postEntity.getExchangeType().equals(ExchangeType.FREE)) {
            exchangeType.append("\uD83C\uDD93 <b>Turkumi <i>").append("Hadya").append("</i></b>\n");
        } else if (postEntity.getExchangeType().equals(ExchangeType.SELL)) {
            exchangeType.append("\uD83D\uDCB8 <b>Turkumi: <i>").append("Sotish").append("</i></b>\n");
            exchangeType.append("\uD83D\uDCB0 <b>Narxi: <i>").append(DetailUtil.getDetail(postEntity.getPrice().toString())).append("</i></b>\n");
            exchangeType.append("\uD83D\uDCB0 <b>Bozor narxi: <i><del>").append(DetailUtil.getDetail(postEntity.getMarketPrice().toString())).append("</del></i></b>\n");
        } else if (postEntity.getExchangeType().equals(ExchangeType.TEMPORARILY)) {
            exchangeType.append("\uD83D\uDD51 <b>Turkum: <i>").append("Vaqtinchalik").append("</i></b>\n");
        }

        String condition = "";

        if (postEntity.getConditionType().equals(ConditionType.NEW)) {
            condition = "\uD83D\uDCD5 <b>Kitob holat: <i>Yangi</i></b>\n";
        } else if (postEntity.getConditionType().equals(ConditionType.OLD)) {
            condition = "\uD83D\uDCD5 <b>Kitob holat: <i>Eski</i></b>\n";
        } else if (postEntity.getConditionType().equals(ConditionType.USED)) {
            condition = "\uD83D\uDCD5 <b>Kitob holati: <i>Foydalanilgan</i></b>\n";
        }

        String lang = "";

        if (postEntity.getBookLanguage().equals(BookLanguage.LATIN)) {
            lang = "\uD83C\uDDFA\uD83C\uDDFF <b>Kitob tili: <i>Latin</i></b>\n";
        } else if (postEntity.getBookLanguage().equals(BookLanguage.KIRILL)) {
            lang = "\uD83C\uDDFA\uD83C\uDDFF <b>Kitob tili: <i>Kirill</i></b>\n";
        } else if (postEntity.getBookLanguage().equals(BookLanguage.RU)){
            lang = "\uD83C\uDDF7\uD83C\uDDFA <b>Kitob tili: <i>Rus</i></b>\n";
        } else if (postEntity.getBookLanguage().equals(BookLanguage.EN)){
            lang = "\uD83C\uDDEC\uD83C\uDDE7 <b>Kitob tili: <i>Ingiliz</i></b>\n>";
        }

        String printType = "";

        if (postEntity.getBookPrintType().equals(BookPrintType.PAPER_BOOK)){
            printType = "\uD83D\uDCD2 <b>Kitob nashri: <i>Kitob shaklida</i></b>\n";
        }else if (postEntity.getBookPrintType().equals(BookPrintType.PEREPLYOT)){
            printType = "\uD83D\uDCD2 <b>Kitob nashri: <i>Pereplyot</i></b>\n";
        }

        try {
            long channelId = -1002133974401L;
            if (attachIds.size() == 1) {
                SendPhoto sendPhoto = new SendPhoto();
                sendPhoto.setChatId(Long.toString(channelId));
                sendPhoto.setPhoto(new InputFile(attachService.asUrlString(attachIds.get(0))));

                sendPhoto.setCaption(
                        "\uD83D\uDCDA <b>Kitob nomi: <i>" + DetailUtil.getDetail(postEntity.getTitle()) + "</i></b>\n" +
                        "✍\uD83C\uDFFC <b>Muallif: <i>" + DetailUtil.getDetail(postEntity.getAuthorName()) + "</i></b>\n" +
                        "\uD83D\uDCCC <b>Kitob haqida: <i>" + DetailUtil.getDetail(postEntity.getDescription()) + "</i></b>\n" +
                        "\uD83D\uDDFA <b>Kitob manzili: <i>" + DetailUtil.getDetail(regionService.get(postEntity.getRegionId(), AppLanguage.uz).getNameUz()) + "</i></b>\n" +
                        "\uD83D\uDCD6 <b>Kitob janri: <i>" + DetailUtil.getDetail(genre) + "</i></b>\n" +
                        exchangeType +
                        condition +
                        lang +
                        printType +
                        "\n" +
                        "\uD83D\uDD0D <b>Toliq info: <i><a href=\"https://kitabu.uz/posts/" + postEntity.getId() + "\">ko'rish</a></i></b>\n" +
                        "\uD83D\uDCCD <b>Veb - saytimiz <a href=\"https://kitabu.uz\">Kitabu.uz</a></b>");
                sendPhoto.setParseMode("HTML");

                telegramBotsApi.execute(sendPhoto);
            } else if (attachIds.size() > 1) {
                List<InputMedia> mediaList = new ArrayList<>();
                SendMediaGroup group = new SendMediaGroup();
                group.setChatId(Long.toString(channelId));

                for (String url : attachIds) {
                    InputMediaPhoto image = new InputMediaPhoto();
                    image.setMedia(attachService.asUrlString(url));
                    mediaList.add(image);
                }

                mediaList.get(0).setCaption(
                        "\uD83D\uDCDA <b>Kitob nomi: <i>" + DetailUtil.getDetail(postEntity.getTitle()) + "</i></b>\n" +
                        "✍\uD83C\uDFFC <b>Muallif: <i>" + DetailUtil.getDetail(postEntity.getAuthorName()) + "</i></b>\n" +
                        "\uD83D\uDCCC <b>Kitob haqida: <i>" + DetailUtil.getDetail(postEntity.getDescription()) + "</i></b>\n" +
                        "\uD83D\uDDFA <b>Kitob manzili: <i>" + DetailUtil.getDetail(regionService.get(postEntity.getRegionId(), AppLanguage.uz).getNameUz()) + "</i></b>\n" +
                        "\uD83D\uDCD6 <b>Kitob janri: <i>" + DetailUtil.getDetail(genre) + "</i></b>\n" +
                        exchangeType +
                        condition +
                        lang +
                        printType +
                        "\n" +
                        "\uD83D\uDD0D <b>Toliq info: <i><a href=\"https://kitabu.uz/posts/" + postEntity.getId() + "\">ko'rish</a></i></b>\n" +
                        "\uD83D\uDCCD <b>Veb - saytimiz <a href=\"https://kitabu.uz\">Kitabu.uz</a></b>");
                mediaList.get(0).setParseMode("HTML");

                group.setMedias(mediaList);

                telegramBotsApi.execute(group);
            } else {
                System.out.println("Hech qanday media fayl mavjud emas.");
            }
        } catch (TelegramApiException e) {
            System.out.println("Telegram API xatosi: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("Noma'lum xatolik: " + e.getMessage());
            e.printStackTrace();
        }
    }

}