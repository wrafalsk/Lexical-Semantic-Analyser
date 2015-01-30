class ParserException extends Exception {

	ParserException(Lexer lexer, String message) {
		super("At line " + lexer.line + " col " + lexer.col + ": " + message);}

	ParserException(Token token, String message) {
		super(token + message);
		
	}

	ParserException(String tokenCopy, String message) {
		super(tokenCopy + message);
	}
}
