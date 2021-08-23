package bgu.spl.mics.application.passiveObjects;

/**
 * Passive data-object representing a forest creature summoned when HanSolo and C3PO receive AttackEvents.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You may add fields and methods to this class as you see fit (including public methods).
 */
public class Ewok {
    int serialNumber;
    boolean available;

    public Ewok(int serialNumber) {
        this.serialNumber = serialNumber;
        //Upon initializing, Ewok is available
        available = true;
    }

    /**
     * Acquires an Ewok
     */
    public void acquire() {
        if (available) {
            available = false;
        }
    }

    /**
     * release an Ewok
     */
    public void release() {
        if (!available) {
            available = true;
        }

    }

    public int getSerialNumber() { return serialNumber;}

    public boolean isAvailable(){return available;}

}