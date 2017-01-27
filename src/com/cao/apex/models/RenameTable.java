package com.cao.apex.models;

import java.util.HashMap;

import com.cao.apex.utility.Constants;

public class RenameTable extends  HashMap<Integer, Integer> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	private static RenameTable _sharedInstance;
	
	public static RenameTable getInstance() {
		if( _sharedInstance == null) {
			_sharedInstance = newInstance();
			_sharedInstance.init();
		}
		
		return _sharedInstance;
	}
	
	protected void init() {
		for(int i =0; i < 16; i++) {
			put(i, i);
		}
		
		for(int i =16; i < 32; i++) {
			put(i, Constants.INVALID);
		}
	}
	
	public static RenameTable newInstance() {
		return _sharedInstance = new RenameTable();
		
	}


	public void updateEntry(int architecturalRegister, int physicalReg) {
		if(architecturalRegister == Constants.INVALID || 
				physicalReg == Constants.INVALID) return;
		
		put(architecturalRegister, physicalReg);
		
		// Mark as allocated in free list
		SharedData.getInstance().markAsAllocated(physicalReg);
		
	}
	
	
	public int mapWithNextAvailableRegister(int architecturalRegister) {
		if(architecturalRegister == Constants.INVALID) return Constants.INVALID;
		
		int physicalReg = SharedData.getInstance().getNextAvailableRegister();
		
		
		updateEntry(architecturalRegister, physicalReg);
	
		return physicalReg;
		
	}
	
	public int getMappedPhysicalRegister(int architecturalRegister) {
		if(architecturalRegister == Constants.INVALID) return Constants.INVALID;
		
		return get(architecturalRegister);
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		
		for(Integer key : keySet()) {
			builder.append("[" + key + " - " + this.get(key) + " ]");
		}
		
		return super.toString();
	}
	
	
}
