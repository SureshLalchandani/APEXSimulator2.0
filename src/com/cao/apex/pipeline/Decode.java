package com.cao.apex.pipeline;

import java.util.ArrayList;
import java.util.List;

import com.cao.apex.functionalunits.ALU;
import com.cao.apex.functionalunits.FunctionalUnit;
import com.cao.apex.models.Dependency;
import com.cao.apex.models.IQueue;
import com.cao.apex.models.IQueueEntry;
import com.cao.apex.models.Instruction;
import com.cao.apex.models.RenameTable;
import com.cao.apex.models.SharedData;
import com.cao.apex.utility.Constants;

public class Decode extends Stage {

	public int cycle = 0;

	public Decode(APEXPipelineHandler handler, int cycle) {
		super(handler);
		this.cycle = cycle;
		requiredCycle = 2;
	}

	@Override
	public boolean render() {

		if(instruction == null) return false;
		// TODO Auto-generated method stub
		//cycle ++;

		rename();

		if(instruction.getSrc1Data() == Constants.INVALID)
			instruction.setSrc1Data(
					SharedData.getInstance().readRegisterFileForRegister(instruction.getSrc1()));

		if(!instruction.containsLiteral() && instruction.getSrc2Data() == Constants.INVALID)
			instruction.setSrc2Data(
					SharedData.getInstance().readRegisterFileForRegister(instruction.getSrc2()));


		if(instruction.isMemoryOperation()) {
			instruction.calculateMemoryAddress();
		}

		if(instruction.getOpcode().equalsIgnoreCase(Constants.STORE)) {
			instruction.setDestResult(SharedData.getInstance().readRegisterFileForRegister(instruction.getDest()));
		}

		//resolveDependecies();



		return true;
	}

	@Override
	public void updateInstruction(Instruction instruction) {
		super.updateInstruction(instruction);
		if(instruction != null ) ;//cycle = 0;
	}

	private void rename() {

		if(cycle == 1) {

			if(instruction.getOpcode().equalsIgnoreCase(Constants.STORE)){
				instruction.setDest(RenameTable.getInstance().getMappedPhysicalRegister(instruction.getDest()));
			}

			instruction.setSrc1(RenameTable.getInstance().getMappedPhysicalRegister(instruction.getSrc1()));
			instruction.setSrc2(RenameTable.getInstance().getMappedPhysicalRegister(instruction.getSrc2()));
		}

		if(cycle == 2 && !instruction.isMemoryOperation()) {
			instruction.setArchDest(instruction.getDest());
			instruction.setDest(RenameTable.getInstance().mapWithNextAvailableRegister(instruction.getDest()));
		}
	}



