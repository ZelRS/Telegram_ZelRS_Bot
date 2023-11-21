package pro.sky.telegrambot.keyboard;

import com.pengrad.telegrambot.model.request.KeyboardButton;
import com.pengrad.telegrambot.model.request.ReplyKeyboardMarkup;
import com.pengrad.telegrambot.model.request.ReplyKeyboardRemove;
import com.pengrad.telegrambot.request.SendMessage;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

//  класс для клавиатуры под строкой ввода
@Data
@Slf4j
public class ReplyKeyboard {
    /*  метод ползволяет создать клавиатуру под строкой ввода. Принимает в параметры сообщение ответа и тексты кнопок.
     *  Добавлять можно любое необходимое количество кнопок.
     *  В методе установлена защита от удаления/изменения параметров его вызова, когда старая реализация остается в чате.
     *  Для этого в методе предусмотрена предварительная чистка с помощью  класса ReplyKeyboardRemove*/
    public static void setReplyKeyboard(SendMessage sendMessage, String... button) {
        removeOldKeyboard(sendMessage);
        sendMessage.replyMarkup(new ReplyKeyboardMarkup(createButtons(button)));
    }

    private static KeyboardButton[] createButtons(String... button) {
        List<KeyboardButton> buttonsList = new ArrayList<>();
        for (String s : button) {
            KeyboardButton keyboardButton = new KeyboardButton(s);
            buttonsList.add(keyboardButton);
        }
        return buttonsList.toArray(new KeyboardButton[0]);
    }

    private static void removeOldKeyboard(SendMessage sendMessage) {
        sendMessage.replyMarkup(new ReplyKeyboardRemove());
    }
}
