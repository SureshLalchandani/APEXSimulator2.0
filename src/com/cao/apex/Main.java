package com.cao.apex;

import com.cao.apex.pipeline.APEXPipelineHandler;

public class Main {

	public static void main(String[] args) {
		initialize();
		
		simulate();
		
	}

	private static void initialize() {
//		Initializer initializer = new Initializer();
//		ICache cache = ICache.getInstance();
//		cache.size();
//		try {
//			initializer.readInstructionsFromFile();
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		} finally {
//			cache.size();
//		}
	}
	
	private static void simulate() {
		APEXPipelineHandler apexPipelineHandler = APEXPipelineHandler.getInstance();
		try {
			apexPipelineHandler.simulate(0);
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
	}

}
