package com.cao.apex.models;

import java.util.ArrayList;
import java.util.List;

import com.cao.apex.functionalunits.Branch;
import com.cao.apex.functionalunits.FunctionalUnit.FunctionalUnitType;
import com.cao.apex.utility.Constants;


public class IQueue extends ArrayList<IQueueEntry> {

	int size = 10;
	public boolean isAluFUAvailable = true;
	public boolean isMulFUAvailable = true;
	public boolean isBranchFUAvailable = true;
	public boolean isLSFUAvailable = true;

	int lastIndex = 0;

	private static final long serialVersionUID = 1L;

	private static IQueue _sharedInstance;

	public static IQueue getIsntance() {

		if(_sharedInstance == null)
			_sharedInstance = newInstance();

		return _sharedInstance;
	}

	public static IQueue newInstance() {
		return _sharedInstance = new IQueue();
	}

	public boolean addInstruction(IQueueEntry entry) {
		if(size == size()) return false;
		this.add(entry);
		return true;
	}

	public boolean addInstruction(Instruction instruction) {
		if(size == size()) return false;

		IQueueEntry entry = new IQueueEntry(instruction, ++lastIndex);
		return addInstruction(entry);
	}

	public void setSize(int size) {
		this.size = size;
	}

	public int getSize() {
		return size;
	}

	public List<IQueueEntry> nextReadyEntries() {

		List<IQueueEntry> entriestoBeIssued = new ArrayList<>();

		boolean aluTaken = false;
		boolean brTaken = false;
		boolean lsTaken = false;
		boolean mulTaken = false;

		IQueueEntry lsEntry = null;

		//Tomake LS in order
		for(IQueueEntry entry : this) {

			if(entry.functionalUnitType != FunctionalUnitType.LSFU) continue;

			if(lsEntry == null) {
				lsEntry = entry;
				continue;
			}


			if(lsEntry.index > entry.index) {
				lsEntry = entry;
			}
		}

		for(IQueueEntry entry : this) {

			if(entry.instruction.getDependencies() != null && entry.instruction.getDependencies().size() > 0) continue; 

			if(entry.src1ReadyBit == 1 && entry.src2ReadyBit == 1) {

				switch (entry.functionalUnitType) {
				case ALU:
					if(!isAluFUAvailable) break;
					
					if(!aluTaken) {
						aluTaken = true;
						entriestoBeIssued.add(entry);
					}
					break;
				case LSFU:
					continue;
				case BRANCH:
					if(!isBranchFUAvailable) break;
					
					if(!brTaken) {
						brTaken = true;
						entriestoBeIssued.add(entry);
					}
					break;
				case MUL:
					if(!isMulFUAvailable) break;
					
					if(!mulTaken) {
						mulTaken = true;
						entriestoBeIssued.add(entry);
					}

					break;
				default:
					break;
				}
			}
		}


		// In order ls entries
		if(isLSFUAvailable && lsEntry != null && !lsTaken && (lsEntry.getInstruction().getDependencies() == null || lsEntry.getInstruction().getDependencies().size() == 0)) {
			lsTaken = true;
			entriestoBeIssued.add(lsEntry);
		}

		return entriestoBeIssued;
	}


	/**
	 * Handle wake-up calls
	 * @param functionalUnitType
	 */
	public void wakeUp(FunctionalUnitType functionalUnitType) {

		switch (functionalUnitType) {
		case ALU:
			isAluFUAvailable = true;
			break;
		case MUL:
			isMulFUAvailable = true;
			break;
		case BRANCH:
			isBranchFUAvailable = true;
			break;
		case LSFU:
			isLSFUAvailable = true;
			break;
		default:
			break;
		}
	}

	/**
	 * Resolve dependencies after getting forwarded data
	 * @param inst
	 */
	public void resolveDependencies(Instruction inst) {
		for(IQueueEntry entry : this) {
			Instruction instruction = entry.getInstruction();

			if(instruction == null) return;
			Dependency dependency = instruction.getDependencyForInstruction(inst);

			if(dependency != null ) {

				if(instruction.getOpcode().equalsIgnoreCase(Constants.STORE) || instruction.getOpcode().equalsIgnoreCase(Constants.LOAD)) {

					Instruction dependInst = dependency.getInstruction();

					if(!inst.equals(dependInst)) return;

					if(instruction.getSrc1() == inst.getDest()) {
						instruction.setSrc1Data(inst.getDestResult());
						entry.src1ReadyBit = 1;
					} else if(instruction.getDest() == inst.getDest()) {
						instruction.setDestResult(inst.getDestResult());
						entry.src2ReadyBit = 1;
					}
					// Handle Forwarding for special register X
				} else if(instruction.getSrc1() != Constants.INVALID && instruction.getSrc1() == instruction.getSrc1() && instruction.getSrc1() == dependency.getReg()) {
					instruction.setSrc1Data(inst.getDestResult());
					entry.src1ReadyBit = 1;
					instruction.setSrc2Data(inst.getDestResult());
					entry.src2ReadyBit = 1;
				} else if(instruction.getOpcode().equalsIgnoreCase(Constants.JUMP) && inst.getOpcode().equalsIgnoreCase(Constants.BAL)) {
					instruction.setDest(inst.getSpecialRegisterIndex());
				}else if(instruction.getOpcode().equalsIgnoreCase(Constants.BNZ) || instruction.getOpcode().equalsIgnoreCase(Constants.BZ)) {
					if(inst.getDestResult() == 6) {
						System.out.println("");
					}
					instruction.setzFlag(inst.getDestResult());
				} else if(instruction.getSrc1() == dependency.getReg()) {
					instruction.setSrc1Data(inst.getDestResult());
					entry.src1ReadyBit = 1;
				} else {
					instruction.setSrc2Data(inst.getDestResult());
					entry.src2ReadyBit = 1;
				}

				instruction.getDependencies().remove(dependency);
			}
		}
	}

	public boolean containsBrancInst() {
		for(IQueueEntry entry : this) {

			if(entry.functionalUnitType == FunctionalUnitType.BRANCH) return true;;
		}
		
		return false;
	}

	public void flushIQOnBranchTaken(Instruction branchInst) {
		
		List<IQueueEntry> iterate = new ArrayList<IQueueEntry>(this);
		
		for(IQueueEntry entry : iterate) {
			if(entry.getInstruction().dispatchIndex > branchInst.dispatchIndex) {
				this.remove(entry);
			}
		}
	}
	
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();

		for(IQueueEntry entry : this) {
			builder.append(entry.toString() + "\n");
		}
		return builder.toString();
	}


}