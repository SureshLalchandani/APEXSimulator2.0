package com.cao.apex.pipeline;

import com.cao.apex.models.ROB;
import com.cao.apex.models.SharedData;
import com.cao.apex.utility.Constants;

public class Writeback extends Stage {
	
	//public IForwardingDataBus dataBus;

	public Writeback(APEXPipelineHandler handler) {
		super(handler);
	}

	@Override
	public boolean render() {
		
		//if(instruction.isMemoryOperation()) return true;
		
		if(instruction.getOpcode().equalsIgnoreCase(Constants.BAL)) {
			
			SharedData.getInstance().updateSplRegister(Constants.splRegX,  instruction.getSpecialRegisterValue());
			return true;
		}
		
		SharedData.getInstance().updateRegister(
				instruction.getDest(), instruction.getDestResult());
		
		//if(instruction.dependentInstCount == 0)
		//SharedData.getInstance().markAsFree(instruction.getDest());
		
		
		
		if(dataBus != null)
			dataBus.writeResult(instruction);
		
		ROB.getInstance().writeResult(instruction);
		
		return true;
		

	}
	
	@Override
	public boolean canMoveToNext() {
		// TODO Auto-generated method stub
		return true;
	}

}
