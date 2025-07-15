package uz.project.moviehivebot.service;

import lombok.AllArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendVideo;
import org.telegram.telegrambots.meta.api.objects.Message;
import uz.project.moviehivebot.entity.User;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Setter
@AllArgsConstructor
@Service
public class MessageService {

    private ButtonService buttonService;

    public SendMessage generateSendMessage(Long chatId, String text) {
        return new SendMessage(chatId.toString(), text);
    }


    public SendMessage sendAdminWelcomeMessage(Long chatId) {
        String str = """
                Hello, Admin! 👑
                
                Welcome to the admin dashboard! 🎬✨
                
                You have full control over the movie bot. Enjoy managing movies, users, and more! 🎥💻
                
                Feel free to explore all the amazing features. 🌟
                """;
        return generateSendMessage(chatId, str);
    }

    public SendMessage sendUserWelcomeMessage(Long chatId, Message msg) {

        String firstName = msg.getFrom().getFirstName();
        String lastName = msg.getFrom().getLastName();
        String username = msg.getFrom().getUserName();

        String userNameDisplay = "";

        if (firstName != null && !firstName.isEmpty()) {
            userNameDisplay += firstName;
        }

        if (lastName != null && !lastName.isEmpty()) {
            if (!userNameDisplay.isEmpty()) {
                userNameDisplay += " ";
            }
            userNameDisplay += lastName;
        }

        if (userNameDisplay.trim().isEmpty() && username != null) {
            userNameDisplay = username;
        }

        String str = """
        Salom, %s! 🎬✨

        Botimizga xush kelibsiz! 😊 Sizni bu yerda ko‘rib turganimizdan xursandmiz.

        Biz bilan qoling va ajoyib filmlar olamidan bahramand bo‘ling! 🍿🎥
        """.formatted(userNameDisplay);


        return generateSendMessage(chatId, str);
    }


    public SendMessage sendUsersNotFoundMessage(Long chatId) {
        String str = """
                ⚠️ *No Users Found!*
                
                There are no available users at the moment. 🚫
                
                Please try again later.
                """;
        return generateSendMessage(chatId, str);
    }

    public SendMessage sendAdminOptionsMessage(Long chatId) {
        String adminMenuMessage = """
                🏠 *Main Menu* 📌
                
                Select an option below to proceed: ⬇️
                
                🔹 *Users* – View the list of registered users. 👥
                🔹 *Upload Movie* – Add a new movie to the database. 🎬
                🔹 *Add Admin* – Promote a user to Super Admin role. 🛡️
                🔹  *Block User* – Prevent this user from accessing the bot. 🚫
                
                Please tap a button below to continue. ⏬
                """;

        return generateSendMessage(chatId, adminMenuMessage);

    }

    public SendMessage sendUserOptionMessage(Long chatId) {
        String str = """
        🏠 *Asosiy menyu* 📌
        
        Quyidagi bo‘limlardan birini tanlang: ⬇️
        
        🔹 *Kinolar* – Kod orqali kinoni toping. 🎥
        
        Davom etish uchun pastdagi tugmani bosing. ⏬
        """;
        return generateSendMessage(chatId, str);


    }

    public SendMessage sendUserMovieCodeMessage(Long chatId) {
        String enterMovieCodeMessage = """
        \uD83C\uDFAC *Kino tanlash*

        Davom etish uchun quyiga kino kodini kiriting. 🎟️

        🔎 Kodni bizning Instagram sahifamizda topishingiz mumkin:
        👉 [@toshmurodov__23](https://instagram.com/toshmurodov__23)

                \uD83D\uDC47
        """;

        return generateSendMessage(chatId, enterMovieCodeMessage);
    }

    public SendMessage sendInvalidCommandMessage(Long chatId) {
        String invalidCommandMessage = """
                🚫 *Invalid Command* ❗
                
                Sorry, the command you entered is not recognized. Please make sure you're using one of the available options.
                
                For the list of valid commands, click one of the buttons below. 👇
                """;
        return generateSendMessage(chatId, invalidCommandMessage);
    }

    public SendMessage sendHaveNoPermissionMessage(Long chatId) {
        String str = """
                🚫 *Access Denied!*
                
                You do *not* have permission to perform this action. 🔒
                
                Please contact an *administrator* or try a different action.
                """;

        return generateSendMessage(chatId, str);

    }

