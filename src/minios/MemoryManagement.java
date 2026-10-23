package minios;

/* TODO: Project 4 - 
    Modification to Interface:
    Now needs a translate(Process p, int addr) method, that translates the given virtual
    address to the corresponding physical address for Process p.

    Modification to Implemetations (Continguous and Paged):
    - Now need to represent the physical memory using an int array, where each array
    element represents one memory unit. Array indices represents physical addresses.
    o For a STORE instruction, store the value in the array.
    o For a LOAD instruction, read the value from the array.
    (Alternatively, this could be done in the Kernel class.)
    - Need to implement the translate(Process p, int addr) method.
    - Note that a given virtual address could be invalid (i.e., outside the process’ size).
    o Your implementation needs to handle such cases by printing a warning
    message and aborting the STORE or LOAD instruction. An invalid instruction
    still consumes 1 tick.
    o After the invalid instruction is aborted, the process is immediately
    terminated (within the same tick).
 */



public interface MemoryManagement {

    /** Allocate memory for p. */
    boolean allocate(Process p, int currentTime);

    /** Return p's memory to the free pool. */
    void release(Process p, int currentTime);
}
