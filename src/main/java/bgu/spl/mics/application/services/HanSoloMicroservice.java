package bgu.spl.mics.application.services;
import bgu.spl.mics.*;
import bgu.spl.mics.application.broadcasts.killCommand;
import bgu.spl.mics.application.messages.AttackEvent;
import bgu.spl.mics.application.passiveObjects.Diary;
import bgu.spl.mics.application.passiveObjects.Ewok;
import bgu.spl.mics.application.passiveObjects.Ewoks;

import java.util.List;

/**
 * HanSoloMicroservices is in charge of the handling {@link AttackEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link AttackEvent}.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class HanSoloMicroservice extends MicroService {

    public HanSoloMicroservice() {
        super("Han");
    }

    @Override
    //Both Han Solo and C3P0 will try to acquire Ewoks simultaneously for their attacks
    //Afterwards they will sleep,release their resources and update latch(CountDownLatch Object)
    protected void initialize() {
        //Define callback function for AttackEvent and Kill Command(termination)
        super.subscribeBroadcast(killCommand.class,(killCommand c)->{
            super.terminate();
            Diary.getInstance().setHanSoloTerminate(System.currentTimeMillis());
        });
        super.subscribeEvent(AttackEvent.class,(AttackEvent c)-> {
            List<Ewok> attained = Ewoks.getInstance().tryAcquire(this);
            try{
                Thread.sleep(c.getDuration());
            }catch (InterruptedException ignored){}
            Ewoks.getInstance().releaseResources(attained);
            super.complete(c,true);
            c.getLatch().countDown();
            Diary.getInstance().getTotalAttacks().incrementAndGet();
            if(c.getLatch().getCount()<=1){
                Diary.getInstance().setHanSoloFinish(System.currentTimeMillis());
            }
        });
    }
}
