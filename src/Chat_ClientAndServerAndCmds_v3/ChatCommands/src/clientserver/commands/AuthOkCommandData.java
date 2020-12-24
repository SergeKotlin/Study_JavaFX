package Chat_ClientAndServerAndCmds_v3.ChatCommands.src.clientserver.commands;

import java.io.Serializable;

public class AuthOkCommandData implements Serializable {

    private final String username;

    public AuthOkCommandData(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }
}
