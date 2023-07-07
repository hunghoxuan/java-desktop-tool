package com.java.core.components;

import java.awt.BorderLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import com.java.core.components.myeditor.MyTextPane;
import com.java.core.logs.LogManager;
import com.java.core.settings.Settings;
import com.java.core.utils.Utils;

import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Scanner;
import java.awt.event.ActionEvent;

public class MyCmdStream implements Runnable {
	private PrintStream out;
	private Scanner inScanner;
	private String name;

	public MyCmdStream(String name, PrintStream out, InputStream inStream) {
		this.name = name;
		this.out = out;
		inScanner = new Scanner(new BufferedInputStream(inStream));
	}

	@Override
	public void run() {
		while (inScanner.hasNextLine()) {
			String line = inScanner.nextLine();

			// do something with the line!
			// check if requesting password

			System.out.printf("%s: %s%n", name, line);
		}
	}
}
