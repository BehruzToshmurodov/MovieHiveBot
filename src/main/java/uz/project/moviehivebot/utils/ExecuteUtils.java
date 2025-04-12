package uz.project.moviehivebot.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.bots.DefaultBotOptions;

@Component
public class ExecuteUtils extends DefaultAbsSender {

    public ExecuteUtils(DefaultBotOptions options,
                   @Value("${bot.token}") String botToken) {
        super(options, botToken);
    }


}
