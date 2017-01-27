package com.cao.apex.pipeline;

import com.cao.apex.functionalunits.ALU;
import com.cao.apex.functionalunits.Multiplication;
import com.cao.apex.models.IQueue;
import com.cao.apex.models.Instruction;

public class MulStage extends MultistageFuncionalUnits {


	int cycleCount = 0;

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public MulStage() {
		Multiplication multiplication = new Multiplication();
		add(multiplication);
	}


	@Override
	public void passInstruction(Instruction instruction) {
		super.passInstruction(instruction);
		IQueue.getIsntance().isMulFUAvailable = false;
	}

	@Override
	public void proceed() throws CloneNotSupportedException {
		// TODO Auto-generated method stub
		Multiplication multiplication = (Multiplication) get(0);

		Instruction insturction = multiplication.getInstruction();

		if(insturction != null) {
			cycleCount++;
			
//			if(cycleCount < 4) {
//				IQueue.getIsntance().isMulFUAvailable = false;
//			}
			IQueue.getIsntance().isMulFUAvailable = false;
			
			if(cycleCount == 4) {
				multiplication.run();
				//Forwarding
				if(dataBus != null) {
					dataBus.writeResult(insturction);
				}

				IQueue.getIsntance().isMulFUAvailable = true;
				
			} else if(cycleCount > 4) {
				nextStage.instruction = insturction;
				nextStage.render();

				multiplication.setInstruction(null);
				cycleCount = 0;

				if(instructionToBeEntered != null) {
					multiplication.setInstruction(instructionToBeEntered.clone());
					instructionToBeEntered = null;
				} 
			}
		} else if(instructionToBeEntered != null) {
			IQueue.getIsntance().isMulFUAvailable = false;
			multiplication.setInstruction(instructionToBeEntered.clone());
			instructionToBeEntered = null;
			cycleCount++;
		} else {
			IQueue.getIsntance().isMulFUAvailable = true;
		}


	}
	@Override
	public boolean isAvailable() {
		// TODO Auto-generated method stub
		return cycleCount > 4;
	}

	@Override
	public String toString() {
		
		StringBuilder builder = new StringBuilder();

		builder.append("\n MUL  - " + get(0).getInstruction() + "\n");
		builder.append(" Writeback - " + nextStage.instruction + "\n");
		
		return builder.toString();  

	}

}
