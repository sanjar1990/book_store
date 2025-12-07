package api.kitabu.uz.telegramBot.service;

import api.kitabu.uz.entity.PostEntity;
import api.kitabu.uz.enums.*;
import api.kitabu.uz.exeptions.exceptionhandler.APIException;
import api.kitabu.uz.service.AttachService;
import api.kitabu.uz.service.PostService;
import api.kitabu.uz.service.RegionService;
import api.kitabu.uz.telegramBot.TelegramBotsApi;
import api.kitabu.uz.util.DetailUtil;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMediaGroup;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.media.InputMedia;
import org.telegram.telegrambots.meta.api.objects.media.InputMediaPhoto;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

@Service
@RequiredArgsConstructor
@Getter
@Setter
@Slf4j
public class TelegramSendPostListService {

    private final TelegramBotsApi telegramBotsApi;
    private final AttachService attachService;
    private final RegionService regionService;
    private final PostService postService;
    private long channelId;

    public void sendToBot(PostEntity postEntity, List<String> attachIds, Long chatId) {

        String genre = TelegramBotGenreService.getGenre(postEntity.getId());

        StringBuilder exchangeType;
        exchangeType = new StringBuilder();

        if (postEntity.getExchangeType().equals(ExchangeType.EXCHANGE)) {
            exchangeType.append("\uD83D\uDD04 <b>Turkumi: <i>").append("Almashish").append("</i></b>\n");
        } else if (postEntity.getExchangeType().equals(ExchangeType.FREE)) {
            exchangeType.append("\uD83C\uDD93 <b>Turkumi <i>").append("Hadya").append("</i></b>\n");
        } else if (postEntity.getExchangeType().equals(ExchangeType.SELL) ) {
            exchangeType.append("\uD83D\uDCB8 <b>Turkumi: <i>").append("Sotish").append("</i></b>\n");
            if (postEntity.getPrice() != null){
                exchangeType.append("\uD83D\uDCB0 <b>Narxi: <i>").append(DetailUtil.getDetail(postEntity.getPrice().toString())).append("</i></b>\n");
                exchangeType.append("\uD83D\uDCB0 <b>Bozor narxi: <i><del>").append(DetailUtil.getDetail(postEntity.getMarketPrice().toString())).append("</del></i></b>\n");
            }
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
        } else if (postEntity.getBookLanguage().equals(BookLanguage.RU)) {
            lang = "\uD83C\uDDF7\uD83C\uDDFA <b>Kitob tili: <i>Rus</i></b>\n";
        } else if (postEntity.getBookLanguage().equals(BookLanguage.EN)) {
            lang = "\uD83C\uDDEC\uD83C\uDDE7 <b>Kitob tili: <i>Ingiliz</i></b>\n>";
        }

        String printType = "";

        if (postEntity.getBookPrintType().equals(BookPrintType.PAPER_BOOK)) {
            printType = "\uD83D\uDCD2 <b>Kitob nashri: <i>Kitob shaklida</i></b>\n";
        } else if (postEntity.getBookPrintType().equals(BookPrintType.PEREPLYOT)) {
            printType = "\uD83D\uDCD2 <b>Kitob nashri: <i>Pereplyot</i></b>\n";
        }



        try {
             channelId = chatId;
            if (attachIds.size() == 1) {
                SendPhoto sendPhoto = new SendPhoto();
                sendPhoto.setChatId(Long.toString(chatId));
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
                Message message = telegramBotsApi.execute(sendPhoto);

                StringJoiner joiner = new StringJoiner(",");
                joiner.add(message.getMessageId().toString());

                InlineKeyboardMarkup inlineKeyboardMarkup = createInlineKeyboard(postEntity.getId(),joiner.toString());

                SendMessage sendMessage = new SendMessage();
                sendMessage.setChatId(Long.toString(chatId));
                sendMessage.setText("<b><i>Elonni o'chirish</i></b>");
                sendMessage.setReplyMarkup(inlineKeyboardMarkup);
                sendMessage.setParseMode("HTML");
                Message replyMessage = telegramBotsApi.execute(sendMessage);

                joiner.add(replyMessage.getMessageId().toString());

                inlineKeyboardMarkup = createInlineKeyboard(postEntity.getId(),joiner.toString());
                EditMessageReplyMarkup editMarkup = new EditMessageReplyMarkup();
                editMarkup.setChatId(chatId.toString());
                editMarkup.setMessageId(replyMessage.getMessageId());
                editMarkup.setReplyMarkup(inlineKeyboardMarkup);
                telegramBotsApi.execute(editMarkup);


                chatId = 0L;

            } else if (attachIds.size() > 1) {
                List<InputMedia> mediaList = new ArrayList<>();
                SendMediaGroup group = new SendMediaGroup();
                group.setChatId(Long.toString(chatId));

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

                List<Message> execute = telegramBotsApi.execute(group);
                StringJoiner joiner = new StringJoiner(",");
                for (Message message : execute) {
                    Integer id = message.getMessageId();
                    joiner.add(id.toString());
                }

                Integer lastMessageId = execute.get(execute.size()-1).getMessageId();

                InlineKeyboardMarkup inlineKeyboardMarkup = createInlineKeyboard(postEntity.getId(),joiner.toString());

                SendMessage sendMessage = new SendMessage();
                sendMessage.setChatId(chatId);
                sendMessage.setText("<b><i>Elonni o'chirish</i></b>");
                sendMessage.setReplyMarkup(inlineKeyboardMarkup);
                sendMessage.setParseMode("HTML");
                Message message = telegramBotsApi.execute(sendMessage);

                joiner.add(message.getMessageId().toString());

                inlineKeyboardMarkup = createInlineKeyboard(postEntity.getId(), joiner.toString());
                EditMessageReplyMarkup editMarkup = new EditMessageReplyMarkup();
                editMarkup.setChatId(chatId.toString());
                editMarkup.setMessageId(message.getMessageId());
                editMarkup.setReplyMarkup(inlineKeyboardMarkup);
                telegramBotsApi.execute(editMarkup);

                chatId = 0L;

            } else {
                System.out.println("Hech qanday media fayl mavjud emas.");
            }
        } catch (TelegramApiException e) {
            System.out.println("Telegram API xatosi: " + e.getMessage());
            throw new APIException("Telegram API xatosi: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Noma'lum xatolik: " + e.getMessage());
            throw new APIException("Noma'lum xatolik: " + e.getMessage());
        }
    }

