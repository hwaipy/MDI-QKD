package com.hydra.test.groundtdcserver;

import com.hydra.device.tdc.TDCDataProcessor;
import com.hydra.device.tdc.TDCParser;
import com.hydra.device.tdc.adapters.GroundTDCDataAdapter;
import com.xeiam.xchart.Histogram;
import java.io.File;
import java.io.FileInputStream;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.prefs.Preferences;

/**
 *
 * @author Hwaipy
 */
public class Ana {

  private static final ArrayList<Long>[] timeEvents = new ArrayList[16];
  private static double totalTime = 0;
  private static double[][][] coins = new double[7][7][100];

  public static void main(String[] args) throws Exception {
    for (int i = 0; i < timeEvents.length; i++) {
      timeEvents[i] = new ArrayList<>();
    }
    final TDCParser tdcParser = new TDCParser(new TDCDataProcessor() {
      @Override
      public void process(Object data) {
        if (data instanceof ArrayList) {
          ArrayList list = (ArrayList) data;
          if (list.size() > 0) {
            dataIncome(list);
          }
        } else {
          throw new RuntimeException();
        }
      }
    }, new GroundTDCDataAdapter(new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15}));
    File file = new File("/Users/Hwaipy/Desktop/TDC/15/20160520230841.dat");
    FileInputStream inputStream = new FileInputStream(file);
    byte[] buffer = new byte[50000000];
    while (true) {
      int read = inputStream.read(buffer);
      if (read <= 0) {
        break;
      }
      byte[] data = read == buffer.length ? buffer : Arrays.copyOf(buffer, read);
      tdcParser.offer(data);
      tdcParser.waitForFinish();
      ana();
    }

    System.out.println("Total time: " + totalTime + " s");
    for (int iT = 0; iT < 7; iT++) {
      for (int iS = iT + 1; iS < 7; iS++) {
        double[] cs = coins[iT][iS];
        double norm = (cs[3] + cs[4] + cs[5] + cs[6]) / 4;
        double Q = cs[0] / norm - 0.035;
        double C = (2 - cs[1] / norm - cs[2] / norm);
        double visibility = (C - Q) / C;
        System.out.println((iT + 1) + "\t" + (iS + 1) + "\t" + visibility + "\t" + cs[0] + "\t" + cs[1] + "\t" + cs[2] + "\t" + cs[3] + "\t" + cs[4] + "\t" + cs[5] + "\t" + cs[6]);
      }
    }
  }

  private static void ana() {
    totalTime += (timeEvents[0].get(timeEvents[0].size() - 1) - timeEvents[0].get(0)) / 1000000000000.;

    double[] delays = new double[timeEvents.length];
    Preferences preferences = Preferences.userNodeForPackage(AppFrame.class);
    for (int i = 0; i < delays.length; i++) {
      try {
        delays[i] = preferences.getDouble("Delay" + i, 0);
      } catch (Exception e) {
      }
    }
    for (int i = 0; i < timeEvents.length; i++) {
      long delay = (long) (delays[i] * 1000);
      for (int j = 0; j < timeEvents[i].size(); j++) {
        timeEvents[i].set(j, timeEvents[i].get(j) + delay);
      }
    }

    int channelCount = 7;
    for (int iT = 0; iT < channelCount; iT++) {
      for (int iS = iT + 1; iS < channelCount; iS++) {
        Histogram histogram = doHistogram(timeEvents[iT], timeEvents[iS]);
        double[] cs = doCoins(histogram.getxAxisData(), histogram.getyAxisData());
//        double Q = reletiveCoins[0] - 0.035;
//        double C = (2 - reletiveCoins[1] - reletiveCoins[2]);
//        double visibility = (C - Q) / C;
//        System.out.print(visibility + ", " + doCoins(histogram.getxAxisData(), histogram.getyAxisData()));
        for (int i = 0; i < cs.length; i++) {
          coins[iT][iS][i] += cs[i];
        }
      }
    }

    for (ArrayList<Long> timeEvent : timeEvents) {
      timeEvent.clear();
    }
  }

  private static void dataIncome(ArrayList<Long> dataList) {
    for (Long data : dataList) {
      long time = data >> 4;
      int channel = (int) (data & 0b1111);
      timeEvents[channel].add(time);
    }
  }

  private static Histogram doHistogram(ArrayList<Long> tList, ArrayList<Long> sList) {
    long viewFromPS = (long) (-50 * 1000);
    long viewToPS = (long) (50 * 1000);
    ArrayList<Long> deltas = new ArrayList<>();
    if (!tList.isEmpty() && !sList.isEmpty()) {
      Iterator<Long> tIt = tList.iterator();
      Iterator<Long> sIt = sList.iterator();
      Long t = tIt.next();
      Long s = sIt.next();
      while (true) {
        Long delta = s - t;
        if (delta >= viewFromPS && delta <= viewToPS) {
          deltas.add(delta);
        }
        if (tIt.hasNext() && sIt.hasNext()) {
          if (s > t) {
            t = tIt.next();
          } else {
            s = sIt.next();
          }
        } else {
          break;
        }
      }
    }
    return new Histogram(deltas, 1000, viewFromPS, viewToPS);
  }

  private static double[] doCoins(List<Double> xAxisData, List<Double> yAxisData) {
    double pulsePeriodPS = (13135);
    double gateWidthPS = (3300);
    double[] lines = new double[]{0, pulsePeriodPS, -pulsePeriodPS, 2 * pulsePeriodPS, 3 * pulsePeriodPS, -2 * pulsePeriodPS, - 3 * pulsePeriodPS};
    double[] coins = new double[lines.length];
    for (int i = 0; i < xAxisData.size(); i++) {
      double x = xAxisData.get(i);
      for (int iLine = 0; iLine < lines.length; iLine++) {
        if (Math.abs(x - lines[iLine]) <= gateWidthPS / 2) {
          coins[iLine] += yAxisData.get(i);
        }
      }
    }
    return coins;
//    double norm = (reletiveCoins[3] + reletiveCoins[4] + reletiveCoins[5] + reletiveCoins[6]) / 4;
//    double p0 = reletiveCoins[0] / norm;
//    double p1P = reletiveCoins[1] / norm;
//    double p1M = reletiveCoins[2] / norm;
//    return new double[]{p0, p1P, p1M};
  }
}
