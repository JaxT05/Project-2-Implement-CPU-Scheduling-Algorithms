package minios;

import java.util.ArrayList;
import java.util.List;


 /* TODO: Project 4 -
    Because there is no longer a separate CPU instruction, the CPU burst is now a run
    of consecutive COMPUTE/STORE/LOAD instructions.
    - We provide the updated Process class which now contains a new method
    currentCPUBurstLength() for returning the current CPU burst length, needed for SJF
    scheduling. (However, you’re not required to retro-Rit your CPU scheduling
    algorithm implementation; the grading of this project will only be based on FCFS.)
 */


public class Process {
    public enum State { NEW, READY, RUNNING, BLOCKED, TERMINATED }

    public final int pid;
    public final int arrivalTime;
    public final int size;

    public State state = State.NEW;
    public final List<Instruction> code;
    public int programCounter = 0;
    public int waitingTime = 0;
    public int memoryQueueWaitingTime = 0;
    public int completionTime = -1;

    
    // required for Contiguous allocation only
    public int baseAddress = -1;

    // required for Paged allocation only
    public int[] pageTable = null;

    public Process(int pid, int arrivalTime, int size, List<Instruction> code) {
        this.pid = pid;
        this.arrivalTime = arrivalTime;
        this.size = size;
        this.code = new ArrayList<>(code);
    }

    public Instruction getCurrentInstruction() {
        if (programCounter < code.size()){
            return code.get(programCounter);
        }else{
            return null;
        }
    }


}