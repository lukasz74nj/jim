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
 
package com.designamus.jim.util;

/**
  * This class can be used to synchronize a number of threads that perform a
  * certain task.
  *
  * @author Lukasz Szelag (luk@hades.itma.pwr.wroc.pl)
  * @version 1.0 10/31/98
  */
public class Barrier {
  protected int count;
  protected int maxCount;

  /**
   * Constructs a new Barrier initialized with the specified maximum counter.
   *
   * @param maxCount  the number of threads to release the barrier
   */ 
  public Barrier(int maxCount) {
    this.maxCount = maxCount;
  }

  /**
   * Called by a thread to join this barrier. A thread is blocked until the
   * barrier is released. The barrier is released when maxCount threads join.
   */ 
  synchronized public void join() {
    if (++count == maxCount) {
      count = 0;
      notifyAll();
    }
    else {
      try {
        wait();
      }
      catch (InterruptedException e) {
      }
    }      
  }
}
