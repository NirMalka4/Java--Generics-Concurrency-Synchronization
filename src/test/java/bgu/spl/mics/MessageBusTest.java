package bgu.spl.mics;

import bgu.spl.mics.application.messages.AttackEvent;
import bgu.spl.mics.application.passiveObjects.Attack;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Vector;

import static org.junit.jupiter.api.Assertions.*;

class MessageBusTest {
    private MessageBus busTest;
    private MicroService serviceTest;

    @BeforeEach
    public void setUp() {
        busTest=MessageBusImpl.getInstance();
        serviceTest= new MicroService("MicroserviceTestUtil") {
            @Override
            protected void initialize() {
                subscribeEvent(AttackEvent.class,(AttackEvent a)->{return;});
                try{
                    busTest.awaitMessage(serviceTest);
                }catch (InterruptedException e){
                    fail();
                    e.printStackTrace();
                }
            }
        };
    }

    @Test
    void subscribeEvent() {
        try{
            busTest.subscribeEvent(AttackEvent.class,serviceTest);
            fail("Subscribe failed");
        }catch (Exception e){};
        busTest.register(serviceTest);
        busTest.subscribeEvent(AttackEvent.class,serviceTest);
        busTest.unregister(serviceTest);
    }

    @Test
    public void testSubscribeBroadcast() {
        try{
            busTest.subscribeBroadcast(Broadcast.class,serviceTest);
            fail("Subscribe failed");
        }catch (Exception e){};
        busTest.register(serviceTest);
        busTest.subscribeBroadcast(Broadcast.class,serviceTest);
        busTest.unregister(serviceTest);
    }

    @Test
    void complete() {

    }

    @Test
    void sendBroadcast() {
    }

    @Test
    void sendEvent() {
    }

    @Test
    public void register() {
        busTest.register(serviceTest);
    }

    @Test
    void unregister() {
        busTest.unregister(serviceTest);
    }

    /*@Test
    public void testAwaitMessage() throws InterruptedException {

        Thread threadEvent= new Thread(serviceTest);
        threadEvent.join();
        Thread.sleep(400);
        busTest.sendEvent(new AttackEvent(new Vector<Integer>(), 6,1) {
        });
        Thread.sleep(2000);
        if(threadEvent.isAlive())
            fail("Thread should be terminated");

                /*
        Thread threadBroadcast= new Thread(serviceTest);
        threadBroadcast.join();
        Thread.sleep(200);
        busTest.sendBroadcast(new Broadcast() {
        });
        Thread.sleep(1000);
        if(threadBroadcast.isAlive())
            fail("Thread should be terminated");

         */
    }
