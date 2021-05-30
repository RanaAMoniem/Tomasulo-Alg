
public class Instruction {
	String op;
	String a1;
	String a2;
	String a3;
	String state;
	int issueCycle;
	int startExecCycle;
	int endExecCycle;
	int writeCycle;
	int cyclesNeeded;
	boolean done;
	String tag;

	public Instruction(String s) {
		tag = null;
		done = false;
		String[] split = s.split(" ");
		state = "unfetched";
		op = split[0];
		a1 = split[1];
		a2 = split[2];
		a3 = split[3];

		if (op.equals("L.D") || op.equals("S.D")) {
			cyclesNeeded = 2;
		}
		if (op.equals("MUL.D")) {
			cyclesNeeded = 5;
		}
		if (op.equals("DIV.D")) {
			cyclesNeeded = 10;
		}
		if (op.equals("ADD.D")) {
			cyclesNeeded = 3;
		}
		if (op.equals("SUB.D")) {
			cyclesNeeded = 4;
		}

	}

	public String toString() {
		return ("Op: " + op + " | RD: " + a1 + " | RS: " + a2 + " | RT: " + a3 + " | State: " + state + " | Issue Cycle: "
				+ issueCycle + " | Start Exec Cycle: " + startExecCycle + " | End Exec Cycle: " + endExecCycle +
				" | Write Cycle: " + writeCycle + " | Cycles Needed: " + cyclesNeeded);
	}

	public void fetch(int cycle, String tag) {
		this.state = "IF";
		this.issueCycle = cycle;
		this.tag = tag;
	}

	public boolean executeCycle() {
		if (!done) {
			this.cyclesNeeded--;
			if (cyclesNeeded == 0) {
				this.done = true;
				return true;
			}
		}
		return false;
	}
}
