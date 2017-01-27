package com.cao.apex.buses;

import com.cao.apex.models.Instruction;

/**
 * An interface channel which acts as Forwarding Databus
 * @author sureshlalchandani
 *
 */
public interface IForwardingDataBus {

	public void writeResult(Instruction instruction);
	public void branchTaken(Instruction instruction);
	public void haltRequest();
	
}
