package com.cao.apex.functionalunits;

import com.cao.apex.models.SharedData;
import com.cao.apex.utility.Constants;

public class Branch extends FunctionalUnit {

	@Override
	public void run() {
		
		//if(!getStage().canMoveToNext()) return;
		// TODO Auto-generated method stub
		if(instruction.getOpcode().equalsIgnoreCase(Constants.BZ) && instruction.getzFlag() == 0) {
			
			SharedData.getInstance().setPc(
					instruction.getPc() + instruction.getLiteralValue());
			
			if(getDataBus() != null) {
				getDataBus().branchTaken(instruction);
			}
			
		} else if(instruction.getOpcode().equalsIgnoreCase(Constants.BNZ) && instruction.getzFlag() != 0) {

			SharedData.getInstance().setPc(
					instruction.getPc() + instruction.getLiteralValue());
			
			if(getDataBus() != null) {
				getDataBus().branchTaken(instruction);
			}
			
		} else if(instruction.getOpcode().equalsIgnoreCase(Constants.BAL)) {
			
			//SharedData.getInstance().updateSplRegister(Constants.splRegX,  instruction.getPc() + 4);
			
			instruction.setSpecialRegisterIndex(Constants.splRegX);
			instruction.setSpecialRegisterValue(instruction.getPc() + 4);
			
			SharedData.getInstance().setPc(instruction.calculateMemoryAddress());
			
			if(getDataBus() != null) {
				getDataBus().branchTaken(instruction);
			}
			
		} else if(instruction.getOpcode().equalsIgnoreCase(Constants.JUMP)) {
			instruction.calculateMemoryAddress();
			SharedData.getInstance().setPc(instruction.getMemoryAddres());
			
			if(getDataBus() != null) {
				getDataBus().branchTaken(instruction);
			}
			
		} else if(instruction.getOpcode().equalsIgnoreCase(Constants.HALT)) {
			if(getDataBus() != null) {
				getDataBus().haltRequest();
			}
		}
	}
	
	public void calculateOffset() {
		
	}
	
	public boolean shouldTake() {
		return false;
	}
	
	public void jump() {
		
	}
	
	public void updatePC() {
		
	}

}
