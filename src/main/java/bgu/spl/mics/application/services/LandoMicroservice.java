package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.broadcasts.killCommand;
import bgu.spl.mics.application.messages.BombDestroyerEvent;
import bgu.spl.mics.application.passiveObjects.Diary;

/**
 * LandoMicroservice
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class LandoMicroservice  extends MicroService {
    private long duration;

    public LandoMicroservice(long duration) {
        super("Lando");
        this.duration=duration;
    }

    @Override
    //After being summoned,Lando will sleep,update latch(CountDownLatch Object)
    //and dispatch the kill command(used to execute simultaneous termination by all involving thread)
    protected void initialize() {
        //Define callback function for Deactivation Event and mutual termination
        super.subscribeBroadcast(killCommand.class, (killCommand c) -> {
            super.terminate();
            Diary.getInstance().setLandoTerminate(System.currentTimeMillis());
        });
        super.subscribeEvent(BombDestroyerEvent.class, (BombDestroyerEvent c) -> {
            try {
                Thread.sleep(duration);
            } catch (InterruptedException ignored) {
            }
            super.complete(c, true);
            c.getLatch().countDown();
        });
    }
}
