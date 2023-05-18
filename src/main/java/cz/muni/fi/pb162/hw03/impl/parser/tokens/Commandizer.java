package cz.muni.fi.pb162.hw03.impl.parser.tokens;

import cz.muni.fi.pb162.hw03.template.TemplateException;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Ladislav Husty
 */
public class Commandizer {
    private final List<Token> tokens;

    /**
     *
     * @param tokens
     */
    public Commandizer(List<Token> tokens){
        this.tokens = tokens;
    }
    private boolean isPrintCmd(int index){
        return tokens.get(index).getKind().equals(Token.Kind.OPEN) &&
                tokens.get(index+1).getKind().equals(Token.Kind.NAME) &&
                tokens.get(index+2).getKind().equals(Token.Kind.CLOSE);
    }
    private boolean isIfCmd(int index){
        return tokens.get(index).getKind().equals(Token.Kind.OPEN) &&
                tokens.get(index+1).getKind().equals(Token.Kind.CMD) &&
                tokens.get(index+1).cmd().equals("if") &&
                tokens.get(index+2).getKind().equals(Token.Kind.NAME) &&
                tokens.get(index+3).getKind().equals(Token.Kind.CLOSE);
    }
    private boolean isElseCmd(int index){
        return tokens.get(index).getKind().equals(Token.Kind.OPEN) &&
                tokens.get(index+1).getKind().equals(Token.Kind.CMD) &&
                tokens.get(index+1).cmd().equals("else") &&
                tokens.get(index+2).getKind().equals(Token.Kind.CLOSE);
    }
    private boolean isDoneCmd(int index){
        return tokens.get(index).getKind().equals(Token.Kind.OPEN) &&
                tokens.get(index+1).getKind().equals(Token.Kind.CMD) &&
                tokens.get(index+1).cmd().equals("done") &&
                tokens.get(index+2).getKind().equals(Token.Kind.CLOSE);
    }

    private boolean isForCmd(int index){
        return tokens.get(index).getKind().equals(Token.Kind.OPEN) &&
                tokens.get(index+1).getKind().equals(Token.Kind.CMD) &&
                tokens.get(index+2).getKind().equals(Token.Kind.NAME) &&
                tokens.get(index+3).getKind().equals(Token.Kind.IN) &&
                tokens.get(index+4).getKind().equals(Token.Kind.NAME) &&
                tokens.get(index+5).getKind().equals(Token.Kind.CLOSE);
    }

    /**
     *
     * @return list of commands
     */
    public List<Command> getCommands() {
        int ifAndForCounter = 0;
        int doneCounter = 0;
        List<Command> commands = new LinkedList<>();
        int index = 0;
        while (index < tokens.size()) {
            Token token = tokens.get(index);
            if (token.getKind().equals(Token.Kind.TEXT)) {
                commands.add(new Command(Command.Kind.CMDTEXT, tokens.subList(index, index + 1)));
                index++;
            } else if (isPrintCmd(index)) {
                commands.add(new Command(Command.Kind.CMDPRINT, tokens.subList(index, index + 3)));
                index = index + 3;
            } else if (isIfCmd(index)) {
                ifAndForCounter++;
                commands.add(new Command(Command.Kind.CMDIF, tokens.subList(index, index + 4)));
                index = index + 4;
            } else if (isElseCmd(index)) {
                commands.add(new Command(Command.Kind.CMDELSE, tokens.subList(index, index + 3)));
                index = index + 3;
            } else if (isDoneCmd(index)) {
                doneCounter++;
                commands.add(new Command(Command.Kind.CMDDONE, tokens.subList(index, index + 3)));
                index = index + 3;
            } else if (isForCmd(index)) {
                ifAndForCounter++;
                commands.add(new Command(Command.Kind.CMDFOR, tokens.subList(index, index + 6)));
                index = index + 6;
            } else if (tokens.isEmpty() && doneCounter != ifAndForCounter) {
                throw new TemplateException("Incorrect pairing of done");
            } else {
                throw new TemplateException("Unable to parse token sequence into command");
            }
        }
        return commands;
    }

}
