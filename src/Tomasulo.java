
public class Tomasulo {
	static InstructionBuffer IB;
	static MulResStationList MultiplierBuffer;
	static AddResStationList AdderBuffer;
	static LoadBufferList LB;
	static StoreBufferList SB;
	static Memory dataMemory;
	static int cycle;

	public Tomasulo() {
		cycle = 0;

		IB = new InstructionBuffer();
		MultiplierBuffer = new MulResStationList("M", 2);
		AdderBuffer = new AddResStationList("A", 3);
		LB = new LoadBufferList("L", 2);
		SB = new StoreBufferList("S", 3);
		dataMemory = new Memory();

		IB.initializeRegister("F0", "C", 1.6);
		IB.initializeRegister("F2", "E", 0);
		IB.initializeRegister("F4", "E", 0);
		IB.initializeRegister("F6", "E", 0);
		IB.initializeRegister("F8", "E", 0);
		IB.initializeRegister("F10", "E", 0);

		IB.initializeIntRegister("R1", null, 2);
		IB.initializeIntRegister("R2", null, 1);
		IB.initializeIntRegister("R3", null, 4);

		while (IB.doneIndices.size() < IB.list.length) {
			cycle++;
			System.out.println("---------------------- Cycle No. " + cycle + " ------------------------\n");

			writeBack();
			execute();
			fetch();

			System.out.println(IB.toString());
			System.out.println(dataMemory.toString());
			System.out.println(AdderBuffer.toString());
			System.out.println(MultiplierBuffer.toString());
			System.out.println(LB.toString());
			System.out.println(SB.toString());

		}
	}

	public void resultInBus(String tagWB, double valueWB) { // called in all ops but S.D, S.D does not return result
		System.out.println("---------------------- Result in Bus ------------------------\n");
		System.out.println("Result: " + valueWB);

		for (int i = IB.fetchedIndices.size() - 1; i >= 0; i--) {
			int fetchedInstIndex = IB.fetchedIndices.get(i);
			Instruction fetchedInst = IB.list[fetchedInstIndex];
			String fetchedInstTag = fetchedInst.tag;

			System.out.println("Instructions Waiting: " + fetchedInst);

			if (fetchedInst.op.equals("MUL.D") || fetchedInst.op.equals("DIV.D")) {

				if (MultiplierBuffer.list.get(fetchedInstTag).Qj == tagWB) {
					MultiplierBuffer.list.get(fetchedInstTag).Qj = null;
					MultiplierBuffer.list.get(fetchedInstTag).Vj = valueWB;
				}
				if (MultiplierBuffer.list.get(fetchedInstTag).Qk == tagWB) {
					MultiplierBuffer.list.get(fetchedInstTag).Qk = null;
					MultiplierBuffer.list.get(fetchedInstTag).Vk = valueWB;
				}
				readyToExecMul(fetchedInstTag, fetchedInstIndex, fetchedInst, i);

			}

			else if (fetchedInst.op.equals("ADD.D") || fetchedInst.op.equals("SUB.D")) {

				if (AdderBuffer.list.get(fetchedInstTag).Qj == tagWB) {
					AdderBuffer.list.get(fetchedInstTag).Qj = null;
					AdderBuffer.list.get(fetchedInstTag).Vj = valueWB;
				}
				if (AdderBuffer.list.get(fetchedInstTag).Qk == tagWB) {
					AdderBuffer.list.get(fetchedInstTag).Qk = null;
					AdderBuffer.list.get(fetchedInstTag).Vk = valueWB;
				}
				readyToExecAdd(fetchedInstTag, fetchedInstIndex, fetchedInst, i);

			}

			else if (fetchedInst.op.equals("S.D")) {

				if (SB.list.get(fetchedInstTag).Q == tagWB) {
					SB.list.get(fetchedInstTag).Q = null;
					SB.list.get(fetchedInstTag).V = valueWB;
				}
				readyToExecSD(fetchedInstTag, fetchedInstIndex, fetchedInst, i);
			}
		}
	}

	public void writeBack() {

		if (!IB.writeIndices.isEmpty()) {
			int instIndex = IB.writeIndices.get(0);
			Instruction instWB = IB.list[instIndex];
			String tagWB = instWB.tag;
			double valueWB = 0;

			System.out.println("---------------------- Writing back ------------------------");
			System.out.println("Instruction Number: " + (instIndex+1) + "\n");

			IB.doneIndices.add(instIndex);
			instWB.state = "WB";
			IB.writeIndices.remove(0);
			// 1) clear station
			// 2) update registerFile reserved with new value
			// 3) update any station that needs the tag

			if (instWB.op.equals("MUL.D") || instWB.op.equals("DIV.D")) {

				valueWB = MultiplierBuffer.list.get(tagWB).Vj + MultiplierBuffer.list.get(tagWB).Vk;
				MultiplierBuffer.list.get(tagWB).clearStation();
				IB.updateRegisterValue(instWB.a1, valueWB);
				resultInBus(tagWB, valueWB);
			}

			else if (instWB.op.equals("ADD.D") || instWB.op.equals("SUB.D")) {

				valueWB = AdderBuffer.list.get(tagWB).Vj + AdderBuffer.list.get(tagWB).Vk;
				AdderBuffer.list.get(tagWB).clearStation();
				IB.updateRegisterValue(instWB.a1, valueWB);
				resultInBus(tagWB, valueWB);
			}

			else if (instWB.op.equals("L.D")) {

				valueWB = dataMemory.read(LB.list.get(tagWB).address);
				LB.list.get(tagWB).clearStation();
				IB.updateRegisterValue(instWB.a1, valueWB);
				resultInBus(tagWB, valueWB);
			}

			else if (instWB.op.equals("S.D")) {

				dataMemory.write(SB.list.get(tagWB).address, SB.list.get(tagWB).V);
				SB.list.get(tagWB).clearStation();
			}
		}
	}

