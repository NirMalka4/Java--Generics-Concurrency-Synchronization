package bgu.spl.mics.application.passiveObjects;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Passive data-object representing a Diary - in which the flow of the battle is recorded.
 * We are going to compare your recordings with the expected recordings, and make sure that your output makes sense.
 * <p>
 * Do not add to this class nothing but a single constructor, getters and setters.
 */
public class Diary {
    private AtomicInteger totalAttacks;
    private long HanSoloFinish,C3POFinish,R2D2Deactivate,LeiaTerminate,HanSoloTerminate,
    C3POTerminate,R2D2Terminate,LandoTerminate;



    private Diary() {
        totalAttacks=new AtomicInteger(0);
    }

    private static class diaryHolder {
        private static final Diary instance = new Diary();
    }

    public static Diary getInstance() {
        return Diary.diaryHolder.instance;
    }

    public AtomicInteger getTotalAttacks(){return totalAttacks;}
    public int getTotal(){return totalAttacks.get();
    }
    public void setHanSoloFinish(long finish){ HanSoloFinish=finish;}
    public long getHanSoloFinish(){return HanSoloFinish;}

    public void setC3POFinish(long finish){ C3POFinish=finish;}
    public long getC3POFinish(){return C3POFinish;}

    public void setR2D2Deactivate(long finish){R2D2Deactivate=finish;}
    public long getR2D2Deactivate(){return R2D2Deactivate;}

    public void setHanSoloTerminate(long finish){ HanSoloTerminate=finish;}
    public long getHanSoloTerminate(){return HanSoloTerminate;}

    public void setC3POTerminate(long finish){ C3POTerminate=finish;}
    public long getC3POTerminate(){return C3POTerminate;}

    public void setLeiaTerminate(long finish){ LeiaTerminate=finish;}
    public long getLeiaTerminate(){return LeiaTerminate;}

    public void setR2D2Terminate(long finish){ R2D2Terminate=finish;}
    public long getR2D2Terminate(){return R2D2Terminate;}

    public void setLandoTerminate(long finish){ LandoTerminate=finish;}
    public long getLandoTerminate(){return LandoTerminate;}


}
