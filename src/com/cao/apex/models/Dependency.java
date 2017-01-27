package com.cao.apex.models;

/**
 * Model class for Dependency
 * @author sureshlalchandani
 *
 */
public class Dependency {
	
	public static enum Types {
		FLOW, OUTPUT, ANTI;
	} 
	
	private Instruction instruction;
	private int reg;
	private Types type;
	private boolean canBeIgnored;
	
	
	public Instruction getInstruction() {
		return instruction;
	}
	public void setInstruction(Instruction instruction) {
		this.instruction = instruction;
	}
	public int getReg() {
		return reg;
	}
	public void setReg(int reg) {
		this.reg = reg;
	}
	public Types getType() {
		return type;
	}
	public void setType(Types type) {
		this.type = type;
	}
	public boolean isCanBeIgnored() {
		return canBeIgnored;
	}
	public void setCanBeIgnored(boolean canBeIgnored) {
		this.canBeIgnored = canBeIgnored;
	}
	
	

}
