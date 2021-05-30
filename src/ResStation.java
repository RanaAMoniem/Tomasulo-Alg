

public class ResStation {
	boolean busy;
	String op;
	String Qj;
	String Qk;
	double Vj;
	double Vk;
	String A;
	
	public ResStation() {
		busy=false;
		op=Qj=Qk=A=null;
		Vj=Vk= 0;		
	}
	
	public String toString() {
		return("Busy: " + busy + " | Op: " + op +  " | Vj: " + Vj + " | Vk: "+ Vk +
				"  | Qj: " + Qj +" | Qk: " + Qk +  " | A: " + A);
	}
	
	public void reserve(Instruction inst) {
		op = inst.op;
		busy = true;
		if(InstructionBuffer.canReadFromReg(inst.a2)) {
			Vj = InstructionBuffer.registerFile.get(inst.a2).value;
		}
		else {
			Qj = InstructionBuffer.registerFile.get(inst.a2).state;
		}
		if(InstructionBuffer.canReadFromReg(inst.a3)) {
			Vk = InstructionBuffer.registerFile.get(inst.a3).value;
		}
		else {
			Qk = InstructionBuffer.registerFile.get(inst.a3).state;
		}
	}
	
	public void clearStation() {
		op = null;
		busy=false;
		Qj=Qk=A=null;
		Vj=Vk= 0;
	}
	
}