	/**
	 * Check for the dependencies and add references of it.
	 */
	public void addDependecies(MultistageFuncionalUnits stages) {
		// Execution Stage
		if(instruction == null || stages.size() == 0) return;

		for(FunctionalUnit fu : stages) {

			if(fu.getInstruction() == null) continue;

			if(instruction.getOpcode().equalsIgnoreCase(Constants.LOAD) || instruction.getOpcode().equalsIgnoreCase(Constants.STORE)) {

				if(fu.getInstruction() != null && 
						( (instruction.getOpcode().equalsIgnoreCase(Constants.STORE) && 
								instruction.getDest() == fu.getInstruction().getDest() && 
								instruction.getDest() != -1) ||
								( (fu.getInstruction().getDest() == instruction.getSrc1() && instruction.getSrc1() != -1 ) ||
										(fu.getInstruction().getDest() == instruction.getSrc2() && instruction.getSrc2() != -1) ) ) ) {
					Dependency dependency = new Dependency();

					dependency.setInstruction(fu.getInstruction());
					dependency.setType(Dependency.Types.FLOW);
					dependency.setCanBeIgnored(false);
					dependency.setReg(fu.getInstruction().getDest());

					instruction.addDependency(dependency);
					fu.getInstruction().dependentInstCount += 1;
				}
			} else if(fu.getInstruction() != null && ((fu.getInstruction().getDest() == instruction.getSrc1() && instruction.getSrc1() != -1)||
					(fu.getInstruction().getDest() == instruction.getSrc2() && instruction.getSrc2() != -1))) {
				Dependency dependency = new Dependency();

				dependency.setInstruction(fu.getInstruction());
				dependency.setType(Dependency.Types.FLOW);
				dependency.setCanBeIgnored(false);
				dependency.setReg(fu.getInstruction().getDest());

				fu.getInstruction().dependentInstCount += 1;
				instruction.addDependency(dependency);
			}


			if(instruction.getOpcode().equalsIgnoreCase(Constants.BNZ) || instruction.getOpcode().equalsIgnoreCase(Constants.BZ)) {

				if(instruction.getPc() != fu.getInstruction().getPc() + 4) continue;

				Dependency dependency = new Dependency();
				dependency.setInstruction(fu.getInstruction());
				dependency.setType(Dependency.Types.FLOW);
				dependency.setCanBeIgnored(false);
				dependency.setReg(fu.getInstruction().getDest());
				instruction.addDependency(dependency);
				fu.getInstruction().dependentInstCount += 1;
				return;		
			} 
		}
	}

	/**
	 * Check for the dependencies and add references of it.
	 */
	public void addDependecies(Stage stage) {
		// Execution Stage
		if(instruction == null || stage == null) return;


		if(instruction.getOpcode().equalsIgnoreCase(Constants.LOAD) || instruction.getOpcode().equalsIgnoreCase(Constants.STORE)) {

			if(stage.instruction != null && 
					( (instruction.getOpcode().equalsIgnoreCase(Constants.STORE) && 
							instruction.getDest() == stage.instruction.getDest() && 
							instruction.getDest() != -1) ||
							( (stage.instruction.getDest() == instruction.getSrc1() && instruction.getSrc1() != -1 ) ||
									(stage.instruction.getDest() == instruction.getSrc2() && instruction.getSrc2() != -1) ) ) ) {
				Dependency dependency = new Dependency();

				dependency.setInstruction(stage.instruction);
				dependency.setType(Dependency.Types.FLOW);
				dependency.setCanBeIgnored(false);
				dependency.setReg(stage.instruction.getDest());

				instruction.addDependency(dependency);
				stage.instruction.dependentInstCount += 1;
			}
		} else if(stage.instruction != null && ((stage.instruction.getDest() == instruction.getSrc1() && instruction.getSrc1() != -1)||
				(stage.instruction.getDest() == instruction.getSrc2() && instruction.getSrc2() != -1))) {
			Dependency dependency = new Dependency();

			dependency.setInstruction(stage.instruction);
			dependency.setType(Dependency.Types.FLOW);
			dependency.setCanBeIgnored(false);
			dependency.setReg(stage.instruction.getDest());

			stage.instruction.dependentInstCount += 1;
			instruction.addDependency(dependency);
		}


		if(instruction.getOpcode().equalsIgnoreCase(Constants.BNZ) || instruction.getOpcode().equalsIgnoreCase(Constants.BZ)) {

			if(instruction.getPc() != stage.instruction.getPc() + 4) return;

			Dependency dependency = new Dependency();
			dependency.setInstruction(stage.instruction);
			dependency.setType(Dependency.Types.FLOW);
			dependency.setCanBeIgnored(false);
			dependency.setReg(stage.instruction.getDest());
			instruction.addDependency(dependency);
			stage.instruction.dependentInstCount += 1;
			return;		
		} 
	}

