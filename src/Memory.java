
public class Memory {
	
private static double[] dataMemory;
	
	public Memory()
	{
		dataMemory = new double[8];
		for(int i=0; i<8; i++) {
			dataMemory[i] =  (int)(Math.random() * (50 - 1 + 1) + 1);
		}
	}
	
	public String toString() {
		System.out.println("Memory:");
		for(int i=0; i<8; i++) {
			System.out.println("Address " + i + ": " + dataMemory[i]);
		}
		return "";
	}
	
	public double read(int address)
	{
		return dataMemory[address];
	}
	
	
	public void write(int address, double value)
	{
		dataMemory[address]=value;
	}

}
