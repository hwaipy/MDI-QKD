package com.hydra.device.tdc.adapters;

import com.hydra.device.tdc.TDCDataAdapter;
import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.util.ArrayList;

/**
 *
 * @author Hwaipy
 */
public class SimpleTDCDataAdapter implements TDCDataAdapter {

    private final ByteBuffer dataBuffer = ByteBuffer.allocate(100000000);
    private final ArrayList<Long> timeEvents = new ArrayList<>(100000);

    public SimpleTDCDataAdapter() {
    }

    @Override
    public Object offer(Object data) {
        if (data == null) {
            return null;
        }
        if (!(data instanceof byte[])) {
            throw new IllegalArgumentException("Input data of SimpleTDCDataAdapter should be byte array, not " + data.getClass());
        }
        byte[] dataB = (byte[]) data;
        try {
            dataBuffer.put(dataB);
        } catch (BufferOverflowException e) {
            throw new IllegalArgumentException("Input data too much.", e);
        }
        dataBuffer.flip();
        timeEvents.clear();
        while (dataBuffer.remaining() >= 8) {
            long unit = dataBuffer.getLong();
            timeEvents.add(unit);
        }
        dataBuffer.compact();
        return timeEvents;
    }

    @Override
    public Object flush(Object data) {
        return offer(data);
    }
}
