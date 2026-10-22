package minios;
import java.util.Arrays;

public class PagedAllocation implements MemoryManagement {
    int PAGE_SIZE;
    int totalFrames;
    boolean[] freeFrames;

    public PagedAllocation(int MEMORY_SIZE, int PAGE_SIZE) {
        this.PAGE_SIZE = PAGE_SIZE;
        this.totalFrames = MEMORY_SIZE / PAGE_SIZE;
        this.freeFrames = new boolean[totalFrames];
        Arrays.fill(this.freeFrames, true);
    }

    @Override
    public boolean allocate(Process p, int currentTime) {
        int processPageNumber = (p.size + PAGE_SIZE - 1) / PAGE_SIZE;
        p.pageTable = new int[processPageNumber];
        Arrays.fill(p.pageTable, -1);

        int availableFrames = 0;
        for (boolean isFrameFree : freeFrames) {
            if (isFrameFree) availableFrames++;
        }

        if (processPageNumber > availableFrames) {
            p.pageTable = null;
            return false;
        }

        int allocatedPages = 0;
        for (int i = 0; ((i < totalFrames) && (allocatedPages < processPageNumber)); i++) {
            if (freeFrames[i]) {
                freeFrames[i] = false;
                p.pageTable[allocatedPages] = i;
                allocatedPages++;
            }
        }
        System.out.println("[Tick " + currentTime + "] Process " + p.pid + " allocated " + processPageNumber + " pages.");
        return true;
    }

    @Override
    public void release(Process p, int currentTime) {
        if (p.pageTable == null) return;
        
        for (int index : p.pageTable) {
            if (index != -1 && index < freeFrames.length) {
                freeFrames[index] = true;
            }
        }
        p.pageTable = null;
    }

    public void printFreeFrames(Process p, int currentTime) {
        int freeFrameCount = 0;
        for (boolean isFreeFrame : freeFrames) {
            if (isFreeFrame) freeFrameCount++;
        }
        System.out.println(" There are " + freeFrameCount + " available frames.");
    }

    public void printPageTable(Process p, int currentTime) {
        if (p.pageTable == null) {
            System.out.println("[Tick " + currentTime + "] Process " + p.pid + " has no allocated memory.");
            return;
        }

        System.out.print("[Tick " + currentTime + "] Process " + p.pid + " Page Table: ");
        for (int i = 0; i < p.pageTable.length; i++) {
            System.out.print("[Page " + i + ": ");
            if (p.pageTable[i] != -1) {
                System.out.print("Frame " + p.pageTable[i]);
            } else {
                System.out.print("Free");
            }
            System.out.print("]");
            if (i < p.pageTable.length - 1) {
                System.out.print(", ");
            }
        }
        System.out.println();
    }
}
