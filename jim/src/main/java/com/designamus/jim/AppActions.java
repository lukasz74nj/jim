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
  * This interface encapsulates constants for the application actions.
  *
  * @author  Lukasz Szelag (luk@hades.itma.pwr.wroc.pl)
  * @version 1.0 11/01/98
  */
interface AppActions {
  // constants for action commands
  String EXIT_ACTION            = "Exit";
  String PQSORT_ACTION          = "Parallel Quick Sort on a Hypercube (SF)...";
  String PBSORT_ACTION          = "Parallel Bucket Sort on a Ring (SF)...";
  String O2A_PERSON_HYP_ACTION  = "One-to-All Personalized Communication on " +
                                  "a Hypercube (SF)...";
  String S_NODE_ACC_HYP_ACTION  = "Single-node Accumulation on a Hypercube " +
                                  "(SF)...";
  String O2A_PERSON_RING_ACTION = "One-to-All Personalized Communication on " +
                                  "a Ring (SF)...";
  String S_NODE_ACC_RING_ACTION = "Single-node Accumulation on a Ring (SF)...";
  String ABOUT_ACTION           = "About...";

  /*
  Constants that can be used as the storage-retrieval keys when setting or
  getting an action properties.
  */
  String TOOLTIP_TEXT = "tooltip-text";
  String MNEMONIC     = "mnemonic";
  String TOOLBAR_ICON = "toolbar-icon";
}
