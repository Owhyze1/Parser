/*******************************************************************************
 * 
 * This class opens a file dialog box so that user may choose file for parsing.
 * Parser and Gui classes parse the file and create a GUI of the output.
 * 
 *******************************************************************************/


import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;



public class Run {

	public static void main(String[] args) {
		

		JOptionPane.showMessageDialog(null, "Choose a file to begin parsing.");
		
		
		File file = null; 
		JFileChooser fileChooser = new JFileChooser(".");
		int fileChoice = fileChooser.showOpenDialog(null);
		
		
		if ( fileChoice == JFileChooser.APPROVE_OPTION){
			file = fileChooser.getSelectedFile();
		}
		
		file = new File("TestFile.txt");
		
	
		Scanner scn;
		try {
			scn = new Scanner(file);
			Parser p = new Parser(scn);
			
			Gui g = new Gui(p);
			
			g.buildWindow();

			scn.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}		
	}
}
