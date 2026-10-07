package minios;

public interface MemoryManagement {

    /** Allocate memory for p. */
    boolean allocate(Process p);

    /** Return p's memory to the free pool. */
    void release(Process p);
}
