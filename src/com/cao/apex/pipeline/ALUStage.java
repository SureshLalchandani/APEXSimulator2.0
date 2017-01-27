package com.cao.apex.pipeline;

import com.cao.apex.functionalunits.ALU;
import com.cao.apex.models.IQueue;
import com.cao.apex.models.Instruction;

public class ALUStage extends MultistageFuncionalUnits {


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public ALUStage() {
		for(int i = 0; i < 2; i++) {
			ALU alu = new ALU();
			add(alu);
		}
	}


	@Override
	public void proceed()  throws CloneNotSupportedException {
		ALU aluStage1 = (ALU) get(0);
		ALU aluStage2 = (ALU) get(1);
		
		Instruction instALU1 = aluStage1.getInstruction();
		Instruction instALU2 = aluStage2.getInstruction();
		
		if(instALU2 != null) {
			nextStage.instruction = instALU2;
			nextStage.render();
			
			aluStage2.setInstruction(null);
		} else {
			nextStage.instruction = null;
		}
		
		if(instALU1 != null) {
			aluStage2.setInstruction(instALU1.clone());
			aluStage1.setInstruction(null);
			IQueue.getIsntance().isAluFUAvailable = true;
		}

		if(instructionToBeEntered != null) {
			aluStage1.setInstruction(instructionToBeEntered.clone());
			instructionToBeEntered = null;
		} else {
			IQueue.getIsntance().isAluFUAvailable = true;
			aluStage1.setInstruction(null);
		}

		if(aluStage2.getInstruction() != null) {
			aluStage2.run();
			
			//Forwarding
			if(dataBus != null) {
				dataBus.writeResult(aluStage2.getInstruction());
			}
		}

	}
	
	

	@Override
	public boolean isAvailable() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public String toString() {
		ALU aluStage1 = (ALU) get(0);
		ALU aluStage2 = (ALU) get(1);

		StringBuilder builder = new StringBuilder();

		builder.append("\n ALU 1 - " + aluStage1.getInstruction() + "\n");
		builder.append(" ALU 2 - " + aluStage2.getInstruction() + "\n");
		builder.append(" Writeback - " + nextStage.instruction + "\n");

		return builder.toString();
	}

}
