package minios;
import java.util.Arrays;

public class PagedAllocation implements MemoryManagement {

    int MEMORY_SIZE;
    int PAGE_SIZE;

    public PagedAllocation(int MEMORY_SIZE, int PAGE_SIZE) {
        this.MEMORY_SIZE = MEMORY_SIZE;
        this.PAGE_SIZE = PAGE_SIZE;
    }
    
    @Override
    public boolean allocate(Process p, int currentTime) {

        p.pageTable = new int[MEMORY_SIZE / PAGE_SIZE];

        Arrays.fill(p.pageTable, -1);

        //add allocation
        return false;
    }

    @Override
    public void release(Process p, int currentTime) {
        if (p.pageTable == null) return;
        
        Arrays.fill(p.pageTable, -1);

        //add release
    }

    public void printPageTable(Process p, int currentTime) {
        if (p.pageTable == null) {
            System.out.println("[Tick " + currentTime + "] Process " + p.pid + " has no allocated memory.");
            return;
        }
        int i = 0;
        while (i < p.pageTable.length - 1) {
            System.out.print("Page " + i + ": ");
            if (i < p.pageTable.length && p.pageTable[i] != -1) {
                System.out.println("Frame " + p.pageTable[i]);
            } else {
                System.out.println("Not allocated");
            }
            i++;
        }
    }
}
