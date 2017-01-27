package com.cao.apex.models;

public class RRAT extends RenameTable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
private static RRAT _sharedInstance;
	
	public static RRAT getInstance() {
		if( _sharedInstance == null) {
			_sharedInstance = newInstance();
			_sharedInstance.init();
		}
		
		return _sharedInstance;
	}

	public static RRAT newInstance() {
		return _sharedInstance = new RRAT();
		
	}
	
	@Override
	public String toString() {
		return super.toString();
	}
}
