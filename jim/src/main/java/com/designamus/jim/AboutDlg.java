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
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;

/**
  * About dialog.
  *
  * @author  Lukasz Szelag (luk@hades.itma.pwr.wroc.pl)
  * @version 1.0 11/01/98
  */
class AboutDlg extends JDialog {
    AboutDlg(JFrame owner) {
    super(owner, "About JIM", true);

    JLabel logo = new JLabel(new ImageIcon(
                             getClass().getResource("images/jim_about.jpg")));
    logo.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));

    JPanel panel = new JPanel(new BorderLayout());
    panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    panel.add(logo, BorderLayout.CENTER);
    panel.add(createButtonPanel(), BorderLayout.SOUTH);
    getContentPane().add(panel);
    pack();
    setResizable(false);

    // center the dialog on the screen
    Dimension scr = Toolkit.getDefaultToolkit().getScreenSize();
    Dimension dlg = getSize();
    setLocation((scr.width - dlg.width) / 2, (scr.height - dlg.height) / 2);    
  }

  // Create the button panel and return handle to it.
  private JPanel createButtonPanel() {
    GridBagLayout      gb    = new GridBagLayout();
    GridBagConstraints c     = new GridBagConstraints();
    JPanel             panel = new JPanel(gb);

    JButton ok = new JButton("OK", new ImageIcon(
                             getClass().getResource("images/small/user.gif")));
    ok.setMnemonic('O');
    ok.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        // close the window
        dispose();
      }
    });

    c.gridx     = 0; 
    c.gridy     = 0;
    c.anchor    = c.CENTER;
    c.gridwidth = c.REMAINDER;
    c.insets    = new Insets(10, 0, 0, 0);
    gb.setConstraints(ok, c);
    panel.add(ok);

    return panel;
  }
}
