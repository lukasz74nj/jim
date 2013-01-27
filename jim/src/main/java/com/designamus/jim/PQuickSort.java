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
  * This class implements engine of parallel quick sort algorithm on a
  * hypercube.
  *
  * @author  Lukasz Szelag (luk@hades.itma.pwr.wroc.pl)
  * @version 1.1 11/01/98
  */
class PQuickSort {
  private PQuickSortListener listener;  // this algorithm listener
  private int[]              seq;       // sequence to be sorted
  private int                pivot;     // used to partition elements
  private int                d;         // dimension of hypercube
  private int                procNo;    // number of processors
  private Processor[]        threads;   // processors threads
  private Barrier            barrier;   // used to synchronize threads

  private class Processor extends Thread {
    int id;  // unique processor label

    // buffer to hold an assigned block of elements
    PartitionBuffer buff = new PartitionBuffer(pivot);  

    Processor(int id) {
      this.id = id;

      // copy a block of elements to local buffer
      for (int i = 0; i < seq.length / procNo; i++) {
        buff.add(seq[i + (seq.length / procNo) * id]);
      }
    }

    public void run() {
      listener.procInited(id, buff.get());

      for (int i = 1; i <= d; i++) {
        /*
        Label of the destination processor along the ith communication link
        (labels of processors that exchange data differ on the ith bit position
        of their binary representation).
        */
        int dest = id ^ (1 << (i - 1));

        // if ith bit is 0
        if ((id & (1 << (i - 1))) == 0) {
          // send elements greater than pivot
          threads[dest].dataSent(buff.popRight());

          listener.procSentRight(id, dest, i, buff.get());
        }
        else {
          // send elements less and equal to pivot
          threads[dest].dataSent(buff.popLeft());

          listener.procSentLeft(id, dest, i, buff.get());
        }

        // wait for all threads to complete split
        barrier.join();

        listener.splitCompleted(i);
      }

      // sort the local buffer and return it
      listener.procFinished(id, buff.getSorted()); 
    }

    // Receive and store elements in the buffer.
    void dataSent(int data[]) {
      buff.add(data);
    }
  }

  PQuickSort(PQuickSortListener listener, int seq[], int pivot, int d) {
    this.listener = listener;
    this.seq      = seq;
    this.pivot    = pivot;
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
