package minios;

import java.util.List;

public class RR implements SchedulingAlgo {

    /** Time quantum in clock ticks. */
    public static final int TIME_QUANTUM = 2;

    @Override
    public void addProcess(List<Process> readyQueue, Process p) {
        throw new UnsupportedOperationException("TODO: implement RR.addProcess");
    }

    @Override
    public Process selectNextProcess(List<Process> readyQueue) {
        throw new UnsupportedOperationException("TODO: implement RR.selectNextProcess");
    }

    @Override
    public boolean shouldPreempt(Process running, int ticksUsed, int currentTime) {
        throw new UnsupportedOperationException("TODO: implement RR.shouldPreempt");
    }
}
