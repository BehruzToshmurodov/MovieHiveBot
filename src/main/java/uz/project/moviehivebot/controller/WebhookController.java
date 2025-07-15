package uz.project.moviehivebot.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.telegram.telegrambots.meta.api.objects.Update;
import uz.project.moviehivebot.bot.Bot;

@RestController
@RequiredArgsConstructor
public class WebhookController {

    private final Bot bot;

    @PostMapping("/webhook/update")
    public void onUpdateReceived(@RequestBody Update update) {
        bot.onWebhookUpdateReceived(update);
    }
}
