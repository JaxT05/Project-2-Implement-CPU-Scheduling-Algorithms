package minios;

import java.util.List;

public class SJF implements SchedulingAlgo {

    @Override
    public void addProcess(List<Process> readyQueue, Process p) {
        // Add process to queue by comparing the burst length of the next CPU instruction.
        int index = 0;
        int nextProcessBurst = p.getCurrentInstruction().remainingTicks;
        while (index < readyQueue.size() && readyQueue.get(index).getCurrentInstruction().remainingTicks <= nextProcessBurst) {
            index++;
        }
        readyQueue.add(index, p);
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
}
