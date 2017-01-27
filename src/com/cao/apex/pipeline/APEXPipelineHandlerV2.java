package com.cao.apex.pipeline;

import java.util.Arrays;
import java.util.List;

import com.cao.apex.buses.IForwardingDataBus;
import com.cao.apex.models.IQueue;
import com.cao.apex.models.IQueueEntry;
import com.cao.apex.models.Instruction;
import com.cao.apex.models.ROB;
import com.cao.apex.models.RRAT;
import com.cao.apex.models.RenameTable;
import com.cao.apex.models.SharedData;

/**
 * Main pipeline engine that makes the chain of all stages and make the instructions pass through
 * @author sureshlalchandani
 *
 */
public class APEXPipelineHandlerV2 extends APEXPipelineHandler implements IForwardingDataBus {

	public static enum Stages {
		FETCH, DECODE, EXECUTE, MEMORY, WRITEBACK;
	}

	private Stage fetch;
	private Stage decode1;
	private Stage decode2;

	private ALUStage aluStage;
	private MulStage mulStage;
	private BranchStage brancStage;
	private LSStage lsStage;

	private Stage memory;
	private Stage writeback;

	private Stage writebackALU;
	private Stage writebackMUL;

	public StringBuilder log;

	public Stage getFetch() {
		return fetch;
	}


	public void setFetch(Stage fetch) {
		this.fetch = fetch;
	}


	public Stage getDecode1() {
		return decode1;
	}

	public void setDecode1(Stage decode1) {
		this.decode1 = decode1;
	}

	public Stage getDecode2() {
		return decode2;
	}

	public void setDecode2(Stage decode2) {
		this.decode2 = decode2;
	}


	public Stage getMemory() {
		return memory;
	}


	public void setMemory(Stage memory) {
		this.memory = memory;
	}


	public Stage getWriteback() {
		return writeback;
	}


	public void setWriteback(Stage writeback) {
		this.writeback = writeback;
	}


	public boolean isTerminate() {
		return terminate;
	}


	public void setTerminate(boolean terminate) {
		this.terminate = terminate;
	}

	boolean terminate = false;
	boolean haltRequest = false;

	private static APEXPipelineHandlerV2 _sharedInstance;

	private boolean booleanIsbranchTaken = false;

	/**
	 * Singleton instance
	 * @return
	 */
	public static APEXPipelineHandlerV2 getInstance() {
		if(_sharedInstance == null) {

			_sharedInstance = newInstance();
		}


		return _sharedInstance;
	}

	public static APEXPipelineHandlerV2 newInstance() {
		_sharedInstance = new APEXPipelineHandlerV2();
		_sharedInstance.setStages();


		return _sharedInstance;
	}

	/**
	 * Initialize all stages
	 */
	private void setStages() {
		_sharedInstance.fetch = new Fetch(_sharedInstance);
		_sharedInstance.decode1 = new Decode(_sharedInstance,1);
		_sharedInstance.decode2 = new Decode(_sharedInstance,2);

		_sharedInstance.aluStage = new ALUStage();
		_sharedInstance.aluStage.dataBus = _sharedInstance;


		_sharedInstance.mulStage = new MulStage();
		_sharedInstance.mulStage.dataBus = _sharedInstance;


		_sharedInstance.brancStage = new BranchStage();
		_sharedInstance.brancStage.dataBus = _sharedInstance;

		_sharedInstance.lsStage = new LSStage();
		_sharedInstance.lsStage.dataBus = _sharedInstance;

		_sharedInstance.memory = new Memory(_sharedInstance);
		_sharedInstance.writeback = new Writeback(_sharedInstance);
		_sharedInstance.writebackALU = new Writeback(_sharedInstance);
		_sharedInstance.writebackMUL = new Writeback(_sharedInstance);

		_sharedInstance.memory.dataBus = _sharedInstance;
		_sharedInstance.writeback.dataBus = _sharedInstance;
		_sharedInstance.writebackALU.dataBus = _sharedInstance;
		_sharedInstance.writebackMUL.dataBus = _sharedInstance;


		_sharedInstance.memory.nextStage =  _sharedInstance.writeback;
		_sharedInstance.lsStage.nextStage =  _sharedInstance.memory;
		_sharedInstance.fetch.nextStage = _sharedInstance.decode1;
		_sharedInstance.decode1.nextStage = _sharedInstance.decode2;

		_sharedInstance.aluStage.nextStage = _sharedInstance.writebackALU;
		_sharedInstance.mulStage.nextStage = _sharedInstance.writebackMUL;
	}


