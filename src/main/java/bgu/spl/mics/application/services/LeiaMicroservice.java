package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.broadcasts.killCommand;
import bgu.spl.mics.application.messages.AttackEvent;
import bgu.spl.mics.application.messages.BombDestroyerEvent;
import bgu.spl.mics.application.messages.DeactivationEvent;
import bgu.spl.mics.application.passiveObjects.Attack;
import bgu.spl.mics.application.passiveObjects.Diary;

import java.util.concurrent.CountDownLatch;


/**
 * LeiaMicroservices Initialized with Attack objects, and sends them as  {@link AttackEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link AttackEvent}.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class LeiaMicroservice extends MicroService {

	private Attack[] attacks;
    //Used for allocating unique key for each Message sent by Leia during the program

	
    public LeiaMicroservice(Attack[] attacks) {
        super("Leia");
        this.attacks = attacks;
    }

    @Override
    //Leia is the only Microservice whom dispatches Events throughout the program
    //Each Event will have CountDownLatch object for allowing trace after it's completion.

    protected void initialize() {
        //Sleep for 10 millisecond to allow services to subscribe
        try{
            Thread.sleep(4000);
        }catch(InterruptedException ignored){}
        super.subscribeBroadcast(killCommand.class,(killCommand c)->{
            super.terminate();
            Diary.getInstance().setLeiaTerminate(System.currentTimeMillis());
        });
        //Distribute each Attack object as Attack Event
        CountDownLatch latch= new CountDownLatch(attacks.length);
        for (Attack a : attacks) {
            super.sendEvent(new AttackEvent(a.getSerials(), a.getDuration(),latch));
        }
        //Leia will await until her Attack Event will be completed
        try {
            latch.await();
        }catch (InterruptedException ignored){};
        //Proceed for Deactivation Event
        CountDownLatch latchR2D2=new CountDownLatch(1);
        super.sendEvent(new DeactivationEvent(latchR2D2));
        try {
            latchR2D2.await();
        }catch (InterruptedException ignored){};
        //Proceed for Bomb Destroyer Event
        CountDownLatch latchLando=new CountDownLatch(1);
        super.sendEvent(new BombDestroyerEvent(latchLando));
        try {
            latchLando.await();
        }catch (InterruptedException ignored){}
        //Proceed to termination step
        killCommand killCommand= new killCommand();
        super.sendBroadcast(killCommand);
    }

}

