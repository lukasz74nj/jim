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
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Hashtable;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JFrame;

/**
  * The main container frame for the application.
  *
  * @author  Lukasz Szelag (luk@hades.itma.pwr.wroc.pl)
  * @version 1.0 11/01/98
  */
class JIMFrame extends JFrame implements AppActions {
  private static final String APP_NAME = "JIM";

  // application actions
  private Action[] actions = {
    new ExitAction(),
    new PQSortAction(),
    new PBSortAction(),
    new OneToAllPersonHypAction(),
    new SingleNodeAccHypAction(),
    new OneToAllPersonRingAction(),
    new SingleNodeAccRingAction(),
    new AboutAction()
  };

  // command table for actions
  private Hashtable commands = new Hashtable();

  JIMFrame() {
    super(APP_NAME);
    setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

    addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent e) {
        exit();
      }
    });

    // build command table for actions 
    for (int i = 0; i < actions.length; i++) {
      Action a = actions[i];
      commands.put(a.getValue(Action.NAME), a);
    }

    setJMenuBar(new AppMenu(commands));

    // set up the container
    Container content = getContentPane();
    content.setLayout(new BorderLayout());
    content.add(new AppToolBar(commands), BorderLayout.NORTH);
    setSize(470, 320);

    // center the frame on the screen
    Dimension scr = Toolkit.getDefaultToolkit().getScreenSize();
    Dimension frm = getSize();
    setLocation((scr.width - frm.width) / 2, (scr.height - frm.height) / 2);    
    setVisible(true);
  }

  // Return the action associated with a given action command.
  private Action getAction(String cmd) {
    return (Action)commands.get(cmd);
  }

  private void exit() {
    dispose();
    System.exit(0);
  }

  /*
  Inner classes that implement actions.
  */

  // terminates the application
  private class ExitAction extends AbstractAction {
    ExitAction() {
      super(EXIT_ACTION);

      putValue(TOOLTIP_TEXT, "Exit");
      putValue(MNEMONIC, new Character('x'));
      putValue(TOOLBAR_ICON, new ImageIcon(getClass().getResource(
               "images/toolbar/exit.gif")));
      putValue(Action.SMALL_ICON, new ImageIcon(getClass().getResource(
               "images/small/exit.gif")));
    }

    public void actionPerformed(ActionEvent e) {
      exit();
    }
  }

  // shows quick sort demo on a hypercube
  private class PQSortAction extends AbstractAction {
    PQSortAction() {
      super(PQSORT_ACTION);

      putValue(MNEMONIC, new Character('p'));
    }

    public void actionPerformed(ActionEvent e) {
      PQuickSortDlg dlg = new PQuickSortDlg(JIMFrame.this);
      dlg.setVisible(true);
    }
  }

  // shows bucket sort demo on a hypercube
  private class PBSortAction extends AbstractAction {
    PBSortAction() {
      super(PBSORT_ACTION);

      putValue(MNEMONIC, new Character('a'));
    }

    public void actionPerformed(ActionEvent e) {
      PBucketSortDlg dlg = new PBucketSortDlg(JIMFrame.this);
      dlg.setVisible(true);
    }
  }

  // shows one-to-all personalized communication demo on a hypercube
  private class OneToAllPersonHypAction extends AbstractAction {
    OneToAllPersonHypAction() {
      super(O2A_PERSON_HYP_ACTION);

      putValue(MNEMONIC, new Character('o'));
    }

    public void actionPerformed(ActionEvent e) {
      OneToAllPersonHypDlg dlg = new OneToAllPersonHypDlg(JIMFrame.this);
      dlg.setVisible(true);
    }
  }

  // shows single-node accumulation demo on a hypercube
  private class SingleNodeAccHypAction extends AbstractAction {
    SingleNodeAccHypAction() {
      super(S_NODE_ACC_HYP_ACTION);

      putValue(MNEMONIC, new Character('s'));
    }

    public void actionPerformed(ActionEvent e) {
      SingleNodeAccHypDlg dlg = new SingleNodeAccHypDlg(JIMFrame.this);
      dlg.setVisible(true);
    }
  }

  // shows one-to-all personalized communication demo on a ring
  private class OneToAllPersonRingAction extends AbstractAction {
    OneToAllPersonRingAction() {
      super(O2A_PERSON_RING_ACTION);

      putValue(MNEMONIC, new Character('n'));
    }

    public void actionPerformed(ActionEvent e) {
      OneToAllPersonRingDlg dlg = new OneToAllPersonRingDlg(JIMFrame.this);
      dlg.setVisible(true);
    }
  }

  // shows single-node accumulation demo on a ring
  private class SingleNodeAccRingAction extends AbstractAction {
    SingleNodeAccRingAction() {
      super(S_NODE_ACC_RING_ACTION);

      putValue(MNEMONIC, new Character('i'));
    }

    public void actionPerformed(ActionEvent e) {
      SingleNodeAccRingDlg dlg = new SingleNodeAccRingDlg(JIMFrame.this);
      dlg.setVisible(true);
    }
  }

  // shows the about dialog
  private class AboutAction extends AbstractAction {
    AboutAction() {
      super(ABOUT_ACTION);

      putValue(MNEMONIC, new Character('A'));
      putValue(TOOLTIP_TEXT, "About");
      putValue(TOOLBAR_ICON, new ImageIcon(getClass().getResource(
               "images/toolbar/info.gif")));
      putValue(Action.SMALL_ICON, new ImageIcon(getClass().getResource(
               "images/small/help.gif")));
    }

    public void actionPerformed(ActionEvent e) {
      AboutDlg dlg = new AboutDlg(JIMFrame.this);
      dlg.setVisible(true);
    }
  }
}
