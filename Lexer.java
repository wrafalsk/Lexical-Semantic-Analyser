import java.io.*;
import java.util.Stack;

class Lexer extends Token{
	Lexer(File file) throws IOException{
        is = new FileInputStream(file);
        advance();
        next(); 
        tokenCopy = new Stack<String>();
    }
    
    Lexer(String s) throws IOException {
        is = new StringBufferInputStream(s);
        advance();
        tokenCopy = new Stack<String>();
    }
    
    Lexer(){
    	tokenCopy = new Stack<String>();
    }
    
    public Token next() throws IOException {
        currentToken = null; 
        String lexeme = null; 
        int state = START;
        
        while(currentToken == null){
            boolean shouldAdvance = true; 
            switch(state){
                case START:
                    lexeme = "";
                    if(Character.isWhitespace(currChar))    state = START;
                    else if(Character.isDigit(currChar))    state = INT;
                    else if(Character.isLetter(currChar))   state = ID;
                    else if(currChar == '#')                state = COMMENT;
                    else if(currChar == ',')                state = COMMA;
                    else if(currChar == ';')                state = SEMICOLON;
                    else if(currChar == '{')                state = LBRACE;
                    else if(currChar == '}')                state = RBRACE;
                    else if(currChar == '(')                state = LPAREN;
                    else if(currChar == ')')                state = RPAREN;      
                    else if(currChar == '=')                state = EQ_OR_ASSIGN;
                    else if(currChar == '<')                state = LT_OR_LE;
                    else if(currChar == '>')                state = GT_OR_GE;
                    else if(currChar == '+')                state = PLUS;
                    else if(currChar == '-')                state = MINUS;
                    else if(currChar == '*')                state = MUL;
                    else if(currChar == '/')                state = DIV;
                    else if(currChar == '%')                state = MOD;
                    else if(currChar == '\0')               state = EOI;
                    else state = ERROR;
                    break;
                case INT:
                    if (Character.isDigit(currChar))
                        state = INT;
                    else
                        return currentToken = new Token(Token.INT_LITERAL, lexeme);
                    break;
                case ID:
                    if(Character.isLetterOrDigit(currChar))
                        state = ID;
                    else {
                        if(keywordMap.get(lexeme) != null)
                            currentToken = keywordMap.get(lexeme);
                        else
                            currentToken = new Token(Token.ID, lexeme);
                            shouldAdvance = false;
                    }
                        break;
                case COMMA: 
                	return currentToken = new Token(Token.COMMA);
                case COMMENT:
                    if(currChar != '\n')
                        state = COMMENT;
                    else
                        currentToken = new Token(Token.COMMENT);
                    break;
                case SEMICOLON: 
                	return currentToken = new Token(Token.SEMICOLON);
                case LBRACE: 
                	return currentToken = new Token(Token.LBRACE);
                case RBRACE: 
                	return currentToken = new Token(Token.RBRACE);
                case LPAREN: 
                	return currentToken = new Token(Token.LPAREN);
                case RPAREN: 
                	return currentToken = new Token(Token.RPAREN);
                case PLUS: 
                	return currentToken = new Token(Token.PLUS_OP);
                case MINUS: 
                	return currentToken = new Token(Token.MIN_OP);
                case EQ_OR_ASSIGN:
                    if(currChar == '=')
                        currentToken = new Token(Token.EQ_OP);
                    else{
                        currentToken = new Token(Token.ASSIGN_OP);
                        shouldAdvance = false;
                    }
                    break;
                case GT_OR_GE:
                    if(currChar == '=')
                        currentToken = new Token(Token.GE_OP);
                    else{
                        currentToken = new Token(Token.GT_OP);
                        shouldAdvance = false;
                    }
                    break;
                case LT_OR_LE:
                    if(currChar == '=')
                        currentToken = new Token(Token.LE_OP);
                    else{
                        currentToken = new Token(Token.LT_OP);
                        shouldAdvance = false;
                    }
                    break;
                case MUL: 
                	return currentToken = new Token(Token.MUL_OP);
                case DIV: 
                	return currentToken = new Token(Token.DIV_OP);
                case MOD: 
                	return currentToken = new Token(Token.MOD_OP);
                case EOI:
                    currentToken = new Token(Token.EOI);
                    break;
                case ERROR:
                    System.err.println("Unexpexted character " + currChar + " on line " + line + " at column " + col);
                    state = START;
                    break;
                default:
                    System.err.println("Unknown state " + state);
                    System.exit(0);
            }
            lexeme += currChar;
            if(shouldAdvance && state != ERROR) 
            	advance();
        }
        /*
        if(currentToken != null && currentToken.lexeme != null)
        	tokenCopy.add(currentToken.lexeme);
        	*/
        return currentToken;
    }

    public Token token(){ 
    	return currentToken; 
    }
    
    public String getValidIDToken(){
    	while(!tokenCopy.isEmpty() &&  tokenCopy.peek() != Token.tokenMap.get(Token.ID))
    		tokenCopy.pop();
    	if(!tokenCopy.isEmpty()){
    		return tokenCopy.pop();
    	}
    	else
    		return null;
    }
    public boolean isStackEmpty(){
    	return tokenCopy.isEmpty();
    }
    /*
    public String getValidIDToken(){
    	if(!tokenCopy.isEmpty()){
    	while(tokenCopy.peek() == Token.tokenMap.get(Token.COMMA) 
    	|| tokenCopy.peek() == Token.tokenMap.get(Token.SEMICOLON)){
    		tokenCopy.pop();
    		if(tokenCopy.peek() == Token.tokenMap.get(Token.ID))
    			return tokenCopy.pop();
    	}
    		
    	}
    } */
    
    void advance() throws IOException{
    	int i = is.read();
    	currChar = (i<0) ? '\0' : (char)i;
    	if (currChar == '\n') line ++; col = 0;
    		col++;
    }
    void commentSkip() throws IOException{
    	line++;

    }
    
            InputStream is;
            char currChar;
            Token currentToken = null;
             public Stack<String> tokenCopy;
            int line = 1, col = 1;

            static final int
                EOI = -2,
                ERROR = -1,
                START = 0,
                ID = 1,
                INT = 2,
                PLUS = 3,
                MINUS = 4,
                MUL = 5,
                DIV = 6,
                MOD = 7,
                LPAREN = 8,
                RPAREN = 9,
                LBRACE = 10,
                RBRACE = 11,
                COMMA = 12,
                SEMICOLON = 13,
                GT_OR_GE = 14,
                LT_OR_LE = 15,
                EQ_OR_ASSIGN = 16,
                COMMENT = 17,
            	READ = 18,
            	PRINT = 19;
}
