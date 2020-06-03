import java.util.Scanner;

public class Parser {

	
	// Scanner object to read text file
	private Scanner scan;
	
	// current line in text file
	private String line;
	
	// current character in parser
	private Character character;
	
	// status of parser
	protected enum state { PARSE, ERROR, COMPLETE};
	private state status;
	
	// punctuation of grammar
	protected static final char LEFT_PARENTHESIS = '(';
	protected static final char COMMA = ',';
	protected static final char RIGHT_PARENTHESIS = ')';
	protected static final char COLON = ':';
	protected static final char SPACE = ' ';
	protected static final char SEMICOLON = ';';
	protected static final char QUOTATIONS = '"';
	protected static final char PERIOD = '.';
	
	private static final String END = "End";
	/**
	 * 
	 * @param scan
	 */
	public Parser(Scanner scan){
		this.scan = scan;
		status = state.PARSE;
		line = "";
	}
	
	
	public char getCharacter(){
		return character;
	}
	
	
	public state getStatus(){
		return status;
	}
	
	protected void completeParse(){
		status = state.COMPLETE;
		line = null;
		character = null;
	}
	
	
	/**
	 * Looks one character ahead to determine which production to choose when multiple 
	 * productions are available
	 * @param firstCharacter
	 * @return
	 */
	public Character lookahead(){

		int firstPosition = 0;
		Character next = null;
		
		if (  line.length() == 0 && scan.hasNext() ){
			line = scan.nextLine();
		}
		
		if ( line != null ){
			next = line.charAt(firstPosition);
		}
		
		return next;
	}
	
	/**
	 * Advances the parser
	 * @return	next character in token
	 */
	protected char advance(){
		
		int firstPosition = 0;

		// get the next token in the file
		if ( line.length() == 0 && scan.hasNext() ){
			line = scan.nextLine().trim();
		}
	
		if ( status == state.PARSE) {
			character = line.charAt(firstPosition);
			line = line.substring(firstPosition+1);
		}
		return character;
	}
	
	

	
	
	
	/**
	 * Test string of tokens to see if it matches keywords in Grammar
	 * @param keyword	keyword to be match with tokens	
	 * @return			Boolean (true = matched)
	 */
	protected Boolean keywordMatch(String keyword){
		
		Boolean match = true;
		int index = 0;
		
		while (match && index < keyword.length()){			
			this.advance();
			
			if (keyword.charAt(index) != character){
				match = false;
				this.errorMessage("Keyword mismatch. \"" 
								+ character + "\" found. \"" 
								+ keyword.charAt(index) + "\" expected.");
			}
			index++;			
		}	
		// after a keyword is matched, advance to next character
		if (match) { this.advance(); }
		
		return match;
	}
	
	
	
	
	
	protected String collectString(char begin, char ending){

		String output = "";

		char collector;
		boolean stop = false;


		// Ensure that the token stream begins with the correct punctuation. 
		// Collect each character until the ending punctuation is reached. 
		if ( character == begin ){

			while ( !stop && status == Parser.state.PARSE)
			{
				collector = this.advance();			
				
				if ( collector == ending ) { 					
					stop = true;
					this.advance();
				}
				else  { 
					output += collector; 
				}				
			}
		}
		else {
			this.errorMessage("\"" + begin + "\" expected. \"" + character + "\" found.");
		}

		return output;

	}
	
	
	
	
	
	protected Integer collectNumber(char begin, char ending, boolean buttonException){

		String output = "";

		char collector;
		boolean stop = false;


		// Ensure that the token stream begins with the correct punctuation. 
		// Collect each character until the ending punctuation is reached. 
		if ( character == begin ){

			while ( !stop && status == Parser.state.PARSE)
			{
				collector = this.advance();		
				
				if ( collector != ending && !Character.isDigit(collector) ) {
					stop = true;
					this.errorMessage("Number expected. \"" + character + "\" found.");
				}				
				else if ( collector == ending ) { 					
					stop = true;
					
					// the button token must have a semicolon after its last quotation 
					if (buttonException) { 
						this.advance();
						this.confirmSemicolon(); 
					}
				}
				else  { 
					output += collector; 
				}				
			}
		}
		else {
			this.errorMessage("\"" + begin + "\" expected. \"" + character + "\" found.");
		}

		return Integer.parseInt(output);

	}
	
	
	public Boolean end()
	{
		if ( this.keywordMatch(END) ){ 
			if 		( this.confirmPeriod() 	  ){ return true; }
			else if ( this.confirmSemicolon() ){ return false; }
		}		
		return false;
	}
	
	
	public boolean confirmSemicolon(){
		
		if ( character == Parser.SEMICOLON){
			return true;
		}
		else {
			this.errorMessage("\"" + Parser.SEMICOLON + "\" expected. \"" + this.lookahead() + "\" found.");
		}
		return false;
	}
	
	
	public boolean confirmPeriod(){
		
		if ( character == Parser.PERIOD){
			System.out.println("End Parsing");
			this.completeParse();
			return true;
		}
		
		return false;
	}

	protected void skipWhiteSpace(){

		if ( character == SPACE){
			this.advance();
		}
		else if ( character != SPACE){
			this.errorMessage("\" \" expected. \"" + character + "\" found.");
		}
	}
	
	
	protected void errorMessage(String error){
		System.out.println(error);
		this.status = state.ERROR;
	}
}
