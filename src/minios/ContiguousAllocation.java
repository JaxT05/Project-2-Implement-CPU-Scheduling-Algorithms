package minios;

import java.util.LinkedList;

public class ContiguousAllocation implements MemoryManagement {

    // TODO: initialize the free list with the entire memory space (100 units)

    public LinkedList<int[]> freelist = new LinkedList<>();

    public ContiguousAllocation() {
        freelist.add(new int[]{0, 100});
    }

    @Override
    public boolean allocate(Process p) {
        // TODO: put process in first fitting space

        for (int i = 0; i < freelist.size(); i++){
            int[] block = freelist.get(i);

            int start = block[0];
            int size = block[1];

            if (size >= p.size){
                p.baseAddress = start;
                freelist.remove(i);

                if (size > p.size) {
                    freelist.add(i, new int[]{
                            start + p.size,
                            size - p.size
                    });
                }
                return true;
            }

        }

        return false;
    }

    @Override
    public void release(Process p) {
        // TODO: free process from list with coalescing
        if (p.baseAddress != -1){
            freelist.add(new int[]{
                    p.baseAddress,
                    p.size
            });

            //sort region by start address
            freelist.sort((a, b) -> Integer.compare(a[0], b[0]));

            //merge free regions
            int i = 0;
            while (i < freelist.size() - 1){
                int[] block1 = freelist.get(i);
                int start1 = block1[0];
                int size1 = block1[1];

                int[] block2 = freelist.get(i+1);
                int start2 = block2[0];
                int size2 = block2[1];

                if (start1 + size1 == start2){
                    block1[1] = size1 + size2;
                    freelist.remove(i+1);
                }
                else {
                    i++;
                }
            }
            p.baseAddress = -1;
        }
    }
}
