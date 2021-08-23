package bgu.spl.mics.application.passiveObjects;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EwokTest {
    private Ewok test;

    @BeforeEach
    public void setUp() {
        test=new Ewok(316534072);
    }

    @Test
    public void testAcquire() throws InterruptedException {
        assertTrue(test.isAvailable());
        test.acquire();
        assertFalse(test.isAvailable());
    }

    @Test
    public void testRelease() {
        assertFalse(test.isAvailable());
        test.release();
        assertTrue(test.isAvailable());
    }

    @Test
    public void testGetSerialNumber() {
        assertEquals(316534072,test.getSerialNumber());
        assertNotEquals(316534073,test.getSerialNumber());
    }

    @Test
    void isAvailable() throws InterruptedException {
        assertTrue(test.isAvailable());
        test.acquire();
        assertFalse(test.isAvailable());
        test.release();
        assertTrue(test.isAvailable());
    }
}