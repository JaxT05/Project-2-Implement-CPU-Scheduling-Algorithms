package minios;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/* TODO: Project 4 - 

    Modification to Trace Format and Instruction OpType:
    The old CPU instruction is replaced by three more specific instructions:
    COMPUTE n: an instruction that only utilizes CPU and not memory (in practice, this
    would be an arithmetic or logic instruction)
    STORE addr value: an instruction that stores the given value at the given virtual
    address. This instruction is always 1 tick.
    LOAD addr: an instruction that loads the value stored at the given virtual address.
    This instruction is always 1 tick. In the trace, a LOAD always come after a STORE to
    the same virtual address.
    The above three instructions are all CPU instructions, so a CPU burst is now a run of
    these instructions.
    We provide the updated workload trace, and the updated TraceParser and
    Instruction classes.
    
    Required Output:
    - Each message should start with a timestamp [Tick X]
    - Each time a virtual address is translated, print out both the virtual and physical
    addresses
    - Each time a LOAD instruction executes, print out the value loaded
    - Each time an invalid address is detected when executing a STORE or LOAD
    instruction, print out a warning message including the process ID and the
    instruction details.

    This implementation will break SJF and RR due to how CPU instructions are now handled. 
    SJF and RR can be fixed to accommodate this but it's doesn't have to be within the scope of this project
 */

public class Simulator {
    private final Kernel kernel;
    private final List<Process> incomingProcesses;
    private final List<Process> allProcesses;
    private int clock = 0;
    final static int MEMORY_SIZE = 120;
    final static int PAGE_SIZE = 10;

    public Simulator(Kernel kernel, List<Process> processes) {
        this.kernel = kernel;
        this.incomingProcesses = processes;
        this.allProcesses = new ArrayList<>(processes);
    }

    public void run() {
        // Continue the simulation as long as:
        // 1. there are more processes to come; or
        // 2. one or more existing processes have not completed yet
        while (!incomingProcesses.isEmpty() || !kernel.isIdle()) {
            // Step 1: Check if any processes should arrive now
            Iterator<Process> it = incomingProcesses.iterator();
            while (it.hasNext()) {
                Process p = it.next();
                if (p.arrivalTime == clock) {
                    System.out.println("[Tick " + clock + "] Process " + p.pid + " arrives (" + p.size + " units)." );
                    kernel.admitProcess(p, clock);
                    it.remove();
                }
            }

            // Step 2: perform tasks that need to happen during this clock tick
            kernel.onClockTick(clock);

            // Step 3: Advance the simulation clock
            clock++;
        }

        printAverageWaitingTime();
    }

    // Average time each process spent waiting in the Ready Queue
    private void printAverageWaitingTime() {
        double totalWaiting = 0;
        double totalMemoryQueueWaiting = 0;
        
        for (Process p : allProcesses) {
            totalWaiting += p.waitingTime;
            totalMemoryQueueWaiting += p.memoryQueueWaitingTime;
        }
        System.out.printf("Average Ready Queue waiting time: %.2f ticks%nAverage Memory Queue waiting time %.2f ticks%n",
                totalWaiting / allProcesses.size(), totalMemoryQueueWaiting / allProcesses.size());
    }

    public static void main(String[] args) throws Exception{
            List<Process> workload = TraceParser.parseWorkload("workload.txt");


			SchedulingAlgo algo = new FCFS();

			if (args.length > 0) {
				String arg = args[0].toLowerCase();
				algo = switch (arg) {
					case "fcfs" -> new FCFS();
					case "sjf" -> new SJF();
					case "rr" -> new RR();
					default -> null;
				};

				if (algo == null) {
					System.out.printf("Chosen algorithm \"%s\" does not exist!\n", arg);
					System.exit(1);
				}
			}

			MemoryManagement memory = new ContiguousAllocation(MEMORY_SIZE);

			if (args.length > 1) {
				String arg = args[1].toLowerCase();
				memory = switch (arg) {
					case "contiguous" -> new ContiguousAllocation(MEMORY_SIZE);
					case "paged" -> new PagedAllocation(MEMORY_SIZE, PAGE_SIZE);
					default -> null;
				};


				if (memory == null) {
					System.out.printf("Chosen memory scheme \"%s\" does not exist!\n", arg);
					System.exit(1);
				}
			}

            Kernel kernel = new Kernel(algo, memory);
            Simulator sim = new Simulator(kernel, workload);

            sim.run();
    }
}
