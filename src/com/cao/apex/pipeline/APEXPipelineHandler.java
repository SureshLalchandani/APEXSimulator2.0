package com.cao.apex.pipeline;

import java.util.Arrays;

import com.cao.apex.buses.IForwardingDataBus;
import com.cao.apex.models.Instruction;
import com.cao.apex.models.SharedData;

/**
 * Main pipeline engine that makes the chain of all stages and make the instructions pass through
 * @author sureshlalchandani
 *
 */
public class APEXPipelineHandler implements IForwardingDataBus {

	public static enum Stages {
		FETCH, DECODE, EXECUTE, MEMORY, WRITEBACK;
	}

	private Stage fetch;
	private Stage decode;
	private Stage executeStage1;
	private Stage executeStage2;
	//private Stage execute;
	private Stage memory;
	private Stage writeback;

	public StringBuilder log;

	public Stage getFetch() {
		return fetch;
	}


	public void setFetch(Stage fetch) {
		this.fetch = fetch;
	}


	public Stage getDecode() {
		return decode;
	}


	public void setDecode(Stage decode) {
		this.decode = decode;
	}


	public Stage getExecuteStage1() {
		return executeStage1;
	}


	public void setExecuteStage1(Stage executeStage1) {
		this.executeStage1 = executeStage1;
	}


	public Stage getExecuteStage2() {
		return executeStage2;
	}


	public void setExecuteStage2(Stage executeStage2) {
		this.executeStage2 = executeStage2;
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

	private static APEXPipelineHandler _sharedInstance;

	private boolean booleanIsbranchTaken = false;

	/**
	 * Singleton instance
	 * @return
	 */
	public static APEXPipelineHandler getInstance() {
		if(_sharedInstance == null) {

			_sharedInstance = newInstance();
		}


		return _sharedInstance;
	}

	public static APEXPipelineHandler newInstance() {
		_sharedInstance = new APEXPipelineHandler();
		_sharedInstance.setStages();


		return _sharedInstance;
	}

	/**
	 * Initialize all stages
	 */
	private void setStages() {
		_sharedInstance.fetch = new Fetch(_sharedInstance);
		_sharedInstance.decode = new Decode(_sharedInstance,1);
		_sharedInstance.executeStage1 = new Execute(_sharedInstance,1);
		_sharedInstance.executeStage2 = new Execute(_sharedInstance,2);
		_sharedInstance.memory = new Memory(_sharedInstance);
		_sharedInstance.writeback = new Writeback(_sharedInstance);

		_sharedInstance.executeStage2.dataBus = _sharedInstance;
		_sharedInstance.memory.dataBus = _sharedInstance;
		_sharedInstance.writeback.dataBus = _sharedInstance;

		_sharedInstance.fetch.nextStage = _sharedInstance.decode;
		_sharedInstance.decode.nextStage = _sharedInstance.executeStage1;
		_sharedInstance.executeStage1.nextStage = _sharedInstance.executeStage2;
		_sharedInstance.executeStage2.nextStage = _sharedInstance.memory;
		_sharedInstance.memory.nextStage = _sharedInstance.writeback;


		_sharedInstance.writeback.prevStage = _sharedInstance.memory;
		_sharedInstance.decode.prevStage = _sharedInstance.fetch;
		_sharedInstance.executeStage1.prevStage = _sharedInstance.decode;
		_sharedInstance.executeStage2.prevStage = _sharedInstance.executeStage1;
		_sharedInstance.memory.prevStage = _sharedInstance.executeStage2;
	}


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
				//((Decode)decode).addDependecies();
				executeStage2.forwardData();
				memory.forwardData();
				writeback.forwardData();
			}

			Instruction fetchInst = fetch.getPrevInstruction();;
			Instruction decodeInst = decode.getPrevInstruction();;
			Instruction execute1Inst = executeStage1.getPrevInstruction();
			Instruction execute2Inst = executeStage2.getPrevInstruction();
			Instruction memoryInst = memory.getPrevInstruction();
			Instruction writebackInst = writeback.getPrevInstruction();

			boolean canFetchMoveNext = fetch.canMoveToNext();
			boolean canDecodeMoveNext = decode.canMoveToNext();
			boolean canEx1MoveNext = executeStage1.canMoveToNext();
			boolean canEx2MoveNext = executeStage2.canMoveToNext();
			boolean canMemMoveNext = memory.canMoveToNext();



			if(!isFirstInstruction && fetchInst == null && decodeInst == null && execute1Inst == null && execute2Inst == null && memoryInst == null && writebackInst == null) {
				terminate = true;
				break;
			}


