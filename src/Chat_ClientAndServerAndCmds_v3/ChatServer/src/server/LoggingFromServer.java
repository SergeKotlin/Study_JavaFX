package Chat_ClientAndServerAndCmds_v3.ChatServer.src.server;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

public class LoggingFromServer {

    // Инициализируем логгер
    protected static final Logger file = Logger.getLogger("file");

    public LoggingFromServer() {

        PropertyConfigurator.configure("src/Chat_ClientAndServerAndCmds_v3/resources/logs/configs/log4j.properties");

//        Logger file = Logger.getLogger("file");

        // Логирование событий, for example:
//        file.info("This is info");
//        file.warn("This is warn");
//        file.error("This is error");
//        file.fatal("This is fatal");

    }

    public Logger getFile() {
        return file;
    }

}

/** Lesson 6.
 * 1. Добавить на серверную сторону чата логирование, с выводом информации о действиях на сервере
 * (запущен, произошла ошибка, клиент подключился, клиент прислал сообщение/команду).
 *
 * Список логирования по проекту:
 * ✓ Ошибка! (ServerApp)
 *
 * ✓ Сервер запущен! (MyServer)
 * ✓ Ошибка создания нового подключения
 * ✓ Клиент подключился!
 * ✓ <- Конец Истории сообщений.
 * ✓ broadcastMessage(null, Command.updateUsersListCommand(usernames)) для подписки/отписки, 2 раза
 *
 * ✓ Ошибка авторизации (ClientHandler)
 * ✓ Логин уже используется
 * ✓ >>> %s присоединился к чату
 * ✓(совмещено с myServer) myServer.subscribe(this)
 * ✓ myServer.broadcastMessage(this, Command.messageInfoCommand(message, sender))
 * ✓ myServer.sendPrivateMessage(recipient, Command.messageInfoCommand(message,
 * username))
 * ✓ Логин или пароль не соответствуют действительности
 * ✓ Получен неизвестный объект
 * ✓ Неизвестный тип команды
 *
 * Сервис аутентификации запущен (BaseAuthService)
 * Сервис аутентификации завершен
 *
 * ✓ + логи для Network
 * **/
// Serega, sure