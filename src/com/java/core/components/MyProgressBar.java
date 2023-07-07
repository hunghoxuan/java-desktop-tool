package com.java.core.components;

import javax.swing.*;

import java.awt.event.*;
import javax.swing.Timer;

public class MyProgressBar extends JProgressBar {
    public Timer timer;
    public boolean autoStop = false;
    public int timerDelay = 200; // ms

    public MyProgressBar() {
        super();
        this.setStringPainted(true);
        this.setVisible(false);
    }

    public MyProgressBar start() {
        this.setValue(this.getMinimum());
        this.setVisible(true);
        timer = new Timer(timerDelay, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setValue(getValue() + 1);
                int max = !autoStop ? getMaximum() - 10 : getMaximum();
                if (getValue() >= max) {
                    ((Timer) e.getSource()).stop();
                    if (autoStop)
                        stop();
                }
            }
        });

        timer.start();
        return this;
    }

    public JProgressBar getProgressBar() {
        return this;
    }

    public MyProgressBar stop() {
        if (timer != null)
            timer.stop();
        timer = new Timer(-1, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setValue(getValue() + 1);
                ;
                if (getValue() >= getMaximum()) {
                    ((Timer) e.getSource()).stop();
                    getProgressBar().setVisible(false);
                    setValue(getMinimum());
                    ;
                }
            }
        });
        timer.start();
        return this;
    }
}
