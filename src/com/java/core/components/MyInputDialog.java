package com.java.core.components;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import com.java.core.data.DBParam;
import com.java.core.settings.Settings;
import com.java.modules.db.DBService;
import com.java.core.utils.Utils;

import java.awt.BorderLayout;
import java.awt.Component;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.LayoutManager;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;

public class MyInputDialog {
	private static final long serialVersionUID = 1L;
	Component parent;
	public static MyInputDialog _instance;

	public static MyInputDialog instance() {
		if (_instance == null)
			_instance = new MyInputDialog(null);
		return _instance;

	}

	public static MyInputDialog instance(Component parent) {
		if (_instance == null)
			_instance = new MyInputDialog(null);
		_instance.parent = parent;
		return _instance;
	}

	public MyInputDialog(Component parent1) {
		parent = parent1;
	}

	public int showComboBox(Object message, String dialogTitle, Integer[] options, Integer defaultValue) {
		int result = (Integer) JOptionPane.showInputDialog(
				parent, message,
				dialogTitle, JOptionPane.QUESTION_MESSAGE, null, options, defaultValue);
		return result;
	}

	public String showComboBox(Object message, String dialogTitle, String[] options, String defaultValue) {
		String result = (String) JOptionPane.showInputDialog(
				parent, message,
				dialogTitle, JOptionPane.QUESTION_MESSAGE, null, options, defaultValue);
		return result;
	}

	public String showTextBox(String dialogTitle, String defaultValue) {
		String result = (String) JOptionPane.showInputDialog(dialogTitle, defaultValue);
		return result;
	}

	public Map<String, String> showMapInput(Object message, String dialogTitle, List<String> options) {
		return showMapInput(message, dialogTitle, options, null);
	}

	public Map<String, String> showMapInput(Object message, String dialogTitle,
			List<String> options,
			Map<String, List<String>> autoCompletes) {

		return showMapInput(message, dialogTitle, options.toArray(new String[0]), autoCompletes);
	}

	public Map<String, String> showMapInput(Object message, String dialogTitle, String[] options) {
		return showMapInput(message, dialogTitle, options, null);
	}

	public Map<String, String> showMapInput(Object message, String dialogTitle, String[] options,
			Map<String, List<String>> autoCompletes) {

		JPanel myPanel = Utils.createJPanelInput(options, autoCompletes);

		return showMapInput(message, dialogTitle, myPanel);
	}

	public List<DBParam> showMapInputListDBParams(Object message, String dialogTitle, List<DBParam> queryParams) {
		Map<String, String> params = DBService.getMapStringFromListDBParam(queryParams);
		Map<String, String> tmpParams = MyInputDialog.instance().showMapInput(null,
				dialogTitle, params);
		if (tmpParams == null)
			return null;
		params = tmpParams;
		for (Map.Entry<String, String> entry : params.entrySet()) {
			DBService.addQueryParam(queryParams, entry.getKey(), entry.getValue());
		}

		return queryParams;
	}

	public Map<String, String> showMapInput(Object message, String dialogTitle, Map<String, String> options) {
		return showMapInput(message, dialogTitle, options, null);
	}

	public Map<String, String> showMapInput(Object message, String dialogTitle, Map<String, String> options,
			Map<String, List<String>> autoCompletes) {

		if (options == null || options.size() == 0)
			return null;
		JPanel myPanel = Utils.createJPanelInput(options, autoCompletes);
		return showMapInput(message, dialogTitle, myPanel);
	}

	public Map<String, String> showMapInput(Object message, String dialogTitle, Component[] components) {

		JPanel myPanel = Utils.createJPanelInput(components);
		return showMapInput(message, dialogTitle, myPanel);
	}

	public Map<String, String> showComponents(Object message, String dialogTitle, Component component) {
		return showMapInput(message, dialogTitle, new Component[] { component });
	}

	public Map<String, String> showComponents(Object message, String dialogTitle, Component[] components) {
		return showMapInput(message, dialogTitle, components);
	}

	public Map<String, String> showMapInput(Object message, String dialogTitle, JPanel myPanel) {
		JPanel myPanel2 = myPanel;
		if (message != null && message instanceof String
				&& !((String) message).isEmpty()) {
			// myPanel2 = new JPanel(new GridLayout(2, 1));
			myPanel2 = new JPanel(new BorderLayout());
			JScrollPane lJPanel = Utils.createJTextAreaScrollable(((String) message), false);

			lJPanel.setBackground(Settings.ColorDialogBG);
			// add component at the top of jPanel
			myPanel2.add(lJPanel, BorderLayout.PAGE_START);

		} else {
			myPanel2 = new JPanel(new GridLayout(1, 1));
		}

		// Utils.setBorderTitle(myPanel, "Please input parameters (from left to right,
		// downward):");
		myPanel2.add(myPanel);

		int result = JOptionPane.showConfirmDialog(
				(message != null && message instanceof Component) ? (Component) message : parent,
				new JScrollPane(myPanel2),
				dialogTitle, JOptionPane.OK_CANCEL_OPTION);
		Utils.closeAutoComplete(myPanel);
		if (result == JOptionPane.OK_OPTION) {
			return getMapInput(myPanel);
		}
		return null;
	}

	public Map<String, String> getMapInput(JPanel myPanel) {
		return Utils.getJPanelInput(myPanel);
	}
}
