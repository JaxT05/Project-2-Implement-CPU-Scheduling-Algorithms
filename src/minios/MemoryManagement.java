package minios;

public interface MemoryManagement {

    /** Allocate memory for p. */
    boolean allocate(Process p, int currentTime);

    /** Return p's memory to the free pool. */
    void release(Process p, int currentTime);
}
