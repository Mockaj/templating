package cz.muni.fi.pb162.hw03.impl.parser.tokens;

import java.util.List;

/**
 * Command name constants
 */
public final class Command {
    public enum Kind {
        CMDTEXT,
        CMDPRINT,
        CMDIF,
        CMDELSE,
        CMDDONE,
        CMDFOR;
    }
    private final Kind kind;
    private final List<Token> tokens;

    /**
     *
     * @param kind
     * @param tokens
     */
    public Command(Kind kind, List<Token> tokens) {
       this.kind = kind;
       this.tokens = tokens;
    }

    public List<Token> getTokens() {
        return tokens;
    }

    public Kind getKind() {
        return kind;
    }
}
