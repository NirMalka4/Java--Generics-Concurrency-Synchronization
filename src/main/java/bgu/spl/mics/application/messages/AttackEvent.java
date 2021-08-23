package bgu.spl.mics.application.messages;
import bgu.spl.mics.Event;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class AttackEvent implements Event<Boolean> {

    private final List<Integer> serials;
    private final long duration;
    private final CountDownLatch latch;

    public AttackEvent(List<Integer> serialNumbers, long duration,CountDownLatch latch){
        serials=serialNumbers;
        Collections.sort(serials);
        this.duration=duration;
        this.latch=latch;
    }

    public List<Integer> getSerials(){return serials;}
    public long getDuration(){return duration;}
    public CountDownLatch getLatch(){return latch;}

}
