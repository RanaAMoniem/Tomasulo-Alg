

public class LoadBuffer {

	int address;
	boolean busyBit;
	
	public LoadBuffer() {
		busyBit = false;
		address = 0;
	}
	
	public String toString() {
		return("Busy Bit: " + busyBit +"| Address: " + address );
	}
	
	public void reserve(Instruction inst) {
		busyBit = true;
		address = Integer.parseInt(inst.a2) + (int) InstructionBuffer.intRegisterFile.get(inst.a3).value ;
	}
	
	public void clearStation() {
		busyBit=false;
		address = 0;
	}
}
