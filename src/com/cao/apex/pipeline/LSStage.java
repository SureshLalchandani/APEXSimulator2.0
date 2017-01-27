package com.cao.apex.pipeline;

import com.cao.apex.functionalunits.LSFU;
import com.cao.apex.models.IQueue;
import com.cao.apex.models.Instruction;
import com.cao.apex.models.Instruction.MemoryDirection;
import com.cao.apex.models.ROB;

public class LSStage extends MultistageFuncionalUnits {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public LSStage() {
		for(int i =0; i < 2; i++) {
			LSFU lsfu = new LSFU(i+1);
			add(lsfu);
		}
	}

	@Override
	public void proceed() throws CloneNotSupportedException {
		LSFU lsfu = (LSFU) get(0);
		LSFU lsfu2 = (LSFU) get(1);

		Instruction instlsfu1 = lsfu.getInstruction();
		Instruction instlsfu2 = lsfu2.getInstruction();


		if(instlsfu2 != null && ROB.getInstance().getHeadRef().getInstruction().getPc() == lsfu2.getInstruction().getPc()) {
			// Move instruction to memory stage
			nextStage.instruction = instlsfu2;
			nextStage.render();

			lsfu2.setInstruction(null);
		} else {
			nextStage.instruction = null;
		}


		if(instlsfu1 != null) {
			if(lsfu2.getInstruction() == null) {
				lsfu2.setInstruction(instlsfu1.clone());

				//Free the LSFU 1
				lsfu.setInstruction(null);

			}
		}

		if(instlsfu1 == null) {
			if(instructionToBeEntered != null ) {

				lsfu.setInstruction(instructionToBeEntered.clone());
				lsfu.run();
				instructionToBeEntered = null;
			}				
		}
		
		
		if(lsfu.getInstruction() == null || lsfu2.getInstruction() == null || 
				(lsfu2.getInstruction() != null && ROB.getInstance().getHeadRef().getInstruction().getPc() == lsfu2.getInstruction().getPc())) {
			IQueue.getIsntance().isLSFUAvailable = true;
		} else {
			IQueue.getIsntance().isLSFUAvailable = false;
		}

	}

	@Override
	public boolean isAvailable() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String toString() {
		LSFU lsfu = (LSFU) get(0);
		LSFU lsfu2 = (LSFU) get(1);

		StringBuilder builder = new StringBuilder();

		builder.append(" LSFU 1 - " + lsfu.getInstruction() + "\n");
		builder.append(" LSFU 2 - " + lsfu2.getInstruction() + "\n");

		return builder.toString();
	}

}
