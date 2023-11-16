package pro.sky.telegrambot.keyboard;

import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.request.SendMessage;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

//  класс для встроенной в сообщение клавиатуры
@Data
@Slf4j
public class InlineKeyboard {
    //  список, хранящий в себе ряды кнопок
    private static final List<InlineKeyboardButton[]> list = new ArrayList<>();

    //  метод, создающий ряд из пришедших в параметр кнопок
    public static void createInlineButtonsRow(InlineKeyboardButton... button) {
        List<InlineKeyboardButton> buttonsList = new ArrayList<>(Arrays.asList(button));
        InlineKeyboardButton[] inlineKeyboardButtons = buttonsList.toArray(new InlineKeyboardButton[0]);
        list.add(inlineKeyboardButtons);
    }

    //  завершающий метод, который устанавливает клавиатуру в сообщение.
    public static void setInlineKeyboard(SendMessage sendMessage) {
        sendMessage.replyMarkup(new InlineKeyboardMarkup(list.toArray(new InlineKeyboardButton[0][])));
        log.info("Keyboard was parsed");
        list.clear();
    }


}