    public SendMessage sendUserAlreadyAdminMessage(Long chatId) {
        String message = """
                ⚠️ *User is already an Admin!*
                
                The user you are trying to promote is already an administrator. 🙌
                
                No further action is required as they already have admin privileges. 🛑
                """;

        return generateSendMessage(chatId, message);
    }

    public SendMessage sendSomethingWrongMessage(Long chatId) {

        String message = """
                🛑 Something went wrong !*
                
                Please try again soon. 🙌
                """;
        return generateSendMessage(chatId, message);

    }

    public SendMessage sendUserChangedToAdminMessage(Long chatId, User user) {
        String message = """
                ✅ *User Successfully Promoted to Admin!*
                
                The user *%s* has been successfully promoted to an Administrator. 🎉
                
                They now have full access to the admin functions. 🛠️
                """.formatted(user.getFirstName() != null ? user.getFirstName() : "Unknown",
                user.getLastName() != null ? user.getLastName() : "");

        return generateSendMessage(chatId, message);
    }

    public SendMessage sendUserChangedToAdminFromAdminMessage(User user) {
        String messageToUser = """
                🎉 *Congratulations!*
                
                You have been promoted to an *Administrator*. 🛠️
                
                You now have the necessary permissions to manage the bot and perform administrative tasks. 
                Enjoy the privileges! 🎉
                """;
        return generateSendMessage(user.getId(), messageToUser);
    }

    public SendMessage sendUserListMessage(Long chatId) {

        String message = """
                📋 *User List* 👥
                
                Please select a user from the list below and promote them to *Super Admin* 🔝  
                
                Tap on a user to proceed. ⬇️
                """;

        return generateSendMessage(chatId, message);
    }

    public SendMessage sendForManageSuperAdminMessage(Long chatId) {
        String manageSuperAdminMessage = """
                👑 *Manage Super Admins* 📌
                
                Here, you can promote a user to *Super Admin* status.
                Select a user from the list below and grant them higher privileges. 🚀
                
                Please choose carefully, as *Super Admins* have full control over the system. ⚠️  
                """;


        return generateSendMessage(chatId, manageSuperAdminMessage);
    }

    public BotApiMethod<Serializable> sendUserRemovedFromSuperAdmin(Long chatId) {
        return null;
    }

    public SendMessage sendRemoveFromSuperAdminMessage(Long chatId) {
        String message = """
                ⚠️ *Remove Super Admin* 🗑️
                
                Please select a *Super Admin* from the list below to revoke their privileges. ❌  
                
                Tap on a user to proceed. ⬇️
                """;

        return generateSendMessage(chatId, message);
    }

    public SendMessage sendUserDoesNotSuperAdmin(Long chatId) {

        String message = """
                ❌ *Action Denied!* ⚠️
                
                The selected user is *not a Super Admin* and cannot be removed from this role. 🚫  
                
                Please select a valid *Super Admin* to proceed. ⬇️
                """;

        return generateSendMessage(chatId, message);

    }

    public SendMessage sendSuperAdminDeletedMessage(Long chatId, User user) {
        String message = """
                🗑️ *Super Admin Removed* ❌
                
                %s has been successfully removed from the *Super Admin* role.  
                
                They no longer have administrative privileges. ⚠️
                """.formatted(user.getFirstName() != null ? user.getFirstName() : user.getUsername());

        return generateSendMessage(chatId, message);
    }

    public SendMessage sendSuperAdminDeletedMessageToUser(User user) {
        String message = """
                ⚠️ *Your Super Admin Access Revoked* ❌
                
                Dear %s, your *Super Admin* privileges have been removed.
                
                You no longer have administrative rights in the system.
                """.formatted(user.getFirstName() != null ? user.getFirstName() : user.getUsername());

        return generateSendMessage(user.getId(), message);
    }

    public SendMessage sendBlockUserCommandMessage(Long chatId) {
        String message = """
                ⚠️ *Block User* 🚫
                
                Please select a *user* from the list below to block. ❌
                
                Tap on a user to proceed. ⬇️
                """;

        return generateSendMessage(chatId, message);
    }


