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

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.TitledBorder;

/**
  * This class represents a dialog that shows a demonstration of quick sort
  * algorithm on a hypercube.
  *
  * @author  Lukasz Szelag (luk@hades.itma.pwr.wroc.pl)
  * @version 1.0 11/01/98
  */
class PQuickSortDlg extends JDialog implements PQuickSortListener {                                    
  private ProcessorsPanel procPan;  // component that visualizes processors
  private JComboBox       cbSize;   // input sequence size
  private JComboBox       cbProc;   // number of processors
  private JComboBox       cbPivot;  // pivot
  private JTextArea       taDebug;  // algorithm output messages
  private JButton         bStart;
  private JButton         bNext;
  private int             pivot;

  // used to synchronize visualization of subsequent algorithm splits
  private Object lock = new Object();

  /*
  This class implements a custom titled border. The base class version doesn't
  provide enough empty space around it.
  */
  private class MyTitledBorder extends TitledBorder {
    MyTitledBorder(String title) {
      super(title);
    }

    public Insets getBorderInsets(Component c) {
      return new Insets(20, 10, 10, 10);
    }
  }

  PQuickSortDlg(JFrame owner) {
    super(owner, "Parallel Quick Sort on a Hypercube (SF)", true);

    JPanel panel = new JPanel(new BorderLayout());
    panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    panel.add(createMainPanel(), BorderLayout.CENTER);

    procPan = new ProcessorsPanel(8, 160, 40);
    procPan.setBorder(new MyTitledBorder("Processors"));
    panel.add(procPan, BorderLayout.EAST);

    panel.add(createButtonPanel(), BorderLayout.SOUTH);
    getContentPane().add(panel);
    pack();
    setResizable(false);

    // center the dialog on the screen
    Dimension scr = Toolkit.getDefaultToolkit().getScreenSize();
    Dimension dlg = getSize();
    setLocation((scr.width - dlg.width) / 2, (scr.height - dlg.height) / 2);    
  }

