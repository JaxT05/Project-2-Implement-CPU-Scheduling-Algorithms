package minios;

import java.util.HashMap;

public class PagedAllocation implements MemoryManagement {

    int MEMORY_SIZE;
    int PAGE_SIZE;
    int NUM_PAGES;

    public PagedAllocation(int MEMORY_SIZE, int PAGE_SIZE) {
        this.MEMORY_SIZE = MEMORY_SIZE;
        this.PAGE_SIZE = PAGE_SIZE;
        this.NUM_PAGES = MEMORY_SIZE / PAGE_SIZE;
    }

    HashMap<Integer, Integer> pageTable = new HashMap<>();

    @Override
    public boolean allocate(Process p) {
        // TODO: do stuff
        return false;
    }

    @Override
    public void release(Process p) {
        // TODO: do stuff
    }
}
