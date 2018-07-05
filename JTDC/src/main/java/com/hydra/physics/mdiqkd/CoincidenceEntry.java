package com.hydra.physics.mdiqkd;

import com.hydra.test.groundtdcserver.AppFrame;

/**
 *
 * @author Administrator
 */
public class CoincidenceEntry {

    private final long index;
    private final int resultMode;
    private final long time1;
    private final long time2;
    private final AppFrame.RandomNumberEntry randomNumberEntry;

    public CoincidenceEntry(long index, int resultMode, long time1, long time2, AppFrame.RandomNumberEntry randomNumberEntry) {
        this.index = index;
        this.resultMode = resultMode;
        this.time1 = time1;
        this.time2 = time2;
        this.randomNumberEntry = randomNumberEntry;
    }

    public long getIndex() {
        return index;
    }

    public int getResultMode() {
        return resultMode;
    }

    public long getTime1() {
        return time1;
    }

    public long getTime2() {
        return time2;
    }

    public AppFrame.RandomNumberEntry getRandomNumberEntry() {
        return randomNumberEntry;
    }
}
