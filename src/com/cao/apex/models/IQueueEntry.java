package com.cao.apex.models;

import com.cao.apex.functionalunits.FunctionalUnit.FunctionalUnitType;
import com.cao.apex.utility.Constants;

public class IQueueEntry {

	FunctionalUnitType functionalUnitType;
	int literal;
	int src1Tag;
	int src1Value;
	int src1ReadyBit = 0;
	int src2Tag;
	int src2Value;
	int src2ReadyBit = 0;
	int statusBit;
	int dest;

	int index;

	Instruction instruction;

	public IQueueEntry() {
		super();
	}

	public IQueueEntry(Instruction instruction, int index) {
		super();
		this.instruction = instruction;
		this.index = index;
		instruction.dispatchIndex = index;
		String opcode = instruction.getOpcode();

		if(opcode.equalsIgnoreCase(Constants.MOVC) || 
				opcode.equalsIgnoreCase(Constants.MOV) || 
				opcode.equalsIgnoreCase(Constants.ADD) || 
				opcode.equalsIgnoreCase(Constants.SUB) ) {
			functionalUnitType = FunctionalUnitType.ALU;
		} 

		//Reg to Reg Op - 3 Reg
		else if(opcode.equalsIgnoreCase(Constants.MUL))  {
			functionalUnitType = FunctionalUnitType.MUL;
		} 

		// Memory Op - 2 Reg
		else if(opcode.equalsIgnoreCase(Constants.LOAD) ||
				opcode.equalsIgnoreCase(Constants.STORE)) {
			functionalUnitType = FunctionalUnitType.LSFU;
		}

		//Branch Op - 3 Reg
		else if(opcode.equalsIgnoreCase(Constants.BZ) || 
				opcode.equalsIgnoreCase(Constants.BNZ) || 
				opcode.equalsIgnoreCase(Constants.BAL) ||
				opcode.equalsIgnoreCase(Constants.JUMP))  {
			functionalUnitType = FunctionalUnitType.BRANCH;
		}  

		else if(opcode.equalsIgnoreCase(Constants.BAL)) {
			functionalUnitType = FunctionalUnitType.BRANCH;
		}

		else if(opcode.equalsIgnoreCase(Constants.JUMP) || opcode.equalsIgnoreCase(Constants.HALT)) {
			functionalUnitType = FunctionalUnitType.BRANCH;
		}

		resolveOperands();
	}

	private void resolveOperands() {
		src1Tag = instruction.getSrc1();
		src2Tag = instruction.getSrc2();
		dest = instruction.getDest();

		if(instruction.getDependencies() == null || instruction.getDependencies().size() == 0) {
			src1ReadyBit = 1;
			src2ReadyBit = 1;
			src1Value = instruction.getSrc1Data();
			src2Value = instruction.getSrc2Data();
			return;
		}

		for(Dependency dependency : instruction.getDependencies()) {

			if(instruction.getOpcode().equalsIgnoreCase(Constants.STORE) || instruction.getOpcode().equalsIgnoreCase(Constants.LOAD)) {

				Instruction dependInst = dependency.getInstruction();

				if(instruction.getSrc1() == dependInst.getDest()) {
					src1ReadyBit = 0;
				} else if(instruction.getDest() == dependInst.getDest()) {
					src2ReadyBit = 0;
				}
			}

			if(dependency.getReg() == instruction.getSrc1()) {
				src1ReadyBit = 0;
			} else {
				src1ReadyBit = 1;
			}
			if(dependency.getReg() == instruction.getSrc2()) {
				src2ReadyBit = 0;
			} else {
				src2ReadyBit = 1;
			}

			if(src1ReadyBit == 1) {
				src1Value = instruction.getSrc1Data();
			}

			if(src2ReadyBit == 1) {
				src2Value = instruction.getSrc2Data();
			}
		}
	}

	public FunctionalUnitType getFunctionalUnitType() {
		return functionalUnitType;
	}

	public void setFunctionalUnit(FunctionalUnitType functionalUnitType) {
		this.functionalUnitType = functionalUnitType;
	}

	public int getLiteral() {
		return literal;
	}
	public void setLiteral(int literal) {
		this.literal = literal;
	}
	public int getSrc1Tag() {
		return src1Tag;
	}
	public void setSrc1Tag(int src1Tag) {
		this.src1Tag = src1Tag;
	}
	public int getSrc1Value() {
		return src1Value;
	}
	public void setSrc1Value(int src1Value) {
		this.src1Value = src1Value;
	}
	public int getSrc1ReadiBit() {
		return src1ReadyBit;
	}
	public void setSrc1ReadiBit(int src1ReadiBit) {
		this.src1ReadyBit = src1ReadiBit;
	}
	public int getSrc2Tag() {
		return src2Tag;
	}
	public void setSrc2Tag(int src2Tag) {
		this.src2Tag = src2Tag;
	}
	public int getSrc2Value() {
		return src2Value;
	}
	public void setSrc2Value(int src2Value) {
		this.src2Value = src2Value;
	}
	public int getSrc2ReadyBit() {
		return src2ReadyBit;
	}
	public void setSrc2ReadyBit(int src2ReadyBit) {
		this.src2ReadyBit = src2ReadyBit;
	}
	public int getStatusBit() {
		return statusBit;
	}
	public void setStatusBit(int statusBit) {
		this.statusBit = statusBit;
	}
	public int getDest() {
		return dest;
	}
	public void setDest(int dest) {
		this.dest = dest;
	}

	public void setInstruction(Instruction instruction) {
		this.instruction = instruction;
	}

	public Instruction getInstruction() {
		return instruction;
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {
		IQueueEntry iQueue = new IQueueEntry();
		iQueue.functionalUnitType = this.functionalUnitType ;
		iQueue.literal = this.literal;
		iQueue.src1Tag = this.src1Tag;
		iQueue.src1Value = this.src1Value;
		iQueue.src1ReadyBit = this.src1ReadyBit;
		iQueue.src2Tag = this.src2Tag;
		iQueue.src2Value = this.src2Value;
		iQueue.src2ReadyBit = this.src2ReadyBit;
		iQueue.statusBit = this.statusBit;
		iQueue.dest = this.dest;
		iQueue.instruction = this.instruction;
		return iQueue;
	}


	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();

		builder.append("("+index+") - " );
		builder.append(instruction + " \n" );

		return builder.toString();
	}


}