	public void execute() {
		if (!IB.executingIndices.isEmpty()) {
			System.out.println("---------------------- Executing ------------------------");
			for (int i = 0; i < IB.executingIndices.size(); i++) {
				int instIndex = IB.executingIndices.get(i);
				Instruction instExec = IB.list[instIndex];
				if (cycle >= instExec.startExecCycle) {
					System.out.println("Instruction Number: " + (instIndex + 1));
					boolean isDone = instExec.executeCycle();
					if (isDone) {
						System.out.println("This instruction is done executing\n");
					}
				}
			}
			for (int j = 0; j < IB.executingIndices.size(); j++) {
				int instIndex = IB.executingIndices.get(j);
				Instruction instExec = IB.list[instIndex];
				if (instExec.done) {
					instExec.state = "Done EX";
					IB.executingIndices.remove(j);
					IB.writeIndices.add(instIndex);
					instExec.writeCycle = cycle + 1;
				}
			}
		}
	}

	public void readyToExecMul(String stationTag, int instIndex, Instruction inst, int i) {
		if (MultiplierBuffer.list.get(stationTag).Qj == null && MultiplierBuffer.list.get(stationTag).Qk == null) {
			System.out.println("Ready to execute multiplicationn-----------------------\n");
			inst.state = "EX";
			inst.startExecCycle = cycle + 1;
			inst.endExecCycle = cycle + inst.cyclesNeeded;
			IB.executingIndices.add(instIndex);
			IB.fetchedIndices.remove(i);
		}
	}

	public void readyToExecAdd(String stationTag, int instIndex, Instruction inst, int i) {
		if (AdderBuffer.list.get(stationTag).Qj == null && AdderBuffer.list.get(stationTag).Qk == null) {
			System.out.println("Ready to execute addition-----------------------\n");
			inst.state = "EX";
			inst.startExecCycle = cycle + 1;
			inst.endExecCycle = cycle + inst.cyclesNeeded;
			IB.executingIndices.add(instIndex);
			IB.fetchedIndices.remove(i);
		}
	}

	public void readyToExecSD(String bufferTag, int instIndex, Instruction inst, int i) {
		if (SB.list.get(bufferTag).Q == null) {
			System.out.println("Ready to execute store instruction-----------------------\n");
			inst.state = "EX";
			inst.startExecCycle = cycle + 1;
			inst.endExecCycle = cycle + inst.cyclesNeeded;
			IB.executingIndices.add(instIndex);
			IB.fetchedIndices.remove(i);
		}
	}

	public void fetch() {
		System.out.println("---------------------- Fetching ------------------------");
		if (!IB.unfetchedIndices.isEmpty()) {
			int instIndex = IB.unfetchedIndices.get(0);
			Instruction inst = IB.list[instIndex];
			System.out.println("Instruction number: " + (instIndex + 1) + "\n");

			// before fetching i have to check if there's an empty station or buffer
			if (inst.op.equals("MUL.D") || inst.op.equals("DIV.D")) {
				String stationTag = MultiplierBuffer.returnFirstEmptyRS();
				if (stationTag != null) {
					inst.fetch(cycle, stationTag);
					IB.unfetchedIndices.remove(0);
					IB.fetchedIndices.add(instIndex);
					MultiplierBuffer.list.get(stationTag).reserve(inst);
					IB.reserveReg(inst.a1, stationTag); // reserve the destination register with my tag

					// if ready -> execute next cycle:
					readyToExecMul(stationTag, instIndex, inst, 0);
				}
			}

			else if (inst.op.equals("ADD.D") || inst.op.equals("SUB.D")) {
				String stationTag = AdderBuffer.returnFirstEmptyRS();
				if (stationTag != null) {
					inst.fetch(cycle, stationTag);
					AdderBuffer.list.get(stationTag).reserve(inst);
					IB.reserveReg(inst.a1, stationTag); // reserve the destination register with my tag
					IB.unfetchedIndices.remove(0);
					IB.fetchedIndices.add(instIndex);
					// if ready -> execute next cycle:
					readyToExecAdd(stationTag, instIndex, inst, 0);
				}
			}

			else if (inst.op.equals("L.D")) {
				String bufferTag = LB.returnFirstEmptyLB();
				if (bufferTag != null) {
					inst.fetch(cycle, bufferTag);
					LB.list.get(bufferTag).reserve(inst);
					IB.reserveReg(inst.a1, bufferTag); // reserve the destination register with my tag
					IB.unfetchedIndices.remove(0);
					IB.fetchedIndices.add(instIndex);
					inst.startExecCycle = cycle + 1;
					inst.endExecCycle = cycle + inst.cyclesNeeded;
					inst.state = "EX";
					IB.executingIndices.add(instIndex);
					IB.fetchedIndices.remove(0);
					System.out.println("Ready to execute load instruction-----------------------\n");
				}
			}

			else if (inst.op.equals("S.D")) {
				String bufferTag = SB.returnFirstEmptySB();
				if (bufferTag != null) {
					inst.fetch(cycle, bufferTag);
					SB.list.get(bufferTag).reserve(inst);
					IB.unfetchedIndices.remove(0);
					IB.fetchedIndices.add(instIndex);
					readyToExecSD(bufferTag, instIndex, inst, 0);
				}
			}

		}
	}

	public static void main(String[] args) {
		System.out.println("Starting the simulation: ");
		Tomasulo simulator = new Tomasulo();
	}
}
