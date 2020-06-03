


import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;



public class Test {

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
