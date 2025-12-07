package api.kitabu.uz.telegramBot;

import api.kitabu.uz.entity.PostEntity;
import api.kitabu.uz.enums.*;
import api.kitabu.uz.exeptions.exceptionhandler.APIException;
import api.kitabu.uz.repository.ProfileRepository;
import api.kitabu.uz.service.*;
import api.kitabu.uz.telegramBot.dto.PostCreateBot;
import api.kitabu.uz.telegramBot.dto.PostFullData;
import api.kitabu.uz.telegramBot.dto.ProfileCreateBot;
import api.kitabu.uz.telegramBot.service.TelegramSendPostListService;
import api.kitabu.uz.util.MD5util;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.*;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.*;

import static api.kitabu.uz.telegramBot.dto.InlineButtonLists.*;

@Component
public class TelegramBotsApi extends TelegramLongPollingBot {
    @Autowired
    private AttachService attachService;
    @Autowired
    private ProfileService profileService;
    @Autowired
    private RegionService regionService;
    @Autowired
    private GenreService genreService;
    @Autowired
    private PostService postService;
    @Autowired
    private @Lazy TelegramSendPostListService telegramSendPostListService;
    @Autowired
    private @Lazy TelegramSendPostListService sendPostListService;
    @Autowired
    private ProfileRepository profileRepository;
    private PostCreateBot postCreateBot = new PostCreateBot();
    private final ProfileCreateBot profileCreateBot = new ProfileCreateBot();
    private String profileId = null;

   /* @Value("${telegram.bot.username}")
    private String botUsername;

    @Value("${telegram.bot.token}")

    private String botToken;*/


    private Map<String, String> userState = new HashMap<>();
    private Map<String, String> userRegister = new HashMap<>();
    private Map<String, String> priceState = new HashMap<>();
    private Map<String, String> startState = new HashMap<>();

    public TelegramBotsApi(org.telegram.telegrambots.meta.TelegramBotsApi api) throws TelegramApiException {
        super("7484522460:AAH66GbNMlOi39bkm_Tj1tvWMHl0tnFqW3Y");
        api.registerBot(this);
    }

    @Override
    public String getBotUsername() {
        return "kitabjonbot";
    }
    //local
    /* public TelegramBotsApi(org.telegram.telegrambots.meta.TelegramBotsApi api) throws TelegramApiException {
        super("7403539766:AAHn3c1N2O_3LlWAriAFZWdWuj9gGl-gnJQ");
        api.registerBot(this);
    }

    @Override
    public String getBotUsername() {
        return "HayaluzBot";
    }*/


    @Override
    public void onUpdateReceived(Update update) {
        sendPostListService.hasCallback(update);
        started(update);
    }

