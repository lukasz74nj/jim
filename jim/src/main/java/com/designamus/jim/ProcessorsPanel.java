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

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Vector;

import javax.swing.JLabel;
import javax.swing.JPanel;

/**
  * This class is a panel that holds components that visualize processors.
  *
  * @author  Lukasz Szelag (luk@hades.itma.pwr.wroc.pl)
  * @version 1.1 11/02/98
  */
class ProcessorsPanel extends JPanel {
  private GridBagLayout      gb = new GridBagLayout();
  private GridBagConstraints c  = new GridBagConstraints();

  private Indicator[] inds;      // components that visualize processors
  private int         maxValue;  // the largest element among all processors
  private double      scaleY;    // scale factor

  /*
  This class implements a component that is used to visualize a buffer that
  holds elements assigned for a processor.
  */
  private class Indicator extends Canvas {
    private int    width, height;         // this component drawable size 
    private Vector left  = new Vector();  // elements less and equal to pivot
    private Vector right = new Vector();  // elements greater than pivot
    private int    pivot;                 // element used to partition elements

    Indicator(int width, int height) {
      this.width  = width;
      this.height = height;

      setSize(width + 2, height + 2);
    }

    synchronized public void paint(Graphics g) {
      // draw 3D filled rectangle
      this.setBackground(Color.black);
      g.setColor(Color.gray);
      g.drawLine(0, 0, 0, height + 1);
      g.drawLine(0, 0, width, 0);
      g.setColor(Color.white);
      g.drawLine(width + 1, 0, width + 1, height + 1);
      g.drawLine(1, height + 1, width + 1, height + 1 );

      int i = 0;

      // visualize elements less and equal to pivot
      g.setColor(Color.red);
      for (i = 0; i < left.size(); i++) {
        int amp = (int)(((Integer)left.elementAt(i)).intValue() * scaleY);
        if (amp > 0) { 
          g.drawLine(i + 1, height, i + 1, height - amp + 1);
        }
      }

      // visualize elements greater than pivot
      g.setColor(Color.blue);
      for (int j = 0; j < right.size(); j++) {
        int amp = (int)(((Integer)right.elementAt(j)).intValue() * scaleY);
        if (amp > 0) { 
          g.drawLine(j + i + 1, height, j + i + 1, height - amp + 1);
        }
      }
    }

    // Clear buffers.
    synchronized void clear() {
      maxValue = 0; 
      left.removeAllElements();
      right.removeAllElements();
      repaint();
    }

    // Partition a given sequence into buffers according to pivot.
    synchronized void setData(int data[], int pivot) {
      this.pivot = pivot;
 
      left.removeAllElements();
      right.removeAllElements();

      for (int i = 0; i < data.length; i++) {
        if (data[i] <= pivot) {
          left.addElement(new Integer(data[i]));
        }
        else {
          right.addElement(new Integer(data[i]));
        }

        if (data[i] > maxValue) {
          maxValue = data[i];
          scaleY   = (double)height / maxValue;
        }
      }

      // update all indicators to preserve proportions
      ProcessorsPanel.this.update();
    }
  }

  ProcessorsPanel(int procNo, int procWidth, int procHeight) {
    super();
    setLayout(gb);

    inds = new Indicator[procNo];

    for (int i = 0; i < procNo; i++) {
      int bottom = ((i == (procNo - 1)) ? 0 : 10);

      JLabel label = new JLabel("P" + i + ":");
      c.gridx      = 0; 
      c.gridy      = i;
      c.anchor     = c.EAST;
      c.insets     = new Insets(0, 0, bottom, 0);
      gb.setConstraints(label, c);
      add(label);
      inds[i]      = new Indicator(procWidth, procHeight);
      c.gridx      = 1; 
      c.gridy      = i;
      c.anchor     = c.WEST;
      c.insets     = new Insets(0, 5, bottom, 0);
      gb.setConstraints(inds[i], c);
      add(inds[i]);
    }
  }

  void clear() {
    for (int i = 0; i < inds.length; i++) {
      inds[i].clear();
    }
  } 

  void setData(int procNo, int data[], int pivot) {
    inds[procNo].setData(data, pivot);
  }

  private void update() {
    for (int i = 0; i < inds.length; i++) {
      inds[i].repaint();
    }
  }
}
