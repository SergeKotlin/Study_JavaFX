package Chat_ClientAndServerAndCmds_v3.ChatServer.src.chat;

import Chat_ClientAndServerAndCmds_v3.ChatClient.src.client.models.Network;
import Chat_ClientAndServerAndCmds_v3.ChatServer.src.chat.auth.AuthService;
import Chat_ClientAndServerAndCmds_v3.ChatServer.src.chat.auth.BaseAuthService;
import Chat_ClientAndServerAndCmds_v3.ChatServer.src.chat.handler.ClientHandler;
import Chat_ClientAndServerAndCmds_v3.ChatCommands.src.clientserver.Command;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class MyServer {

    private final ServerSocket serverSocket;
    private final AuthService authService;
    private final List<ClientHandler> clients = new ArrayList<>();

    public MyServer(int port) throws IOException {
        this.serverSocket = new ServerSocket(port);
        this.authService = new BaseAuthService();
    }


    public void start() throws IOException {
        System.out.println("Сервер запущен!");

        try {
            while (true) {
                waitAndProcessNewClientConnection();
            }
        } catch (IOException e) {
            System.out.println("Ошибка создания нового подключения");
            e.printStackTrace();
        } finally {
            serverSocket.close();
        }
    }

    private void waitAndProcessNewClientConnection() throws IOException {
        System.out.println("Ожидание пользователя...");
        Socket clientSocket = serverSocket.accept();
        clientSocket.setSoTimeout(8000);
        System.out.println("Клиент подключился!");
        processClientConnection(clientSocket);
    }

    private void processClientConnection(Socket clientSocket) throws IOException {
        ClientHandler clientHandler = new ClientHandler(this, clientSocket);
        clientHandler.handle();
    }

    public AuthService getAuthService() {
        return authService;
    }

    public synchronized boolean isUsernameBusy(String clientUsername) {
        for (ClientHandler client : clients) {
            if (client.getUsername().equals(clientUsername)) {
               return true;
            }
        }
        return false;
    }

    public synchronized void subscribe(ClientHandler clientHandler) throws IOException {
        clients.add(clientHandler);
        List<String> usernames = getAllUsernames();
        broadcastMessage(null, Command.updateUsersListCommand(usernames));
    }

    private List<String> getAllUsernames() {
        List<String> usernames = new ArrayList<>();
        for (ClientHandler client : clients) {
            usernames.add(client.getUsername());
        }
        return usernames;
    }

    public synchronized void unSubscribe(ClientHandler clientHandler) throws IOException {
        clients.remove(clientHandler);
        List<String> usernames = getAllUsernames();
        broadcastMessage(null, Command.updateUsersListCommand(usernames));
    }

    public synchronized void broadcastMessage(ClientHandler sender, Command command) throws IOException {
        for (ClientHandler client : clients) {
            if (client == sender) {
                //+
                // client.sendMessage( loadHistory(client.getUsername()) );
                //
                // Т.е command представляет из себя Command.messageInfoCommand(message, null)
                loadHistory(client);
                //
                continue;
            }
          client.sendMessage(command);

        }
    }

    private void loadHistory(ClientHandler client) {
        File file = Network.getFileHistory();
        String printHistory = "";
        printHistory += ">>>Подгружается История сообщений..\n Не пишите, дождитесь завершения!\n\n";
        int countMax = 100;
        printHistory += "История сообщений (последние " + countMax + "):\n";
        int strCount = countMax*2;
        try (var in = new InputStreamReader(new FileInputStream(file), "UTF-8")){
            int n;
//            System.out.println("\n"+user+"\n");
            while ( (n = in.read()) != -1 & strCount != 0) {
                String messageItem = String.valueOf( (char)n);
                if (messageItem.equals("\n")) {
                    strCount -= 1;
//                    System.out.println(strCount);
                }
                System.out.print( (char)n );
//                client.sendMessage(Command.messageInfoCommand(String.valueOf( (char)n ), null));
                printHistory += messageItem;
//                System.out.println((char) n);
            }
            in.close();
            // Хм, недодебажил. Кажется, можно выставить таймер для .read()
            // Но ведь он в цикле while, стало быть будет обновляться каждую итерацию..
            // Проблема в том - что после прогрузки до конца письма проходит несколько секунд, прежде, чем можно
            // начать писать. Иначе история будет вновь и вновь подгружаться. Хотя уже и 100 пробелов считал.
            // Пока вариант такой.
            // Да, и ориентир "два пробела" = одно письмо, поменял бы, хотя бы, на "["
            // В следующих апдейтах))) Тупо нет времени =(
            printHistory += "\n <- Конец Истории сообщений.\n";
            client.sendMessage(Command.messageInfoCommand( printHistory, null));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized void sendPrivateMessage(String recipient, Command command) throws IOException {
        for (ClientHandler client : clients) {
            if (client.getUsername().equals(recipient)) {
                client.sendMessage(command);
                break;
            }
        }
    }
}
