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

import java.util.Vector;

/**
  * This class implements a buffer for buckets.
  *
  * @author  Lukasz Szelag (luk@hades.itma.pwr.wroc.pl)
  * @version 1.0 11/15/98
  */
class BucketsBuffer {
  private Vector buckets[];
  private int    min, max;   // interval of values
  
  BucketsBuffer(int buckNo, int min, int max) {
    this.min = min;
    this.max = max;

    buckets = new Vector[buckNo];
    for (int i = 0; i < buckNo; i++) {
      buckets[i] = new Vector();
    }
  }

  // Add element to buffer.
  synchronized void add(int e) {
    // interval length for each bucket
    double d = (double)(max - min + 1) / buckets.length;

    // partition data into buckets
    for (int i = 0; i < buckets.length; i++) {
      if (i < buckets.length - 1) {
        if ((e >= min + d * i) && (e < min + d * (i + 1))) {
          buckets[i].addElement(new Integer(e));
          break;
        }
      }
      else {
        if (e >= min + d * i) {
          buckets[i].addElement(new Integer(e));
          break;
        }
      }
    }
  }

  // Add elements to buffer.
  synchronized void add(int data[]) {
    for (int i = 0; i < data.length; i++) {
      add(data[i]);
    }
  }

  // Pop and return all elements but not from a given bucket.
  synchronized int[] popExcept(int bucket) {
    int size = 0;
    for (int i = 0; i < buckets.length; i++) {
      if (i != bucket) {
        size += buckets[i].size();
      }
    }

    int data[] = new int[size];

    // join elements
    int c = 0;
    for (int i = 0; i < buckets.length; i++) {
      if (i != bucket) {
        for (int j = 0; j < buckets[i].size(); j++) {
          data[c++] = ((Integer)buckets[i].elementAt(j)).intValue();
        } 
        buckets[i].removeAllElements();
      }
    }

    return data;
  } 

  // Return all elements.
  synchronized int[] get() {
    int size = 0;
    for (int i = 0; i < buckets.length; i++) {
      size += buckets[i].size();
    }
 
    int data[] = new int[size];

    // join elements
    int c = 0;
    for (int i = 0; i < buckets.length; i++) {
      for (int j = 0; j < buckets[i].size(); j++) {
        data[c++] = ((Integer)buckets[i].elementAt(j)).intValue();
      }
    }

    return data;
  } 

  // Return sorted elements.
  synchronized int[] getSorted() {
    int data[] = get();

    bsort(data);
    return data;
  }

  // Sort a given sequence using bubble sort.
  private static void bsort(int data[]) {
    boolean flag = true;

    while (flag) {
      flag = false;

      for (int i = 0; i < data.length - 1; i++) {
        if (data[i] > data[i + 1]) {
          int tmp     = data[i];
          data[i]     = data[i + 1];
          data[i + 1] = tmp;
          flag = true;
        }
      }
    }
  }
}
