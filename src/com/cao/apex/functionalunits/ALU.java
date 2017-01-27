package com.cao.apex.functionalunits;

import com.cao.apex.utility.Constants;

@SuppressWarnings("unused")

/**
 * Functional Unit that handles arithmetic instructions
 * @author sureshlalchandani
 *
 */
public class ALU extends FunctionalUnit {

	@Override
	public void run() {
		// TODO Auto-generated method stub
		if(instruction.getOpcode().equalsIgnoreCase(Constants.ADD)) {
			add();
		} else if(instruction.getOpcode().equalsIgnoreCase(Constants.SUB)) {
			sub();
		} else if(instruction.getOpcode().equalsIgnoreCase(Constants.MUL)) {
			mul();
		} else if(instruction.getOpcode().equalsIgnoreCase(Constants.LOAD)) {
			instruction.calculateMemoryAddress();
		} else if(instruction.getOpcode().equalsIgnoreCase(Constants.STORE)) {
			instruction.calculateMemoryAddress();
		} else if(instruction.getOpcode().equalsIgnoreCase(Constants.MOVC)) {
			int valToAdd = instruction.getLiteralValue() != Constants.INVALID ? instruction.getLiteralValue() : instruction.getSrc1Data();
			instruction.setDestResult(valToAdd + 0);
		}
	}


	/**
	 * Method to add
	 */
	private void add() {

		if(instruction.getSrc2() == -1) {
			instruction.setDestResult(instruction.getSrc1Data() + instruction.getLiteralValue());
		} else {
			instruction.setDestResult(instruction.getSrc1Data() + instruction.getSrc2Data());
		}
	}

	/**
	 * Method to Subtract
	 */
	private void sub()
	{
		if(instruction.getSrc2() == -1) {
			instruction.setDestResult(instruction.getSrc1Data() - instruction.getLiteralValue());
		} else {
			instruction.setDestResult(instruction.getSrc1Data() - instruction.getSrc2Data());
		}
	}

	/**
	 * Method to multiplication
	 */
	private void mul() {
		if(instruction.getSrc2() == -1) {
			instruction.setDestResult(instruction.getSrc1Data() * instruction.getLiteralValue());
		} else {
			instruction.setDestResult(instruction.getSrc1Data() * instruction.getSrc2Data());
		}
	}

	/**
	 * Method to division
	 */
	private void div() {
		if(instruction.getSrc2() == -1) {
			instruction.setDestResult(instruction.getSrc1Data() / instruction.getLiteralValue());
		} else {
			instruction.setDestResult(instruction.getSrc1Data() / instruction.getSrc2Data());
		}
	}
}
