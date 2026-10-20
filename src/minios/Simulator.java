package minios;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Simulator {
    private final Kernel kernel;
    private final List<Process> incomingProcesses;
    private final List<Process> allProcesses;
    private int clock = 0;

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
                    System.out.println("[Tick " + clock + "] Process " + p.pid + " arrives.");
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
        for (Process p : allProcesses) {
            totalWaiting += p.waitingTime;
        }
        System.out.printf("Average waiting time: %.2f ticks%n",
                totalWaiting / allProcesses.size());
    }

    public static void main(String[] args) throws Exception{
            List<Process> workload = TraceParser.parseWorkload("workload.txt");
            int MEMORY_SIZE = 120;
            int PAGE_SIZE = 10;

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

			MemoryManagement memory = new ContiguousAllocation();

			if (args.length > 1) {
				String arg = args[1].toLowerCase();
				memory = switch (arg) {
					case "contiguous" -> new ContiguousAllocation();
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
