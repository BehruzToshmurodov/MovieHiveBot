package uz.project.moviehivebot.service;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.*;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import uz.project.moviehivebot.entity.Advertisement;
import uz.project.moviehivebot.entity.User;
import uz.project.moviehivebot.entity.enums.AddAdvertisementState;
import uz.project.moviehivebot.entity.enums.Role;
import uz.project.moviehivebot.entity.enums.State;
import uz.project.moviehivebot.entity.enums.Status;
import uz.project.moviehivebot.repository.UserRepository;
import uz.project.moviehivebot.utils.ExecuteUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Log4j
public class CommandService {

    private final UserService userService;
    private final MessageService messageService;
    private final ExecuteUtils executeUtils;
    private final ButtonService buttonService;
    private final AdvertisementService advertisementService;
    private final PhotoService photoService;


    private static final String CHANNEL_ID = "-1002618857199";

    @NotNull
    private final Map<Long, Integer> userState = new HashMap<>();

    private final UserRepository userRepository;

    public void startCommand(Long chatId, Message msg) throws TelegramApiException {

        userService.checkExistingUser(msg.getFrom());
        userState.put(chatId, 0);

        SendMessage message;

        if (userService.checkIsAdmin(chatId)) {
            message = messageService.sendAdminWelcomeMessage(chatId);
        } else {
            message = messageService.sendUserWelcomeMessage(chatId, msg);
        }

        message = buttonService.buttonMenu(message);
        executeUtils.execute(message);

        SendMessage sendMessage;

        if (userService.checkIsAdmin(chatId)) {
            sendMessage = messageService.sendAdminOptionsMessage(chatId);
        } else {

            sendMessage = messageService.sendUserOptionMessage(chatId);
        }

        executeUtils.execute(sendMessage);
    }

    @SneakyThrows
    public void moviesCommand(Long chatId, String text, Message msg) {

        if (userState.getOrDefault(chatId, 0) == 0 ){
            userState.put(chatId, 1);

            ReplyKeyboardMarkup buttons = buttonService.createButtons(List.of("\uD83C\uDFE0 Main menu"), false);

            SendMessage sendMessage = messageService.sendUserMovieCodeMessage(chatId);

            sendMessage.setReplyMarkup(buttons);

            executeUtils.execute(sendMessage);

            User user = userService.getUserByChatId(String.valueOf(chatId));
            user.setState(State.WAITING_FOR_ENTER_MOVIE_CODE.name());
            userRepository.save(user);

        } else {
            executeUtils.execute(messageService.sendSomethingWrongMessage(chatId));
        }

    }


    @SneakyThrows
    public void users(Long chatId, String text, Message msg) {

        if( userState.getOrDefault(chatId, 0) == 0 ){
            SendMessage sendMessage;

            if (!userService.checkIsAdmin(chatId)) {
                executeUtils.execute(messageService.sendHaveNoPermissionMessage(chatId));
            } else {

                List<User> users = userService.findByRole("USER");

                if (users.isEmpty()) {
                    executeUtils.execute(messageService.sendUsersNotFoundMessage(chatId));
                    return;
                }

                StringBuilder messageText = new StringBuilder("👥 *User List:*\n\n");

                for (User user : users) {
                    messageText.append("🆔 Chat ID: `").append(user.getId() != null ? user.getId() : "N/A").append("`\n");
                    messageText.append("👤 Name: *").append(user.getFirstName() != null ? user.getFirstName() : "Unknown")
                            .append(" ").append(user.getLastName() != null ? user.getLastName() : "").append("*\n");
                    messageText.append("💬 Username: `").append(user.getUsername() != null ? user.getUsername() : "Not Set").append("`\n");
                    messageText.append("🔹 Role: `").append(user.getRole() != null ? user.getRole() : "Unassigned").append("`\n");
                    messageText.append("➖➖➖➖➖➖➖➖\n");
                }

                sendMessage = messageService.generateSendMessage(chatId, messageText.toString());
                executeUtils.execute(sendMessage);


            }
        } else {
            executeUtils.execute(messageService.sendSomethingWrongMessage(chatId));
        }

    }


    @SneakyThrows
    public void addSuperAdmin(Long chatId, String text, Message msg) {

        if (userState.getOrDefault(chatId, 0) == 1) {
            if (userService.checkIsAdmin(chatId)) {

                ReplyKeyboardRemove keyboardRemove = new ReplyKeyboardRemove();
                keyboardRemove.setRemoveKeyboard(true);

                List<User> users = userService.findByRole("USER");

                System.out.println(users);

                InlineKeyboardMarkup inlineKeyboardMarkup = buttonService.createInlineButtonsForUsers(users, "change_role_");

                SendMessage sendMessage = messageService.sendUserListMessage(chatId);

                sendMessage.setReplyMarkup(inlineKeyboardMarkup);
                executeUtils.execute(sendMessage);

            } else {
                executeUtils.execute(messageService.sendHaveNoPermissionMessage(chatId));
            }
        } else {
            executeUtils.execute(messageService.sendSomethingWrongMessage(chatId));
            log.warn("error");
        }
    }


