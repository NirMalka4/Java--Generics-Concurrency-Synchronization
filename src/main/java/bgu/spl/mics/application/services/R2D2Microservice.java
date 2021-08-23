package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.broadcasts.killCommand;
import bgu.spl.mics.application.messages.DeactivationEvent;
import bgu.spl.mics.application.passiveObjects.Diary;

/**
 * R2D2Microservices is in charge of the handling {@link DeactivationEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link DeactivationEvent}.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class R2D2Microservice extends MicroService {
    private long duration;

    public R2D2Microservice(long duration) {
        super("R2D2");
        this.duration=duration;
    }

    @Override
    protected void initialize() {
        //Define callback function for Deactivation Event and mutual termination
        super.subscribeBroadcast(killCommand.class,(killCommand c)->{
            super.terminate();
            Diary.getInstance().setR2D2Terminate(System.currentTimeMillis());
        });
        super.subscribeEvent(DeactivationEvent.class, (DeactivationEvent c)-> {
            try {
                Thread.sleep(duration);
            } catch (InterruptedException ignored) {};
            R2D2Microservice.super.complete(c, true);
            c.getLatch().countDown();
            Diary.getInstance().setR2D2Deactivate(System.currentTimeMillis());
        });
    }
}
