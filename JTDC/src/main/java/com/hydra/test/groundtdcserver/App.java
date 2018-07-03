package com.hydra.test.groundtdcserver;

import com.hydra.device.tdc.TDCDataProcessor;
import com.hydra.device.tdc.TDCParser;
import com.hydra.device.tdc.adapters.GroundTDCDataAdapter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;

/**
 *
 * @author Hwaipy
 */
public class App {

  public static void main(String[] args) throws FileNotFoundException, IOException {
    TDCParser tdcParser = new TDCParser(new TDCDataProcessor() {
      @Override
      public void process(Object data) {
        if (data instanceof ArrayList) {
          ArrayList list = (ArrayList) data;
          if (list.size() > 0) {
            System.out.println(list.size());
//            Iterator iterator = list.iterator();
//            for (int i = 0; i < 10; i++) {
//              System.out.println(iterator.next());
//            }
          }
        } else {
          throw new RuntimeException();
        }
      }
    }, new GroundTDCDataAdapter(new int[]{0}));

    ServerSocket server = new ServerSocket(20156);
    byte[] buffer = new byte[1024 * 1024 * 16];
    while (!server.isClosed()) {
      Socket socket = server.accept();
      System.out.println("Connected");
      try {
        InputStream in = socket.getInputStream();
        while (!socket.isClosed()) {
          int read = in.read(buffer);
          tdcParser.offer(Arrays.copyOfRange(buffer, 0, read));
        }
      } catch (Exception e) {
      }
      System.out.println("End");
    }

  }
}