    @SneakyThrows
    public void invalidCommand(Long chatId, String text, Message msg) {
        executeUtils.execute(messageService.sendInvalidCommandMessage(chatId));
    }

    @SneakyThrows
    public void change_role(CallbackQuery callbackQuery) {

        String queryData = callbackQuery.getData();

        Long userId = Long.parseLong(queryData.split("_")[2]);

        User user = userService.getUserByChatId(String.valueOf(userId));

        if (user != null) {

            if (user.getRole().equals("USER")) {
                user.setRole(Role.SUPER_ADMIN.toString());
                userService.save(user);

                executeUtils.execute(messageService.sendUserChangedToAdminMessage(callbackQuery.getMessage().getChatId(), user));
                executeUtils.execute(messageService.sendUserChangedToAdminFromAdminMessage(user));

            } else {
                executeUtils.execute(messageService.sendUserAlreadyAdminMessage(callbackQuery.getMessage().getChatId()));
            }

        } else {
            executeUtils.execute(messageService.sendSomethingWrongMessage(callbackQuery.getMessage().getChatId()));
        }


    }

    @SneakyThrows
    public void managementOfSuperAdmins(Long chatId, String text, Message msg) {

        userState.put(chatId, 1);

        SendMessage sendMessage = messageService.sendForManageSuperAdminMessage(chatId);

        ReplyKeyboardMarkup buttons = buttonService.createButtons(List.of("\uD83D\uDC51 Add Super Admin", "\uD83D\uDDD1\uFE0F Delete Super Admin \uD83D\uDC51"), true);

        sendMessage.setReplyMarkup(buttons);

        executeUtils.execute(sendMessage);

    }

    @SneakyThrows
    public void deleteSuperAdminCommand(Long chatId, String text, Message msg) {

        if (userState.getOrDefault(chatId, 0) == 1) {
            if (userService.checkIsAdmin(chatId)) {

                SendMessage sendMessage = messageService.sendRemoveFromSuperAdminMessage(chatId);

                List<User> super_admins = userRepository.getAllUsersByRoleAndStatus(Role.SUPER_ADMIN.toString(), Status.ACTIVE.toString());

                InlineKeyboardMarkup inlineButtonsForUsers = buttonService.createInlineButtonsForUsers(super_admins, "delete_super_admin_");

                sendMessage.setReplyMarkup(inlineButtonsForUsers);
                executeUtils.execute(sendMessage);


            } else {
                executeUtils.execute(messageService.sendHaveNoPermissionMessage(chatId));
            }
        } else {
            executeUtils.execute(messageService.sendSomethingWrongMessage(chatId));
        }

    }

    @SneakyThrows
    public void deleteSuperAdmin(CallbackQuery callbackQuery) {

        String queryData = callbackQuery.getData();

        Long userId = Long.parseLong(queryData.split("_")[3]);

        User user = userRepository.getUserById(userId);

        if (user != null) {

            if (user.getRole().equals(Role.SUPER_ADMIN.toString())) {

                user.setRole(Role.USER.toString());
                userRepository.save(user);

                executeUtils.execute(messageService.sendSuperAdminDeletedMessage(callbackQuery.getMessage().getChatId(), user));
                executeUtils.execute(messageService.sendSuperAdminDeletedMessageToUser(user));


            } else {
                executeUtils.execute(messageService.sendUserDoesNotSuperAdmin(callbackQuery.getMessage().getChatId()));
            }

        } else {
            executeUtils.execute(messageService.sendUsersNotFoundMessage(callbackQuery.getMessage().getChatId()));
        }

    }

    @SneakyThrows
    public void backCommand(Long chatId, String text, Message msg) {


    }


    @SneakyThrows
    public void mainMenuCommand(Long chatId) {
        if (userState.getOrDefault(chatId, 0) > 0) {

            userState.put(chatId, 0);

            SendMessage sendMessage;

            if (userService.checkIsAdmin(chatId)) {
                sendMessage = messageService.sendAdminOptionsMessage(chatId);
                buttonService.buttonMenu(sendMessage);
                User user = userService.getUserByChatId(String.valueOf(chatId));
                user.setState(null);
                userRepository.save(user);
            } else {
                User user = userService.getUserByChatId(String.valueOf(chatId));
                user.setState(null);
                userRepository.save(user);
                sendMessage = messageService.sendUserOptionMessage(chatId);
                buttonService.buttonMenu(sendMessage);
            }
            executeUtils.execute(sendMessage);
        } else {
            executeUtils.execute(messageService.sendSomethingWrongMessage(chatId));
        }
    }

