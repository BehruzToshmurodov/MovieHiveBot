package uz.project.moviehivebot.bot;

import lombok.extern.log4j.Log4j;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;
import uz.project.moviehivebot.controller.UpdateController;

@Component
@Log4j2
public class Bot extends TelegramLongPollingBot {

    @Value("${bot.name}")
    private String name;

    private final UpdateController updateController;

    public Bot(@Value("${bot.token}") String token, UpdateController updateController) {
        super(token);
        this.updateController = updateController;
    }

    @Override
    public void onUpdateReceived(Update update) {
        updateController.processUpdate(update);
    }


    @Override
    public String getBotUsername() {
        return name;
    }

}
