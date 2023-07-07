package com.rs2.core.components;

import javax.swing.*;

import java.awt.Color;
import java.awt.event.*;
import javax.swing.Timer;

import com.rs2.Main;
import com.rs2.core.logs.LogManager;

import java.awt.GridBagLayout;
import java.awt.geom.RoundRectangle2D;
import java.awt.Component;

public class MyToastMessage extends JFrame {
   private JLabel label;
   public static MyToastMessage instance;

   public static void showMessage(String message, Component parent) {
      if (instance == null)
         instance = new MyToastMessage();
      instance.setLocationRelativeTo(parent);
      instance.display(message);
   }

   public MyToastMessage() {
      setUndecorated(true);
      setLayout(new GridBagLayout());
      setBackground(new Color(240, 240, 240, 250));
      setLocationRelativeTo(null);
      setSize(300, 50);
      label = new JLabel();
      add(label);

      addComponentListener(new ComponentAdapter() {
         @Override
         public void componentResized(ComponentEvent e) {
            setShape(new RoundRectangle2D.Double(0, 0, getWidth(),
                  getHeight(), 20, 20));
         }
      });
   }

   public void display(String message) {

      Thread queryThread = new Thread() {
         public void run() {
            try {
               label.setText(message);
               setOpacity(1);
               setVisible(true);
               Thread.sleep(300);

               // hide the toast message in slow motion
               for (double d = 1.0; d > 0.2; d -= 0.1) {
                  Thread.sleep(30);
                  setOpacity((float) d);
               }

               // set the visibility to false
               setVisible(false);
            } catch (Exception e) {
               LogManager.getLogger().debug(e.getMessage());
            }
         }
      };
      queryThread.start();

   }
}
