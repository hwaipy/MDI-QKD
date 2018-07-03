package com.hydra.test.groundtdcserver;

//package com.hwaipy.jatlas.groundtdcserver;
//
//import com.hwaipy.vi.tdc.GroundTDCParser;
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.FileNotFoundException;
//import java.io.IOException;
//import java.util.ArrayList;
//
///**
// *
// * @author Hwaipy
// */
//public class _HOMTEST {
//
//  public static void main(String[] args) throws FileNotFoundException, IOException {
//    File file = new File("/Users/Hwaipy/Documents/Data/Samples/20151128140929--003.dat");
//    long length = file.length();
//    FileInputStream in = new FileInputStream(file);
//    byte[] data = new byte[(int) length];
//    int readed = in.read(data);
//    assert readed == length;
//    System.out.println("Read Done.");
//    GroundTDCParser parser = new GroundTDCParser();
//    long[][] timeEvents = parser.offer(data);
//    ArrayList<Long> c1 = new ArrayList<>();
//    ArrayList<Long> c2 = new ArrayList<>();
//    System.out.println(timeEvents[0][1]);
//    for (long[] timeEvent : timeEvents) {
////      if (timeEvent[0] == 2 || timeEvent[0] == 3) {
////        System.out.println(timeEvent[0] + ":" + timeEvent[1]);
////      }
//
//      if (timeEvent[0] == 2) {
//        c1.add(timeEvent[1]);
//      } else if (timeEvent[0] == 3) {
//        c2.add(timeEvent[1]);
//      }
//    }
//    long scanRange = 1000000;
//    System.out.println("Channel 1: " + c1.size());
//    System.out.println("Channel 2: " + c2.size());
//    int start2 = 0;
//    for (Long time1 : c1) {
//      System.out.println("new time1: " + time1);
//      for (int index2 = start2; index2 < 10; index2++) {
//        long time2 = c2.get(index2);
//        long diff = time2 - time1;
//        if (diff < -scanRange) {
//          start2++;
//          System.out.println("scan a time2");
//        } else {
//          break;
//        }
//      }
//      for (int index2 = start2; index2 < 10; index2++) {
//        long time2 = c2.get(index2);
//        long diff = time2 - time1;
//        if (diff >= scanRange) {
//          System.out.println("end: diff = " + diff);
//          break;
//        }
//        System.out.println(diff);
//      }
//    }
//  }
//}
