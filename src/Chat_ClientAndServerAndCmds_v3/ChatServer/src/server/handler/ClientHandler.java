package Chat_ClientAndServerAndCmds_v3.ChatServer.src.server.handler;

import Chat_ClientAndServerAndCmds_v3.ChatServer.src.ServerApp;
import Chat_ClientAndServerAndCmds_v3.ChatServer.src.server.MyServer;
import Chat_ClientAndServerAndCmds_v3.ChatServer.src.server.auth.AuthService;
import Chat_ClientAndServerAndCmds_v3.ChatCommands.src.clientserver.Command;
import Chat_ClientAndServerAndCmds_v3.ChatCommands.src.clientserver.CommandType;
import Chat_ClientAndServerAndCmds_v3.ChatCommands.src.clientserver.commands.AuthCommandData;
import Chat_ClientAndServerAndCmds_v3.ChatCommands.src.clientserver.commands.PrivateMessageCommandData;
import Chat_ClientAndServerAndCmds_v3.ChatCommands.src.clientserver.commands.PublicMessageCommandData;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClientHandler {

    private final MyServer myServer;
    private final Socket clientSocket;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private String username;

    public ClientHandler(MyServer myServer, Socket clientSocket) {
        this.myServer = myServer;
        this.clientSocket = clientSocket;
    }

    public void handle() throws IOException {
        in = new ObjectInputStream(clientSocket.getInputStream());
        out = new ObjectOutputStream(clientSocket.getOutputStream());

//        new Timer().schedule();
        new Thread(() -> {
            try {
                authentication();
                readMessage();
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }

        }).start();

    }

    private void authentication() throws IOException {

        while (true) {

            Command command = readCommand();
            if (command == null) {
                continue;
            }
            if (command.getType() == CommandType.AUTH) {

                boolean isSuccessAuth = processAuthCommand(command);
                if (isSuccessAuth) {
                    break;
                }

            } else {
                sendMessage(Command.authErrorCommand("Ошибка авторизации"));
                ServerApp.getLogFromServer().getFile().warn("Ошибка авторизации ClientHandler");

            }
        }

    }

    private boolean processAuthCommand(Command command) throws IOException {
        AuthCommandData cmdData = (AuthCommandData) command.getData();
        String login = cmdData.getLogin();
        String password = cmdData.getPassword();

        AuthService authService = myServer.getAuthService();
        this.username = authService.getUsernameByLoginAndPassword(login, password);
        if (username != null) {
            if (myServer.isUsernameBusy(username)) {
                sendMessage(Command.authErrorCommand("Логин уже используется"));
                ServerApp.getLogFromServer().getFile().warn("Логин уже используется ClientHandler");
                return false;
            }

            sendMessage(Command.authOkCommand(username));
            String message = String.format(">>> %s присоединился к чату", username);
            myServer.broadcastMessage(this, Command.messageInfoCommand(message, null));
            myServer.subscribe(this);
            ServerApp.getLogFromServer().getFile().info("Пользователь с именем " + username + " присоединился к чату ClientHandler");
            return true;
        } else {
            sendMessage(Command.authErrorCommand("Логин или пароль не соответствуют действительности"));
            ServerApp.getLogFromServer().getFile().info("Введённые логин или пароль не соответствуют действительности ClientHandler");
            return false;
        }
    }

    private Command readCommand() throws IOException {
        try {
            return (Command) in.readObject();
        } catch (ClassNotFoundException e) {
            String errorMessage = "Получен неизвестный объект";
            ServerApp.getLogFromServer().getFile().error("Получен неизвестный объект вместо команды ClientHandler");
            System.err.println(errorMessage);
            e.printStackTrace();
            return null;
        }
    }

    private void readMessage() throws IOException {
        while (true) {
            Command command = readCommand();
            if (command == null) {
                continue;
            }

            switch (command.getType()) {
                case END:
                    myServer.unSubscribe(this);
                    return;
                case PUBLIC_MESSAGE: {
                    PublicMessageCommandData data = (PublicMessageCommandData) command.getData();
                    String message = data.getMessage();
                    String sender = data.getSender();
                    myServer.broadcastMessage(this, Command.messageInfoCommand(message, sender));
                    ServerApp.getLogFromServer().getFile().info("Публичное сообщение от " + sender + " ClientHandler");
                    break;
                }
                case PRIVATE_MESSAGE:
                    PrivateMessageCommandData data = (PrivateMessageCommandData) command.getData();
                    String recipient = data.getReceiver();
                    String message = data.getMessage();
                    myServer.sendPrivateMessage(recipient, Command.messageInfoCommand(message, username));
                    ServerApp.getLogFromServer().getFile().info("Приватное сообщение от " + username + " ClientHandler");
                    break;
                default:
                    String errorMessage = "Неизвестный тип команды" + command.getType();
                    ServerApp.getLogFromServer().getFile().warn("Получен неизвестный тип команды (тип сообщения) ClientHandler");
                    System.err.println(errorMessage);
                    sendMessage(Command.errorCommand(errorMessage));
            }
        }
    }

    public String getUsername() {
        return username;
    }

    public void sendMessage(Command command) throws IOException {
        out.writeObject(command);
    }
}
