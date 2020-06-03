/**
 * 
 * Layout class determines the characters of the Flow and Grid Layouts used
 * in the GUI generated from the parsed input file
 * 
 */

import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.LayoutManager;

public class Layout {

	
	Parser p;
	
	private String layoutType;
	
	private static final String LAYOUT = "Layout";

	private static final String FLOW = "Flow";
	private static final String GRID = "Grid";
	
	
	
	// grid sizing parameters
	private Integer gridRows;
	private Integer gridColumns;
	private Integer gridHorizontalGap;
	private Integer gridVerticalGap;
	
	
	/**
	 * Determines Layout type used for GUI
	 * @param p
	 */
	protected Layout(Parser p){
		
		this.p = p;
		
		this.type();
		
		if ( layoutType == GRID ){
			this.gridSizing();
		}
		System.out.println(this);
	}
	
	/**
	 * Sets up the Layout used for the GUI
	 * @return Layout
	 */
	protected LayoutManager getLayoutType(){
		
		if 		( layoutType == FLOW ) { return new FlowLayout(); }
		else if ( layoutType == GRID ) 
		{ 
			if  ( this.hasGaps() )	   { return new GridLayout( gridRows, gridColumns, gridHorizontalGap, gridVerticalGap); }
			else 					   { return new GridLayout( gridRows, gridColumns); }
		}
		
		return null;
	}
	
	/**
	 * Returns number of rows in Grid Layout
	 * @return	
	 */
	public int getRows(){
		return gridRows;
	}
	
	/**
	 * Returns number of columns in Grid Layout
	 * @return
	 */
	public int getColumns(){
		return gridColumns;
	}
	
	/**
	 * Returns horizontal gap in Grid Layout
	 * @return
	 */
	public int getHGap(){
		return gridHorizontalGap;
	}
	
	/**
	 * Returns vertical gap in Grid Layout
	 * @return
	 */
	public int getVGap(){
		return gridVerticalGap;
	}
	
	
	
	
	
	
	/**
	 * Determines type of Layout and displays error if it does not match one of the available Layouts
	 * used in the grammar
	 */
	public void type(){
			
		
		if ( p.getCharacter() == Parser.SPACE){
			
			if ( p.keywordMatch(LAYOUT)){
				
				// check first character of production before checking remaining characters to determine layout type
				if      ( p.lookahead()=='F' && p.keywordMatch(FLOW) ) { layoutType = FLOW; }
				else if ( p.lookahead()=='G' && p.keywordMatch(GRID) ) { layoutType = GRID; }
				else    { p.errorMessage("Incorrect layout type."); }
			}
		}
	}
	
	
	
	/**
	 * Verifies that colon appears as required by grammar
	 */
	private void confirmColon(){
				
		if ( p.lookahead() == Parser.COLON){
			p.advance();
		}
		else {
			p.errorMessage("\"" + Parser.COLON + "\" expected. " + p.lookahead() + " found.");
		}
	}
	
	/**
	 * Determines size of grid rows in Grid Layout
	 */
	public void gridSizing(){
		
		Integer output;
		
		output = p.collectNumber(Parser.LEFT_PARENTHESIS, Parser.COMMA, false);
		
		if ( output != null ){
			
			gridRows = output ;			
			this.optionalGridGaps();						
			}			
		}
	
	
	/**
	 * Determines the number of columns in a Grid Layout and the optional horizontal and
	 * vertical gaps
	 */
	private void optionalGridGaps(){
		
		boolean stop = false;
		String output = "";
		char letter;
		

		if (p.getCharacter() == Parser.COMMA){
			p.advance();
		}
		
		if ( p.getCharacter() == Parser.SPACE || Character.isDigit( p.getCharacter() )){
						
			output += p.getCharacter();
			output = output.trim();
			
			
			while ( !stop && p.getStatus() == Parser.state.PARSE ){
				
				letter = p.advance();
				
				
				if ( letter == Parser.COMMA){
					
					if 		( gridColumns 		== null ) { gridColumns 	  = Integer.parseInt(output); }
					else if ( gridHorizontalGap == null ) { gridHorizontalGap = Integer.parseInt(output); }
					else 	{ p.errorMessage("\")\" expected. \"" + letter + "\" found."); }
					
					output = "";
					
					// eliminate additional space character in front of numbers
					if ( p.lookahead() == Parser.SPACE) {
						p.advance();
					}				
					
				}
				else if ( letter == Parser.RIGHT_PARENTHESIS){
					
					stop = true; 
					
					if 		( gridColumns 		== null ) { gridColumns 	= Integer.parseInt(output); }
					else if ( gridHorizontalGap != null ) { gridVerticalGap = Integer.parseInt(output); }
					else    { p.errorMessage("\",\" expected. \"" + letter + "\" found."); }
					
					this.confirmColon();					
					
				}
				else {
					output += letter;
				}
			}			
		}
	}
	
	/**
	 * Verifies that Grid Layout will contain horizontal or vertical gaps
	 * @return	boolean (true if gaps present)
	 */
	private boolean hasGaps(){
		if ( gridHorizontalGap != null && gridVerticalGap != null){
			return true;
		}
		return false;
	}
	
	
	/**
	 * Displays Layout information in console
	 */
	@Override
	public String toString(){
		
		String output = "";
		
		output += String.format("---Layout Class---\n");
		output += String.format("%s Layout\n", layoutType);
		
		
		if ( layoutType == GRID){
				output += String.format("  %d Rows\n"   , gridRows);
				output += String.format("  %d Columns\n", gridColumns);
			
			if ( hasGaps() ){
				output += String.format("  %d Horizontal Gap\n", gridHorizontalGap);
				output += String.format("  %d Vertical Gap\n"  , gridVerticalGap);
			}
		}		
		return output;		
	}
}