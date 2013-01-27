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

/**
  * This interface is a bridge between bucket sort engine and GUI that
  * visualizes the algorithm.
  *
  * @author  Lukasz Szelag (luk@hades.itma.pwr.wroc.pl)
  * @version 1.0 11/15/98
  */
interface PBucketSortListener {
  void procInited(int id, int data[]);
  void procSent(int id, int to, int phase, int buff[], int data[]);
  void phaseCompleted(int phase);
  void procFinished(int id, int sorted[]);
}