  // Create the main panel and return handle to it.
  private JPanel createMainPanel() {
    GridBagLayout      gb    = new GridBagLayout();
    GridBagConstraints c     = new GridBagConstraints();
    JPanel             panel = new JPanel(gb);
    panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));

    JLabel lHypercube = new JLabel(new ImageIcon(getClass().getResource(
                                   "images/hypercube.gif")));
    c.gridx           = 0; 
    c.gridy           = 0;
    c.anchor          = c.CENTER;
    c.gridwidth       = c.REMAINDER;
    c.insets          = new Insets(0, 0, 10, 0);
    gb.setConstraints(lHypercube, c);
    panel.add(lHypercube);

    JLabel lSize = new JLabel("Input sequence size:");
    lSize.setDisplayedMnemonic('I');
    c.gridx      = 0; 
    c.gridy      = 1;
    c.anchor     = c.EAST;
    c.gridwidth  = c.RELATIVE;
    c.insets     = new Insets(0, 0, 10, 0);
    gb.setConstraints(lSize, c);
    panel.add(lSize);
    cbSize       = new JComboBox(new Object[] { "8", "80", "160" });
    cbSize.setSelectedIndex(2);
    lSize.setLabelFor(cbSize);
    c.gridx      = 1; 
    c.gridy      = 1;
    c.anchor     = c.WEST;
    c.insets     = new Insets(0, 5, 10, 0);
    gb.setConstraints(cbSize, c);
    panel.add(cbSize);

    JLabel lProc = new JLabel("Number of processors:");
    lProc.setDisplayedMnemonic('N');
    c.gridx      = 0; 
    c.gridy      = 2;
    c.anchor     = c.EAST;
    c.insets     = new Insets(0, 0, 10, 0);
    gb.setConstraints(lProc, c);
    panel.add(lProc);
    cbProc       = new JComboBox(new Object[] { "1", "2", "4", "8" });
    cbProc.setSelectedIndex(3);
    lProc.setLabelFor(cbProc);
    c.gridx      = 1; 
    c.gridy      = 2;
    c.anchor     = c.WEST;
    c.insets     = new Insets(0, 5, 10, 0);
    gb.setConstraints(cbProc, c);
    panel.add(cbProc);

    JLabel lPivot = new JLabel("Pivot:");
    lPivot.setDisplayedMnemonic('P');
    c.gridx       = 0; 
    c.gridy       = 3;
    c.anchor      = c.EAST;
    c.insets      = new Insets(0, 0, 10, 0);
    gb.setConstraints(lPivot, c);
    panel.add(lPivot);
    cbPivot       = new JComboBox(new Object[] { "First", "Last", "Random" });
    cbPivot.setSelectedIndex(2);
    lPivot.setLabelFor(cbPivot);
    c.gridx       = 1; 
    c.gridy       = 3;
    c.anchor      = c.WEST;
    c.insets      = new Insets(0, 5, 10, 0);
    gb.setConstraints(cbPivot, c);
    panel.add(cbPivot);

    taDebug        = new JTextArea(10, 40);
    taDebug.setEditable(false);
    taDebug.setLineWrap(true);
    JScrollPane sp = new JScrollPane(taDebug); 
    c.gridx        = 0; 
    c.gridy        = 4;
    c.anchor       = c.CENTER;
    c.gridwidth    = c.REMAINDER;
    c.insets       = new Insets(0, 0, 10, 0);
    gb.setConstraints(sp, c);
    panel.add(sp);

    return panel;
  }

  // Create buttons panel and return handle to it.
  private JPanel createButtonPanel() {
    GridBagLayout      gb    = new GridBagLayout();
    GridBagConstraints c     = new GridBagConstraints();
    JPanel             panel = new JPanel(gb);

    JPanel buttons = new JPanel(new GridLayout(1, 3, 10, 0));

    bStart = new JButton("Start");
    bStart.setMnemonic('S');
    bStart.addActionListener(new StartButtListener());
    buttons.add(bStart);

    bNext = new JButton("Next");
    bNext.setEnabled(false);
    bNext.addActionListener(new NextButtListener());
    bNext.setMnemonic('N');
    buttons.add(bNext);

    JButton bClose = new JButton("Close");
    bClose.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        dispose();
      }
    });
    bClose.setMnemonic('C');
    buttons.add(bClose);

    c.gridx     = 0; 
    c.gridy     = 0;
    c.anchor    = c.CENTER;
    c.gridwidth = c.REMAINDER;
    c.insets    = new Insets(10, 0, 0, 0);
    gb.setConstraints(buttons, c);
    panel.add(buttons);

    return panel;
  }

  /*
  Listeners for buttons.
  */

  private class StartButtListener implements ActionListener {
    public void actionPerformed(ActionEvent e) {
      taDebug.setText("");
      procPan.clear();

      int seqSize = 0;
      int dim     = cbProc.getSelectedIndex();

      try { 
        seqSize = Integer.parseInt((String)cbSize.getSelectedItem());
      }
      catch (NumberFormatException ex) {
      }

      int seq[] = new int[seqSize];
      for (int i = 0; i < seq.length; i++) {
        seq[i] = (int)(Math.random() * 100);
      }

      String piv = (String)cbPivot.getSelectedItem();
      if (piv.equals("First")) {
        pivot = seq[0];
      }
      else if (piv.equals("Last")) {
        pivot = seq[seq.length - 1];
      }
      else {
        pivot = seq[(int)(Math.random() * (seq.length - 1))];
      }

      new PQuickSort(PQuickSortDlg.this, seq, pivot, dim);
      bStart.setEnabled(false);
      cbSize.setEnabled(false);
      cbProc.setEnabled(false);
      cbPivot.setEnabled(false);
      bNext.setEnabled(true);
    }
  }

  private class NextButtListener implements ActionListener {
    public void actionPerformed(ActionEvent e) {
      taDebug.setText("");

      // wake up processors threads so algorithm can proceed 
      synchronized(lock) {
        lock.notifyAll();
      }
    }
  }

  /*
  PQuickSortListener implementation. These callback methods are invoked during
  the execution of the algorithm.
  */

  public void procInited(int id, int data[]) {
    taDebug.append("P" + id + " has received its block of elements and pivot" +
                   "\n");
    procPan.setData(id, data, pivot);

    // force a thread to wait until the "Next" button is pressed
    synchronized(lock) {
      try {
        lock.wait();
      }
      catch (InterruptedException e) {
      } 
    }
  }

  public void procSentLeft(int id, int to, int split, int data[]) {
    taDebug.append("[split " + split + "]: " +
                   "P" + id + " has sent elements less and equal to pivot to " +
                   "P" + to + "\n");
    procPan.setData(id, data, pivot);
  }

  public void procSentRight(int id, int to, int split, int data[]) {
    taDebug.append("[split " + split + "]: " +
                   "P" + id + " has sent elements greater than pivot to " +
                   "P" + to + "\n");
    procPan.setData(id, data, pivot);
  }

  public void splitCompleted(int split) {
    // force a thread to wait until the "Next" button is pressed
    synchronized(lock) {
      try {
        lock.wait();
      }
      catch (InterruptedException e) {
      } 
    }
  }
  
  public void procFinished(int id, int sorted[]) {
    if (sorted.length > 0) {
      taDebug.append("P" + id + " has sorted its elements\n");
      procPan.setData(id, sorted, -1);
    }
 
    bStart.setEnabled(true);
    cbSize.setEnabled(true);
    cbProc.setEnabled(true);
    cbPivot.setEnabled(true);
    bNext.setEnabled(false);
  }
}