    public void hasCallback(Update update){

        if (update.hasCallbackQuery()) {
            CallbackQuery callbackQuery = update.getCallbackQuery();
            String data = callbackQuery.getData();
            Long chatId = callbackQuery.getMessage().getChatId();
            Integer messageId = callbackQuery.getMessage().getMessageId();

            if (data.startsWith("delete_")) {
                int firstIndex = data.indexOf("_");
                int lastIndex = data.indexOf("_", firstIndex + 1);
                String result = "";
                if (firstIndex != -1 && lastIndex != -1) {
                    result = data.substring(firstIndex + 1, lastIndex);
                    log.info("Deleted post: {}", result);
                } else {
                    log.info("Tag chiziqlar orasida substring topilmadi.");
                }
                postService.deletePostByBot(result);

                String[] split = getMessageIds(data).split(",");
                for (String id : split) {
                    deleteMessage(chatId,Integer.valueOf(id));
                }


            }
        }
    }

    public void deleteMessage(Long chatId, Integer messageId) {
        DeleteMessage deleteMessage = new DeleteMessage();
        deleteMessage.setChatId(chatId.toString());
        deleteMessage.setMessageId(messageId);
        telegramBotsApi.dlMsg(deleteMessage);
    }

    private InlineKeyboardMarkup createInlineKeyboard(String postId,String messageId) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

        List<InlineKeyboardButton> row1 = new ArrayList<>();
        InlineKeyboardButton deleteButton = new InlineKeyboardButton();
        deleteButton.setText("\uD83D\uDDD1\uFE0F O'chirish");
        deleteButton.setCallbackData("delete_" + postId + "_" + messageId);

        row1.add(deleteButton);

        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        keyboard.add(row1);

        inlineKeyboardMarkup.setKeyboard(keyboard);

        return inlineKeyboardMarkup;
    }

    public String getMessageIds(String fileName) { //messageId
        int lastIndex = fileName.lastIndexOf("_");
        return fileName.substring(lastIndex + 1);
    }

}
