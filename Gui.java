import javax.swing.JFrame;

public class Gui {

	private Parser p;
			
	private String windowName;
	private Integer width;
	private Integer height;
	
	
	private static final String WINDOW = "Window";
			
	
	

	public Gui(Parser p){
		this.p = p;			
	}
	
	
	public void buildWindow(){
		
		Layout layout;
		Widget w;
		JFrame frame;
		
		this.name();		
		this.width();
		this.height();
		
		System.out.println(this);
		
		layout = this.layout();
		
		frame = new JFrame();		
		frame.setTitle(windowName);
		frame.setSize(width, height);
		frame.setLayout( layout.getLayoutType() );
		
		w = new Widget(p, frame);
		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
	

	
	/**
	 * Determines name of GUI Window
	 */
	private void name(){
		
		if ( p.keywordMatch(WINDOW)){
			
			if ( p.getStatus() == Parser.state.PARSE ) {

				p.skipWhiteSpace();
				windowName = p.collectString(Parser.QUOTATIONS, Parser.QUOTATIONS);
			}
		}
	}
	
	
	
	
	private void width(){
				
		Integer output;
		
		if ( p.advance() == Parser.LEFT_PARENTHESIS ){
			
			output = p.collectNumber(Parser.LEFT_PARENTHESIS, Parser.COMMA, false);
			
			if ( output != null ){ width = output; }			
		}
	}
	
	
	
	
	private void height(){
		
		boolean stop = false;
		String output = "";
		char letter;
		
		if ( width != null ){
		// The parser allows for a single whitespace character or a digit to come immediately
		// after the comma

			
			if ( p.getCharacter() == Parser.COMMA){ p.advance(); }

			
			if ( p.getCharacter() == Parser.SPACE || Character.isDigit( p.getCharacter() )){
							
				output += p.getCharacter();
				output = output.trim();
				
				
				while ( !stop && p.getStatus() == Parser.state.PARSE ){
					
					letter = p.advance();					
					
					if ( letter == Parser.RIGHT_PARENTHESIS){
						height = Integer.parseInt(output);
						p.advance();
						stop = true;
					}
					else if ( Character.isDigit( letter )) { output += letter; }
					else    { p.errorMessage("Number expected. \"" + letter + "\" found."); }
				}
			}			
		}
	}
	
	
	
	private Layout layout(){
		
		return new Layout(p);
	}
		

	
	
	
	@Override
	public String toString(){
		
		String output = "";
		
		output += String.format("---Gui Class---\n");
		output += String.format("Window Name: %s\n", windowName);
		output += String.format("  %s Width\n" , width);
		output += String.format("  %s Height\n" , height);
		
		return output;
	}	
}