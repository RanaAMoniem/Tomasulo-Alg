

public class StoreBuffer {

	int address;
	boolean busyBit;
	double V;
	String Q;
	
	public StoreBuffer() {
		busyBit = false;
		address = 0;
		V =0;
		Q=null;
		
	}
	
	public String toString() {
		return("Busy Bit: " + busyBit + " | Address: " + address +  " | V: " + V + " | Q: " + Q );
	}
	
	public void reserve(Instruction inst) {
		busyBit = true;
		address = Integer.parseInt(inst.a2) + (int) InstructionBuffer.intRegisterFile.get(inst.a3).value ;

		if(InstructionBuffer.canReadFromReg(inst.a1)) {
			V = InstructionBuffer.registerFile.get(inst.a1).value;
		}
		else {
			Q = InstructionBuffer.registerFile.get(inst.a1).state;
		}
	}
	
	public void clearStation() {
		busyBit=false;
		address = 0;
		V =0;
		Q=null;
	}
}
