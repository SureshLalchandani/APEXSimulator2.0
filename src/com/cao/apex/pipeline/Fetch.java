package com.cao.apex.pipeline;

import com.cao.apex.models.ICache;
import com.cao.apex.models.SharedData;
import com.cao.apex.utility.Parser;

public class Fetch extends Stage {

	public Fetch(APEXPipelineHandler handler) {
		super(handler);
	}

	@Override
	public boolean render() {

		SharedData data = SharedData.getInstance();

		int pc = data.getPc();

		ICache cache = ICache.getInstance();
		
		instruction = cache.getInstructionAtLocation(pc);
		
		if(instruction == null) {
			return false;
		}

		// This updates instruction parameters
		Parser parser = new Parser(instruction);
		parser.parse();
		
		return true;
	}	
	
}
