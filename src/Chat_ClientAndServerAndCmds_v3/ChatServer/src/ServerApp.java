package Chat_ClientAndServerAndCmds_v3.ChatServer.src;

import Chat_ClientAndServerAndCmds_v3.ChatServer.src.chat.MyServer;

import java.io.IOException;

public class ServerApp {

//    private static final int DEFAULT_PORT = 8189;
    private static final int DEFAULT_PORT = 8190;

    public static void main(String[] args) {
        int port = DEFAULT_PORT;

        if (args.length != 0) {
            port = Integer.parseInt(args[0]);
        }

        try {
            new MyServer(port).start();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Ошибка!");
            System.exit(1);
        }
    }
}
