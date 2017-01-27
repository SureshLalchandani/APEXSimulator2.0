package com.cao.apex.pipeline;

import java.util.ArrayList;
import java.util.List;

import com.cao.apex.functionalunits.ALU;
import com.cao.apex.functionalunits.Branch;
import com.cao.apex.functionalunits.FunctionalUnit.FunctionalUnitType;
import com.cao.apex.functionalunits.LSFU;
import com.cao.apex.functionalunits.Multiplication;
import com.cao.apex.models.Instruction;
import com.cao.apex.utility.Constants;

public class Execute extends Stage {
	
	List<ALU> aluPipeline;
	Branch branchPipeLine;
	List<LSFU> lsfuPipeline;
	
	Multiplication mulFu;

	private int stage = 0;

	public Execute(APEXPipelineHandler handler, int stage) {
		super(handler);
		requiredCycle = 2;
		this.stage = stage;


		// Init Functional Units and make pipeline
		mulFu = new Multiplication();
		branchPipeLine = new Branch();
		initAluPipeline();
	}
	
	public Execute(APEXPipelineHandler handler) {
		super(handler);

		// Init Functional Units and make pipeline
		mulFu = new Multiplication();
		branchPipeLine = new Branch();
		initAluPipeline();
	}
	
	private void initAluPipeline() {
		aluPipeline = new ArrayList<>();
		for(int i = 0; i < 2; i++) {
			ALU alu = new ALU();
			aluPipeline.add(alu);
		}
	}


	@Override
	public void updateInstruction(Instruction instruction) {
		super.updateInstruction(instruction);
		//cycleCount = 0;
	}
	
	@Override
	public boolean render() {

		//cycleCount += 1;

		if(stage == 2 && !instruction.isBranch()) {
			ALU alu = new ALU();
			alu.setInstruction(instruction);
			alu.run();

		} else if(stage == 1) {
			Branch branch = new Branch();
			branch.setStage(this);
			branch.setInstruction(instruction);
			branch.setDataBus(handler);
			branch.run();
		}
		
		

//		if(!instruction.isMemoryOperation())
//			if(stage == 2 && dataBus != null)
//				dataBus.writeResult(instruction);

		return true;
	}

//	@Override
//	public boolean render() {
//
//		//cycleCount += 1;
//
//		if(stage == 2 && !instruction.isBranch()) {
//			ALU alu = new ALU();
//			alu.setInstruction(instruction);
//			alu.run();
//
//		} else if(stage == 1) {
//			Branch branch = new Branch();
//			branch.setStage(this);
//			branch.setInstruction(instruction);
//			branch.setDataBus(handler);
//			branch.run();
//		}
//
////		if(!instruction.isMemoryOperation())
////			if(stage == 2 && dataBus != null)
////				dataBus.writeResult(instruction);
//
//		return true;
//	}
	
	public boolean isFunctionalUnitAvailable(FunctionalUnitType unitType)
	{
		switch (unitType) {
		case ALU:
			return aluPipeline.get(0).getInstruction() == null ? true : false;
		case BRANCH:
			return branchPipeLine.getInstruction() == null ? true : false;
		case MUL:
			return mulFu.getInstruction() == null ? true : false;
		case LSFU:
			return lsfuPipeline.get(0).getInstruction() == null ? true : false;
		default:
			return false;
		}
	}
	
	@Override
	public void forwardData() {
		if(instruction != null && !instruction.isMemoryOperation() && stage == 2)
		super.forwardData();
	}
	

	public boolean needMoreCycle() {
		return false;
	}


	@Override
	public boolean canMoveToNext() {
		if(instruction == null) return true;

		// TODO Auto-generated method stub
		return stage == 2 ? true : !instruction.getOpcode().equalsIgnoreCase(Constants.STORE) ? true : ( instruction.getDependencies() == null || instruction.getDependencies().size() == 0) ? true : false;
	}
}
