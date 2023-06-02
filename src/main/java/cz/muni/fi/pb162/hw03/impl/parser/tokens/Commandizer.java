package cz.muni.fi.pb162.hw03.impl.parser.tokens;

import cz.muni.fi.pb162.hw03.impl.For;
import cz.muni.fi.pb162.hw03.impl.If;
import cz.muni.fi.pb162.hw03.impl.Print;
import cz.muni.fi.pb162.hw03.impl.Template;
import cz.muni.fi.pb162.hw03.impl.TemplateEvaluable;
import cz.muni.fi.pb162.hw03.impl.Text;
import cz.muni.fi.pb162.hw03.template.TemplateException;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Ladislav Husty
 */
public class Commandizer {
    private final List<Token> tokens;
    private final List<TemplateEvaluable> commands;
    public static class CommandManager {
        private  final List<TemplateEvaluable> commands = new LinkedList<>();
        public  List<TemplateEvaluable> getCommands() {
            return commands;
        }
    }

    /**
     *
     * @param tokens tokens to be converted to commands
     */
    public Commandizer(List<Token> tokens) {
        this.tokens = tokens;
        commands = new CommandManager().getCommands(); // Get the shared commands list
    }

    private boolean isText(){
        return tokens.get(0).getKind().equals(Token.Kind.TEXT);
    }
    private boolean isPrintCmd(){
        return tokens.get(0).getKind().equals(Token.Kind.OPEN) &&
                tokens.get(1).getKind().equals(Token.Kind.NAME) &&
                tokens.get(2).getKind().equals(Token.Kind.CLOSE);
    }
    private boolean isIfCmd(){
        return isIfCmd(this.tokens);
    }
    private boolean isIfCmd(List<Token> tokens){
        return tokens.get(0).getKind().equals(Token.Kind.OPEN) &&
                tokens.get(1).getKind().equals(Token.Kind.CMD) &&
                tokens.get(1).cmd().equals("if") &&
                tokens.get(2).getKind().equals(Token.Kind.NAME) &&
                tokens.get(3).getKind().equals(Token.Kind.CLOSE);
    }
    private boolean isElseCmd(){
        return isElseCmd(this.tokens);
    }
    private boolean isElseCmd(List<Token> tokens){
        return tokens.get(0).getKind().equals(Token.Kind.OPEN) &&
                tokens.get(1).getKind().equals(Token.Kind.CMD) &&
                tokens.get(1).cmd().equals("else") &&
                tokens.get(2).getKind().equals(Token.Kind.CLOSE);
    }
    private boolean isDoneCmd(){
        return isDoneCmd(tokens);
    }
    private boolean isDoneCmd(List<Token> tokens){
        return tokens.get(0).getKind().equals(Token.Kind.OPEN) &&
                tokens.get(1).getKind().equals(Token.Kind.CMD) &&
                tokens.get(1).cmd().equals("done") &&
                tokens.get(2).getKind().equals(Token.Kind.CLOSE);
    }

    private boolean isForCmd(){
        return isForCmd(tokens);
    }
    private boolean isForCmd(List<Token> tokens){
        return tokens.get(0).getKind().equals(Token.Kind.OPEN) &&
                tokens.get(1).getKind().equals(Token.Kind.CMD) &&
                tokens.get(2).getKind().equals(Token.Kind.NAME) &&
                tokens.get(3).getKind().equals(Token.Kind.IN) &&
                tokens.get(4).getKind().equals(Token.Kind.NAME) &&
                tokens.get(5).getKind().equals(Token.Kind.CLOSE);
    }


    private TemplateEvaluable[] getIfBlock(List<Token> tokens){
        boolean hasPairedDones = false;
        int nestingCount = 0;
        int elseIndex = 0;
        int doneIndex = 0;
        for (int i = 4; i < tokens.size(); i++){
            List<Token> remainingTokens = tokens.subList(i, tokens.size());
            if (isIfCmd(remainingTokens)){
                nestingCount++;
            } else if (isElseCmd(remainingTokens) && nestingCount == 0) {
                elseIndex = i;
            } else if (isDoneCmd(remainingTokens)) {
                if (nestingCount != 0){
                    nestingCount--;
                } else{
                    hasPairedDones = true;
                    doneIndex = i;
                    break;
                }
            }
        }
        if (!hasPairedDones){
            throw new TemplateException("Missing done statement");
        }
        elseIndex = elseIndex == 0 ? doneIndex : elseIndex;
        List<Token> ifBlockTokens = new ArrayList<>(tokens.subList(4, elseIndex));
        TemplateEvaluable ifTemplate = new Template(new Commandizer(ifBlockTokens).getCommands());
        TemplateEvaluable elseTemplate = elseIndex == doneIndex ? null :
                new Template((
                        new Commandizer(
                                new ArrayList<>(tokens.subList(elseIndex+3, doneIndex)))).getCommands());
        TemplateEvaluable[] result = new TemplateEvaluable[] {ifTemplate, elseTemplate};
        this.consume(doneIndex);
        return result;
    }
    private TemplateEvaluable getForBlock(List<Token> tokens){
        int doneIndex = 0;
        int nestingCount = 0;
        for (int i = 6; i<tokens.size(); i++){
            List<Token> remainingTokens = tokens.subList(i, tokens.size());
            if (isForCmd(remainingTokens) || isIfCmd(remainingTokens)){
                nestingCount++;
            } else if(isDoneCmd() && nestingCount != 0){
                nestingCount--;
            } else if (isDoneCmd(remainingTokens) && nestingCount == 0){
                doneIndex = i;
                break;
            }
        }
        if (doneIndex == 0){
            throw new TemplateException("Done not found in for block");
        }
        TemplateEvaluable forBlock = new Template((
                new Commandizer(
                        new ArrayList<>(tokens.subList(6, doneIndex)))).getCommands());
        this.consume(doneIndex);
        return forBlock;
    }

    private void consume(){
        tokens.remove(0);
    }
    private void consume(int numberOfTokensToConsume){
        tokens.subList(0, numberOfTokensToConsume).clear();
    }

    /**
     * Checks if tokens variable contains at least one token
     * @return boolean
     */
    public boolean containsTokens() {
        return !(tokens.isEmpty());
    }


    /**
     *
     * @return list of commands
     */
    public List<TemplateEvaluable> getCommands() {
        while (this.containsTokens()) {
            if (isText()) {
                commands.add(new Text( tokens.get(0).text()) );
                this.consume();
            } else if (isPrintCmd()) {
                commands.add(new Print( (tokens.get(1)).name()) );
                this.consume(3);
            } else if (isIfCmd()) {
                String variable = tokens.get(2).name();
                TemplateEvaluable[] ifElseBlock = getIfBlock(tokens);
                TemplateEvaluable ifBlock = ifElseBlock[0];
                TemplateEvaluable elseBlock = ifElseBlock[1];
                commands.add(new If(variable, ifBlock,  elseBlock) );
            } else if (isForCmd()) {
                var iterated = tokens.get(2).name();
                var iterable = tokens.get(4).name();
                TemplateEvaluable forBlock = getForBlock(tokens);
                commands.add((new For(iterated, iterable, forBlock)));
            } else if (isDoneCmd()){
                this.consume(3);
            } else {
                throw new TemplateException("Unable to parse token sequence into command");
            }
        }
        return commands;
    }
}

