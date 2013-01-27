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
  * This class implements a buffer that is used by a processor in each split
  * to keep and partition local elements.
  *
  * @author  Lukasz Szelag (luk@hades.itma.pwr.wroc.pl)
  * @version 1.0 10/29/98
  */
class PartitionBuffer {
  private Vector left  = new Vector();  // elements less and equal to pivot
  private Vector right = new Vector();  // elements greater than pivot
  private int    pivot;                 // element used to partition elements

  PartitionBuffer(int pivot) {
    this.pivot = pivot;
  }

  // Add element to buffer.
  synchronized void add(int e) {
    if (e <= pivot) {
      left.addElement(new Integer(e));
    }
    else {
      right.addElement(new Integer(e));
    }
  }

  // Add elements to buffer.
  synchronized void add(int data[]) {
    for (int i = 0; i < data.length; i++) {
      add(data[i]);
    }
  }

  // Pop and return elements less and equal to pivot.
  synchronized int[] popLeft() {
    int data[] = new int[left.size()];

    for (int i = 0; i < data.length; i++) {
      data[i] = ((Integer)left.elementAt(i)).intValue();
    }

    left.removeAllElements();
    return data;  
  }

  // Pop and return elements greater than pivot.
  synchronized int[] popRight() {
    int data[] = new int[right.size()];

    for (int i = 0; i < data.length; i++) {
      data[i] = ((Integer)right.elementAt(i)).intValue();
    }

    right.removeAllElements();
    return data;
  }

  // Return all elements.
  synchronized int[] get() {
    int data[] = new int[left.size() + right.size()];
    int i;

    // join elements
    for (i = 0; i < left.size(); i++) {
      data[i] = ((Integer)left.elementAt(i)).intValue();
    }
    for (int j = 0; j < right.size(); j++) {
      data[j + i] = ((Integer)right.elementAt(j)).intValue();
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
