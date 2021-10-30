package cw2.part_c;

import cw2.part_a.BinarySemaphore;
import cw2.part_a.MySemaphore;

public class GeneralSemaphore implements MySemaphore {

    private int sources;
    private final BinarySemaphore areSourcesEmpty;
    private final BinarySemaphore criticalSectionSemaphore;


    public GeneralSemaphore(int sources){
        this.sources = sources;
        this.areSourcesEmpty = new BinarySemaphore();
        this.criticalSectionSemaphore = new BinarySemaphore();
    }

    public GeneralSemaphore(){
        this(0);
    }

    @Override
    public void P() {
        areSourcesEmpty.P();

        criticalSectionSemaphore.P();

        sources--;
        if (sources > 0)
            areSourcesEmpty.V();
        criticalSectionSemaphore.V();
    }

    @Override
    public void V() {
        criticalSectionSemaphore.P();
        sources++;
        if (sources == 1){
            areSourcesEmpty.V();
        }
        criticalSectionSemaphore.V();
    }
}
