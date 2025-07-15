package uz.project.moviehivebot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext; // 💡 To‘g‘ri import
import org.springframework.scheduling.annotation.EnableAsync;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import uz.project.moviehivebot.bot.Bot;

@SpringBootApplication
@EnableAsync
public class MovieHiveBotApplication {

    public static void main(String[] args) throws TelegramApiException {
        ApplicationContext context = SpringApplication.run(MovieHiveBotApplication.class, args);

        Bot bot = context.getBean(Bot.class);
        String webhookUrl = context.getEnvironment().getProperty("bot.webhook-url");

        TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
        SetWebhook setWebhook = SetWebhook.builder().url(webhookUrl).build();

        botsApi.registerBot(bot, setWebhook);
    }
}
