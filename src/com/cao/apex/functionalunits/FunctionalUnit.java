package com.cao.apex.functionalunits;

import com.cao.apex.buses.IForwardingDataBus;
import com.cao.apex.models.Instruction;
import com.cao.apex.pipeline.Stage;
import com.cao.apex.utility.Constants;

public abstract class FunctionalUnit {
	
	public enum FunctionalUnitType {
		ALU,
		BRANCH,
		MUL,
		LSFU;
	}
	
	
	protected Instruction instruction;
	private IForwardingDataBus dataBus;
	private Stage stage;
	
	public FunctionalUnitType getFunctionalUnitType() {
		if(instruction.getOpcode().equalsIgnoreCase(Constants.ADD) ||
				instruction.getOpcode().equalsIgnoreCase(Constants.SUB)) {
			return FunctionalUnitType.ALU;
 		} else if(instruction.getOpcode().equalsIgnoreCase(Constants.MUL)) {
 			return FunctionalUnitType.MUL;
 		} if(instruction.getOpcode().equalsIgnoreCase(Constants.BAL) ||
 				instruction.getOpcode().equalsIgnoreCase(Constants.BNZ) ||
 				instruction.getOpcode().equalsIgnoreCase(Constants.JUMP) ||
 				instruction.getOpcode().equalsIgnoreCase(Constants.BZ)) {
 			return FunctionalUnitType.BRANCH;
 		}
 		
 		return FunctionalUnitType.ALU;
 	}
	
	public void setInstruction(Instruction instruction) {
		this.instruction = instruction;
	}
	
	public void setDataBus(IForwardingDataBus dataBus) {
		this.dataBus = dataBus;
	}
	
	public IForwardingDataBus getDataBus() {
		return dataBus;
	}
	
	public Instruction getInstruction() {
		return instruction;
	}
	
	/**
	 * Method to perform appropriate functional unit operation.
	 */
	public abstract void run();
	
	public void setStage(Stage stage) {
		this.stage = stage;
	}
	
	public Stage getStage() {
		return stage;
	}

}
