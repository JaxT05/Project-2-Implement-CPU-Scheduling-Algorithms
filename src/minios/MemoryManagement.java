package minios;

public interface MemoryManagement {

    /** Allocate memory for p. */
    boolean allocate(Process p, int currentTime);

    /** Return p's memory to the free pool. */
    void release(Process p, int currentTime);

    /**
     * Translate the given virtual address to the corresponding physical address for Process p.
     * Returns -1 if the address is invalid (i.e., outside the process' size).
     */
    int translate(Process p, int addr);
}