	/**
	 * Start simulation
	 * @param numOfCycles
	 * @throws CloneNotSupportedException
	 */
	/*public void simulate(int numOfCycles) throws CloneNotSupportedException {

		boolean isFirstInstruction = true;
		int cycle = 0;
		log = new StringBuilder();

		while(!terminate) {

			if(!isFirstInstruction) {

				//memory.forwardData();
				//writeback.forwardData();
			}

			List<IQueueEntry> entries	= IQueue.getIsntance().nextReadyEntries();

			Instruction fetchInst = fetch.getPrevInstruction();
			Instruction decodeInst = decode.getPrevInstruction();

			Instruction memoryInst = memory.getPrevInstruction();
			Instruction writebackInst = writeback.getPrevInstruction();

			boolean canFetchMoveNext = fetch.canMoveToNext();
			boolean canDecodeMoveNext = decode.canMoveToNext();
			boolean canMemMoveNext = memory.canMoveToNext();

			//			if(!isFirstInstruction && fetchInst == null && decodeInst == null && memoryInst == null && writebackInst == null ) {
			//				//terminate = true;
			//				break;
			//			}


			if(isFirstInstruction) {
				fetch.render();
				isFirstInstruction = false;
				continue;
			}


			cycle += 1;
			String msg = "Cycle - " + cycle + " \n Fetch - " + fetchInst  + " \n " + "Decode - " + decodeInst + "IQ: " + IQueue.getIsntance() + " \n" + aluStage.toString()  + brancStage.toString()  + mulStage.toString()  + lsStage.toString() + "\n Memory - " + memoryInst + " \n Writeback - " + writebackInst;
			System.out.println(msg.replace("null", " (idle)"));

			log.append("\n" + msg);

			System.out.println("RegisterFile " + Arrays.toString(SharedData.getInstance().getRegisterFile() ) + "\n");

			log.append("\nRegisterFile " + Arrays.toString(SharedData.getInstance().getRegisterFile() ) + "\n");
			// Terminate if required cycles are completed
			if(numOfCycles != 0 && cycle == numOfCycles) {
				terminate = true;
			}


			// If the FU is not available then instruction should not be in the list
			for(IQueueEntry entry : entries) {
				switch (entry.getFunctionalUnitType()) {
				case ALU:
					aluStage.passInstruction(entry.getInstruction());
					break;
				case MUL:
					mulStage.passInstruction(entry.getInstruction());
					break;
				case BRANCH:
					brancStage.passInstruction(entry.getInstruction());
					break;
				case LSFU:
					lsStage.passInstruction(entry.getInstruction());
					break;
				default:
					break;
				}

				// Dispatch
				IQueue.getIsntance().remove(entry);
			}

			// Add to IQ
			if(decodeInst != null && canDecodeMoveNext) {
				IQueue.getIsntance().addInstruction(decodeInst);
			}


			if(decode.canMoveToNext()) {

				SharedData.getInstance().incrementPC();

				fetch.render();

				decode.updateInstruction(fetchInst);



			}  
			decode.render();

			if(decode.canMoveToNext()) {
				((Decode) decode).addDependecies(aluStage);
				((Decode) decode).addDependecies(mulStage);
				((Decode) decode).addDependecies(lsStage);
				((Decode) decode).addDependecies(IQueue.getIsntance());
			}

			aluStage.proceed();
			mulStage.proceed();
			lsStage.proceed();
			brancStage.proceed();
		}
	}*/



