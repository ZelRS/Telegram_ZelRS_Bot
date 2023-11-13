package pro.sky.telegrambot.configuration;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.DeleteMyCommands;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
public class TelegramBotConfiguration {

    @Value("${telegram.bot.token}")
    private String token;

    @Value("${telegram.bot.msg.start}")
    private String startMsg;

    @Value("${telegram.bot.msg.help}")
    private String helpMsg;

    @Value("${telegram.bot.msg.notify}")
    private String notifyMsg;

    @Value("${telegram.bot.msg.success}")
    private String successMsg;

    @Value("${telegram.bot.msg.exception.datetime}")
    private String exceptionDateTimeMsg;

    @Value("${telegram.bot.msg.exception.text}")
    private String exceptionTextMsg;

    @Value("${telegram.bot.msg.exception.unknown}")
    private String exceptionUnknownMsg;

    @Bean
    public TelegramBot telegramBot() {
        TelegramBot bot = new TelegramBot(token);
        bot.execute(new DeleteMyCommands());
        return bot;
    }

}
