package com.cao.apex.models;

import java.util.HashMap;

/**
 * Implementation of ICache
 * @author sureshlalchandani
 *
 */
public class ICache extends HashMap<Integer, Instruction> {

	private static ICache _instance;

	public synchronized static ICache getInstance() {

		if(_instance == null) {
			_instance = new ICache();
		}

		return _instance;
	}


	public synchronized static ICache newInstance() {

		_instance = new ICache();
		return _instance;
	}



	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public Instruction getInstructionAtLocation(int location) {
		return get(location);
	}

	public void addInstructionAtLocation(int location, Instruction instruction) {
		put(location, instruction);
	}


}
