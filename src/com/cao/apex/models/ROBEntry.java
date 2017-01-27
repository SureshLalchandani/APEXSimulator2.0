package com.cao.apex.models;

public class ROBEntry {

	Instruction instruction;
	int statusBit;
	int exceptionCode;
	int resultValue;
	int addDestReg;

	public ROBEntry() {
		// TODO Auto-generated constructor stub
	}


	public ROBEntry(Instruction instruction) {
		this.instruction = instruction;
		this.statusBit = 0;
		this.resultValue = instruction.getDestResult();
		this.addDestReg = instruction.getDest();
	}


	public Instruction getInstruction() {
		return instruction;
	}
	public void setInstruction(Instruction instruction) {
		this.instruction = instruction;
	}
	public int getStatusBit() {
		return statusBit;
	}
	public void setStatusBit(int statusBit) {
		this.statusBit = statusBit;
	}
	public int getExceptionCode() {
		return exceptionCode;
	}
	public void setExceptionCode(int exceptionCode) {
		this.exceptionCode = exceptionCode;
	}
	public int getResultValue() {
		return resultValue;
	}
	public void setResultValue(int resultValue) {
		this.resultValue = resultValue;
	}
	public int getAddDestReg() {
		return addDestReg;
	}
	public void setAddDestReg(int addDestReg) {
		this.addDestReg = addDestReg;
	}

	@Override
	public String toString() {

		StringBuilder builder = new StringBuilder();

		builder.append(instruction + " Status -" + statusBit + "\n");

		return builder.toString();

	}

}
