package minios;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Kernel {
    private final SchedulingAlgo algo;
    private final MemoryManagement memory;
    private final List<Process> readyQueue = new ArrayList<>();
    private final List<Process> waitQueue = new ArrayList<>();
    private final List<Process> memoryQueue = new ArrayList<>();
    private final int[] physicalMemory;
    private Process runningProcess = null;
    // Number of CPU ticks the running process has consumed since it was dispatched
    private int ticksUsedByRunning = 0;

    public Kernel(SchedulingAlgo algo, MemoryManagement memory, int memorySize) {
        this.algo = algo;
        this.memory = memory;
        this.physicalMemory = new int[memorySize];
    }

    public void admitProcess(Process p, int currentTime) {
        if (memory.allocate(p, currentTime)) {
            p.state = Process.State.READY;
            algo.addProcess(readyQueue, p);
        } else {
            // Not enough memory, hold the process until another process 
			// terminates and releases memory
            System.out.print("[Tick " + currentTime + "] Process " + p.pid
                    + " waits in memory queue (needs " + p.size + " units).");
            if (memory instanceof ContiguousAllocation contiguousAllocation) {
                contiguousAllocation.printFreeList();
            } else if (memory instanceof PagedAllocation pagedAllocation) {
                pagedAllocation.printFreeFrames(p, currentTime);
            }
            memoryQueue.add(p);
        }
    }

    // Called on every clock tick
    public void onClockTick(int currentTime) {
        // check all processes currently waiting in I/O wait queue
        serviceWaitQueue(currentTime);

        boolean cpuCycleUsed = false;
        // Execute one CPU cycle
        // (One clock tick is one CPU cycle)
        while (!cpuCycleUsed) {
            // No process currently running
            if (runningProcess == null) {
                if (dispatchNextProcess(currentTime) == null){
                    // No more process to schedule; simulation finishes.
                    break;
                }
            }

            Instruction inst = runningProcess.getCurrentInstruction();

            // Current process has finished.
            if (inst == null) {
                System.out.println("[Tick " + currentTime + "] Process " + runningProcess.pid + " Terminates.");
                terminateProcess(runningProcess, currentTime);
                // In this case, no instruction is executed, so no CPU cycle
                // is used. Can't advance the simulation clock. Loop
                // back and grab the next process to execute.
                continue;
            }

            // I/O instruction: process enters I/O and no longer uses CPU
            // (Assume this transition itself doesn't consume CPU cycles.)
            else if (inst.type == Instruction.OpType.IO) {
                System.out.println("[Tick " + currentTime + "] Process " + runningProcess.pid + " enters I/O wait.");
                runningProcess.state = Process.State.BLOCKED;
                waitQueue.add(runningProcess);
                runningProcess = null;
                // Can't advance the simulation clock. Loop back
                // and grab the next process to execute.
                continue;
            }

            // CPU instruction: the instruction has not finished;
            // Utilize one CPU cycle
            else if(inst.remainingTicks > 0){
                inst.remainingTicks--;
                cpuCycleUsed = true;
                if (inst.remainingTicks == 0) {
                    if (inst.type == Instruction.OpType.STORE || inst.type == Instruction.OpType.LOAD) {
                        executeMemoryInstruction(inst, currentTime);
                    } else {
                        runningProcess.programCounter++;
                    }
                }

                // Every process still sitting in the Ready Queue has been waiting during this CPU cycle.
                for (Process p : readyQueue) {
                    p.waitingTime++;
                }

                for (Process p : memoryQueue) {
                    p.memoryQueueWaitingTime++;
                }

                // Preemption check
                // (plus runningProcess may have just been terminated by an invalid STORE/LOAD)
                ticksUsedByRunning++;
                if (runningProcess != null
                        && runningProcess.getCurrentInstruction() != null
                        && algo.shouldPreempt(runningProcess, ticksUsedByRunning, currentTime)) {
                    preemptRunningProcess(currentTime);
                }
            }

            // CPU instruction: the instruction has finished
            // (should never come here as this is already handled above,
            // but just in case...)
            else{
                runningProcess.programCounter++;
                // Can't advance the simulation clock. Loop back
                // and move to the next instruction.
                continue;
            }
        }
    }

    private void serviceWaitQueue(int currentTime) {
        Iterator<Process> it = waitQueue.iterator();
        while (it.hasNext()) {
            Process p = it.next();
            Instruction inst = p.getCurrentInstruction();
            if (inst != null) {
                inst.remainingTicks--;
                if (inst.remainingTicks <= 0) {
                    // The I/O wait has completed;
                    // load the next instruction.
                    System.out.println("[Tick " + currentTime + "] Process " + p.pid + " I/O completes.");
                    p.programCounter++;
                    p.state = Process.State.READY;
                    // Move the process to Ready Queue
                    algo.addProcess(readyQueue, p);
                    it.remove();
                }
            }
        }
    }

    // Execute a STORE or LOAD instruction for the running process
    private void executeMemoryInstruction(Instruction inst, int currentTime) {
        int physAddr = memory.translate(runningProcess, inst.address);

		// Invalid virtual address
        if (physAddr == -1) {
            System.out.println("[Tick " + currentTime + "] WARNING: Process "
                    + runningProcess.pid + " " + inst.type
                    + " instruction with invalid virtual address " + inst.address
                    + (inst.type == Instruction.OpType.STORE ? " and value " + inst.value : "")
                    + "; aborting instruction.");
            System.out.println("[Tick " + currentTime + "] Process "
                    + runningProcess.pid + " Terminates.");

            runningProcess.programCounter++;
            terminateProcess(runningProcess, currentTime);

            return;
        }

        System.out.println("[Tick " + currentTime + "] Process " + runningProcess.pid
                + " TRANSLATE: virtual address " + inst.address
                + " -> physical address " + physAddr);

        if (inst.type == Instruction.OpType.STORE) {
            physicalMemory[physAddr] = inst.value;
        } else {
            System.out.println("[Tick " + currentTime + "] Process " + runningProcess.pid
                    + " LOAD loaded value " + physicalMemory[physAddr]);
        }

        runningProcess.programCounter++;
    }

    private void terminateProcess(Process p, int currentTime) {
        memory.release(p, currentTime);
        p.state = Process.State.TERMINATED;
        p.completionTime = currentTime;
        runningProcess = null;
        admitFromMemoryQueue(currentTime);
    }

    private void admitFromMemoryQueue(int currentTime) {
        Iterator<Process> it = memoryQueue.iterator();
        while (it.hasNext()) {
            Process p = it.next();
            if (memory.allocate(p, currentTime)) {
                System.out.println("[Tick " + currentTime + "] Process " + p.pid
                        + " admitted from memory queue.");
                p.state = Process.State.READY;
                algo.addProcess(readyQueue, p);
                it.remove();
            }
        }
    }

    // Preempt the running process
    private void preemptRunningProcess(int currentTime) {
        Process p = runningProcess;
        System.out.println("[Tick " + currentTime + "] Process " + p.pid + " preempted; back to Ready Queue.");
        p.state = Process.State.READY;
        algo.addProcess(readyQueue, p);
        runningProcess = null;
    }

    private Process dispatchNextProcess(int currentTime) {
        runningProcess = algo.selectNextProcess(readyQueue);
        if (runningProcess != null) {
            System.out.println("[Tick " + currentTime + "] Process " + runningProcess.pid + " executes.");
            runningProcess.state = Process.State.RUNNING;
            ticksUsedByRunning = 0;
            return runningProcess;
        }else{
            return null;
        }

    }

    public boolean isIdle() {
        return runningProcess == null &&
                readyQueue.isEmpty() &&
                waitQueue.isEmpty() &&
                memoryQueue.isEmpty();
    }
}