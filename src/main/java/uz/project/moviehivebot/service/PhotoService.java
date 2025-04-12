package uz.project.moviehivebot.service;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.*;
import uz.project.moviehivebot.entity.User;
import uz.project.moviehivebot.entity.enums.AddAdvertisementState;
import uz.project.moviehivebot.utils.ExecuteUtils;

import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Random;

@RequiredArgsConstructor
@Service
public class PhotoService {

    private static final String SAVE_DIRECTORY = "/Users/fahrittin/UploadedFilesFromTelegramBot/photoes";  // Yangi papka
    private final ExecuteUtils executeUtils;
    private final AdvertisementService advertisementService;
    private final MessageService messageService;
    private final UserService userService;
    private final ButtonService buttonService;

    @SneakyThrows
    public void savePhoto(Update update) {

        if (update.getMessage() == null || update.getMessage().getPhoto() == null || update.getMessage().getPhoto().isEmpty()) {
            executeUtils.execute(messageService.sendSomethingWrongMessage(update.getMessage().getChatId()));
        } else {
            Message message = update.getMessage();

            List<PhotoSize> photos = message.getPhoto();

            PhotoSize photoSize = photos.get(photos.size() - 1);
            String fileId = photoSize.getFileId();

            String savedPhotoUrl = savePhotoToStorage(fileId);

            advertisementService.setPhotoUrl(message.getChatId(), savedPhotoUrl);

            User user = userService.getUserByChatId(String.valueOf(update.getMessage().getChatId()));
            user.setState(AddAdvertisementState.APPROVAL_FOR_ADVERTISEMENT.name());
            userService.save(user);


            SendPhoto sendPhoto = new SendPhoto();
            sendPhoto.setChatId(update.getMessage().getChatId());

            InputFile inputFile = new InputFile(new java.io.File(savedPhotoUrl));
            sendPhoto.setPhoto(inputFile);

            sendPhoto.setCaption(advertisementService.getAdvertisement().getDescription());
            executeUtils.execute(sendPhoto);

            executeUtils.execute(messageService.sendApprovalMessage(update.getMessage().getChatId()));


        }

    }


    @SneakyThrows
    private String savePhotoToStorage(String fileId) {
        GetFile getFile = new GetFile();
        getFile.setFileId(fileId);
        File executedFile = executeUtils.execute(getFile);

        String fileUrl = "https://api.telegram.org/file/bot" + executeUtils.getBotToken() + "/" + executedFile.getFilePath();

        String fileExtension = getFileExtension(executedFile.getFilePath());
        String uniqueFileName = System.currentTimeMillis() + "_" + new Random().nextInt(1000) + fileExtension;
        Path path = Path.of(SAVE_DIRECTORY, uniqueFileName);

        if (Files.exists(path)) {
            return path.toString();
        }

        Files.createDirectories(path.getParent());

        try (InputStream in = new URL(fileUrl).openStream()) {
            Files.copy(in, path, StandardCopyOption.REPLACE_EXISTING);
        }

        return path.toString();
    }

    private String getFileExtension(String filePath) {
        if (filePath != null && filePath.contains(".")) {
            return filePath.substring(filePath.lastIndexOf("."));
        }
        return ".jpg";
    }

}
