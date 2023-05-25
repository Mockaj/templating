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
    private static List<TemplateEvaluable> commands;

    /**
     *
     * @param tokens
     */
    public Commandizer(List<Token> tokens){
        this.tokens = tokens;
        this.commands = new LinkedList<>();
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
        return tokens.get(0).getKind().equals(Token.Kind.OPEN) &&
                tokens.get(1).getKind().equals(Token.Kind.CMD) &&
                tokens.get(1).cmd().equals("if") &&
                tokens.get(2).getKind().equals(Token.Kind.NAME) &&
                tokens.get(3).getKind().equals(Token.Kind.CLOSE);
    }
    private boolean isIfCmd(List<Token> tokens){
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
    private boolean isElseCmd(List<Token> tokens){
        return tokens.get(0).getKind().equals(Token.Kind.OPEN) &&
                tokens.get(1).getKind().equals(Token.Kind.CMD) &&
                tokens.get(1).cmd().equals("else") &&
                tokens.get(2).getKind().equals(Token.Kind.CLOSE);
    }
    private boolean isDoneCmd(List<Token> tokens){
        return tokens.get(0).getKind().equals(Token.Kind.OPEN) &&
                tokens.get(1).getKind().equals(Token.Kind.CMD) &&
                tokens.get(1).cmd().equals("done") &&
                tokens.get(2).getKind().equals(Token.Kind.CLOSE);
    }

    private boolean isForCmd(int index){
        return tokens.get(index).getKind().equals(Token.Kind.OPEN) &&
                tokens.get(index+1).getKind().equals(Token.Kind.CMD) &&
                tokens.get(index+2).getKind().equals(Token.Kind.NAME) &&
                tokens.get(index+3).getKind().equals(Token.Kind.IN) &&
                tokens.get(index+4).getKind().equals(Token.Kind.NAME) &&
                tokens.get(index+5).getKind().equals(Token.Kind.CLOSE);
    }


    private List<Template> getIfBlock(List<Token> tokens) {
        int nestingCount = 0;
        int endOfIfElseBlockIndex = 0;
        List<Token> ifElseBlockTokens = new LinkedList<>();

        for (int i = 4; i < tokens.size(); i++) {
            Token currToken = tokens.get(i);
            if (currToken.getKind().equals(Token.Kind.CMD)) {
                if (currToken.cmd().equals("if")) {
                    nestingCount++;
                } else if (currToken.cmd().equals("done")) {
                    if (nestingCount == 0) {
                        endOfIfElseBlockIndex = i+1;
                        ifElseBlockTokens.addAll(tokens.subList(0, endOfIfElseBlockIndex+1));
                        break;
                    } else {
                        nestingCount--;
                    }
                }
            }
        }
        nestingCount = 0;
        int elseIndex = 0;
        for (int i=4; i<ifElseBlockTokens.size(); i++){
            List<Token> remainingBlockTokens = ifElseBlockTokens.subList(i, ifElseBlockTokens.size());
            if (isIfCmd(remainingBlockTokens)){
                nestingCount++;
            }
            if (isDoneCmd(remainingBlockTokens) &&
            nestingCount != 0){
                nestingCount--;
            }
            if (isElseCmd(remainingBlockTokens) &&
            nestingCount == 0){
                elseIndex = i;
            }
        }
        List<Token> ifBlockTokens = new LinkedList<>();
        List<Token> elseBlockTokens = new LinkedList<>();
        Commandizer commandizerElseBlock = null;
        Template templateElseBlock = null;
        if (elseIndex != 0){
            ifBlockTokens = new LinkedList<>(ifElseBlockTokens.subList(4, elseIndex));
            elseBlockTokens = new LinkedList<>(
                    ifElseBlockTokens.subList(elseIndex+3,
                            ifElseBlockTokens.size()));
            templateElseBlock = new Template(new Commandizer(elseBlockTokens).getCommands());
        }
        else{
            ifBlockTokens = new LinkedList<>(ifElseBlockTokens.subList(4, ifElseBlockTokens.size()));
        }
        Commandizer commandizerIfBlock = new Commandizer(ifBlockTokens);
        this.consume(ifElseBlockTokens.size());
        List<Template> result = new LinkedList<>();
        var templateIfBlock = new Template(commandizerIfBlock.getCommands());
        result.add(templateIfBlock);
        result.add(templateElseBlock);
        return result;
    }

//    private TemplateEvaluable getElseBlock(List<Token> tokens) {
//        int nestingCount = 0;
//        int endOfElseBlockIndex = 0;
//        if (isElseCmd()){
//                for (int i = 3; i < tokens.size(); i++){
//                    Token currToken = tokens.get(i);
//                    if (currToken.getKind().equals(Token.Kind.CMD)){
//                        if(currToken.cmd().equals("if")){
//                            nestingCount++;
//                        } else if ((currToken.cmd().equals("done") && nestingCount == 0) ){
//                            endOfElseBlockIndex = i+1;
//                        } else if (currToken.cmd().equals("done") && nestingCount != 0 ){
//                            nestingCount--;
//                        }
//                    }
//                }
//            Commandizer commandizer = new Commandizer(tokens.subList(4, endOfElseBlockIndex));
//            this.consume(endOfElseBlockIndex+1);
//            return new Template(commandizer.getCommands());
//        } else{
//            return null;
//        }
//    }

    private void consume(){
        tokens.remove(0);
    }
    private void consume(int numberOfTokensToConsume){
        tokens.subList(0, numberOfTokensToConsume).clear();
    }
    public boolean containsTokens() {
        return !(tokens.isEmpty());
    }

//    public void updateTokens(List<Token> tokens){
//        this.tokens = new LinkedList<>(tokens);
//    }

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
                List<Template> ifElseBlock = getIfBlock(tokens);
                TemplateEvaluable ifBlock = ifElseBlock.get(0);
                TemplateEvaluable elseBlock = ifElseBlock.get(1);
                commands.add(new If(variable, ifBlock,  elseBlock) );
            } else if (isDoneCmd(tokens)){
                this.consume(3);
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
