package bgu.spl.mics.application.messages;
import bgu.spl.mics.Event;

import java.util.concurrent.CountDownLatch;

public class BombDestroyerEvent implements Event<Boolean> {
    private CountDownLatch latch;

    public BombDestroyerEvent(CountDownLatch latch){
        this.latch=latch;
    }

    public CountDownLatch getLatch() { return latch;}

}