    @SneakyThrows
    public void blockUserCommand(Long chatId, String text, Message msg) {

        if (userState.getOrDefault(chatId, 0) == 0) {
            if (userService.checkIsAdmin(chatId)) {

                SendMessage sendMessage = messageService.sendBlockUserCommandMessage(chatId);

                List<User> users = userRepository.getAllUsersByRoleAndStatus(Role.USER.toString(), Status.ACTIVE.toString());

                InlineKeyboardMarkup inlineButtonsForUsers = buttonService.createInlineButtonsForUsers(users, "block_user_");

                sendMessage.setReplyMarkup(inlineButtonsForUsers);
                executeUtils.execute(sendMessage);


            } else {
                executeUtils.execute(messageService.sendHaveNoPermissionMessage(chatId));
            }
        } else {
            executeUtils.execute(messageService.sendSomethingWrongMessage(chatId));
        }


    }

    @SneakyThrows
    public void blockUser(CallbackQuery callbackQuery) {

        String queryData = callbackQuery.getData();

        Long userId = Long.parseLong(queryData.split("_")[2]);

        User user = userRepository.getUserById(userId);


        if (user != null) {

            if (user.getRole().equals(Role.USER.toString()) && !user.getStatus().equals(Status.BLOCKED.toString())) {

                user.setStatus(Status.BLOCKED.toString());
                userRepository.save(user);

                executeUtils.execute(messageService.sendBlockUserMessage(callbackQuery.getMessage().getChatId(), user));
                executeUtils.execute(messageService.sendBlockUserMessageToUser(user));

            } else {
                executeUtils.execute(messageService.sendAlreadyBlockedMessage(callbackQuery.getMessage().getChatId()));
            }

        } else {
            executeUtils.execute(messageService.sendUsersNotFoundMessage(callbackQuery.getMessage().getChatId()));
        }

    }


    @SneakyThrows
    public boolean checkUser(Long chatId) {

        if (!userService.checkIsActive(chatId)) {
            SendMessage warningMessage = new SendMessage(chatId.toString(), """
                    ⚠️ *Access Restricted* ⚠️
                    
                    Dear user, your access to this bot is currently *restricted*.
                    If you believe this is a mistake, please contact an admin.
                    """);
            executeUtils.execute(warningMessage);

            var adminChatId = 2087546645L;
            SendMessage adminNotification = new SendMessage(Long.toString(adminChatId), """
                    🚨 *User Attempted to Access the Bot* 🚨
                    
                    A restricted user (ID: %s) attempted to interact with the bot.
                    Consider reviewing their status.
                    """.formatted(chatId));
            executeUtils.execute(adminNotification);

            return false;
        }
        return true;
    }

    @SneakyThrows
    public void unblockUserCommand(Long chatId, String text, Message msg) {

        if (userState.getOrDefault(chatId, 0) == 0) {
            if (userService.checkIsAdmin(chatId)) {

                SendMessage sendMessage = messageService.sendUnblockUserSelectionMessage(chatId);

                List<User> users = userRepository.getAllUsersByRoleAndStatus(Role.USER.toString(), Status.BLOCKED.toString());

                InlineKeyboardMarkup inlineButtonsForUsers = buttonService.createInlineButtonsForUsers(users, "unblock_user_");

                sendMessage.setReplyMarkup(inlineButtonsForUsers);
                executeUtils.execute(sendMessage);


            } else {
                executeUtils.execute(messageService.sendHaveNoPermissionMessage(chatId));
            }
        } else {
            executeUtils.execute(messageService.sendSomethingWrongMessage(chatId));
        }


    }

    @SneakyThrows
    public void unblockUser(CallbackQuery callbackQuery) {
        String queryData = callbackQuery.getData();

        Long userId = Long.parseLong(queryData.split("_")[2]);

        User user = userRepository.getUserById(userId);


        if (user != null) {

            if (user.getRole().equals(Role.USER.toString()) && !user.getStatus().equals(Status.ACTIVE.toString())) {

                user.setStatus(Status.ACTIVE.toString());
                userRepository.save(user);

                executeUtils.execute(messageService.sendUnBlockUserMessage(callbackQuery.getMessage().getChatId(), user));
                executeUtils.execute(messageService.sendUnBlockUserMessageToUser(user));

            } else {
                executeUtils.execute(messageService.sendAlreadyBlockedMessage(callbackQuery.getMessage().getChatId()));
            }

        } else {
            executeUtils.execute(messageService.sendUsersNotFoundMessage(callbackQuery.getMessage().getChatId()));
        }

    }

