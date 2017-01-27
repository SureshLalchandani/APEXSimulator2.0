package com.cao.apex.utility;

import com.cao.apex.models.Instruction;

public class Parser {

	Instruction instruction;

	public Parser(Instruction instruction) {
		this.instruction = instruction;
	}
	
	private void movInstParsing() {
		String[] components = instruction.getHighLevelInstruction().split(" ");

		if(components.length == 3) {
			String dest = components[1];
			String srcComp = components[2];

			if(dest.contains(",")) {
				String src1Reg = dest.replace(",", "").trim();
				String index = src1Reg.toUpperCase().replace("R", "");
				instruction.setDest(Integer.parseInt(index));
			}


			if(srcComp.contains("#")) {
				String value = srcComp.replace("#", "").trim();
				instruction.setLiteralValue(Integer.parseInt(value));
			} else if(srcComp.contains("R")) {
				String index = srcComp.toUpperCase().replace("R", "").trim();
				instruction.setSrc1(Integer.parseInt(index));
				instruction.setSrc2(Constants.INVALID);
			}
		}
	}
	
	private void aluInstParsing() {
		String[] components = instruction.getHighLevelInstruction().split(" ");

		if(components.length == 4) {
			String dest = components[1];
			String src1Comp = components[2];
			String src2Comp = components[3];

			if(dest.contains(",")) {
				String src1Reg = dest.replace(",", "").trim();
				String index = src1Reg.toUpperCase().replace("R", "");
				instruction.setDest(Integer.parseInt(index));
			}


			if(src1Comp.contains(",")) {
				String src1Reg = src1Comp.replace(",", "").trim();
				String index = src1Reg.toUpperCase().replace("R", "").trim();
				instruction.setSrc1(Integer.parseInt(index));
			}

			if(src2Comp.contains("#")) {
				String value = src2Comp.replace("#", "").trim();
				instruction.setLiteralValue(Integer.parseInt(value));
			} else if(src2Comp.contains("R")) {
				String index = src2Comp.toUpperCase().replace("R", "").trim();
				instruction.setSrc2(Integer.parseInt(index));
			}
		}
	}
	
	private void memInstParsing() {
		String[] components = instruction.getHighLevelInstruction().split(" ");

		if(components.length == 4) {
			String dest = components[1];
			String src1Comp = components[2];
			String src2Comp = components[3];

			if(dest.contains(",")) {
				String src1Reg = dest.replace(",", "").trim();
				String index = src1Reg.toUpperCase().replace("R", "");
				instruction.setDest(Integer.parseInt(index));
			}

			if(src1Comp.contains(",")) {
				String src1Reg = src1Comp.replace(",", "").trim();
				String index = src1Reg.toUpperCase().replace("R", "").trim();
				instruction.setSrc1(Integer.parseInt(index));
			}

			if(src2Comp.contains("#")) {
				String value = src2Comp.replace("#", "").trim();
				instruction.setLiteralValue(Integer.parseInt(value));
			} else if(src2Comp.contains("R")) {
				String index = src2Comp.toUpperCase().replace("R", "").trim();
				instruction.setSrc2(Integer.parseInt(index));
			}
		}
	}
	
	private void branchInstParsing() {
		String[] components = instruction.getHighLevelInstruction().split(" ");

		if(components.length == 2) {
			String literal = components[1];

			if(literal.contains("#")) {
				String value = literal.replace("#", "").trim();
				instruction.setLiteralValue(Integer.parseInt(value));
			}
		}
	}
	
	private void balInstParsing() {
		String[] components = instruction.getHighLevelInstruction().split(" ");

		if(components.length == 3) {
			String src1Comp = components[1];
			String src2Comp = components[2];

			if(src1Comp.contains(",")) {
				 if(src1Comp.contains("R")) {
					String index = src1Comp.toUpperCase().replace("R", "").trim();
					instruction.setSrc1(Integer.parseInt(index.replace(",", "").trim()));
					instruction.setSrc2(Constants.INVALID);
				} else if(src1Comp.contains("X")) {
					instruction.setSpecialRegisterIndex(Constants.splRegX);
				} else if(src1Comp.contains("Z")) {
					instruction.setSpecialRegisterIndex(Constants.splRegZ);
				}
			}


			if(src2Comp.contains("#")) {
				String value = src2Comp.replace("#", "").trim();
				instruction.setLiteralValue(Integer.parseInt(value));
			} else if(src2Comp.contains("R")) {
				String index = src2Comp.toUpperCase().replace("R", "").trim();
				instruction.setSrc2(Integer.parseInt(index));
			} else if(src2Comp.contains("X")) {
				instruction.setSpecialRegisterIndex(Constants.splRegX);
			} else if(src2Comp.contains("Z")) {
				instruction.setSpecialRegisterIndex(Constants.splRegZ);
			}
		}
	}

	public void parse() {
		String opcode = getOpcode().trim();
		instruction.setOpcode(opcode);
		// Reg to Reg Copy - 2 reg
		if(opcode.equalsIgnoreCase(Constants.MOVC) || 
				opcode.equalsIgnoreCase(Constants.MOV)) {
			movInstParsing();
		} 

		//Reg to Reg Op - 3 Reg
		else if(opcode.equalsIgnoreCase(Constants.ADD) || 
				opcode.equalsIgnoreCase(Constants.SUB) || 
				opcode.equalsIgnoreCase(Constants.MUL))  {
			aluInstParsing();
		} 

		// Memory Op - 2 Reg
		else if(opcode.equalsIgnoreCase(Constants.LOAD) ||
				opcode.equalsIgnoreCase(Constants.STORE)) {
			memInstParsing();
		}


		//Branch Op - 3 Reg
		else if(opcode.equalsIgnoreCase(Constants.BZ) || 
				opcode.equalsIgnoreCase(Constants.BNZ))  {
			branchInstParsing();
		}  
		
		else if(opcode.equalsIgnoreCase(Constants.BAL)) {
			balInstParsing();
		}
		
		else if(opcode.equalsIgnoreCase(Constants.JUMP)) {
			balInstParsing();
		}
		
		
	}


	public String getOpcode() {
		if(instruction.getHighLevelInstruction().trim().equalsIgnoreCase(Constants.HALT)) {
			return Constants.HALT;
		}
		
		String[] components = instruction.getHighLevelInstruction().split(" ");

		return components[0].trim().toUpperCase();
	}





}
