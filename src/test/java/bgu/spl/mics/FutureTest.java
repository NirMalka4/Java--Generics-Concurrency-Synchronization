package bgu.spl.mics;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

class FutureTest {

    private Future<String> test;
    private static final String testUtil= "Test";

    @BeforeEach
    public void setUp() {
        test= new Future<>();
    }

    // Added for get tests
    private class MicroServiceResolve implements Runnable{
        @Override
        public void run() {
            test.resolve("resolved");
        }
    }

    // Added for get tests
    private class MicroServiceGet implements Runnable{
        @Override
        public void run() {
            test.get();
        }
    }

    // Added for get tests
    private class MicroServiceGetTimeout implements Runnable{
        private int time;

        public MicroServiceGetTimeout(int time){
            this.time = time;
        }
        @Override
        public void run() {
            test.get(time, TimeUnit.MICROSECONDS);
        }
    }

    @Test
    public void testBlockingGet() {
        // Added MicroServices for the test (and threads)
        try{
            assertFalse(test.isDone());
            Thread service1 = new Thread(new MicroServiceGet());
            Thread service2 = new Thread(new MicroServiceResolve());
            service1.start();
            //Expecting the thread to wait until being resolved
            assertFalse(test.isDone());
            service2.start();
            service2.join();
            assertTrue(test.isDone());
            assertEquals(test.get(),"resolved");
        }
        catch (InterruptedException e) { assertTrue(false, "Test MicroServices interrupted");}
    }

    @Test
    public void testResolve() {
        assertFalse(test.isDone());
        test.resolve(testUtil);
        assertTrue(test.isDone());
        assertEquals(testUtil,test.get());
    }

    @Test
    public void testIsDone() {
        assertFalse(test.isDone());
    }

    @Test
    public void testGetWithTimeout() {
        // Added MicroServices for the test (and threads)
        assertFalse(test.isDone());
        test.get(100, TimeUnit.MILLISECONDS);
        assertFalse(test.isDone());
        test.resolve("Test");
        assertEquals(test.get(100, TimeUnit.MILLISECONDS), testUtil);
    }
}