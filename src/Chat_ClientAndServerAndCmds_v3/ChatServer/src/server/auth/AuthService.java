package Chat_ClientAndServerAndCmds_v3.ChatServer.src.server.auth;

public interface AuthService {

    void start();

    String getUsernameByLoginAndPassword(String login, String password);

    void close();
}