			if(isFirstInstruction) {
				fetch.render();
				isFirstInstruction = false;
				continue;
			}


			cycle += 1;
			String msg = "Cycle - " + cycle + " \n Fetch - " + fetchInst  + " \n " + "Decode - " + decodeInst + " \n ExecuteStage1 -" + execute1Inst + " \n ExecuteStage2 -" + execute2Inst + "\n Memory - " + memoryInst + " \n Writeback - " + writebackInst;
			System.out.println(msg.replace("null", " (idle)"));

			log.append("\n" + msg);

			System.out.println("RegisterFile " + Arrays.toString(SharedData.getInstance().getRegisterFile() ) + "\n");

			log.append("\nRegisterFile " + Arrays.toString(SharedData.getInstance().getRegisterFile() ) + "\n");
			// Terminate if required cycles are completed
			if(numOfCycles != 0 && cycle == numOfCycles) {
				terminate = true;
			}

			//if(decodeInst == null || !decodeInst.equals(decode.instruction))



			if(canFetchMoveNext && !haltRequest) {

				//TODO:: Remove this logic from here. Allow new instruction to be fetched
				if(!booleanIsbranchTaken) SharedData.getInstance().incrementPC();

				booleanIsbranchTaken = false;

				fetch.render();	

				if(fetchInst != null) {

					if(fetch.instruction == null || fetch.instruction.getPc() != fetchInst.getPc()) {
						decode.updateInstruction(fetchInst.clone());
						decode.render();
					}
				} else if(canDecodeMoveNext ) {
					decode.updateInstruction(null);
				}
			}

			if(decodeInst != null) {

				if(canDecodeMoveNext /*(decode.instruction == null || decode.instruction.getPc() != decodeInst.getPc()) && decode.canMoveToNext()*/) {
					executeStage1.updateInstruction(decodeInst.clone());
					executeStage1.render();
				}
			} else if(canEx1MoveNext ){
				executeStage1.updateInstruction(null);
			}

			if(execute1Inst != null) {

				if(canEx1MoveNext /*(executeStage1.instruction == null || executeStage1.instruction.getPc() != execute1Inst.getPc()) && */) {
					executeStage2.updateInstruction(execute1Inst.clone());
					executeStage2.render();

					if(executeStage1.instruction != null && executeStage1.instruction.getPc() == execute1Inst.getPc()) {
						executeStage1.updateInstruction(null);
					}

				}
			} else {
				executeStage2.updateInstruction(null);
			}

			if(execute2Inst != null) {
				if(canEx2MoveNext/*(executeStage2.instruction == null || executeStage2.instruction.getPc() != execute2Inst.getPc()) && */) {
					memory.updateInstruction(execute2Inst.clone());
					memory.render();
					
					if(executeStage2.instruction != null && executeStage2.instruction.getPc() == execute2Inst.getPc()) {
						executeStage2.updateInstruction(null);
					}
				}
			} else if(canMemMoveNext ){
				memory.updateInstruction(null);
			}

			/*if(execute2Inst != null) {
				//We need two cycles so no need to check for PC
				if((executeStage2.instruction == null || execute2Inst.getPc() != executeStage2.instruction.getPc()) || executeStage2.canMoveToNext()) {
					memory.updateInstruction(execute2Inst.clone());
					memory.render();
				} else if(((Execute) execute).needMoreCycle()) {
					execute.render();
					memory.updateInstruction(null);
				}
			} else {
				memory.updateInstruction(null);
			}*/

			if(memoryInst != null) {
				writeback.updateInstruction(memoryInst.clone()); 
				writeback.render();
			} else {
				writeback.updateInstruction(null);
			}

		}

	}

	@SuppressWarnings("unused")
	private boolean isStallRequired() {
		if(fetch.canMoveToNext() && decode.canMoveToNext() && executeStage1.canMoveToNext()) {
			return false;
		}
		return true;
	}


	// In case of forwarding
	@Override
	public void writeResult(Instruction instruction) {
		decode.resolveDependencies(instruction);
		executeStage1.resolveDependencies(instruction);
		executeStage2.resolveDependencies(instruction);
	}

	@Override
	public void branchTaken(Instruction instruction) {
		// TODO Auto-generated method stub
		booleanIsbranchTaken = true;
		fetch.flush();
		decode.flush();
	}

	@Override
	public void haltRequest() {
		haltRequest = true;
		fetch.flush();
		decode.flush();
		
	}


}
