package com.cao.apex.functionalunits;

public class LSFU extends FunctionalUnit {
	
	int cycle = 0;
	
	public LSFU(int cycle) {
		this.cycle = cycle;
	}

	@Override
	public void run() {
		if(cycle == 1) {
			instruction.calculateMemoryAddress();
		} else if(cycle == 2) {
			// TLB Lookup
		}
		
	}

}
