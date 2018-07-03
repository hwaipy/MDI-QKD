package com.hydra.test.groundtdcserver;

import com.hydra.device.tdc.TDCDataProcessor;
import com.hydra.device.tdc.TDCParser;
import com.hydra.device.tdc.adapters.BufferedOrderTDCDataAdapter;
import com.hydra.device.tdc.adapters.DeserializingTDCDataAdapter;
import com.hydra.device.tdc.adapters.GroundTDCDataAdapter;
import com.hydra.device.tdc.adapters.SerializingTDCDataAdapter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author Hwaipy
 */
public class _Tester {

  public void testParseTime() throws FileNotFoundException, IOException, InterruptedException {
    GroundTDCDataAdapter groundTDCAdapter = new GroundTDCDataAdapter(new int[]{0, 6});
    BufferedOrderTDCDataAdapter bufferedOrderTDCDataAdapter = new BufferedOrderTDCDataAdapter();
    SerializingTDCDataAdapter serializingTDCDataAdapter = new SerializingTDCDataAdapter(2, 100);
    DeserializingTDCDataAdapter deserializingTDCDataAdapter = new DeserializingTDCDataAdapter();
    DataProcessor processor = new DataProcessor();
    TDCParser parser = new TDCParser(processor, groundTDCAdapter, bufferedOrderTDCDataAdapter, serializingTDCDataAdapter, deserializingTDCDataAdapter
    );
//    File file = new File("/users/hwaipy/documents/data/samples/20151129114403-帧错误示例.dat");
    File file = new File("/users/hwaipy/documents/data/samples/Ground_TDC_1.dat");;
    int fileLength = (int) file.length();
    RandomAccessFile raf = new RandomAccessFile(file, "r");
    byte[] data = new byte[fileLength];
    if (raf.read(data) != fileLength) {
      throw new RuntimeException();
    }
    System.out.println("Data size: " + data.length);
    ArrayList<byte[]> dataSection = new ArrayList<>();
    int randomSeed = 198917;
    int position = 0;
    while (position < data.length) {
      int nextPosition = Math.min(data.length, position + randomSeed);
      randomSeed = (randomSeed * 2) % 4097 + 7;
      dataSection.add(Arrays.copyOfRange(data, position, nextPosition));
      position = nextPosition;
    }
    long startTime = System.nanoTime();
    for (byte[] section : dataSection) {
      parser.offer(section);
    }
    parser.waitForFinish();
    long endTime = System.nanoTime();
    System.out.println((endTime - startTime) / 1e9);
    System.out.println("----In GroundTDCDataAdapter----");
    System.out.println("Frame readed: " + groundTDCAdapter.getFrameCount());
    System.out.println("Frame valid: " + groundTDCAdapter.getValidFrameCount());
    System.out.println("Skipped in seeking head: " + groundTDCAdapter.getSkippedInSeekingHead());
    System.out.println("Unknown channel events: " + groundTDCAdapter.getUnknownChannelEventCount());
    System.out.println("Valid events: " + sum(groundTDCAdapter.getValidEventCount()) + " " + Arrays.toString(groundTDCAdapter.getValidEventCount()));
    System.out.println("Remaining: " + groundTDCAdapter.getDataRemaining());
    System.out.println("Addressed bytes: " + (groundTDCAdapter.getFrameCount() * 2048 + groundTDCAdapter.getSkippedInSeekingHead() + groundTDCAdapter.getDataRemaining()));
    System.out.println("----In bufferedOrderTDCDataAdapter----");
    System.out.println("SortOuttedCount: " + bufferedOrderTDCDataAdapter.getSortOuttedCount());
    System.out.println("----In SerializingTDCDataAdapter----");
  }

  private int sum(int... items) {
    int sum = 0;
    for (int item : items) {
      sum += item;
    }
    return sum;
  }

  private class DataProcessor implements TDCDataProcessor {

    private long lastTime = 0;

    @Override
    public void process(Object data) {
      if (data == null) {
        return;
      }
      if (data instanceof long[]) {
        long[] timeEvents = (long[]) data;
        for (long timeEvent : timeEvents) {
          long channel = timeEvent % 4;
          if (channel == 0) {
            continue;
          }
          long time = timeEvent >> 2;
          long diff = time - lastTime;
          if (diff > 1100000) {
            System.out.println(diff);
          }
          lastTime = time;
        }
      } else if (data instanceof List) {
        List<Long> timeEvents = (List<Long>) data;
        for (Long timeEvent : timeEvents) {
          long channel = timeEvent % 4;
          if (channel == 0) {
            continue;
          }
          long time = timeEvent >> 2;
          long diff = time - lastTime;
          if (diff > 1100000) {
            System.out.println(diff);
          }
          lastTime = time;
        }
      } else {
        System.out.println(data.getClass());
        throw new RuntimeException();
      }
    }
  }
}