    @SneakyThrows
    public void addAdvertisementCommand(Long chatId, String text, Message msg) {

        if (userService.checkIsActive(chatId)) {

            userState.put(chatId, 4);

            User user = userService.getUserByChatId(String.valueOf(chatId));
            user.setState(AddAdvertisementState.WAITING_FOR_DESCRIPTION.name());
            userRepository.save(user);

            SendMessage sendMessage = messageService.sendDescriptionForAdvertisementMessage(chatId);
            sendMessage.setReplyMarkup(buttonService.createButtons(List.of(), true));

            executeUtils.execute(sendMessage);

        } else {
            executeUtils.execute(messageService.sendHaveNoPermissionMessage(chatId));
        }


    }


    public boolean checkState(Long chatId) {

        User user = userService.getUserByChatId(String.valueOf(chatId));
        return user.getState() != null && !user.getState().isEmpty();

    }

    @SneakyThrows
    public void addDescriptionForAdvertisementCommand(Long chatId, String text, Message msg) {
        advertisementService.setDescription(text);
        User user = userService.getUserByChatId(String.valueOf(chatId));
        user.setState(AddAdvertisementState.WAITING_FOR_IMAGE.name());
        userRepository.save(user);
        executeUtils.execute(messageService.sendImageForAdvertisementMessage(chatId));
    }


    public void uploadPhoto(Update update) {
        photoService.savePhoto(update);
    }

    @SneakyThrows
    public void yesCommand(Long chatId, String text, Message msg) {
        User currentUser = userService.getUserByChatId(String.valueOf(chatId));

        if (!currentUser.getState().equals(AddAdvertisementState.APPROVAL_FOR_ADVERTISEMENT.name())) {
            executeUtils.execute(messageService.sendInvalidCommandMessage(chatId));
            return;
        }

        Advertisement ad = advertisementService.getAdvertisement();
        InputFile photoFile = new InputFile(new java.io.File(ad.getImageUrl()));
        ad.setFormId(String.valueOf(chatId));

        List<User> users = userService.getUsers();


        for (User user : users) {
            try {

                SendPhoto sendPhoto = new SendPhoto();
                sendPhoto.setChatId(user.getId());
                sendPhoto.setPhoto(photoFile);
                sendPhoto.setCaption(ad.getDescription());
                executeUtils.execute(sendPhoto);


                Thread.sleep(100);
            } catch (Exception e) {
                log.error("Failed to send photo to user {%s}: {%s}".formatted(user.getId(), e.getMessage()));
            }
        }

        advertisementService.save();
        User user = userService.getUserByChatId(String.valueOf(chatId));
        user.setState(null);
        userRepository.save(user);
        executeUtils.execute(messageService.generateSendMessage(chatId, """
                ✨ Your advertisement has been successfully delivered to all users!
                🎉 Thank you for choosing our service! We hope it reaches the right audience and brings great results! 🚀
                """));


        SendMessage sendMessage = messageService.sendAdminOptionsMessage(chatId);
        buttonService.buttonMenu(sendMessage);
        user = userService.getUserByChatId(String.valueOf(chatId));
        user.setState(null);
        userRepository.save(user);

        executeUtils.execute(sendMessage);

    }


    @SneakyThrows
    public void noCommand(Long chatId, String text, Message msg) {

        advertisementService.cancel();

        executeUtils.execute(messageService.generateSendMessage(chatId, """
                🚫 Your advertisement has been successfully canceled.
                😞 We are sorry to see it go, but you can always create a new one in the future.
                ✨ Thank you for using our service, and we hope to assist you again soon! 🙌
                """));

        userService.getUserByChatId(String.valueOf(chatId));
        User user;

        SendMessage sendMessage = messageService.sendAdminOptionsMessage(chatId);
        buttonService.buttonMenu(sendMessage);
        user = userService.getUserByChatId(String.valueOf(chatId));
        user.setState(null);
        userRepository.save(user);

        executeUtils.execute(sendMessage);

    }

    @SneakyThrows
    public void uploadMovieCommand(Long chatId, String text, Message msg) {
        executeUtils.execute(messageService.generateSendMessage(chatId , "not available "));
    }

    public String getUserStateByChatId(Long chatId) {
        User user = userRepository.getUserById(chatId);
        return user.getState();
    }


}
