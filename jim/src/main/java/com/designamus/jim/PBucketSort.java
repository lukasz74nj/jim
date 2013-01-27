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
  * This class implements engine of parallel bucket sort algorithm on a ring.
  *
  * @author  Lukasz Szelag (luk@hades.itma.pwr.wroc.pl)
  * @version 1.0 11/15/98
  */
class PBucketSort {
  private PBucketSortListener listener;  // this algorithm listener
  private int[]               seq;       // sequence to be sorted
  private int                 min, max;  // interval of sequence values
  private int                 procNo;    // number of processors
  private Processor[]         threads;   // processors threads
  private Barrier             barrier;   // used to synchronize threads

  private class Processor extends Thread {
    int id;          // unique processor label
    int recvBuff[];  // receiving buffer

    // buffer to hold an assigned block of elements
    BucketsBuffer buff = new BucketsBuffer(procNo, min, max);  

    Processor(int id) {
      this.id = id;

      // copy a block of elements to local buffer
      for (int i = 0; i < seq.length / procNo; i++) {
        buff.add(seq[i + (seq.length / procNo) * id]);
      }
    }

    public void run() {
      listener.procInited(id, buff.get());

      for (int i = 1; i < procNo; i++) {
        // label of the destination processor
        int dest = (id + 1) % procNo;

        /*
        Send elements that do not belong to this processor bucket to the next
        processor on the ring.
        */
        int data[] = buff.popExcept(id);
        threads[dest].dataSent(data);

        // synchronize all threads and read data from the receiving buffer
        barrier.join();
        buff.add(recvBuff);

        listener.procSent(id, dest, i, buff.get(), data);

        // wait for all threads to complete phase
        barrier.join();

        listener.phaseCompleted(i);
      }

      // sort the local buffer and return it
      listener.procFinished(id, buff.getSorted()); 
    }

    // Receive and store elements in the buffer.
    void dataSent(int data[]) {
      recvBuff = data;
    }
  }

  PBucketSort(PBucketSortListener l, int seq[], int min, int max, int procNo) {
    listener    = l;
    this.seq    = seq;
    this.min    = min;
    this.max    = max;
    this.procNo = procNo;
    threads     = new Processor[procNo];
    barrier     = new Barrier(procNo);

    // create and start processors threads
    for (int i = 0; i < procNo; i++) {
      threads[i] = new Processor(i);
    }
    for (int i = 0; i < procNo; i++) {
      threads[i].start();
    }
  }
}
