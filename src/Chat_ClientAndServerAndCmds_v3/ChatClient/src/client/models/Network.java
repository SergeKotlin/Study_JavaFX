package Chat_ClientAndServerAndCmds_v3.ChatClient.src.client.models;

import Chat_ClientAndServerAndCmds_v3.ChatClient.src.NetworkClient;
import Chat_ClientAndServerAndCmds_v3.ChatClient.src.client.controllers.ChatController;
import Chat_ClientAndServerAndCmds_v3.ChatCommands.src.clientserver.Command;
import Chat_ClientAndServerAndCmds_v3.ChatCommands.src.clientserver.commands.*;
import Chat_ClientAndServerAndCmds_v3.ChatServer.src.ServerApp;
import javafx.application.Platform;

import java.io.*;
import java.net.Socket;

public class Network {

    private static final String SERVER_ADRESS = "localhost";
//    private static final int SERVER_PORT = 8189;
    private static final int SERVER_PORT = 8190;
    //+
    private static final File fileHistory = new File("src/Chat_ClientAndServerAndCmds_v3/resources/lib/fileHistory.txt");
    //
    private final String host;
    private final int port;

    private ObjectOutputStream dataOutputStream;
    private ObjectInputStream dataInputStream;

    private Socket socket;

    private String username;

    public ObjectOutputStream getDataOutputStream() {
        return dataOutputStream;
    }

    public ObjectInputStream getDataInputStream() {
        return dataInputStream;
    }

    public Network() {
        this(SERVER_ADRESS, SERVER_PORT);
    }

    public Network(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public boolean connect() {
        try {
            socket = new Socket(host, port);
            dataOutputStream = new ObjectOutputStream(socket.getOutputStream());
            dataInputStream = new ObjectInputStream(socket.getInputStream());
            return true;

        } catch (IOException e) {
            System.out.println("Соединение не было установлено!");
            ServerApp.getLogFromServer().getFile().warn("Соединение не было установлено Network");
            e.printStackTrace();
            return false;
        }

    }

    public void close() {
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void waitMessage(ChatController chatController) {

       Thread thread = new Thread( () -> {
           try { while (true) {

               Command command = readCommand();
               if(command == null) {
                   NetworkClient.showErrorMessage("Error","Ошибка серверва", "Получена неверная команда");
                   ServerApp.getLogFromServer().getFile().error("Error Ошибка серверва Получена неверная команда Network");
                   continue;
               }

               switch (command.getType()) {
                   case INFO_MESSAGE: {
                       MessageInfoCommandData data = (MessageInfoCommandData) command.getData();
                       String message = data.getMessage();
                       String sender = data.getSender();
                       String formattedMessage = sender != null ? String.format("%s: %s", sender, message) : message;
                       Platform.runLater(() -> {
                           chatController.appendMessage(formattedMessage);
                       });
                       break;
                   }
                   case ERROR: {
                       ErrorCommandData data = (ErrorCommandData) command.getData();
                       String errorMessage = data.getErrorMessage();
                       Platform.runLater(() -> {
                           NetworkClient.showErrorMessage("Error", "Server error", errorMessage);
                           ServerApp.getLogFromServer().getFile().error("Error Server error Network| Error text: " + errorMessage);
                       });
                       break;
                   }
                   case UPDATE_USERS_LIST: {
                       UpdateUsersListCommandData data = (UpdateUsersListCommandData) command.getData();
                       Platform.runLater(() -> chatController.updateUsers(data.getUsers()));
                       break;
                   }
                   default:
                       Platform.runLater(() -> {
                           NetworkClient.showErrorMessage("Error","Unknown command from server!", command.getType().toString());
                           ServerApp.getLogFromServer().getFile().error("Error Unknown command from server! Network| Unknown-command text: " + command.getType().toString());
                       });
               }

           }
           } catch (IOException e) {
               e.printStackTrace();
               System.out.println("Соединение потеряно!");
               ServerApp.getLogFromServer().getFile().warn("Соединение потеряно Network");
           }
       });
        thread.setDaemon(true);
        thread.start();
    }


    public String sendAuthCommand(String login, String password) {
        try {
            Command authCommand = Command.authCommand(login, password);
            dataOutputStream.writeObject(authCommand);

            Command command = readCommand();
            if (command == null) {
                ServerApp.getLogFromServer().getFile().error("Ошибка чтения команды с сервера Network");
                return "Ошибка чтения команды с сервера";
            }

            switch (command.getType()) {
                case AUTH_OK: {
                    AuthOkCommandData data = (AuthOkCommandData) command.getData();
                    this.username = data.getUsername();
                    return null;
                }

                case AUTH_ERROR:
                case ERROR: {
                    AuthErrorCommandData data = (AuthErrorCommandData) command.getData();
                    return data.getErrorMessage();
                }
                default:
                    ServerApp.getLogFromServer().getFile().warn("Error Network| Unknown type of command: : " + command.getType());
                    return "Unknown type of command: " + command.getType();

            }
        } catch (IOException e) {
            e.printStackTrace();
            return e.getMessage();
        }
    }

    public String getUsername() {
        return username;
    }

    public void sendMessage(String message) throws IOException {
        sendMessage(Command.publicMessageCommand(username, message));
        long id_date = System.currentTimeMillis();
        fileWriter(username, message, id_date);
    }

    public void sendMessage(Command command) throws IOException {
        dataOutputStream.writeObject(command);
    }

    private void fileWriter(String username, String message, long id_date) throws IOException {
        if (!fileHistory.exists()) {
            fileHistory.createNewFile();
        }

        try (FileWriter writer = new FileWriter(fileHistory, true)) {
//        (<-- Вынес в параметры)
//        try {
//            FileWriter writer = new FileWriter(file, true);
            writer.write("\n" + username + "[" + id_date + "]" + ": \n" + message);
//            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void sendPrivateMessage(String message, String recipient) throws IOException {
        Command command = Command.privateMessageCommand(recipient, message);
        sendMessage(command);

    }

    private Command readCommand() throws IOException {
        try {
            return (Command) dataInputStream.readObject();
        } catch (ClassNotFoundException e) {
            String errorMessage = "Получен неизвестный объект";
            System.err.println(errorMessage);
            ServerApp.getLogFromServer().getFile().warn("Получен неизвестный объект Network");
            e.printStackTrace();
            sendMessage(Command.errorCommand(errorMessage));
            return null;
        }
    }

    public static File getFileHistory() {
        return fileHistory;
    }
}
