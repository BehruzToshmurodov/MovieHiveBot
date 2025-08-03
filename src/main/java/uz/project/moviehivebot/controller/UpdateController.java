package uz.project.moviehivebot.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import uz.project.moviehivebot.service.UpdateService;


@Component
@RequiredArgsConstructor
@Log4j2
public class UpdateController {

    private final UpdateService updateService;


    public void processUpdate(Update update) {
        if (update == null)
            log.error("Update is null");
        else
            distributeMessageByType(update);

    }

    private void distributeMessageByType(Update update) {

        if (update.hasChannelPost()) {
            Message channelPost = update.getChannelPost();
            if (channelPost.hasDocument()) {
                updateService.processUploadVideo(update);
            }
            return;
        }

        if (update.hasCallbackQuery()) {
            updateService.processCallbackQuery(update.getCallbackQuery());
            return;
        }

        Message message = update.getMessage();

        if (message == null) {
            log.warn("Update does not contain any message or channel post");
            return;
        }

        if (message.hasPhoto()) {
            updateService.processUploadPhoto(update);
        } else if (message.hasVideo()) {
            updateService.processUploadVideo(update);
        } else if (message.hasText() && !message.hasDocument()) {
            updateService.processTextMessage(message.getChatId(), message.getText(), message);
        } else {
            log.warn("Unsupported update type for chat: " + message.getChatId());
        }
    }
}