    public SendMessage sendBlockUserMessageToUser(User user) {
        String fullName = user.getFirstName();
        if (user.getLastName() != null) {
            fullName += " " + user.getLastName();
        }
        String username = user.getUsername() != null ? " (@" + user.getUsername() + ")" : "";

        String message = """
                🚫 *Your Account Has Been Blocked* 🔴
                
                Dear %s%s, your access to the bot has been temporarily suspended.
                Please contact support for more information.
                """.formatted(fullName != null ? fullName : "User", username);

        return generateSendMessage(user.getId(), message);
    }


    public SendMessage sendBlockUserMessage(Long chatId, User user) {

        String message = """
                ⚠️ *User Blocked Notification* 🚫
                
                Admin, the following user has been blocked:
                
                📛 *User ID*: %s
                🧑‍💼 *Username*: %s
                🗓️ *Blocked On*: %s
                
                """.formatted(user.getId(), user.getUsername(), LocalDateTime.now());

        return generateSendMessage(chatId, message);
    }

    public SendMessage sendAlreadyBlockedMessage(Long chatId) {
        String messageToAdmin = """
                🚫 *User Blocked Attempt* 🔴
                
                The user with chat ID: %d has already been blocked.
                
                """.formatted(chatId);
        return generateSendMessage(chatId, messageToAdmin);
    }

    public SendMessage sendUnblockUserSelectionMessage(Long chatId) {
        String message = """
                🔓 *Unblock User* ✅
                
                Please select the user you want to unblock from the list below. ⬇️  
                
                After selecting a user, grant them the necessary permissions they should have.
                """;

        return generateSendMessage(chatId, message);
    }


    public SendMessage sendUnBlockUserMessage(Long chatId, User user) {
        String fullName = user.getFirstName();
        if (user.getLastName() != null) {
            fullName += " " + user.getLastName();
        }
        String username = user.getUsername() != null ? " (@" + user.getUsername() + ")" : "";

        String message = """
                ✅ *User Unblocked Successfully* 🔓
                
                The user %s%s has been successfully unblocked and can now access the bot again.
                """.formatted(fullName != null ? fullName : "User", username);

        return generateSendMessage(chatId, message);
    }

    public SendMessage sendUnBlockUserMessageToUser(User user) {
        String fullName = user.getFirstName();
        if (user.getLastName() != null) {
            fullName += " " + user.getLastName();
        }
        String username = user.getUsername() != null ? " (@" + user.getUsername() + ")" : "";

        String message = """
                🔓 *You Have Been Unblocked* ✅
                
                Dear %s%s, your access to the bot has been restored.
                You can now use all available features again.
                
                Welcome back! 🎉
                """.formatted(fullName != null ? fullName : "User", username);

        return generateSendMessage(user.getId(), message);
    }

    public SendMessage sendAdvertisementCommandMessage(Long chatId) {
        String message = """
                🎉✨ *Advertisement Creation Guide* ✨🎉
                
                🚀 In this case, you need to provide both a description and an image for your advertisement.
                """;
        return generateSendMessage(chatId, message);
    }

    public SendMessage sendDescriptionForAdvertisementMessage(Long chatId) {
        String message = """
                        📝✨ *Enter Description for Your Advertisement* ✨📝
                
                        ✍️ Make sure the description grabs attention and provides all necessary information!
                
                """;
        return generateSendMessage(chatId, message);
    }

    public SendMessage sendImageForAdvertisementMessage(Long chatId) {
        String message = """
                        📝✨ *Upload Image for Your Advertisement* ✨📝
                
                """;
        return generateSendMessage(chatId, message);
    }

    public SendMessage sendApprovalMessage(Long chatId) {
        String message = """
                ✅ Your information has been successfully received!
                
                ❓ Is everything correct?
                
                🔹 If everything is correct, please confirm by clicking "✅ Yes".  
                🔹 If you need to make changes, click "❌ No".
                """;

        SendMessage sendMessage = generateSendMessage(chatId, message);
        sendMessage.setReplyMarkup(buttonService.createButtons(List.of("✅ Yes", "❌ No"), false));


        return sendMessage;
    }

    public SendMessage sendMovieSavedMessage(Long chatId) {

        String message = """
                ✅🎬 *Movie saved successfully!*
                
                You can now find it in the channel or via search. 🔍
                
                Keep going, you're doing great! 💪
                """;

        return generateSendMessage(chatId, message);


    }

}
