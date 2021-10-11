package com.example.demo.view;

import com.example.demo.controller.ClientController;
import com.example.demo.model.Client;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Objects;

@Component
public class UserInfoView {

    private final ClientController controller;

    public UserInfoView(ClientController clientController) {
        this.controller = clientController;
    }

    public SendMessage help(Update update) {
        String text = "/reset - reset chatId\n" +
                "/info - overall info\n" +
                "/sho_tam - get info by chat id";
        return createResponseMessage(update, text);
    }

    public SendMessage rules(Update update){
        String info = "Загальна інформація \uD83D\uDCCB\n" +
                "\n" +
                "\uD83D\uDCB5Ціна:\n" +
                "1 тренування - 250 грн\n" +
                "5 тренувань - 800 грн\n" +
                "10 тренувань - 1300 грн\n" +
                "Безлім на місяць - 2500 грн\n" +
                "\n" +
                "\uD83D\uDD66Часові рамки:\n" +
                "5 тренувань / 3 тижні\n" +
                "10 тренувань / 5 тижнів\n" +
                "Безлім / календарний місяць\n" +
                "\n" +
                "❗(Час починає рахуватися від дати першого тренування)❗\n" +
                "\n" +
                "❗Запис на наступне тренування відбувається на тренуванні, або за добу до запланованого тренування❗\n" +
                "\n" +
                "❗Відміна тренування відбувається за добу до тренування❗\n" +
                "\n" +
                "Один раз на 10 тренувань є можливість виписатись з тренування в останній момент, далі тренування списуються❌\n" +
                "\n" +
                "В разі якщо мене немає, захворів, кудись поїхав, і тд - час моєї відсутності додається до часу абонементу ✔️";

        return createResponseMessage(update, info);
    }

    public SendMessage start(Update update) {
        int chatId = Integer.parseInt(update.getMessage().getChatId().toString());
        if(!checkChatId(chatId)){
            return createResponseMessage(update, "Для чого знову /start?\uD83E\uDD14\n" +
                    "Все ж уже працює\uD83D\uDE43");
        }

        String text =
                "Хелоу! \uD83D\uDE09\uD83E\uDD1C\uD83C\uDFFB\uD83E\uDD1B\uD83C\uDFFB\n" +
                        "Ага, замість того щоб Їсти/спати, хтось сидить в інтернетах \uD83D\uDE09\uD83D\uDE04\n" +
                        "Щоб розпочати роботу з ботом, введи свій номер телефону в форматі -> 0500000000";
        return createResponseMessage(update, text);
    }

    public SendMessage connect(Update update){
        String phone = update.getMessage().getText();
        int id = Integer.parseInt(update.getMessage().getChatId().toString());

        if(!checkPhone(phone)){
            return createResponseMessage(update, "Номер телефону не закріплений ні за ким в залі\uD83D\uDE35\n" +
                    "Спробуй ще раз\uD83D\uDE43");
        }

        Client client = controller.getByPhone(phone);

        if(chatIdPresent(client)){
            return createResponseMessage(update, "Користувач з цим номером телефону вже зареєєструвався\uD83E\uDDD0");
        }
        client.setChatid(id);
        controller.update(client);

        return createResponseMessage(update, "Є контакт \uD83D\uDE0A\n" +
                "\n" +
                "В цей бот будуть приходити:\n" +
                "-Сповіщення про відвідування\uD83C\uDFC3\u200D♂️\n" +
                "-Сповіщення про додавання нових тренувань\uD83D\uDE0E\n" +
                "-Нагадування про закінчення тренувань\uD83D\uDE23\n" +
                "-Важливі повідомлення\n" +
                "(коли наприклад зал закритий, і тренування відміняється)\uD83E\uDD37\uD83C\uDFFB\u200D♂️\n" +
                "-Ну і ще якщо щось придумаю, обов'язково напишу сюди\uD83D\uDE04\n" +
                "\n" +
                "❗Важливо❗\n" +
                "Цей бот НЕ відповідає на повідомлення, він тільки сповіщає\uD83E\uDDD0\n" +
                "Я не буду бачити повідомлень\uD83D\uDE2C\n" +
                "Тому якщо хочеш щось розказати, то приходь краще в зал\uD83D\uDE43\n" +
                "\n" +
                "Окей, якщо нічого не тягне і не болить, тоді можна спробувати отримати всю інформацію про твої тренування \uD83C\uDFCB️\u200D♂️\uD83D\uDEB4\u200D♂️\uD83C\uDFCB️\u200D♀️\uD83E\uDD3E\u200D♂️\uD83D\uDC83\n" +
                "\n" +
                "❗Натискай -> /sho_tam\n" +
                "\uD83C\uDFC3\u200D♂️\uD83C\uDFC3\u200D♀️" +
                "\n\n" +
                "А тут ти можеш дізнатись загальну інформацію щодо цін, правил запису та відміни тренування, або ж час дії абонементів\uD83D\uDCC5 -> /info");
    }

    public SendMessage info(Update update){
        int chatId = Integer.parseInt(update.getMessage().getChatId().toString());
        if(checkChatId(chatId)){
            return createResponseMessage(update, "Не поспішай! \uD83D\uDE2C\uD83E\uDD1A\n" +
                    "Щоб розпочати роботу з ботом, введи свій номер телефону в форматі -> 0500000000");
        }
        return createResponseMessage(update, controller.getByChatId(chatId).toString());
    }

    public SendMessage reset(Update update){
        int chatId = Integer.parseInt(update.getMessage().getChatId().toString());
        if(checkChatId(chatId)){
            return createResponseMessage(update, "ChatId does not exist");
        }
        Client client = controller.getByChatId(chatId);
        client.setChatid(0);
        controller.update(client);
        return createResponseMessage(update, "ChatId reset completed");
    }

    private SendMessage createResponseMessage(Update update, String text){
        SendMessage message = new SendMessage();
        message.setChatId(update.getMessage().getChatId().toString());
        message.setText(text);

        return message;
    }

    private boolean checkPhone(String phone){
        Client client = controller.getAbsolutelyAll().stream()
                .filter(n -> n.getPhone().equals(phone))
                .findFirst()
                .orElse(null);
        return Objects.nonNull(client);
    }

    private boolean checkChatId(int id){
        Client client = controller.getAbsolutelyAll().stream()
                .filter(n -> n.getChatid() == id)
                .findFirst()
                .orElse(null);
        return Objects.isNull(client);
    }

    private boolean chatIdPresent(Client client){
        return client.getChatid() != 0;
    }

}
