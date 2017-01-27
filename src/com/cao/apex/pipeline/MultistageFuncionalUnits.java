package com.cao.apex.pipeline;

import java.util.ArrayList;

import com.cao.apex.buses.IForwardingDataBus;
import com.cao.apex.functionalunits.FunctionalUnit;
import com.cao.apex.functionalunits.FunctionalUnit.FunctionalUnitType;
import com.cao.apex.models.IQueueEntry;
import com.cao.apex.models.Instruction;

public abstract class MultistageFuncionalUnits extends ArrayList<FunctionalUnit> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	Instruction instructionToBeEntered = null;
	public  void passInstruction(Instruction instruction) {
		this.instructionToBeEntered = instruction;
	}
	public abstract void proceed() throws CloneNotSupportedException;
	
	public Stage nextStage;
	
	public IForwardingDataBus dataBus;
	
	public abstract boolean isAvailable();
	
	public boolean isIDLE() {
		boolean toReturn = true;
		
		for(FunctionalUnit unit : this) {
			toReturn = unit.getInstruction() != null;
			
			if(toReturn) break;
		}
		
		return !toReturn;
	}
	
	public boolean containsBrancInst() {
		for(FunctionalUnit entry : this) {
			
			if(entry.getInstruction() == null) continue;

			if(entry.getInstruction().isBranch()) return true;;
		}
		
		return false;
	}
	
	public boolean flushOnBranchTaken(Instruction instruction) {
		
		for(FunctionalUnit entry : this) {
			
			if(entry.getInstruction() == null) continue;

			if(entry.getInstruction().dispatchIndex > instruction.dispatchIndex) {
				entry.setInstruction(null);
			}
		}
		
		return false;
	}
}
