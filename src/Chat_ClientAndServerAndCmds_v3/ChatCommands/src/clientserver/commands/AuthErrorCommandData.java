package Chat_ClientAndServerAndCmds_v3.ChatCommands.src.clientserver.commands;

import java.io.Serializable;

public class AuthErrorCommandData implements Serializable {

    private final String errorMessage;

    public AuthErrorCommandData(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
