/**
 * Copyright 1998 Lukasz Szelag
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
 
package com.designamus.jim;

import com.designamus.jim.util.Barrier;

/**
  * This class implements engine of one to all personalized communication on a
  * hypercube.
  *
  * @author  Lukasz Szelag (luk@hades.itma.pwr.wroc.pl)
  * @version 1.0 11/29/98
  */
class OneToAllPersonRing {
  private OneToAllPersonListener listener;  // this algorithm listener
  private int[]                  data;      // personalized message
  private int                    procNo;    // number of processors
  private Processor[]            threads;   // processors threads
  private Barrier                barrier;   // used to synchronize threads

  private class Processor extends Thread {
    int id;     // unique processor label
    int msg[];  // buffer for a message

    Processor(int id) {
      this.id = id;

      // processor 0 holds the initial message 
      if (id == 0) {
        dataSent(data);
      }
    }

    public void run() {
      if (id == 0) {
        listener.sourceInited(id, msg);
      }

      for (int i = 1; i <= procNo / 2; i++) {
        int     dest    = 0;      // label of the destination processor
        boolean active  = false;  // set if the processor sends data
        int     sendLen = 0;      // size of the message

        // in the first phase the message is splited into two halves
        if (i == 1) {
          if (id == 0) {
            dest    = procNo - 1;
            sendLen = procNo / 2;
            active  = true;
          }
        }
        else {
          if (id == i - 2) {
            dest    = i - 1;
            active  = true;
          }
          else if (id == procNo - i + 1) {
            dest    = procNo - i;  
            active  = true;
          } 
          sendLen = procNo / 2 - i + 1; 
        }

        /*
        If the processor is active in this phase partition its message and send
        the second part to the destination processor. In the first phase P0 has
        to reverse the message before sending it.
        */
        if (active) {
          int first[]  = new int[msg.length - sendLen];
          int second[] = new int[sendLen];

          for (int j = 0; j < msg.length; j++) {
            if (j < msg.length - sendLen) {
              first[j]  = msg[j];
            }
            else {
              if (i == 1) {
                second[j - (msg.length - sendLen)] = 
                msg[msg.length + msg.length - sendLen - j - 1];
              }
              else { 
                second[j - (msg.length - sendLen)] = msg[j];
              }
            }
          }
          msg = first;
          threads[dest].dataSent(second);

          listener.procSent(id, dest, i, msg, second);
       }

       // wait for all threads to complete split
       barrier.join(); 

       listener.phaseCompleted(i, i == procNo / 2);
      } 
      if (id == 0) {
        listener.finished();
      } 
    }

    // Receive and store message in the buffer.
    void dataSent(int data[]) {
      msg = new int[data.length];
      for (int i = 0; i < data.length; i++) {
        msg[i] = data[i];
      }
    }
  }

  OneToAllPersonRing(OneToAllPersonListener listener, int data[], int procNo) {
    this.listener = listener;
    this.data     = data;
    this.procNo   = procNo;
    threads       = new Processor[procNo];
    barrier       = new Barrier(procNo);

    // create and start processors threads
    for (int i = 0; i < procNo; i++) {
      threads[i] = new Processor(i);
    }
    for (int i = 0; i < procNo; i++) {
      threads[i].start();
    }
  }
}
