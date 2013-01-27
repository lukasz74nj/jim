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
  * This class implements engine of single node accumulation on a ring.
  *
  * @author  Lukasz Szelag (luk@hades.itma.pwr.wroc.pl)
  * @version 1.0 12/22/98
  */
class SingleNodeAccRing {
  private SingleNodeAccListener listener;  // this algorithm listener
  private int                   procNo;    // number of processors
  private Processor[]           threads;   // processors threads
  private Barrier               barrier;   // used to synchronize threads

  private class Processor extends Thread {
    int id;  // unique processor label

    /*
    Initially it holds a message to be contributed to the sum. The final result
    is accumulated on processor 0.
    */
    int sum;

    Processor(int id, int msg) {
      this.id = id;
      sum     = msg;
    }

    public void run() {
      listener.procInited(id, sum);

      for (int i = procNo / 2; i >= 1; i--) {
        int     dest    = 0;      // label of the destination processor
        boolean active  = false;  // set if the processor sends data

        if (i == 1) {
          if (id == procNo - 1) {
            dest   = 0;
            active = true;
          }
        }
        else {
          if (id == i - 1) {
            dest   = i - 2;
            active = true;
          }
          else if (id == procNo - i) {
            dest   = procNo - i + 1;
            active = true;
          } 
        }

        if (active) {
          // send sum
          threads[dest].dataSent(id, procNo / 2 - i + 1, sum);
        }

        // wait for all threads to complete phase
        barrier.join();

        listener.phaseCompleted(procNo / 2 - i + 1, i == 1);
      }
      if (id == 0) {
        listener.finished();
      }
    }

    // Receive and accumulate message.
    void dataSent(int from, int phase, int msg) {
      sum += msg;

      listener.procAcc(id, from, phase, sum);
    }
  }

  SingleNodeAccRing(SingleNodeAccListener listener, int msg[], int procNo) {
    this.listener = listener;
    this.procNo   = procNo;
    threads       = new Processor[procNo];
    barrier       = new Barrier(procNo);

    // create and start processors threads
    for (int i = 0; i < procNo; i++) {
      threads[i] = new Processor(i, msg[i]);
    }
    for (int i = 0; i < procNo; i++) {
      threads[i].start();
    }
  }
}
