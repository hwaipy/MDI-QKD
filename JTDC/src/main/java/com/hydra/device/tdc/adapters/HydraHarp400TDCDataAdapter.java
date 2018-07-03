package com.hydra.device.tdc.adapters;

import com.hydra.device.tdc.TDCDataAdapter;
import java.io.IOException;
import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Arrays;

/**
 *
 * @author Hwaipy
 */
public class HydraHarp400TDCDataAdapter implements TDCDataAdapter {

    public static final int CHANNEL_SYNC = 8;
    private final ByteBuffer dataBuffer = ByteBuffer.allocate(100000000);
    private static final int UNIT_SIZE = 4;
//  private static final long COARSE_TIME_LIMIT = 1 << 28;
//  private long lastCoarseTime = -1;
//  private final long[] unitLong = new long[8];
    private final ArrayList<Long> timeEvents = new ArrayList<>(100000);
    private final int[] channelMapping;
    private final int[] antiChannelMapping;
    private final int channelBit;
    private final long maxTime;
    private long carry = 0;
    private static final long TIME_UNIT = 1 << 24;

    public HydraHarp400TDCDataAdapter(int[] channelMapping) {
        this.channelMapping = channelMapping;
        int maxChannel = 0;
        for (int channel : channelMapping) {
            if (channel > maxChannel) {
                maxChannel = channel;
            }
        }
        maxChannel++;
        antiChannelMapping = new int[maxChannel];
        Arrays.fill(antiChannelMapping, -1);
        for (int i = 0; i < channelMapping.length; i++) {
            antiChannelMapping[channelMapping[i]] = i;
        }
        channelBit = (int) Math.ceil(Math.log(channelMapping.length) / Math.log(2));
        System.out.println(channelBit);
        maxTime = Long.MAX_VALUE >> channelBit;
        dataBuffer.order(ByteOrder.LITTLE_ENDIAN);
    }

    @Override
    public Object offer(Object data) {
        if (data == null) {
            return null;
        }
        if (!(data instanceof byte[])) {
            throw new IllegalArgumentException("Input data of HydraDarp400TDCDataAdapter should be byte array, not " + data.getClass());
        }
        byte[] dataB = (byte[]) data;
        try {
            dataBuffer.put(dataB);
        } catch (BufferOverflowException e) {
            throw new IllegalArgumentException("Input data too much.", e);
        }
        dataBuffer.flip();
        timeEvents.clear();
        while (dataBuffer.hasRemaining()) {
            if (dataBuffer.remaining() < UNIT_SIZE) {
                break;
            }
            int unit = dataBuffer.getInt();
            int special = unit & 0x80000000;
            int channel = (unit & 0x7E000000) >> 25;
            int time = (unit & 0x1FFFFFF) >> 1;
            if (special == 0x80000000) {
                if (channel == 63) {
                    carry++;
                } else if (channel == 0) {
                    channel = CHANNEL_SYNC;
                    long fullTime = carry * TIME_UNIT + time;
//                    if (fullTime < lastTime) {
//                        imOrder++;
////                        System.out.println("A" + imOrder + "\t" + (lastTime - fullTime));
//                    }
//                    lastTime = fullTime;
                    int mappedChannel = antiChannelMapping[channel];
                    if (time > maxTime) {
                        throw new RuntimeException("Time (" + time + ") exceed max time limit (" + maxTime + ").");
                    }
                    long timeEvent = (fullTime << channelBit) + mappedChannel;
                    timeEvents.add(timeEvent);
                } else if (channel == 1 || channel == 2 || channel == 4 || channel == 8) {
//                    long fullTime = carry * TIME_UNIT + time;
//                    if (fullTime < lastTime) {
//                        imOrder++;
////                        System.out.println("B" + imOrder + "\t" + (lastTime - fullTime));
//                    }
//                    lastTime = fullTime;
//                    channel = channel == 1 ? 1 : (channel == 2 ? 2 : (channel == 4 ? 3 : 4));
//                    return new TimeEvent(fullTime, CHANNEL_MARKER_OFFSET + channel);
                } else {
////                    System.out.println(channel);
////                    System.out.println(reader.position());
////                    System.out.println(reader.remaining());
////                    throw new RuntimeException();
                }
            } else {
                long fullTime = carry * TIME_UNIT + time;
//                if (fullTime < lastTime) {
//                    imOrder++;
////                    System.out.println("C" + imOrder + "\t" + (lastTime - fullTime));
//                }
//                lastTime = fullTime;
                int mappedChannel = antiChannelMapping[channel];
//                if (mappedChannel < 0 || mappedChannel > 3) {
//                    System.out.println("Wrong mappedChannel: " + mappedChannel);
//                }
                if (time > maxTime) {
                    throw new RuntimeException("Time (" + time + ") exceed max time limit (" + maxTime + ").");
                }
                long timeEvent = (fullTime << channelBit) + mappedChannel;
                timeEvents.add(timeEvent);
            }
//        }
//      if (checkFrameTail()) {
//        frameCount++;
//        if (crc()) {
//          validFrameCount++;
//          int pStart = dataBuffer.position() + 8;
//          int pEnd = pStart + FRAME_SIZE - 16;
//          for (int p = pStart; p < pEnd; p += 8) {
//            parseToTimeEvent(p);
//          }
//          dataBuffer.position(dataBuffer.position() + FRAME_SIZE);
//        } else {
//          dataBuffer.position(dataBuffer.position() + FRAME_SIZE);
//        }
//      } else {
//        dataBuffer.position(dataBuffer.position() + 4);
//        skippedInSeekingHead += 4;
//      }
        }
        dataBuffer.compact();
        return timeEvents;
    }

    @Override
    public Object flush(Object data) {
        return offer(data);
    }

//  private int frameCount = 0;
//  private int validFrameCount = 0;
//  private int unknownChannelEventCount = 0;
//  private final int validEventCount[];
//  private long skippedInSeekingHead = 0;
}
