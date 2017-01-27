package com.cao.apex.pipeline;

import com.cao.apex.buses.IForwardingDataBus;
import com.cao.apex.models.Dependency;
import com.cao.apex.models.Instruction;
import com.cao.apex.utility.Constants;

public abstract class Stage {

	protected int requiredCycle = 1;
	public Instruction instruction;
	public Instruction prevInstruction;
	protected int stageIndex;
	public Stage nextStage;
	public Stage prevStage;
	protected APEXPipelineHandler handler;
	public IForwardingDataBus dataBus;
	
	public Stage(APEXPipelineHandler handler) {
		this.handler = handler;
	}

	/**
	 * Perform the appropriate operation of respective stage
	 * @return
	 */
	public abstract boolean render();

	/**
	 * Check if current stage is allowed to take new instruction
	 * @return
	 */
	public boolean canMoveToNext() {
		return instruction == null || nextStage.canMoveToNext();
	};

	/**
	 * Flush instructions
	 */
	public void flush() {
		instruction = null;
		requiredCycle = 1;
	
	}

	public void updateInstruction(Instruction instruction) {
		this.instruction = instruction;
	}
	
	public Instruction getPrevInstruction() {
		return this.prevInstruction = this.instruction;
	}
	
	/**
	 * Resolve dependencies after getting forwarded data
	 * @param inst
	 */
	public void resolveDependencies(Instruction inst) {
		if(instruction == null) return;
		Dependency dependency = instruction.getDependencyForInstruction(inst);
				
		if(dependency != null ) {

			if(instruction.getOpcode().equalsIgnoreCase(Constants.STORE) || instruction.getOpcode().equalsIgnoreCase(Constants.LOAD)) {

				Instruction dependInst = dependency.getInstruction();

				if(!inst.equals(dependInst)) return;

				if(instruction.getSrc1() == inst.getDest()) {
					instruction.setSrc1Data(inst.getDestResult());
				} else if(instruction.getDest() == inst.getDest()) {
					instruction.setDestResult(inst.getDestResult());
				}
				// Handle Forwarding for special register X
			}else if(instruction.getOpcode().equalsIgnoreCase(Constants.BNZ) || instruction.getOpcode().equalsIgnoreCase(Constants.BZ)) {
				instruction.setzFlag(inst.getDestResult());
			} else if(instruction.getOpcode().equalsIgnoreCase(Constants.JUMP) && inst.getOpcode().equalsIgnoreCase(Constants.BAL)) {
				instruction.setDest(inst.getSpecialRegisterIndex());
			} else if(instruction.getSrc1() == dependency.getReg()) {
				instruction.setSrc1Data(inst.getDestResult());
			} else {
				instruction.setSrc2Data(inst.getDestResult());
			}
			
			dependency.getInstruction().dependentInstCount -= 1;
			instruction.getDependencies().remove(dependency);
		}
	}
	
	public void forwardData() {
		if(dataBus != null)
			dataBus.writeResult(instruction);
	}

}
