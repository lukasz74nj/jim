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
  * @version 1.0 11/08/98
  */
class OneToAllPersonHyp {
  private OneToAllPersonListener listener;  // this algorithm listener
  private int[]                  data;      // personalized message
  private int                    d;         // dimension of hypercube
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

      // set all d bits of mask to 1
      int mask = (1 << d ) - 1;

      for (int i = d - 1; i >= 0; i--) {
        // set ith bit of mask to 0
        mask ^= (1 << i);

        // check if the processor is active in this phase
        if ((id & mask) == 0) {
          // if ith bit is 0
          if ((id & (1 << i)) == 0) {
            // label of the destination processor
            int dest = id ^ (1 << i);

            /*
            Send half of the message to the destination processor and retain the
            first half.
            */
            int first[]  = new int[msg.length >> 1];
            int second[] = new int[msg.length >> 1];
            for (int j = 0; j < msg.length >> 1; j++) {
              first[j]  = msg[j];
              second[j] = msg[(msg.length >> 1) + j];
            }
            msg = first;
            threads[dest].dataSent(second);
           
            listener.procSent(id, dest, d - i, msg, second);
          }
        }

        // wait for all threads to complete phase
        barrier.join();

        listener.phaseCompleted(d - i, i == 0);
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

  OneToAllPersonHyp(OneToAllPersonListener listener, int data[], int d) {
    this.listener = listener;
    this.data     = data;
    this.d        = d;
    procNo        = 1 << d;
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
