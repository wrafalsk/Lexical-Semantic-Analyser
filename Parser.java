import java.io.*;
import java.util.*;
import java.util.*;
class Parser {
	Set<String> synAn;
	
	Parser(Lexer lexer) throws IOException, ParserException {
		this.lexer = lexer;
		synAn = new HashSet<String>(); 
	}

	void parse()  throws IOException, ParserException {
		program();
		match(Token.EOI);
	}
		
	void program() throws IOException, ParserException {
		if(lexer.token().type == Token.COMMENT)
			comment();
		match(Token.PROGRAM);
		while (optMatch(Token.INT)){
			declaration();
			if(optMatch(Token.COMMENT))
				comment();
		}
		while (!optMatch(Token.END)){
			statement();
			if(optMatch(Token.COMMENT))
				comment();
		}
	}

	void statement() throws IOException, ParserException {
		if (lexer.token().type == Token.ID){
			if(!synAn.contains(lexer.token().lexeme))
				throw new ParserException(lexer.token(), " not declared");
			lexer.next();
			assignmentStatement();
		}
		else if (optMatch(Token.IF))
			ifStatement();
		else if (optMatch(Token.FOR))
			forStatement();
		else if (optMatch(Token.WHILE))
			whileStatement();
		else if (optMatch(Token.READ))
			readStatement();
		else if (optMatch(Token.PRINT))
			printStatement();
		else if(optMatch(Token.COMMENT))
			comment();
		else
			throw new ParserException(lexer, "Expecting statement, found " + lexer.token());

	}

	void assignmentStatement() throws IOException, ParserException {
		if(isValidID(lexer) && synAn.add(lexer.token().lexeme))
				throw new ParserException(lexer.token(), " not declared");
		if(optMatch(Token.ASSIGN_OP) || optMatch(Token.EQ_OP))
			expression();
		match(Token.SEMICOLON);
		
	}

	void ifStatement() throws IOException, ParserException {
		match(Token.LPAREN);
		expression();
		if (!lexer.token().isRelop()) 
			throw new ParserException(lexer, "Expecting RELOP, found " + lexer.token());
		lexer.next();
		expression();
		match(Token.RPAREN);
		if(lexer.token().type != Token.LBRACE)
			statement();
		else if (optMatch(Token.LBRACE))
			compoundStatement();
		if(optMatch(Token.ELSE)){
			if(lexer.token().type != Token.LBRACE)
				statement();
			else if (optMatch(Token.LBRACE))
				compoundStatement();
		}
		//<if-statement> ::= (<expression> RELOP <expression>) <statement> [else <statement>]
	}
	void forStatement() throws IOException, ParserException {
		match(Token.LPAREN);
		if(lexer.token().type != Token.ID) throw new ParserException(lexer, "Expecting ID, found " + lexer.token());
		lexer.next();
		if(optMatch(Token.EQ_OP) || optMatch(Token.ASSIGN_OP))
		expression();
		match(Token.COMMA);
		expression();
		match(Token.RPAREN);
		if(lexer.token().type != Token.LBRACE)
			statement();
		else if (optMatch(Token.LBRACE))
			compoundStatement();
			
		//<for-statement> ::= (ID = <expression>, <expression>) {<statement>}
	}
	void whileStatement() throws IOException, ParserException {
		if(!optMatch(Token.LPAREN)) throw new ParserException(lexer, "Expecting Left Paren, found " + lexer.token());
		expression();
		if(!lexer.token().isRelop()) throw new ParserException(lexer, "Expecting Relational Op, found " + lexer.token());
		lexer.next();
		expression();
		if(!optMatch(Token.RPAREN)) throw new ParserException(lexer, "Expecting Right Paren, found " + lexer.token());
		statement();
		//<while-statement> ::= while (<expression> RELOP <expression>) <statement>
	}
	void printStatement() throws IOException, ParserException {
		expression();
		match(Token.SEMICOLON);
		//<print-statement> ::= print <expression>;
	}
	void readStatement() throws IOException, ParserException {
		match(Token.ID);
		match(Token.SEMICOLON);
		//<read-statement> ::= read ID;
	}
	void compoundStatement() throws IOException, ParserException {
		while(lexer.token().type != Token.RBRACE){
		statement();
		}
		match(Token.RBRACE);
		//<compound-statement> ::= { {<statement>} }
	}
	 void comment() throws IOException, ParserException{
		 lexer.commentSkip();
	    }
	
	void term() throws IOException, ParserException {
		factor();
		while(optMatch(Token.MUL_OP) || optMatch(Token.DIV_OP) || optMatch(Token.MOD_OP))
			factor();
	}
	void declaration() throws IOException, ParserException { 
		if(lexer.token().type == Token.ID){
			if(!synAn.add(lexer.token().lexeme))
				throw new ParserException(lexer.token(), " already added");
			lexer.next();
		}
		if(optMatch(Token.ASSIGN_OP) || optMatch(Token.EQ_OP))
			expression();
		while(optMatch(Token.COMMA)){
			if(lexer.token().type == Token.ID){
				if(!synAn.add(lexer.token().lexeme))
					throw new ParserException(lexer.token(), " already added");
				lexer.next();
			}
			else
				throw new ParserException(lexer, "Expecting ID, found " + lexer.token());
		}
		match(Token.SEMICOLON);
		//<declaration> ::= int ID {, ID};
	}

	void expression() throws IOException, ParserException {
		term();
		while (optMatch(Token.PLUS_OP) || optMatch(Token.MIN_OP)) 
			term();
	}
	
	void factor() throws IOException, ParserException {
		if (lexer.token().type == Token.ID){
			if(!synAn.contains(lexer.token().lexeme))
				throw new ParserException(lexer.token(), " not declared");
			lexer.next();
			return;
		}
		else if(optMatch(Token.INT) || optMatch(Token.INT_LITERAL))
			return;
		else if(optMatch(Token.LPAREN)){
			expression();
			match(Token.RPAREN);
		}
		else
			throw new ParserException(lexer, "Expecting factor, found " + lexer.token());
	}

	boolean optMatch(int tokenType) throws IOException {
		if (lexer.token().type == tokenType) {
			lexer.next();
			return true;
		}
		return false;
	}

	void match(int tokenType) throws IOException, ParserException {
		if (!optMatch(tokenType)) throw new ParserException(lexer, "Expecting '" + new Token(tokenType) + "', found " + lexer.token());
	}
	
	boolean isValidID(Lexer lex){
		if(Token.tokenMap.containsKey(lex.token().type))
			if(Token.tokenMap.get(lex.token().type) == "ID"){
				if(lex.token().lexeme != "SEMICOLON" 
				&& lex.token().lexeme != "INT" 
				&& lex.token().lexeme != "COMMA")
					return true;
			}
				
		
		return false;
	}
	Lexer lexer;
}