	/**

	 * Start simulation

	 * @param numOfCycles

	 * @throws CloneNotSupportedException

	 */

	public void simulate(int numOfCycles) throws CloneNotSupportedException {
		boolean isFirstInstruction = true;
		int cycle = 0;
		log = new StringBuilder();

		while(!terminate) {

			if(!isFirstInstruction) {
				//memory.forwardData();
				//writeback.forwardData();
			}

			List<IQueueEntry> entries	= IQueue.getIsntance().nextReadyEntries();
			Instruction fetchInst = fetch.getPrevInstruction();
			Instruction decode1Inst = decode1.getPrevInstruction();
			Instruction decode2Inst = decode2.getPrevInstruction();
			Instruction memoryInst = memory.getPrevInstruction();
			Instruction writebackInst = writeback.getPrevInstruction();

			boolean canFetchMoveNext = fetch.canMoveToNext();
			boolean canDecode1MoveNext = decode1.canMoveToNext();
			boolean canDecode2MoveNext = decode2.canMoveToNext();
			boolean canMemMoveNext = memory.canMoveToNext();

			if(isFirstInstruction) {
				fetch.render();
				isFirstInstruction = false;
				continue;
			}
			
			cycle += 1;
			String msg = "\n=====================================================\nCycle - " 
					+ cycle + 
					"\n--------------------------------------------\n" +
					" Fetch - " + fetchInst  + 
					"\n--------------------------------------------\n" +
					" Decode1 - " + decode1Inst +
					"\n"  +
					" Decode2 - " + decode2Inst +
					"\n--------------------------------------------\n"  +
					" IQ - " + IQueue.getIsntance() + 
					"\n--------------------------------------------\n" +
					aluStage.toString()  + 
					"\n--------------------------------------------\n" +
					brancStage.toString()  +
					"\n--------------------------------------------\n" +
					" Mul-" + mulStage.toString()  + 
					"\n---------------------------------------------\n" + 
					lsStage.toString() + 
					"\n---------------------------------------------\n" + 
					" Memory - " + memoryInst + 
					"\n---------------------------------------------\n Writeback - " 
					+ writebackInst
					+ "\n---------------------------------------------\n"
					+ ROB.getInstance()
					+ "\n---------------------------------------------\n"
					+ "RAT - " + RenameTable.getInstance()
					+ "\n---------------------------------------------\n"
					+ "R-RAT - " + RRAT.getInstance()
					+ "\n---------------------------------------------\n"
					+ " Free List - " + SharedData.getInstance().printFreeList()
					+ "\n---------------------------------------------\n";

			System.out.println(msg.replace("null", " (idle)"));
			log.append("\n" + msg.replace("null", " (idle)"));

			System.out.println(" RegisterFile " + Arrays.toString(SharedData.getInstance().getRegisterFile() ) + "\n");
			log.append("\nRegisterFile " + Arrays.toString(SharedData.getInstance().getRegisterFile() ) + "\n");
			// Terminate if required cycles are completed

			if(!isFirstInstruction && fetchInst == null && decode1Inst == null && decode2Inst == null && aluStage.isIDLE() && mulStage.isIDLE() && lsStage.isIDLE() && brancStage.isIDLE() && memoryInst == null && writebackInst == null && ROB.getInstance().isEmpty()) {
				terminate = true;
				break;
			}


			if(numOfCycles != 0 && cycle == numOfCycles) {
				terminate = true;
			}
			

			// Commit the ROB Entry / Instruction if status is valid
			ROB.getInstance().commit();
			
			// If the FU is not available then instruction should not be in the list

			for(IQueueEntry entry : entries) {
				switch (entry.getFunctionalUnitType()) {
				case ALU:
					aluStage.passInstruction(entry.getInstruction());
					break;
				case MUL:
					mulStage.passInstruction(entry.getInstruction());
					break;
				case BRANCH:
					brancStage.passInstruction(entry.getInstruction());
					break;
				case LSFU:
					lsStage.passInstruction(entry.getInstruction());
					break;
				default:
					break;
				}
				// Dispatch
				IQueue.getIsntance().remove(entry);
			}

			/*if(canFetchMoveNext && !haltRequest) {
				//TODO:: Remove this logic from here. Allow new instruction to be fetched

				if(!booleanIsbranchTaken) SharedData.getInstance().incrementPC();

				booleanIsbranchTaken = false;

				fetch.render();
				if(fetchInst != null) {
					if(fetch.instruction == null || fetch.instruction.getPc() != fetchInst.getPc()) {
						decode.updateInstruction(fetchInst.clone());
						decode.render();
						((Decode) decode).addDependecies(aluStage);
						((Decode) decode).addDependecies(mulStage);
						((Decode) decode).addDependecies(lsStage);
						((Decode) decode).addDependecies(IQueue.getIsntance());
						//((Decode)decode).addDependecies(brancStage);
					}

				} else {
					decode.render();
				}
			} else {
				decode.render();
			}*/


			if(!haltRequest) {

				// Add to IQ and ROB
				boolean isAnyUnresolvedBranchAvaialble = isUnresolvedBranchAvailable();
				if(decode2Inst != null && canDecode2MoveNext && (!decode2Inst.isBranch() || !isAnyUnresolvedBranchAvaialble)) {
					IQueue.getIsntance().addInstruction(decode2Inst);
					ROB.getInstance().add(decode2Inst);
				}


				if(decode2Inst == null || (!decode2Inst.isBranch() || !isAnyUnresolvedBranchAvaialble) ) {


					if(!decode2.canMoveToNext()) {
						decode2.render();
					} else {

						if(decode1Inst != null)
							decode2.updateInstruction(decode1Inst.clone());
						else
							decode2.updateInstruction(null);

						decode2.render();

					}

					if(!decode1.canMoveToNext()) {
						decode1.render();
					} else {

						if(!booleanIsbranchTaken) SharedData.getInstance().incrementPC();
						booleanIsbranchTaken = false;

						fetch.render();

						if(fetchInst != null)
							decode1.updateInstruction(fetchInst.clone());
						else
							decode1.updateInstruction(null);

						decode1.render();

						((Decode) decode1).addDependecies(decode2);
						((Decode) decode1).addDependecies(aluStage);
						((Decode) decode1).addDependecies(mulStage);
						((Decode) decode1).addDependecies(lsStage);
						((Decode) decode1).addDependecies(IQueue.getIsntance());


						// Remove false dependencies
						((Decode) decode1).removeFalseDependecies(); 
					}

				}
			} 

			aluStage.proceed();
			mulStage.proceed();
			lsStage.proceed();
			brancStage.proceed();

			memory.render();

			if(memoryInst != null) {
				writeback.updateInstruction(memoryInst.clone()); 
				writeback.render();
			} else {
				writeback.updateInstruction(null);
			}

		}

	}

	private boolean isUnresolvedBranchAvailable() {

		if(IQueue.getIsntance().containsBrancInst()) return true;

		if(brancStage.containsBrancInst()) return true;

		return false;
	}

	// In case of forwarding
	@Override
	public void writeResult(Instruction instruction) {
		decode1.resolveDependencies(instruction);
		decode2.resolveDependencies(instruction);
		IQueue.getIsntance().resolveDependencies(instruction);

		//ROB.getInstance().writeResult(instruction);
	}

	@Override
	public void branchTaken(Instruction instruction) {
		booleanIsbranchTaken = true;
		fetch.flush();
		decode1.flush();
		decode2.flush();
		aluStage.flushOnBranchTaken(instruction);
		mulStage.flushOnBranchTaken(instruction);
		brancStage.flushOnBranchTaken(instruction);
		lsStage.flushOnBranchTaken(instruction);
		//Flush all instructions dispatched after Branch instruction
		IQueue.getIsntance().flushIQOnBranchTaken(instruction);

		ROB.getInstance().branchTaken(instruction);
	}

	@Override
	public void haltRequest() {
		haltRequest = true;
		fetch.flush();
		decode1.flush();
		decode2.flush();
	}
}
