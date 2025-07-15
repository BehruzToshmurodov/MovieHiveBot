package uz.project.moviehivebot.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import uz.project.moviehivebot.entity.User;
import uz.project.moviehivebot.entity.enums.Role;
import uz.project.moviehivebot.entity.enums.Type;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Log4j
public class ButtonService {


    private final UserService userService;


    public SendMessage buttonMenu(SendMessage message) {

        User user = userService.getUserByChatId(message.getChatId());
        if (user.getRole().equals(Role.ADMIN.toString()) || user.getRole().equals(Role.SUPER_ADMIN.toString())) {
            message.setReplyMarkup((createButtons(List.of("\uD83D\uDC65 Users", "\uD83C\uDFAC Upload Movie", "\uD83D\uDC51 *Manage Super Admins*" , "\uD83D\uDEAB Block User" , "\uD83D\uDD13 Unblock User" , "\uD83D\uDCE2 Add Advertisement" ), false)));
        } else
            message.setReplyMarkup(createButtons(List.of("\uD83C\uDFA5 Kinolar"), false));

        return message;
    }

    public ReplyKeyboardMarkup createButtons(List<String> list, boolean addButton) {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> rows = new ArrayList<>();

        KeyboardRow row = new KeyboardRow();

        for (int i = 0; i < list.size(); i++) {

            row.add(list.get(i));
            if (i % 2 != 0) {
                rows.add(row);
                row = new KeyboardRow();
            }
        }
        if (list.size() % 2 != 0)
            rows.add(row);

        if (addButton) {
            rows.add(addBackMainMenuButton());
        }

        replyKeyboardMarkup.setKeyboard(rows);
        replyKeyboardMarkup.setResizeKeyboard(true);
        return replyKeyboardMarkup;
    }


    public InlineKeyboardMarkup createInlineButtonsForUsers(List<User> users, String callData) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        List<InlineKeyboardButton> row = new ArrayList<>();

        for (User user : users) {
            InlineKeyboardButton button = getInlineKeyboardButton(user, callData);
            row.add(button);

            if (row.size() == 2 || user.equals(users.get(users.size() - 1))) {
                rows.add(new ArrayList<>(row));
                row.clear();
            }
        }

        inlineKeyboardMarkup.setKeyboard(rows);
        return inlineKeyboardMarkup;
    }


    private static InlineKeyboardButton getInlineKeyboardButton(User user , String callData) {
        InlineKeyboardButton button = new InlineKeyboardButton();

        String buttonText = "";

        if (user.getFirstName() != null && !user.getFirstName().isEmpty()) {
            buttonText += user.getFirstName();
        }

        if (user.getLastName() != null && !user.getLastName().isEmpty()) {
            if (!buttonText.isEmpty()) {
                buttonText += " ";
            }
            buttonText += user.getLastName();
        }

        if (buttonText.isEmpty() && user.getUsername() != null) {
            buttonText = user.getUsername();
        }

        button.setText(buttonText);
        button.setCallbackData( callData + user.getId());

        return button;
    }


    private KeyboardRow addBackMainMenuButton() {
        KeyboardRow row = new KeyboardRow();
        row.add("\uD83D\uDD19 Back");
        row.add("\uD83C\uDFE0 Main menu");
        return row;
    }

}