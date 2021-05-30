
import java.util.*;

public class InstructionBuffer {
	Instruction [] list;
	public static Hashtable<String,RegisterEntry> registerFile = new Hashtable<String, RegisterEntry>() ;
	public static Hashtable<String,RegisterEntry> intRegisterFile = new Hashtable<String, RegisterEntry>();
	ArrayList<Integer> unfetchedIndices;
	ArrayList<Integer> fetchedIndices;
	ArrayList<Integer> executingIndices;
	ArrayList<Integer> writeIndices;
	ArrayList<Integer> doneIndices;
	
	
	public InstructionBuffer() {
		Scanner sc= new Scanner(System.in);
		System.out.print("Enter number of Instructions: ");  
		int n= Integer.parseInt(sc.nextLine());  
		unfetchedIndices=new ArrayList<Integer>();
		fetchedIndices=new ArrayList<Integer>();
		executingIndices=new ArrayList<Integer>();
		writeIndices=new ArrayList<Integer>();
		doneIndices=new ArrayList<Integer>();
		list = new Instruction[n];
		for (int i=0;i<n;i++) {
			int c = i+1;
			System.out.println("Enter instruction number " + c );
			list[i] =  new Instruction(sc.nextLine());
			unfetchedIndices.add(i);
		}
		sc.close();
	}
	
	public String toString(){
		 registerFile.forEach( (key, entry) -> System.out.println("FP Register File: " + "Register: " + key + " | Reg Info: " + entry.toString()));
		 System.out.println("");
		 intRegisterFile.forEach( (key, entry) -> System.out.println("Int Register File: " + "Register: " + key + " | Reg Info: " + entry.toString()));
		 System.out.println("");
		 for (int i=0;i<list.length;i++) {
				System.out.println("Instruction Buffer List Items: " + list[i].toString() + "\n");
		 }
		return "";
	}
	
	public static void initializeIntRegister(String Register, String state, int Value) {
		intRegisterFile.put(Register, new RegisterEntry(state,Value));
	}
	public static void updateIntRegisterValue(String Register, double value) {
		intRegisterFile.put(Register, new RegisterEntry(null,value));
	}
	
	public static void initializeRegister(String Register, String state, double Value) {
		registerFile.put(Register, new RegisterEntry(state,Value));
	}
	public static void updateRegisterValue(String Register, double value) {
		registerFile.put(Register, new RegisterEntry("C",value));
	}
	public static boolean canReadFromReg(String register) {
		if(registerFile.get(register).state == "C")
			return true;
		return false;
	}
	public static void reserveReg(String register, String tag) {
		registerFile.put(register, new RegisterEntry(tag, 0));
	}
	
}
