package cz.muni.fi.pb162.hw03.impl.parser.tokens;

import cz.muni.fi.pb162.hw03.impl.If;
import cz.muni.fi.pb162.hw03.impl.Print;
import cz.muni.fi.pb162.hw03.impl.Template;
import cz.muni.fi.pb162.hw03.impl.TemplateEvaluable;
import cz.muni.fi.pb162.hw03.impl.Text;
import cz.muni.fi.pb162.hw03.template.TemplateException;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Ladislav Husty
 */
public class Commandizer {
    private List<Token> tokens;

    /**
     *
     * @param tokens
     */
    public Commandizer(List<Token> tokens){
        this.tokens = tokens;
    }
    private boolean isPrintCmd(){
        return tokens.get(0).getKind().equals(Token.Kind.OPEN) &&
                tokens.get(1).getKind().equals(Token.Kind.NAME) &&
                tokens.get(2).getKind().equals(Token.Kind.CLOSE);
    }
    private boolean isIfCmd(){
        return tokens.get(0).getKind().equals(Token.Kind.OPEN) &&
                tokens.get(1).getKind().equals(Token.Kind.CMD) &&
                tokens.get(1).cmd().equals("if") &&
                tokens.get(2).getKind().equals(Token.Kind.NAME) &&
                tokens.get(3).getKind().equals(Token.Kind.CLOSE);
    }
    private boolean isElseCmd(){
        return tokens.get(0).getKind().equals(Token.Kind.OPEN) &&
                tokens.get(1).getKind().equals(Token.Kind.CMD) &&
                tokens.get(1).cmd().equals("else") &&
                tokens.get(2).getKind().equals(Token.Kind.CLOSE);
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


    private TemplateEvaluable getIfBlock (List<Token> tokens) {
        int nestingCount = 0;
        int endOfIfBlockIndex = 0;
        for (int i = 4; i < tokens.size(); i++){
            Token currToken = tokens.get(i);
            if (currToken.getKind().equals(Token.Kind.CMD)){
                if(currToken.cmd().equals("if")){
                    nestingCount++;
                } else if ( (currToken.cmd().equals("else") && nestingCount == 0) ||
                        (currToken.cmd().equals("done") && nestingCount == 0) ){
                    endOfIfBlockIndex = i-1;
                } else if (currToken.cmd().equals("done")){
                    nestingCount--;
                }
            }
        }
        Commandizer commandizer = new Commandizer(tokens.subList(4, endOfIfBlockIndex));
        if (tokens.get(endOfIfBlockIndex+1).cmd().equals("done")){
            this.consume(endOfIfBlockIndex+3);
        } else {
            this.consume(endOfIfBlockIndex);
        }
        return new Template(commandizer.getCommands());
    }

    private TemplateEvaluable getElseBlock(List<Token> tokens) {
        int nestingCount = 0;
        int endOfElseBlockIndex = 0;
        if (isElseCmd()){
                for (int i = 3; i < tokens.size()+1; i++){
                    Token currToken = tokens.get(i);
                    if (currToken.getKind().equals(Token.Kind.CMD)){
                        if(currToken.cmd().equals("if")){
                            nestingCount++;
                        } else if ( (currToken.cmd().equals("else") && nestingCount == 0) ||
                                (currToken.cmd().equals("done") && nestingCount == 0) ){
                            endOfElseBlockIndex = i-1;
                        } else if (currToken.cmd().equals("done")){
                            nestingCount--;
                        }
                    }
                }
            Commandizer commandizer = new Commandizer(tokens.subList(4, endOfElseBlockIndex));
            this.consume(endOfElseBlockIndex);
            return new Template(commandizer.getCommands());
        } else{
            return null;
        }
    }

    private void consume(){
        updateTokens(tokens.subList(1, tokens.size()));
    }
    private void consume(int numberOfTokensToConsume){
        updateTokens(tokens.subList(numberOfTokensToConsume, tokens.size()));
    }
    public boolean containsTokens() {
        return !(tokens.isEmpty());
    }

    public void updateTokens(List<Token> tokens){
        this.tokens = new LinkedList<>(tokens);
    }

    /**
     *
     * @return list of commands
     */
    public List<TemplateEvaluable> getCommands() {
        List<TemplateEvaluable> commands = new LinkedList<>();
        while (this.containsTokens()) {
            Token token = tokens.get(0);
            if (token.getKind().equals(Token.Kind.TEXT)) {
                commands.add(new Text( token.text()) );
                this.consume();
            } else if (isPrintCmd()) {
                commands.add(new Print( (tokens.get(1)).name()) );
                this.consume(3);
            } else if (isIfCmd()) {
                TemplateEvaluable ifBlock = getIfBlock(tokens);
                TemplateEvaluable elseBlock = getElseBlock(tokens);
                commands.add(new If( tokens.get(2).name(), ifBlock,  elseBlock));
            }
//            else if (isForCmd(index)) {
//                commands.add(new Command(Command.Kind.CMDFOR, tokens.subList(index, index + 6)));
//                index = index + 6;
//            }
            else {
                throw new TemplateException("Unable to parse token sequence into command");
            }
        }
        return commands;
    }



}
