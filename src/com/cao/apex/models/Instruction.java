package com.cao.apex.models;

import java.util.ArrayList;
import java.util.List;

import com.cao.apex.pipeline.APEXPipelineHandler.Stages;
import com.cao.apex.utility.Constants;

/**
 * Model class for instruction
 * @author sureshlalchandani
 *
 */
public class Instruction implements Cloneable {

	public static enum MemoryDirection {
		READ,WRITE;
	}

	private String opcode;
	private int src1 = -1;
	private int src2 = -1;
	private int dest = -1;
	private int archDest = -1;
	private int literalValue = -1;
	private int src1Data = -1;
	private int src2Datal = -1;
	private int destResult = -1;
	private int pc = -1;
	private int memoryAddres = -1;
	private MemoryDirection memOp;
	private String highLevelInstruction;
	private List<Dependency> dependencies;
	private Stages stageCompleted;
	private int specialRegisterIndex;
	private int specialRegisterValue;
	private int zFlag = -1;
	public int dependentInstCount = 0;
	public int dispatchIndex;
	
	public void setzFlag(int zFlag) {
		this.zFlag = zFlag;
	}
	
	public int getzFlag() {
		return zFlag;
	}

	public void setSpecialRegisterValue(int specialRegisterValue) {
		this.specialRegisterValue = specialRegisterValue;
	}
	
	public int getSpecialRegisterValue() {
		return specialRegisterValue;
	}
	
	public void setSpecialRegisterIndex(int specialRegisterIndex) {
		this.specialRegisterIndex = specialRegisterIndex;
	}

	public int getSpecialRegisterIndex() {
		return specialRegisterIndex;
	}

	public void setStageCompleted(Stages stageCompleted) {
		this.stageCompleted = stageCompleted;
	}

	public Stages getStageCompleted() {
		return stageCompleted;
	}

	public String getOpcode() {
		return opcode;
	}
	public void setOpcode(String opcode) {
		this.opcode = opcode;
	}
	public int getSrc1() {
		return src1;
	}
	public void setSrc1(int src1) {
		this.src1 = src1;
	}
	public int getSrc2() {
		return src2;
	}
	public void setSrc2(int src2) {
		this.src2 = src2;
	}
	public int getDest() {
		return dest;
	}
	public void setDest(int dest) {
		this.dest = dest;
	}
	public int getLiteralValue() {
		return literalValue;
	}
	public void setLiteralValue(int literalValue) {
		this.literalValue = literalValue;
	}
	public int getSrc1Data() {
		return src1Data;
	}
	public void setSrc1Data(int src1Data) {
		this.src1Data = src1Data;
	}
	public int getSrc2Data() {
		return src2Datal;
	}
	public void setSrc2Data(int src2Datal) {
		this.src2Datal = src2Datal;
	}
	public int getDestResult() {
		return destResult;
	}
	public void setDestResult(int destResult) {
		this.destResult = destResult;
	}
	public int getPc() {
		return pc;
	}
	public void setPc(int pc) {
		this.pc = pc;
	}
	public int getMemoryAddres() {
		return memoryAddres;
	}
	public void setMemoryAddres(int memoryAddres) {
		this.memoryAddres = memoryAddres;
	}
	public void setMemOp(MemoryDirection memOp) {
		this.memOp = memOp;
	}
	public MemoryDirection getMemOp() {
		return memOp;
	}
	public String getHighLevelInstruction() {
		return highLevelInstruction;
	}
	public void setHighLevelInstruction(String highLevelInstruction) {
		this.highLevelInstruction = highLevelInstruction;
	}
	public List<Dependency> getDependencies() {
		return dependencies;
	}
	public void setDependencies(List<Dependency> dependencies) {
		this.dependencies = dependencies;
	}


	public void addDependency(Dependency dependecy) {
		if(this.dependencies == null) {
			this.dependencies = new ArrayList<>();
		}

		if(getDependencyForInstruction(dependecy.getInstruction()) == null)
			this.dependencies.add(dependecy);
	}


	public  boolean containsLiteral() {
		String[] components = this.getHighLevelInstruction().split(" ");

		if(components.length == 3) {
			String lastComp = components[2];

			if(lastComp.contains("#")) {
				return true;
			}


		}

		return false;
	}

	public boolean isMemoryOperation() {



		if(opcode.toUpperCase().equalsIgnoreCase("LOAD") 
				|| opcode.toUpperCase().equalsIgnoreCase("STORE")) {

			memOp = opcode.toUpperCase().equalsIgnoreCase("LOAD") ? MemoryDirection.READ : MemoryDirection.WRITE;

			return true;
		}
		return false;
	}

	public int calculateMemoryAddress() {
		if(src1 != Constants.INVALID)
			 memoryAddres = src1Data + literalValue;
		else if(specialRegisterIndex != Constants.INVALID)
			  memoryAddres = SharedData.getInstance().readSpecialReg(specialRegisterIndex) + literalValue;
		else 
			 memoryAddres = literalValue;
		
		//memoryAddres = (memoryAddres % 4) == 0 ? memoryAddres : (memoryAddres + 4) - (memoryAddres % 4);
		
		return memoryAddres;
	}

	public boolean canDependentProceed() {
		return isMemoryOperation() ? stageCompleted == Stages.MEMORY : 
			stageCompleted == Stages.WRITEBACK;
	}

	public boolean isBranch() {
		return opcode.trim().equalsIgnoreCase("BZ") ||
				opcode.trim().equalsIgnoreCase("BNZ") ||
				opcode.trim().equalsIgnoreCase("JUMP") ||
				opcode.trim().equalsIgnoreCase("BAL") ||
				opcode.trim().equalsIgnoreCase("HALT");
	}

	@Override
	public Instruction clone() throws CloneNotSupportedException {
		Instruction instruction = new Instruction();

		instruction.opcode = opcode;
		instruction.src1 = src1;
		instruction.src2 = src2;
		instruction.dest = dest;
		instruction.literalValue = literalValue;
		instruction.src1Data = src1Data;
		instruction.src2Datal = src2Datal;
		instruction.destResult = destResult;
		instruction.pc = pc;
		instruction.memoryAddres = memoryAddres;
		instruction.memOp = memOp;
		instruction.highLevelInstruction = highLevelInstruction;
		instruction.dependencies = dependencies;
		instruction.stageCompleted = stageCompleted;
		instruction.archDest = archDest;
		instruction.zFlag = zFlag;
		instruction.dependentInstCount = dependentInstCount;
		instruction.dispatchIndex = dispatchIndex;

		return instruction;
	}
	
	public void setArchDest(int archDest) {
		this.archDest = archDest;
	}
	
	public int getArchDest() {
		return archDest;
	}



	@Override
	public String toString() {
		// TODO Auto-generated method stub
//		StringBuilder builder = new StringBuilder();
		
		return highLevelInstruction == null ? "Idle" : highLevelInstruction;
	}

	@Override
	public boolean equals(Object obj) {
		if(obj == null) return false;
		Instruction instruction = (Instruction) obj;
		return this.pc == instruction.pc;
	}

	public Dependency getDependencyForInstruction(Instruction instruction) {

		if(getDependencies() == null ||  getDependencies().size() == 0) return null;

		for(Dependency dependency : getDependencies()) {
			if(dependency.getInstruction().equals(instruction)) {
				return dependency;
			}
		}

		return null;
	}
	

}
