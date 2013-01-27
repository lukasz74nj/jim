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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Hashtable;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JToolBar;

/**
  * The application toolbar.
  *
  * @author  Lukasz Szelag (luk@hades.itma.pwr.wroc.pl)
  * @version 1.0 11/01/98
  */
class AppToolBar extends JToolBar implements AppActions {
  /*
  This class encapsulates a button component that is bound to a given action. 
  The button is registered as the state-change listener for the action.
  Enabling/disabling the action will result in the button to update itself.
  */
  private class Button extends JButton implements PropertyChangeListener {
    Button(Action a) {
      setIcon((Icon)a.getValue(TOOLBAR_ICON));
      setToolTipText((String)a.getValue(TOOLTIP_TEXT));
      this.setEnabled(a.isEnabled());
      addActionListener(a);
      a.addPropertyChangeListener(this);
    }

    public void propertyChange(PropertyChangeEvent e) {
      if (e.getPropertyName().equals("enabled")) {
        if (e.getNewValue() != e.getOldValue()) {
          this.setEnabled(((Boolean)e.getNewValue()).booleanValue());
        }
      }
    }
  }

  AppToolBar(Hashtable commands) {
    setFloatable(false);

    add(new Button((Action)commands.get(EXIT_ACTION)));
    addSeparator();
    add(new Button((Action)commands.get(ABOUT_ACTION)));
  }
}
