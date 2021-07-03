/**
 *
 * Widget class uses the Parser class to parse the file and generate a
 * GUI of its contents. Widget class can be used to create a calculator.
 *
 */
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class Widget {

	private Parser parser;
	private String calculatorDisplay;
	private String calculatorInput;

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
	private double operandOne;
	private double operandTwo;
	private String operation;

	// booleans for indicating an the existence of an operand
	private boolean haveFirstOperand = false;
	private boolean haveSecondOperand = false;

	public Widget(Parser p, JFrame frame) {
		this.parser = p;
		this.callWidget(frame, null);
		calculatorDisplay = "";
		calculatorInput = "";
		operation = "";
	}


	/**
	 * Creates widgets of GUI (button, label, panel, textfield)
	 * @param frame		JFrame that widget is added to
	 * @param panel		JPanel that widget is added to
	 */
	private void callWidget(JFrame frame, JPanel panel) {
		while (parser.getStatus() == Parser.state.PARSE) {
			if (parser.lookahead() == 'B') { this.button(frame, panel); }
			else if (parser.lookahead() == 'G') { }
			else if (parser.lookahead() == 'L') { this.label(frame, panel);  }
			else if (parser.lookahead() == 'P') { this.panel(frame, panel);  }
			else if (parser.lookahead() == 'T') { this.textfield(frame, panel); }
			else if (parser.lookahead() == 'E') { parser.end(); }
			else { parser.errorMessage("Incorrect widget type. \nCharacter found: " + parser.lookahead()); }
		}
	}


	/**
	 * Creates Button with numbers or text.
	 * @param frame
	 * @param panel
	 */
	private void button(JFrame frame, JPanel panel) {

		String buttonName;
		JButton button = null;

		if (parser.keywordMatch(BUTTON)) {
			parser.skipWhiteSpace();
			buttonName = parser.collectString(Parser.QUOTATIONS, Parser.QUOTATIONS);
			button = new JButton(buttonName);

			// If string for button's text is not compatible with the calculator, disable the button
			if (this.checkButtonString(button)) {
				button.addActionListener(a -> { this.changeTextfield(frame, panel, buttonName);
				});
			} else {
//				button.setEnabled(false);
				button.setBackground(Color.white);
			}

			if (buttonName != null) {
				if (frame == null) { panel.add(button); }
				if (panel == null) { frame.add(button); }
			}
		}
	}


	/**
	 *
	 * @param frame
	 * @param panel
	 * @param buttonText
	 */
	private void changeTextfield(JFrame frame, JPanel panel, String buttonText) {

		// calculated result
		double answer;
		// array for components of GUI
		Component[] GUI_components = null;

		if (frame != null) { GUI_components = frame.getComponents(); }
		if (panel != null) { GUI_components = panel.getParent().getComponents(); }

		// search components in the GUI to obtain a reference to the JTextField
		// widget to change its display text
		for (int i = 0; i < GUI_components.length; i++) {
			if (GUI_components[i] instanceof JTextField) {
				JTextField j = (JTextField) GUI_components[i];
				switch (buttonText) {
				// Assign number to first operand when an operator button is pressed
				case ADD:
				case SUBTRACT:
				case MULTIPLY:
				case TIMES:
				case DIVIDE: {
					// convert the letter "x" used as multiplication
					if (buttonText.equals(TIMES)) {
						buttonText = MULTIPLY;
					}
					// store operator if numbers have been entered for an operand
					try{
						if (calculatorInput.equals("")) {
							j.setText("Enter a number please");
						} else {
							operation = buttonText;
						}

						if (!haveFirstOperand) {
							operandOne = Double.parseDouble(calculatorInput);
							calculatorInput = "";
							haveFirstOperand = true;
						} else {
							j.setText(operation);
						}
					} catch (NumberFormatException e) {
						this.resetCalculator();
					}
					break;
				}

				// Assign number to second operand and perform calculation when equal button is pressed
				case EQUAL: {
					if (haveFirstOperand && !haveSecondOperand) {
						operandTwo = Double.parseDouble(calculatorInput);
						// check for division by zero
						if (operation.equals(DIVIDE) && operandTwo == 0) {
							j.setText("Cannot divide by zero");
							this.resetCalculator();
						} else {
							haveSecondOperand = true;
						}
					}

					// perform calculation if two operands have been entered
					if (haveFirstOperand && haveSecondOperand) {
						answer = this.calculate(operandOne, operandTwo, operation);
						// display mathematical operation performed and its result
						calculatorDisplay = String.format("%d %s %d = %d", operandOne, operation, operandTwo, answer);
						j.setText(calculatorDisplay);

						// reset values for new calculation
						this.resetCalculator();
					}
					break;
				}

				// reset calculator values
				case CLEAR:
				case CLEAR_LOWERCASE: {
					this.resetCalculator();
					j.setText("");
					break;
				}

				// collect digits
				default: {
					calculatorInput += buttonText;
					j.setText(calculatorInput);
				}
				}
			}
		}
	}


	/**
	 * Performs calculations on two operands
	 * @param first
	 * @param second
	 * @param operator
	 * @return
	 */
	private double calculate(double first, double second, String operator) {
		if (operator.equals(ADD)) {
			return first + second;
		} else if (operator.equals(SUBTRACT)) {
			return first - second;
		} else if (operator.equals(MULTIPLY)) {
			return first * second;
		} else if (operator.equals(DIVIDE)) {
			return first / second;
		}
		return 0;
	}

	/**
	 * Erases operand values saved in calculator for calculation
	 */
	private void resetCalculator() {
		haveFirstOperand = false;
		haveSecondOperand = false;
		calculatorInput = "";
		calculatorDisplay = "";
	}


	/**
	 * Verifies string entered for button string is a digit to determine if calculator
	 * functions can be used for that specific button
	 * @param jb	JButton created in text file
	 * @return		Boolean (true if button is for calculator)
	 */
	private boolean checkButtonString(JButton jb) {

		boolean allowed = false;
		String str = jb.getText();

		for (int i = 0; i < str.length(); i++) {
			if (Character.isDigit(str.charAt(i))) {
				allowed = true;
				jb.setFont(new Font("Arial", Font.BOLD, 20));
			}
		}

		if (!allowed) {
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
					jb.setFont(new Font("Arial", Font.BOLD, 25));
				}
			}
		}
		return allowed;
	}




	private void group() { }


	/**
	 * Creates JLabel
	 * @param frame
	 * @param panel
	 */
	private void label(JFrame frame, JPanel panel) {

		String labelName;

		if (parser.keywordMatch(LABEL)) {

			parser.skipWhiteSpace();
			labelName = parser.collectString(Parser.QUOTATIONS, Parser.QUOTATIONS);

			if (labelName != null) {
				if (frame == null) { panel.add(new JLabel(labelName)); }
				if (panel == null) { frame.add(new JLabel(labelName)); }
			}
		}
	}


	/**
	 * Creates JPanel, sets Layout type, and adds to JFrame
	 * @param frame
	 * @param panel
	 */
	private void panel(JFrame frame, JPanel panel) {

		if (panel == null) {
			panel = new JPanel();
		}

		if (parser.keywordMatch(PANEL)) {

			Layout panelLayout = new Layout(parser);
			panel.setLayout(panelLayout.getLayoutType());

			while (parser.getStatus() == Parser.state.PARSE) {

				this.callWidget(null, panel);
			}
			frame.add(panel);
		}
	}



	/**
	 * Creates JTextField and adds to frame or panel
	 * @param frame
	 * @param panel
	 */
	private void textfield(JFrame frame, JPanel panel) {

		Integer size;
		JTextField textfield;

		if (parser.keywordMatch(TEXTFIELD)) {

			size = parser.collectNumber(Parser.SPACE, Parser.SEMICOLON, false);

			if (size != null) {

				textfield = new JTextField(size);
				textfield.setEditable(false);
				textfield.setFont(new Font("Arial", Font.PLAIN, 15));

				if (panel == null) { frame.add(textfield); }
				if (frame == null) { panel.add(textfield); }
			}
		}
	}
}
