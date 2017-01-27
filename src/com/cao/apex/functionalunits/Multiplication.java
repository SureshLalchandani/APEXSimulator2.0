package com.cao.apex.functionalunits;

public class Multiplication extends FunctionalUnit {

	@Override
	public void run() {
		multiply();
	}
	
	/**
	 * Method to mul
	 */
	private void multiply() {

		if(instruction.getSrc2() == -1) {
			instruction.setDestResult(instruction.getSrc1Data() * instruction.getLiteralValue());
		} else {
			instruction.setDestResult(instruction.getSrc1Data() * instruction.getSrc2Data());
		}
	}


}
