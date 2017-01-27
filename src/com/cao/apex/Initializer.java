package com.cao.apex;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import com.cao.apex.models.ICache;
import com.cao.apex.models.Instruction;
import com.cao.apex.models.RRAT;
import com.cao.apex.models.RenameTable;
import com.cao.apex.models.SharedData;

/**
 * Class that reads input file and extract instructions form it. 
 * @author sureshlalchandani
 *
 */
public class Initializer {

	/**
	 * Reads the input text file and iterate through all the instructions and prepare ICache.
	 * @param file
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public void readInstructionsFromFile(File file) throws FileNotFoundException, IOException {
		//File file = new File("/Users/sureshlalchandani/Desktop/SimpleTestInput1.txt");
		ICache cache = ICache.newInstance();
		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
			String line;
			int index = 4000;
			while ((line = br.readLine()) != null) {

				if(line.trim().length() == 0) continue;

				Instruction instruction = new Instruction();
				instruction.setHighLevelInstruction(line);
				instruction.setPc(index);


				cache.put(index, instruction);

				index += 4;
			}
		}

		SharedData.getInstance().setPc(4000);
		RenameTable.getInstance();
		RRAT.getInstance();
	}
	
	
	

}
