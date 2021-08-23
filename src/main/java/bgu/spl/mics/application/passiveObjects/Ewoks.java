package bgu.spl.mics.application.passiveObjects;

import bgu.spl.mics.Message;
import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.AttackEvent;

import java.util.*;

/**
 * Passive object representing the resource manager.
 * <p>
 * This class must be implemented as a thread-safe singleton.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You can add ONLY private methods and fields to this class.
 */
public class Ewoks {

    private Map<Integer, Ewok> resources;

    private Ewoks() {
    }

    public void init(int numOfEwoks) {
        resources = new HashMap<>();
        for (Integer i = 1; i <= numOfEwoks; i++)
            resources.put(i, new Ewok(i));
    }

    private static class ewoksHolder {
        private static final Ewoks instance = new Ewoks();
    }

    public static Ewoks getInstance() {
        return ewoksHolder.instance;
    }

    //Each attempt for acquire Ewoks resources will executed as follow:
    //1. if the current desired Ewok is available, add it to attained list
    //if all necessary Ewoks has been attained, set stop=true proceed to execute the attack
    //2. Otherwise, release already acquired Ewoks(from attained),
    // insert current Attack Event to Microservice requester queue, proceed for next Attack Event

    public List<Ewok> tryAcquire(MicroService requester) {
        Queue<Message> myQueue= MessageBusImpl.getInstance().getQueue(requester);
        List<Ewok> attained = new ArrayList<>();
        boolean stop=false;
        for(Message message : myQueue){
            //According to the scenario, at this stage only AttackEvent might be at Han/C3PO queue
            AttackEvent a=(AttackEvent) message;
            for (Integer i : a.getSerials()) {
                Ewok tryGetMe = resources.get(i);
                if (!tryGetMe.isAvailable()) {
                    releaseResources(attained);
                    myQueue.add(a);
                    break;
                }//if
                else {
                    attained.add(tryGetMe);
                    //if(stop)-> all required Ewoks has been obtained
                    stop= (attained.size() == a.getSerials().size());
                    if (stop)
                        break;
                }//else
            }//inner for
            if(stop)
                break;
        }//outer for
        return attained;
    }

    public void releaseResources(List<Ewok> toRelease) {
        for (Ewok freeMe : toRelease) {
            freeMe.release();
        }
    }

}

