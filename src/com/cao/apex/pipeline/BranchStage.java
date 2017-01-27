package com.cao.apex.pipeline;

import com.cao.apex.functionalunits.Branch;
import com.cao.apex.models.IQueue;
import com.cao.apex.models.ROB;

public class BranchStage extends MultistageFuncionalUnits {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public BranchStage() {
		Branch branch = new Branch();
		add(branch);
	}

	@Override
	public void proceed() {
		Branch branch = (Branch)get(0);
		//branch.setStage(this);
		branch.setInstruction(instructionToBeEntered);
		instructionToBeEntered = null;
		branch.setDataBus(dataBus);

		if(branch.getInstruction() != null) {
			branch.run();
			//branch.setInstruction(null);

			if(dataBus != null)
				dataBus.writeResult(branch.getInstruction());
		}

		if(branch.getInstruction() != null)
			ROB.getInstance().writeResult(branch.getInstruction());

		IQueue.getIsntance().isBranchFUAvailable = true;
	}

	@Override
	public boolean isAvailable() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public String toString() {
		Branch branch = (Branch) get(0);

		StringBuilder builder = new StringBuilder();

		builder.append(" Branch - " + branch.getInstruction() + "\n");

		return builder.toString();
	}


}
