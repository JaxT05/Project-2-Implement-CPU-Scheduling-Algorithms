package minios;

import java.util.ArrayList;
import java.util.List;

public class Process {
    public enum State { NEW, READY, RUNNING, BLOCKED, TERMINATED }

    public final int pid;
    public final int arrivalTime;
    public final int size;

    public State state = State.NEW;
    public final List<Instruction> code;
    public int programCounter = 0;
    public int waitingTime = 0;
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