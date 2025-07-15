package uz.project.moviehivebot.bot; // 🔧 BU LINIYANI joyiga yozing!

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;
import uz.project.moviehivebot.controller.UpdateController;

@Component
@Log4j2
public class Bot extends TelegramWebhookBot {

    private final String token;
    private final String name;
    private final String webhookPath;
    private final UpdateController updateController;


    public Bot(
            @Value("${bot.token}") String token,
            @Value("${bot.name}") String name,
            @Value("${bot.webhook-url}") String webhookPath,
            UpdateController updateController
    ) {
        this.token = token;
        this.name = name;
        this.webhookPath = webhookPath;
        this.updateController = updateController;
    }

    @Override
    public String getBotUsername() {
        return name;
    }

    @Override
    public String getBotToken() {
        return token;
    }

    @Override
    public String getBotPath() {
        return webhookPath;
    }

    @Override
    public BotApiMethod<?> onWebhookUpdateReceived(Update update) {
        updateController.processUpdate(update);
        return null;
    }
}
