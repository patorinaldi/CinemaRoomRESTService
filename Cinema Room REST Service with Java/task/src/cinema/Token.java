package cinema;

import java.util.UUID;

public class Token {

    private UUID token;

    public Token() {
        this.token = UUID.randomUUID();
    }

    public UUID getToken() {
        return token;
    }

    private void setToken(UUID token) {
        this.token = token;
    }

    @Override
    public String toString() {
        return token.toString();
    }
}
