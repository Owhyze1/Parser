
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class Widget {

	private Parser p;
	private String display;
	private String collector;
	
	// Grammar keywords for widgets
	private static final String BUTTON = "Button";
	private static final String GROUP = "Group";
	private static final String LABEL = "Label";
	private static final String PANEL = "Panel";
	private static final String TEXTFIELD = "Textfield";
	
	// Operators for calculator 
	// The multiplication button and the clear button accept two different characters
	private static final String ADD = "+";
	private static final String SUBTRACT = "-";
	private static final String MULTIPLY = "*";
	private static final String TIMES = "x";
	private static final String DIVIDE = "/";
	private static final String EQUAL = "=";
	private static final String CLEAR = "C";
	private static final String CLEAR_LOWERCASE = "c";
	
	
	// operands
	private int operandOne;
	private int operandTwo;
	private String operation;
	
	// booleans for indicating an the existence of an operand
	private boolean haveFirstOperand = false;
	private boolean haveSecondOperand = false;
	
	
	
	
	public Widget(Parser p, JFrame frame){
		this.p = p;
		this.callWidget(frame, null);
		display = "";
		collector = "";
		operation = "";
	}
	
	
	/**
	 * Creates widgets of GUI (button, label, panel, textfield)
	 * @param frame		JFrame that widget is added to
	 * @param panel		JPanel that widget is added to
	 */
	private void callWidget(JFrame frame, JPanel panel){
		
		
		while ( p.getStatus() == Parser.state.PARSE){
			
			if		( p.lookahead() == 'B') { this.button(frame, panel); } 
			else if ( p.lookahead() == 'G') { }
			else if ( p.lookahead() == 'L') { this.label(frame, panel);  }
			else if ( p.lookahead() == 'P') { this.panel(frame, panel);  } 
			else if ( p.lookahead() == 'T') { this.textfield(frame, panel); }
			else if ( p.lookahead() == 'E') { p.end(); 			}
				
			else    { p.errorMessage("Incorrect widget type. \nCharacter found: " + p.lookahead()); }
		}		
	}
	
	
	private void button(JFrame frame, JPanel panel){
		
		String buttonName;
		JButton button = null;

		if ( p.keywordMatch(BUTTON)){
			
			p.skipWhiteSpace();

			
			buttonName = p.collectString(Parser.QUOTATIONS, Parser.QUOTATIONS);
			button = new JButton(buttonName);

			// If string for button's text is not compatible with the calculator, disable the button
			if (this.checkButtonString( button)){
				
				button.addActionListener( a -> { this.changeTextfield(frame, panel, buttonName);
				});
			}
			else {
//				button.setEnabled(false);
				button.setBackground(Color.white);
			}
			
						
			
			
			
			if ( buttonName != null ){
				if (frame == null ) { panel.add(button); }
				if (panel == null ) { frame.add(button); }
			}
		}				
	}
	
	
	
	private void changeTextfield(JFrame frame, JPanel panel, String buttonText){
		
		// calculated result
		int answer;
		
		// array for components of GUI
		Component[] c = null;
		
		
		if (frame != null){ c = frame.getComponents(); }
		if (panel != null){ c = panel.getParent().getComponents(); }

		// search components in the GUI to obtain a reference to the JTextField
		// widget to change its display text
		for (int i = 0; i < c.length; i++){

			if ( c[i] instanceof JTextField ) {

				JTextField j = (JTextField) c[i];
				
				switch (buttonText)
				{
				// Assign number to first operand when an operator button is pressed
				case ADD:
				case SUBTRACT:
				case MULTIPLY:
				case TIMES:						
				case DIVIDE:
				{
					// convert the letter "x" used as multiplication
					if (buttonText.equals(TIMES) ){
						buttonText = MULTIPLY; 
					}
					
					// store operator if numbers have been entered for an operand
					try{						
						if ( collector.equals("")){
							j.setText("Enter a number please");
						}
						else {
							operation = buttonText;
						}
						
						if ( !haveFirstOperand ){ 
							
							operandOne = Integer.parseInt(collector); 
							
							collector = "";
							haveFirstOperand = true;
						}
						else { 					
							j.setText(operation);
						}
					} catch (NumberFormatException e) {
						this.resetCalculator();
					}
					
					break;
				}
				
				
				// Assign number to second operand and perform calculation when equal button is pressed
				case EQUAL:
				{					
					if ( haveFirstOperand && !haveSecondOperand ){
						
						operandTwo = Integer.parseInt(collector);
						
						// check for division by zero
						if (operation.equals(DIVIDE) && operandTwo == 0){
							
							j.setText("Cannot divide by zero");
							this.resetCalculator();
							
						} else {
							haveSecondOperand = true;
						}
					}

						
					// perform calculation if two operands have been entered
					if ( haveFirstOperand && haveSecondOperand ){

						answer = this.calculate(operandOne, operandTwo, operation);

						// display mathematical operation performed and its result
						display = String.format("%d %s %d = %d", operandOne, operation, operandTwo, answer );
						j.setText(display);
						
						// resst values for new calculation
						this.resetCalculator();						
					}
					break;
				}
				
				// reset calculator values
				case CLEAR:
				case CLEAR_LOWERCASE:
				{
					this.resetCalculator();
					j.setText("");
					break;
				}
				
				// collect digits
				default:
				{				
					collector += buttonText;
					j.setText( collector );			
				}
				}
			}
			
		}			
	}
	
	

	
	private int calculate(int first, int second, String operator){
				
		if 		( operator.equals(ADD) 	    ){ return first + second; }		
		else if ( operator.equals(SUBTRACT) ){ return first - second; }
		else if ( operator.equals(MULTIPLY) ){ return first * second; }
		else if ( operator.equals(DIVIDE)   ){ return first / second; }

		return 0;
	}
	
	
	private void resetCalculator(){
		
		haveFirstOperand = false;
		haveSecondOperand = false;
		collector = "";
		display = "";		
	}

	
	/**
	 * Verifies string entered for button string to determine if calculator
	 * functions can be used for that specific button
	 * @param jb	JButton created in text file
	 * @return		Boolean (true if button is for calculator)
	 */
	private boolean checkButtonString(JButton jb){
		
		boolean allowed = false;
		
		String str = jb.getText();
		
		
		for (int i = 0; i < str.length(); i++){

			if (Character.isDigit( str.charAt(i))){
				allowed = true;
				jb.setFont( new Font("Arial", Font.BOLD, 20));
			}
		}
		
		if ( !allowed ) {
			switch (str)
			{
			case CLEAR:
			case CLEAR_LOWERCASE:						
			case ADD:
			case SUBTRACT:
			case MULTIPLY:
			case TIMES:			
			case DIVIDE:
			case EQUAL:
			{
				allowed = true;
				jb.setBackground(Color.LIGHT_GRAY);
				jb.setForeground(Color.WHITE);
				jb.setFont( new Font("Arial", Font.BOLD, 25));
			}
			}			
		}
		return allowed;
	}
	
	
	
	
	private void group(){
		
	}
	

	
	private void label(JFrame frame, JPanel panel){
		
		String labelName;
		
		if (p.keywordMatch(LABEL)){
			
			p.skipWhiteSpace();
			labelName = p.collectString( Parser.QUOTATIONS, Parser.QUOTATIONS);
			
			if ( labelName != null ){
				if (frame == null ) { panel.add(new JLabel(labelName)); }
				if (panel == null ) { frame.add(new JLabel(labelName)); }
			}
		}		
	}	
	
	
	private void panel(JFrame frame, JPanel panel){
				
		if (panel == null) { 
			panel = new JPanel(); 
		}		

		if (p.keywordMatch(PANEL)){
			
			Layout panelLayout = new Layout(p);
			panel.setLayout(  panelLayout.getLayoutType() );
					
			while ( p.getStatus() == Parser.state.PARSE ){
				
				this.callWidget(null, panel);
			}
			frame.add(panel);
		}		
	}
	

	
	
	private void textfield(JFrame frame, JPanel panel){
		
		Integer size;
		JTextField textfield;
		
		if (p.keywordMatch(TEXTFIELD)){
			
			size = p.collectNumber( Parser.SPACE, Parser.SEMICOLON, false);
			
			if ( size != null ){
				
				textfield = new JTextField(size);
				textfield.setEditable(false);
				textfield.setFont( new Font("Arial", Font.PLAIN, 15));
												
				if (panel == null) { frame.add( textfield ); }
				if (frame == null) { panel.add( textfield ); }
			}
		}		
	}
	
	
	


			
}
