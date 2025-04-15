package uz.project.moviehivebot.service;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import uz.project.moviehivebot.entity.enums.AddAdvertisementState;
import uz.project.moviehivebot.entity.enums.State;

@Log4j
@Service
@RequiredArgsConstructor
public class UpdateService {

    private final CommandService commandService;
    private final VideoService videoService;

    public void processCallbackQuery(CallbackQuery callbackQuery) {

        String queryData = callbackQuery.getData();

        if (queryData.startsWith("change_role_")) {
            commandService.change_role(callbackQuery);
        } else if (queryData.startsWith("delete_super_admin_")) {
            commandService.deleteSuperAdmin(callbackQuery);
        } else if (queryData.startsWith("block_user_")) {
            commandService.blockUser(callbackQuery);
        } else if (queryData.startsWith("unblock_user_")) {
            commandService.unblockUser(callbackQuery);
        }

    }


    @SneakyThrows
    public void processTextMessage(Long chatId, String text, Message msg) {
        if (text == null || chatId == null) return;

        if (!commandService.checkUser(chatId)) {
            return;
        }


        switch (text) {

            case "/start" -> commandService.startCommand(chatId, text, msg);
            case "\uD83C\uDFA5 Movies" -> commandService.moviesCommand(chatId, text, msg);
            case "\uD83D\uDC65 Users" -> commandService.users(chatId, text, msg);
            case "\uD83D\uDC51 *Manage Super Admins*" -> commandService.managementOfSuperAdmins(chatId, text, msg);
            case "\uD83C\uDFAC Upload Movie" -> commandService.uploadMovieCommand(chatId, text, msg);
            case "\uD83D\uDC51 Add Super Admin" -> commandService.addSuperAdmin(chatId, text, msg);
            case "\uD83D\uDDD1\uFE0F Delete Super Admin \uD83D\uDC51" ->
                    commandService.deleteSuperAdminCommand(chatId, text, msg);
            case "\uD83D\uDEAB Block User" -> commandService.blockUserCommand(chatId, text, msg);
            case "\uD83D\uDD13 Unblock User" -> commandService.unblockUserCommand(chatId, text, msg);
            case "\uD83D\uDCE2 Add Advertisement" -> commandService.addAdvertisementCommand(chatId, text, msg);
            case "✅ Yes" -> commandService.yesCommand(chatId , text , msg);
            case "❌ No" -> commandService.noCommand(chatId , text , msg);
            case "\uD83D\uDD19 Back" -> commandService.mainMenuCommand(chatId);
            case "\uD83C\uDFE0 Main menu" -> commandService.mainMenuCommand(chatId);
            default -> {
                if (commandService.checkState(chatId)){
                   String state = commandService.getUserStateByChatId(chatId);
                   if (state.equals(AddAdvertisementState.WAITING_FOR_DESCRIPTION.name())) {
                       commandService.addDescriptionForAdvertisementCommand(chatId, text, msg);
                   } else if ( state.equals(State.WAITING_FOR_ENTER_MOVIE_CODE.name()) ){
                       videoService.sendMovieByCode(chatId , text , msg);
                   }
                } else {
                    commandService.invalidCommand(chatId , text , msg);
                }
            }

        }


    }


    @SneakyThrows
    public void processUploadPhoto(Update update) {
        commandService.uploadPhoto(update);
    }

    public void processUploadVideo(Update update) {
        videoService.uploadMovie(update);

    }
}