	/**
	 * Check for the dependencies and add references of it.
	 */
	public void addDependecies(IQueue stages) {
		// Execution Stage
		if(instruction == null || stages.size() == 0) return;

		for(IQueueEntry fu : stages) {

			if(fu.getInstruction() == null) continue;

			if(instruction.getOpcode().equalsIgnoreCase(Constants.LOAD) || instruction.getOpcode().equalsIgnoreCase(Constants.STORE)) {

				if(fu.getInstruction() != null && ((instruction.getOpcode().equalsIgnoreCase(Constants.STORE) && 
						instruction.getDest() == fu.getInstruction().getDest() && instruction.getDest() != -1) ||
						((fu.getInstruction().getDest() == instruction.getSrc1() && instruction.getSrc1() != -1) ||
								(fu.getInstruction().getDest() == instruction.getSrc2() && instruction.getSrc2() != -1)))) {
					Dependency dependency = new Dependency();

					dependency.setInstruction(fu.getInstruction());
					dependency.setType(Dependency.Types.FLOW);
					dependency.setCanBeIgnored(false);
					dependency.setReg(fu.getInstruction().getDest());

					instruction.addDependency(dependency);
					fu.getInstruction().dependentInstCount += 1;

				}
			} else if(fu.getInstruction() != null && ((instruction.getSrc1() != -1 &&  fu.getInstruction().getDest() == instruction.getSrc1()) ||
					(instruction.getSrc2() != -1 && fu.getInstruction().getDest() == instruction.getSrc2()))) {
				Dependency dependency = new Dependency();

				dependency.setInstruction(fu.getInstruction());
				dependency.setType(Dependency.Types.FLOW);
				dependency.setCanBeIgnored(false);
				dependency.setReg(fu.getInstruction().getDest());

				instruction.addDependency(dependency);
				fu.getInstruction().dependentInstCount += 1;
			}


			if(instruction.getOpcode().equalsIgnoreCase(Constants.BNZ) || instruction.getOpcode().equalsIgnoreCase(Constants.BZ)) {

				if(instruction.getPc() != fu.getInstruction().getPc() + 4) continue;

				Dependency dependency = new Dependency();
				dependency.setInstruction(fu.getInstruction());
				dependency.setType(Dependency.Types.FLOW);
				dependency.setCanBeIgnored(false);
				dependency.setReg(fu.getInstruction().getDest());
				instruction.addDependency(dependency);
				fu.getInstruction().dependentInstCount += 1;
				return;		
			} 
		}
	}

	public void removeFalseDependecies() {

		if(instruction == null || (instruction.getDependencies() == null || instruction.getDependencies().size() == 0)) return;
		
		Dependency prevDependency = null;
		
		List<Dependency> toRemove = new ArrayList<>(instruction.getDependencies());

		for(Dependency dependency : toRemove) {

			if(prevDependency == null) {
				prevDependency = dependency;
				continue;
			}

			if(prevDependency.getReg() == dependency.getReg()) {
				int diffPrev = instruction.getPc() - prevDependency.getInstruction().getPc();
				int diffCur = instruction.getPc() - dependency.getInstruction().getPc();

				if(diffPrev > diffCur) {
					instruction.getDependencies().remove(prevDependency);
				} else {
					instruction.getDependencies().remove(dependency);
				}
			}
			
			prevDependency = dependency;
		}
	}


	//	@Override
	//	public boolean canMoveToNext() {
	//		// TODO If the instruction is store send to first ALU Stage
	//		if(instruction == null) return true; 
	//		return handleStore() ||  ((instruction.getDependencies() == null || instruction.getDependencies().size() == 0)
	//				&& super.canMoveToNext());
	//	}

	@Override
	public boolean canMoveToNext() {
		return IQueue.getIsntance().size() < 10 && SharedData.getInstance().getNextAvailableRegister() != -1;
	}


	//	private boolean handleStore() {
	//		if(instruction.getOpcode().equalsIgnoreCase(Constants.STORE)) {
	//			
	//			return instruction.getSrc1Data() != Constants.INVALID;
	//			
	//		}
	//		return false;
	//	}

}
