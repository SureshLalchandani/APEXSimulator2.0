package com.cao.apex.models;

import java.util.ArrayList;
import java.util.List;

import com.cao.apex.buses.IForwardingDataBus;

public class ROB implements IForwardingDataBus{

	private static ROB _sharedInstance;

	List<ROBEntry> entries;

	int head = 0;
	int tail = 0;

	public static ROB getInstance() {
		if(_sharedInstance == null) {
			_sharedInstance = newInstance();
		}

		return _sharedInstance;
	}

	public static ROB newInstance() {
		_sharedInstance = new ROB();
		_sharedInstance.entries = new ArrayList<>();
		return _sharedInstance;
	}


	public void add(Instruction instruction) {
		ROBEntry entry = new ROBEntry(instruction);
		entries.add(entry);
		tail = entries.size() - 1;
	}


	public ROBEntry getHeadRef() {
		return entries.size() > 0 ? entries.get(head) : null;
	}

	public ROBEntry getTailRef() {
		return entries.size() > 0 && entries.size() > tail ? entries.get(tail) : null;
	}

	public boolean isEmpty() {
		return entries.size() == 0;
	}

	public void commit() {
		if(getHeadRef() == null || getHeadRef().statusBit != 1) return;

		int prevReg = getHeadRef().getInstruction().getArchDest();

		RRAT.getInstance().updateEntry(getHeadRef().getInstruction().getArchDest(), getHeadRef().getInstruction().getDest());

		SharedData.getInstance().markAsFree(prevReg);

		entries.remove(getHeadRef());
	}

	@Override
	public void writeResult(Instruction instruction) {
		for(ROBEntry entry : entries) {
			try {

				if(entry.instruction.getPc() != instruction.getPc()) {
					continue;
				}

				entry.resultValue = instruction.getDestResult();
				entry.statusBit = 1;
				entry.addDestReg = instruction.getDest();

				entry.instruction = instruction.clone();

			} catch (CloneNotSupportedException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public synchronized void branchTaken(Instruction instruction) {

		List<ROBEntry> copy = new ArrayList<>(entries);

		for(ROBEntry entry : copy) {
			try {

				if(entry.instruction.getPc() != instruction.getPc()) {
					continue;
				}

				List<ROBEntry> toDelete = new ArrayList<>();
				for(int i = entries.indexOf(entry)+1; i < copy.size(); i ++) {
					toDelete.add(entries.get(i));
				}

				entries.removeAll(toDelete);				
				tail = entries.size() - 1;

			} catch (Exception e) {
				e.printStackTrace();
			}
		}


		RenameTable.getInstance().clear();
		RenameTable.getInstance().putAll(RRAT.getInstance());

		for(ROBEntry entry : entries) {
			RenameTable.getInstance().updateEntry(entry.getInstruction().getArchDest(), entry.getInstruction().getDest());
		}
	}

	@Override
	public void haltRequest() {
		// TODO Auto-generated method stub

	}


	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder( "\n ROB : " );

		for(ROBEntry entry : entries) {
			builder.append(entry + "\n");
		}

		return builder.toString();
	}


}
