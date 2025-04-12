package uz.project.moviehivebot.service;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.send.SendVideo;
import org.telegram.telegrambots.meta.api.objects.*;
import uz.project.moviehivebot.entity.Movie;
import uz.project.moviehivebot.repository.MovieRepository;
import uz.project.moviehivebot.utils.ExecuteUtils;

import java.io.IOException;
import java.util.Random;

@Service
@Log4j
@RequiredArgsConstructor
public class VideoService {

    private final ExecuteUtils executeUtils;
    private static final String CHANNEL_ID = "-1002618857199";
    private final MovieRepository movieRepository;
    private final MessageService messageService;


    @SneakyThrows
    public void uploadMovie(Update update) {

        Message message = update.getChannelPost();

        if (message.hasDocument()) {
            Document document = message.getDocument();
            String fileName = document.getFileName();
            String caption = message.getCaption();

            if (fileName != null && fileName.contains("_")) {
                String nameWithExt = fileName.substring(0, fileName.indexOf('.'));
                String[] s = nameWithExt.split("_");
                String movieName = s[0];
                String movieCode = s[1];

                Movie movie = new Movie();
                movie.setMovieName(movieName);
                movie.setCaption(caption);
                movie.setMovieCode(movieCode);
                movie.setFileId(document.getFileId()); // Document orqali file_id olish
                movieRepository.save(movie);

                executeUtils.execute(messageService.sendMovieSavedMessage(Long.valueOf("2087546645")));
            }
        } else {
            log.warn("No document found in the update.");
        }
    }

    @SneakyThrows
    public void sendMovieByCode(Long chatId, String text, Message msg) {
        Movie movieByMovieCode = movieRepository.getMovieByMovieCode(text);

        if (movieByMovieCode != null) {
            sendMovieToUser(chatId, movieByMovieCode);
        } else {
            executeUtils.execute(messageService.sendSomethingWrongMessage(chatId));
        }

    }

    @SneakyThrows
    private void sendMovieToUser(Long chatId, Movie movieByMovieCode) {

        String fileId = movieByMovieCode.getFileId();

        SendVideo sendVideoRequest = new SendVideo();
        sendVideoRequest.setChatId(chatId.toString());
        sendVideoRequest.setVideo(new InputFile(fileId));
        sendVideoRequest.setCaption(movieByMovieCode.getCaption());

        executeUtils.execute(sendVideoRequest);


    }
}
