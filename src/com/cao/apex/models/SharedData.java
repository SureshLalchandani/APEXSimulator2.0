package com.cao.apex.models;

import java.util.Arrays;

import com.cao.apex.utility.Constants;

/**
 * Global data wrapper class
 * @author sureshlalchandani
 *
 */
public class SharedData {

	private int[] registerFile = new int[32];
	private int[] memory = new int[4000];
	private int[] specialRegisters = new int[3];
	private int[] freeList = new int[32];
	
	private int pc;


	private static SharedData _instance;

	public static synchronized SharedData getInstance() {

		if(_instance == null) {
			return newInstance();
		}

		return _instance;
	}
	
	public void setURFSize(int size) {
		registerFile = new int[size];
	}

	public static synchronized SharedData newInstance() {

		_instance = new SharedData();
		
		//Initialize all element in register file with 0
		_instance.registerFile = new int[32];
		Arrays.fill(_instance.registerFile, -1);
		
		//Initialize all element in memory with 0
		_instance.memory = new int[4000];
		Arrays.fill(_instance.memory, -1);
		
		// Map 16 Architectural Register with Physical Register
		Arrays.fill(_instance.freeList, 0, 16, 1);
		Arrays.fill(_instance.freeList, 16, 31, 0);
		
		_instance.pc = -1;
		
		return _instance;

	}

	public int readSpecialReg(int reg) {
		return reg >=0 && reg < specialRegisters.length ? specialRegisters[reg] : Constants.INVALID;
	}
	
	public void updateSplRegister(int reg, int value) {
		specialRegisters[reg] = value;
	}

	public int readRegisterFileForRegister(int reg) {
		return reg >=0 && reg < registerFile.length ? registerFile[reg] : Constants.INVALID;
	}
	
	public int[] getRegisterFile() {
		return registerFile;
	}

	public int readMemoryForLocation(int offset) {
		return offset >=0 && offset < memory.length ? memory[offset] : Constants.INVALID;
	}

	public void updateRegister(int reg, int value) {
		if(reg < 0 || reg > registerFile.length) return;
		registerFile[reg] = value;
	}

	public void updateMemoryLocation(int offset, int value) {
		if(offset < 0 || offset > memory.length) return;
		memory[offset] = value;
	}
	
	public int getPc() {
		return this.pc;

	}
	
	public void setPc(int pc) {
		this.pc = pc;
	}
	
	public void incrementPC() {
		pc += 4;
	}
	
	public void markAsAllocated(int physicalRegister) {
		freeList[physicalRegister] = 1;

	}
	
	public void markAsFree(int physicalRegister) {
		
		if(physicalRegister == Constants.INVALID) return;
		
		freeList[physicalRegister] = 0;
		registerFile[physicalRegister] = Constants.INVALID;
	}
	
	public boolean isFree(int physicalRegister) {
		return freeList[physicalRegister] == 0;
	}
	
	public int getNextAvailableRegister() {
		for(int i =0; i < freeList.length; i++) {
			
			if(isFree(i)) {
				return i;
			}
		}
		
		return Constants.INVALID;
	}
	
	
	public String printFreeList() {
		StringBuilder sbr = new StringBuilder();

		for(int i =0; i < freeList.length; i++) {
			sbr.append("[" + i +" = "+ freeList[i] + "] ");
		}
		
		return sbr.toString();
	}


}
