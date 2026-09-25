package minios;

import java.util.ArrayList;
import java.util.List;

public class Process {
    public enum State { NEW, READY, RUNNING, BLOCKED, TERMINATED }

    public final int pid;
    public final int arrivalTime;
    public final int requiredMemory;

    public State state = State.NEW;
    public final List<Instruction> code;
    public int programCounter = 0;
    public int waitingTime = 0;
    public int completionTime = -1;

    
    /*
     TODO:
     size(limit register): read from the trace parser file
     relocation register (base memory address): specified by the kernel
     */

    public Process(int pid, int arrivalTime, int requiredMemory, List<Instruction> code) {
        this.pid = pid;
        this.arrivalTime = arrivalTime;
        this.requiredMemory = requiredMemory;
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