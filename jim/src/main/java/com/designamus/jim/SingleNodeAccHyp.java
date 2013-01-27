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
  * This class implements engine of single node accumulation on a hypercube.
  *
  * @author  Lukasz Szelag (luk@hades.itma.pwr.wroc.pl)
  * @version 1.0 11/11/98
  */
class SingleNodeAccHyp {
  private SingleNodeAccListener listener;  // this algorithm listener
  private int                   d;         // dimension of hypercube
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

      int mask = 0;

      for (int i = 0; i < d; i++) {
        // check if the processor is active in this phase
        if ((id & mask) == 0) {
          // if ith bit is 1
          if ((id & (1 << i)) != 0) {
            // label of the destination processor
            int dest = id ^ (1 << i);

            // send sum
            threads[dest].dataSent(id, i + 1, sum);
          }
        }

        // set ith bit of mask to 1
        mask ^= (1 << i);

        // wait for all threads to complete phase
        barrier.join();

        listener.phaseCompleted(i + 1, i == (d - 1));
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

  SingleNodeAccHyp(SingleNodeAccListener listener, int msg[], int d) {
    this.listener = listener;
    this.d        = d;
    procNo        = 1 << d;
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
