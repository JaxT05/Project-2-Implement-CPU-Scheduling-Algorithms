package minios;

import java.util.List;

public interface SchedulingAlgo {
    // Add a process to the Ready Queue
    void addProcess(List<Process> readyQueue, Process p);

    // Select and return the next process to execute,
    // according to the scheduling algorithm.
    // If no process is available (i.e., Ready Queue is entry),
    // return null
    Process selectNextProcess(List<Process> readyQueue);

    // Preemption hook: called by the kernel after the running process has
    // consumed one full CPU tick of its current dispatch.
    default boolean shouldPreempt(Process running, int ticksUsed, int currentTime) {
        return false;
    }
}
