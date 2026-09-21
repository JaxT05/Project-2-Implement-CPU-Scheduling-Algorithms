package minios;

import java.util.List;

public class RR implements SchedulingAlgo {

    /** Time quantum in clock ticks. */
    public static final int TIME_QUANTUM = 2;

    @Override
    public void addProcess(List<Process> readyQueue, Process p) {
        readyQueue.add(p);
    }

    @Override
    public Process selectNextProcess(List<Process> readyQueue) {
        if (readyQueue.isEmpty()){
            return null;
        }
        else{
         return readyQueue.remove(0);
        }
    }

    @Override
    public boolean shouldPreempt(Process running, int ticksUsed, int currentTime) {
        Instruction next = running.getCurrentInstruction();
        return ticksUsed >= TIME_QUANTUM
                && next != null
                && next.type == Instruction.OpType.CPU;
    }
}