    private void started(Update update) {
        boolean generalVisible = false;
        if (update.hasMessage() && update.getMessage().hasText()) {
            String chatId = update.getMessage().getChatId().toString();
            String messageText = update.getMessage().getText();
            textMessage(update, chatId, messageText);
        }
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            if (messageText.equals("E'lonlarim") && profileId != null) {
                int count = 0;
                PostFullData full = getProfilePosts();
                if (full != null) {
                    for (PostEntity postEntity : full.getPostEntityList()) {
                        count++;
                        telegramSendPostListService.sendToBot(postEntity, full.getImages().get(postEntity.getId()), update.getMessage().getChatId());
                    }
                    if (count == 0) {
                        sendMsg(update.getMessage().getChatId().toString(), italicAndBold("Sizda hali e'lonlar mavjud emas"));
                    }
                    count = 0;
                    full.getImages().clear();
                    full.getPostEntityList().clear();
                } else {
                    sendMsg(update.getMessage().getChatId().toString(), italicAndBold("Sizda hali e'lonlar mavjud emas"));
                }
            } else if (profileId == null && messageText.equals("E'lonlarim")) {
                sendMsg(update.getMessage().getChatId().toString(), italicAndBold("Siz hali raqamni jo'natmadingiz"));
            }
        } else if (update.hasMessage() && update.getMessage().hasContact()) {
            Contact contact = update.getMessage().getContact();
            String phoneNumber = contact.getPhoneNumber();
            profileCreateBot.setPhone(phoneNumber);
            var profile = profileRepository.findByPhoneAndVisible(phoneNumber, true);
            if (profile.isEmpty()) {
                SendMessage sendMessage = new SendMessage();
                sendMessage.setChatId(update.getMessage().getChatId().toString());
                sendMessage.setText(italicAndBold("Siz hali Kitabu saytidan ro'yxatdan o'tmagan ekansiz"));
                sendMessage.setParseMode("HTML");
                sendMessage.setReplyToMessageId(update.getMessage().getMessageId());
                Message sentMessage = null;
                try {
                    sentMessage = execute(sendMessage);
                } catch (TelegramApiException e) {
                    throw new RuntimeException(e);
                }
                caseButton(update, new String[]{"Saytga orqali", "Bot orqali"}, new String[]{"site_reg", "bot_reg"});

                try {
                    Thread.sleep(8000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                try {
                    DeleteMessage deleteMessage = new DeleteMessage();
                    deleteMessage.setChatId(update.getMessage().getChatId().toString());
                    deleteMessage.setMessageId(sentMessage.getMessageId());
                    execute(deleteMessage);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            } else {
                postCreateBot.setProfileId(profile.get().getId());
                profileId = profile.get().getId();
                start(update);
            }
        } else if (update.hasMessage() && update.getMessage().hasPhoto()) {
            if (update.hasMessage() && update.getMessage().hasPhoto()) {
                postCreateBot.imagesAdd(photoAdd(update));
                caseButton(update, new String[]{"Yana rasm yuklaysizmi", "Keyingi qadamga o'tish"}, new String[]{"upload_more_photo", "next_step"});
                userState.put(update.getMessage().getChatId().toString(), "waiting_for_book_name");

            }
        } else if (update.hasCallbackQuery()) {
            String callBackData = update.getCallbackQuery().getData();
            String chatId = update.getCallbackQuery().getMessage().getChatId().toString();
            boolean regionVisible = false;
            boolean genreVisible = false;
            boolean exchangeVisible = false;
            boolean conditionVisible = false;
            boolean languageVisible = false;
            boolean bookPrintTypeVisible = false;
            Integer messageId = update.getCallbackQuery().getMessage().getMessageId();
            Result result = getResult(update, callBackData, chatId, messageId, genreVisible, regionVisible, exchangeVisible, conditionVisible, languageVisible, bookPrintTypeVisible);
            condition(update, result.regionVisible(), result.genreVisible(), result.exchangeVisible(), result.conditionVisible(), result.languageVisible(), result.bookPrintTypeVisible(), generalVisible);


        }

    }

    private Result getResult(Update update, String callBackData, String chatId, Integer messageId, boolean genreVisible, boolean regionVisible, boolean exchangeVisible, boolean conditionVisible, boolean languageVisible, boolean bookPrintTypeVisible) {
        switch (callBackData) {
            case "ELON" -> {
                sendMessage(chatId, "Kitob rasmini jo`nating", messageId);
            }
            case "upload_more_photo" -> {
                sendMessage(chatId, "Rasmni yuboring", messageId);
                userState.put(chatId, "waiting_for_photo");
            }
            case "next_step" -> {
                sendMessage(chatId, "Kitob nomini kiriting", messageId);
                userState.put(chatId, "waiting_for_book_name");
            }
            case "bot_reg" -> {
                sendMessage(chatId, "Ismingizni kiriting", messageId);
                userRegister.put(chatId, "waiting_for_name");
            }
            case "add_genre" -> {
                sendButtonList(update, genres, "Kitob janrini tanlang");
            }
            case "genre_continue" -> genreVisible = true;

            case "Toshkent shahri",
                    "Toshkent viloyati",
                    "Andijon viloyati",
                    "Buxoro viloyati",
                    "Jizzax viloyati",
                    "Qoraqalpog‘iston",
                    "Navoiy viloyati",
                    "Namangan viloyati",
                    "Samarqand viloyati",
                    "Sirdaryo viloyati",
                    "Farg‘ona viloyati",
                    "Xorazm viloyati",
                    "Surxondaryo viloyati",
                    "Qashqadaryo viloyati" -> {
                String data = update.getCallbackQuery().getData();
                postCreateBot.setRegionId(regionService.getByNameUz(data));
                regionVisible = true;
            }
            case "Triller",
                    "Hududiy adabiyot",
                    "Detektiv",
                    "Fantaziya",
                    "Ilmiy adabiyot",
                    "Drama",
                    "Qo`rqinchli",
                    "Romantika",
                    "Tarixiy",
                    "Diniy",
                    "Biografiya",
                    "Ilmiy fantastika",
                    "Bolalar adabiyoti",
                    "She'riyat",
                    "Chet tili",
                    "Ilmiy ommabop",
                    "Tragediya" -> {
                String data = update.getCallbackQuery().getData();
                postCreateBot.genresAdd(genreService.getByName(data));
                caseButtonForGenre(chatId, update.getCallbackQuery().getMessage().getMessageId(), new String[]{"Yana janr tanlaysizmi", "Keyingi qadamga o'tish"}, new String[]{"add_genre", "genre_continue"});
            }
            case "Hadya", "Vaqtinchalik", "Almashish", "Sotish" -> {
                String exchange = update.getCallbackQuery().getData();
                switch (exchange) {
                    case "Hadya" -> {
                        postCreateBot.setExchangeType(ExchangeType.FREE);
                        exchangeVisible = true;
                    }
                    case "Vaqtinchalik" -> {
                        postCreateBot.setExchangeType(ExchangeType.TEMPORARILY);
                        exchangeVisible = true;
                    }
                    case "Almashish" -> {
                        postCreateBot.setExchangeType(ExchangeType.EXCHANGE);
                        exchangeVisible = true;
                    }
                    case "Sotish" -> {
                        postCreateBot.setExchangeType(ExchangeType.SELL);
                        sendMessage(chatId, "Sotmoqchi bo`lgan narxingizni kiriting", messageId);
                        priceState.put(chatId, "price_sell");
                    }
                }
            }
            case "Yangi", "Ishlatilgan", "Eski" -> {
                String conditions = update.getCallbackQuery().getData();
                switch (conditions) {
                    case "Yangi" -> postCreateBot.setCondition(ConditionType.NEW);
                    case "Ishlatilgan" -> postCreateBot.setCondition(ConditionType.USED);
                    case "Eski" -> postCreateBot.setCondition(ConditionType.OLD);
                }

                conditionVisible = true;
            }
            case "en", "ru", "latin", "krill" -> {
                String lan = update.getCallbackQuery().getData();
                switch (lan) {
                    case "en" -> postCreateBot.setLanguage(BookLanguage.EN);
                    case "ru" -> postCreateBot.setLanguage(BookLanguage.RU);
                    case "latin" -> postCreateBot.setLanguage(BookLanguage.LATIN);
                    case "krill" -> postCreateBot.setLanguage(BookLanguage.KIRILL);
                }

                languageVisible = true;
            }
            case "Kitob shaklida", "Pereplyot" -> {
                String print = update.getCallbackQuery().getData();
                switch (print) {
                    case "Kitob shaklida" -> postCreateBot.setBookPrintType(BookPrintType.PAPER_BOOK);
                    case "Pereplyot" -> postCreateBot.setBookPrintType(BookPrintType.PEREPLYOT);
                }
                bookPrintTypeVisible = true;
                editMsg(chatId, "Joylagan e'loningiz 24 soat ichida ko'rib chiqiladi", update.getCallbackQuery().getMessage().getMessageId());
            }
        }
        return new Result(regionVisible, genreVisible, exchangeVisible, conditionVisible, languageVisible, bookPrintTypeVisible);
    }

    private record Result(boolean regionVisible, boolean genreVisible, boolean exchangeVisible,
                          boolean conditionVisible, boolean languageVisible, boolean bookPrintTypeVisible) {
    }

    private void textMessage(Update update, String chatId, String messageText) {
        if (update.getMessage().getText().equals("/start")) {
            sendRequestForPhoneNumber(update.getMessage().getChatId());
        } else if (userState.containsKey(chatId) && userState.get(chatId).equals("waiting_for_book_name")) {
            postCreateBot.setTitle(messageText);
            sendMsg(chatId, "Kitob muallifi nomini kiriting");
            userState.put(chatId, "waiting_for_author_name");
        } else if (userState.containsKey(chatId) && userState.get(chatId).equals("waiting_for_author_name")) {
            postCreateBot.setAuthorName(messageText);
            sendMsg(chatId, "Kitob haqida qisqacha malumot bering");
            userState.put(chatId, "waiting_for_description");
        } else if (userState.containsKey(chatId) && userState.get(chatId).equals("waiting_for_description")) {
            postCreateBot.setDescription(messageText);
            userState.clear();
            sendButtonList(update, regions, "Viloyatni tanlang");
        } else if (userRegister.containsKey(chatId) && userRegister.get(chatId).equals("waiting_for_name")) {
            profileCreateBot.setFirstName(messageText);
            sendMsg(chatId, "Familiyangizni kiriting");
            userRegister.put(chatId, "waiting_for_surname");
        } else if (userRegister.containsKey(chatId) && userRegister.get(chatId).equals("waiting_for_surname")) {
            profileCreateBot.setLastName(messageText);
            sendMsg(chatId, "Parolingizni kiriting kamida 6 ta belgidan iborat bo'lsin");
            userRegister.put(chatId, "waiting_for_password");
        } else if (userRegister.containsKey(chatId) && userRegister.get(chatId).equals("waiting_for_password")) {
            profileCreateBot.setPassword(MD5util.encode(messageText));
            profileService.createForBot(profileCreateBot);
            sendMsg(chatId, "Siz ro`yxatdan o`tdingiz qayta /start ni bosing");
            userRegister.clear();
        } else if (priceState.containsKey(chatId) && priceState.get(chatId).equals("price_sell")) {
            if (NumberUtils.isDigits(messageText)) {
                postCreateBot.setPrice(Double.parseDouble(messageText));
                sendMsg(chatId, "Bozordagi narxni kiriting");
                priceState.put(chatId, "really_price");
            } else {
                sendMsg(chatId, "Iltimos raqam kiriting");
            }
        } else if (priceState.containsKey(chatId) && priceState.get(chatId).equals("really_price")) {
            if (NumberUtils.isDigits(messageText)) {
                postCreateBot.setMarketPrice(Double.parseDouble(messageText));
                sendButtonList(update, conditionType, "Kitob holatini tanlang");
                priceState.clear();
            } else {
                sendMsg(chatId, "Iltimos raqam kiriting");
            }
        } /*else if (!messageText.equals("/start")*//* && startState.get(chatId).equals("mazgi")*//*) {
            if (startState.get(chatId) == null) {
                sendMsg(chatId, italicAndBold("/start tugmasini bosing😊"));
                startState.put(chatId, "start_waiting");
            } else if (startState.get(chatId).equals("start_waiting")) {
                sendMsg(chatId, italicAndBold("Telefon raqamni ulashish tanlovini bosing\uD83D\uDC47"));
                startState.put(chatId, "started");
            } else if (startState.get(chatId).equals("started")) {
                sendMsg(chatId, italicAndBold("Notog'ri format"));
                startState.put(chatId, "started");
            }
        }*/
    }

    private void condition(Update update, boolean regionVisible, boolean genreVisible, boolean exchangeVisible, boolean conditionVisible, boolean languageVisible, boolean bookPrintTypeVisible, boolean generalVisible) {
        if (regionVisible) {
            sendButtonList(update, genres, "Kitob janrini tanlang");
        }
        if (genreVisible) {
            sendButtonList(update, exchangeType, "E'lon turkumini tanlang");
        }
        if (exchangeVisible) {
            sendButtonList(update, conditionType, "Kitob holatini tanlang");
        }
        if (conditionVisible) {
            sendButtonList(update, languages, "Kitob tilini tanlang");
        }
        if (languageVisible) {
            sendButtonList(update, bookPrintTypes, "Kitob shaklini tanlang");
        }
        if (bookPrintTypeVisible) {
            generalVisible = true;
        }
        if (generalVisible) {
            postService.createPostForBot(postCreateBot);
            postCreateBot.getImages().clear();
            postCreateBot.getGenres().clear();
            postCreateBot = null;
        }
    }

    private void caseButton(Update update, String[] messages, String[] callback) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowList = getListList(messages, callback);
        inlineKeyboardMarkup.setKeyboard(rowList);

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(update.getMessage().getChatId().toString());
        sendMessage.setText(italicAndBold("Tanlov"));
        sendMessage.setParseMode("HTML");
        sendMessage.setReplyMarkup(inlineKeyboardMarkup);

        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private static List<List<InlineKeyboardButton>> getListList(String[] messages, String[] callback) {
        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();

        List<InlineKeyboardButton> row1 = new ArrayList<>();
        InlineKeyboardButton button1 = new InlineKeyboardButton();
        if (callback[0].equals("site_reg")) {
            button1.setText(messages[0]);
            button1.setUrl("https://kitabu.uz/register");
            row1.add(button1);
        } else {
            button1.setText(messages[0]);
            button1.setCallbackData(callback[0]);
            row1.add(button1);
        }
        InlineKeyboardButton button2 = new InlineKeyboardButton();
        button2.setText(messages[1]);
        button2.setCallbackData(callback[1]);
        row1.add(button2);

        rowList.add(row1);
        return rowList;
    }

    private void caseButtonForGenre(String chatId, Integer messageId, String[] messages, String[] callback) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowList = getLists(messages, callback);
        inlineKeyboardMarkup.setKeyboard(rowList);

        EditMessageText sendMessage = new EditMessageText();
        sendMessage.setChatId(chatId);
        sendMessage.setText(italicAndBold("Tanlov"));
        sendMessage.setParseMode("HTML");
        sendMessage.setMessageId(messageId);
        sendMessage.setReplyMarkup(inlineKeyboardMarkup);

        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private static List<List<InlineKeyboardButton>> getLists(String[] messages, String[] callback) {
        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();

        List<InlineKeyboardButton> row1 = new ArrayList<>();
        InlineKeyboardButton button1 = new InlineKeyboardButton();

        button1.setText(messages[0]);
        button1.setCallbackData(callback[0]);
        row1.add(button1);

        InlineKeyboardButton button2 = new InlineKeyboardButton();
        button2.setText(messages[1]);
        button2.setCallbackData(callback[1]);
        row1.add(button2);

        rowList.add(row1);
        return rowList;
    }

    private void start(Update update) {
        SendMessage message = new SendMessage();
        message.setChatId(update.getMessage().getChatId().toString());
        message.setText(italicAndBold("Assalomu alaykum " + update.getMessage().getFrom().getFirstName()));
        message.setParseMode("HTML");
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = getLists();

        inlineKeyboardMarkup.setKeyboard(rowsInline);
        message.setReplyMarkup(inlineKeyboardMarkup);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private static List<List<InlineKeyboardButton>> getLists() {
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();

        List<InlineKeyboardButton> rowInline = new ArrayList<>();
        InlineKeyboardButton elonButton = new InlineKeyboardButton();
        elonButton.setText("E'lon joylash");
        elonButton.setCallbackData("ELON");

        rowInline.add(elonButton);
        rowsInline.add(rowInline);
        return rowsInline;
    }

    private void sendMsg(String chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(italicAndBold(text));
        message.setParseMode("HTML");
        try {
            execute(message);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }

    }


    private void editMsg(String chatId, String text, Integer messageId) {
        EditMessageText message = new EditMessageText();
        message.setChatId(chatId);
        message.setText(italicAndBold(text));
        message.setParseMode("HTML");
        message.setMessageId(messageId);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }

    }

    private void sendButtonList(Update update, String[] buttonList, String text) {
        String chatId;
        InlineKeyboardMarkup keyboard = createInlineKeyboard(buttonList);

        if (update.hasMessage()) {
            chatId = update.getMessage().getChatId().toString();
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(chatId);
            sendMessage.setText(italicAndBold(text));
            sendMessage.setParseMode("HTML");
            sendMessage.setReplyMarkup(keyboard);

            try {
                execute(sendMessage);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        } else if (update.hasCallbackQuery()) {
            chatId = update.getCallbackQuery().getMessage().getChatId().toString();
            Integer messageId = update.getCallbackQuery().getMessage().getMessageId();

            EditMessageText editMessage = new EditMessageText();
            editMessage.setChatId(chatId);
            editMessage.setText(italicAndBold(text));
            editMessage.setParseMode("HTML");
            editMessage.setReplyMarkup(keyboard);
            editMessage.setMessageId(messageId);

            try {
                execute(editMessage);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Xatolik: Update na Message va na CallbackQuery o'z ichiga oladi");
        }
    }


    private InlineKeyboardMarkup createInlineKeyboard(String[] regions) {
        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();

        for (String region : regions) {
            InlineKeyboardButton button = new InlineKeyboardButton();
            button.setText(region);
            button.setCallbackData(region);

            buttons.add(List.of(button));
        }

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        inlineKeyboardMarkup.setKeyboard(buttons);
        return inlineKeyboardMarkup;
    }


    private String photoAdd(Update update) {

        List<PhotoSize> photos = update.getMessage().getPhoto();

        PhotoSize largestPhoto = photos.stream()
                .max(Comparator.comparing(PhotoSize::getFileSize))
                .orElse(null);

        if (largestPhoto != null) {
            String fileId = largestPhoto.getFileId();
            try {
                File file = execute(new GetFile(fileId));

                String fileUrl = "https://api.telegram.org/file/bot" + getBotToken() + "/" + file.getFilePath();

                InputStream inputStream = new URL(fileUrl).openStream();

                MultipartFile multipartFile = createMultipartFile(inputStream, file.getFilePath());
                return attachService.uploadFile(multipartFile, AppLanguage.en).getData().id();
            } catch (IOException | TelegramApiException e) {
                e.printStackTrace();
            }
        }
        return null;
    }


    private MultipartFile createMultipartFile(InputStream inputStream, String fileName) throws IOException {
        byte[] fileContent = inputStream.readAllBytes();
        return new MockMultipartFile("file", fileName, "image/jpeg", fileContent);
    }

    public void sendMessage(String chatId, String text, Integer messageId) {
        EditMessageText sendMessage = new EditMessageText();
        sendMessage.setChatId(chatId);
        sendMessage.setText(italicAndBold(text));
        sendMessage.setParseMode("HTML");
        sendMessage.setMessageId(messageId);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendRequestForPhoneNumber(Long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(italicAndBold("""
                Assalomu alaykum yaxshimisiz siz kitabu.uz platformasini botiga tashrif buyurdingiz.
                Bu platforma sizning o‘qib bo‘lgan kitoblaringizni uyda chang bosib  yotishini oldini oladi.
                Bu platformada kitoblaringizni sotishingiz , alishtirishingiz , hadya qilishingiz  va sotish imkonini beradi.
                Agar siz bu platformaga tashrif buyurmagan bo‘lsangiz telefon raqamingizni jo‘natib ro‘yxatdan o‘tishingiz mumkin.
                Tashrif buyurgan bo‘lsangiz telefon raqamingizni jo‘natish orqali e’lonlaringizni ko‘rinishingiz mumkin."""));
        message.setParseMode("HTML");
        ReplyKeyboardMarkup keyboardMarkup = getReplyKeyboardMarkup();
        message.setReplyMarkup(keyboardMarkup);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private static ReplyKeyboardMarkup getReplyKeyboardMarkup() {
        KeyboardButton sharePhoneButton = new KeyboardButton();
        sharePhoneButton.setText("Telefon raqamni ulashish");
        sharePhoneButton.setRequestContact(true);

        KeyboardButton elonlarim = new KeyboardButton();
        elonlarim.setText("E'lonlarim");

        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        keyboardMarkup.setResizeKeyboard(true);
        keyboardMarkup.setOneTimeKeyboard(true);

        KeyboardRow keyboardRow = new KeyboardRow();
        keyboardRow.add(sharePhoneButton);
        keyboardRow.add(elonlarim);

        List<KeyboardRow> keyboard = new ArrayList<>();
        keyboard.add(keyboardRow);

        keyboardMarkup.setKeyboard(keyboard);
        return keyboardMarkup;
    }

    public PostFullData getProfilePosts() {
        return postService.getProfilePosts(profileId);
    }

    public void dlMsg(DeleteMessage deleteMessage) {
        try {
            execute(deleteMessage);
        } catch (TelegramApiException e) {
            throw new APIException("Telegram bot exception");
        }
    }

    public String italicAndBold(String text) {
        return String.format("<b><i>%s</i></b>", text);
    }

}