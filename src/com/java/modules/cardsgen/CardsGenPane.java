package com.java.modules.cardsgen;

import java.awt.event.ItemListener;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import com.java.core.components.MyInputDialog;
import com.java.core.utils.RS2Util;
import com.java.core.utils.Utils;
import com.java.modules.dataviewer.DataViewerService;
import com.java.modules.files.FilesService;

import java.awt.event.*;
import java.awt.Component;

import javax.swing.JSplitPane;

import com.java.core.components.MyTree;
import com.java.core.components.treeview.DataNode;
import com.java.core.logs.LogManager;
import com.java.core.settings.Settings;
import com.java.core.base.MyPane;
import com.java.core.base.MyService;
import com.java.core.data.DBQuery;

import java.awt.BorderLayout;

public class CardsGenPane extends MyPane {
	private static final long serialVersionUID = 1L;
	public static String ServiceTitle = "Cards Generator";
	JSplitPane splitPane, splitPane1;

	private JTextArea textCard;
	private JTextField textHowMany, textStartBin, textCountry;
	private JComboBox cardOranizations;
	private JTextField textServiceTypes;
	private JTextField textCardBrands;
	// private JTextField textboxClientNumber, textBoxInstitutionNumber;
	private JPanel myPanel;

	// BSchema schema;
	MyInputDialog inputDialog = new MyInputDialog(this);

	public String getId() {
		if (panelId.isEmpty())
			panelId = ServiceTitle;
		return super.getId();
	}

	public CardsGenPane(Component view) {
		super(view);
	}

	public CardsGenPane() {
		super();
		progressBar.timerDelay = 1;
	}

	@Override
	public void refreshLayout() {
		if (theGUITree == null) {
			theGUITree = createJTree("theGUITree");
		}
		setMainPanel(theGUITree);

		splitPane = Utils.createSplitPane(JSplitPane.VERTICAL_SPLIT, getTopJPanel(),
				getMainJPanel());
		splitPane.setDividerLocation(200);
		getJPanel().add(splitPane, BorderLayout.CENTER);
	}

	public void initMenuPanel() {
		getMenuJpanel().add(btnOk);
		// getMenuJpanel().add(btnAdd);
		// getMenuJpanel().add(btnCollapse);
		getMenuJpanel().add(btnHideLog);
		// getMenuJpanel().add(btnClearLog);
		getMenuJpanel().add(btnCancel);
		btnCancel.setText("Export Excel");
		// getMenuJpanel().add(btnCancel);
		getMenuJpanel().add(btnClose);
	}

	public void initUI(Component view) {

		super.initUI(view);
		isHideLog = true;

		getMenuJpanel().setVisible(true);
		getMainJPanel().setVisible(true);
		getBottomJPanel().setVisible(false);
		getParamsPanel().setVisible(true);
		getSQLJpanel().setVisible(true);

		// textBoxInstitutionNumber = Utils.createTextField();
		initComboConns();

		refreshConnectionCombo();
		cardOranizations = Utils.createComboBox(RS2Util.CARD_BRANDS);
		textServiceTypes = Utils.createTextField("", "Service");

		textCardBrands = Utils.createTextField("", "Card Brands");
		textHowMany = Utils.createTextField("5", "Quantity");
		textStartBin = Utils.createTextField("", "Start bin");
		textCountry = Utils.createTextField("280", "Country");

		Utils.setComponentName(cardOranizations, "Card Organizations");

		myPanel = Utils.createJPanelInput(new Component[] { cardOranizations, textCardBrands, textStartBin,
				textCountry, textServiceTypes, textHowMany });

		if (view != null) {
			component = view;
			if (view instanceof MyTree)
				theGUITree = (MyTree) view;
		}

		// textCard = Utils.createJTextArea("", true);
		// textCard.setRows(10);
		// addMainPanel(textCard);
	}

	public void refreshUIMenu() {
		btnAdd.setVisible(viewOption == Settings.viewDetail);
		btnEdit.setText(getButtonText(btnEdit));
		btnEdit.setVisible(true);
		btnCancel.setVisible(true);
		btnReset.setVisible(viewOption == Settings.viewDetail);
		btnCollapse.setVisible(viewOption == Settings.viewDetail);
		btnOk.setVisible(viewOption == Settings.viewDetail);
		btnHideLog.setText("Check Luhn");
		btnOk.setText("Gen Cards");
		// btnClearLog.setVisible(!isHideLog);
		btnClose.setVisible(true);
	}

	@Override
	public void setSQL(String sql) {
		txtSQL.setText(txtSQL.getText().trim().isEmpty() ? sql : (txtSQL.getText().trim() + "\n" + sql));
	}

	public void initData() {
		initConnection();
	}

	@Override
	public void hideLog() {
		checkLuhn();
	}

	public void checkLuhn() {
		if (txtSQL == null)
			return;
		String[] cards = txtSQL.getText().trim().split("\n");
		for (String card : cards) {
			if (luhnCheck(card))
				log(card + " is valid card number.");
			else
				log(card + " is invalid card number !!");
		}
	}

	public void run(boolean overrideOldTree) {
		try {
			initData();
			conn = getConnection();
			generateCardNumbers();
			generateDataPane(listAllQueryDefinitions, overrideOldTree);

		} catch (Exception e) {
			LogManager.getLogger().error(e);
		}
	}

	public DataNode generateRootDataNode() {
		DataNode anfang = DataViewerService
				.generateDataNode(listAllQueryDefinitions, "Card Numbers", viewOption, true, true);
		return anfang;
	}

	public void generateCardNumbers() {
		if (MyInputDialog.instance().showMapInput(null, "Input Params", myPanel) == null)
			return;

		String cardOrganization = Utils.convertToStringValue(cardOranizations.getSelectedItem());
		String cardBrand = Utils.convertToStringValue(textCardBrands.getText().trim());
		String country = textCountry.getText().trim();
		String cardservice = textServiceTypes.getText().trim();

		String[] cards1 = RS2Util.generateCards(conn,
				cardOrganization,
				cardBrand,
				Utils.convertToInt(textHowMany.getText().trim()),
				textStartBin.getText().trim(),
				country,
				cardservice);
		setSQL(String.join("\n", cards1));

		String[] cards = txtSQL.getText().trim().split("\n");
		DBQuery query = new DBQuery("", cardOrganization + "_" + cardBrand + "_" + country);
		List<List<String>> data = new LinkedList<List<String>>();
		List<String> line = new LinkedList<String>();
		line.add("Card Number");
		line.add("Card Organization");
		line.add("Card Brand");
		line.add("Card Service");
		line.add("Country");
		data.add(line);

		for (String card : cards1) {
			line = new LinkedList<String>();
			line.add(card);
			line.add(cardOrganization);
			line.add(cardBrand);
			line.add(cardservice);
			line.add(country);
			data.add(line);

			if (luhnCheck(card))
				logger.debug(card + " is valid card number.");
			else
				logger.error(card + " is invalid card number !!");
		}

		query.setData(data);
		query.setConnection(getConnection());
		if (listAllQueryDefinitions == null)
			listAllQueryDefinitions = new LinkedList<DBQuery>();
		listAllQueryDefinitions.add(query);
	}

	public void initComboConns() {
		conns = getConnectionNames();
		if (connectionName == null)
			connectionName = "";
		if (conns != null && !conns.isEmpty()) {
			// Menu Connections
			comboBoxStoredConns = Utils.createComboBox(conns, connectionName);
			comboBoxStoredConns.addItemListener(new ItemListener() {
				public void itemStateChanged(ItemEvent arg0) {
					if (!connectionName.equalsIgnoreCase((String) comboBoxStoredConns.getSelectedItem())) {
						refreshConnectionCombo();
						connectionName = (String) comboBoxStoredConns.getSelectedItem();
					}
				}
			});
			getParamsPanel().add(comboBoxStoredConns);
		}
	}

	public void refreshtextCardBrands() {
		if (textCardBrands != null && cardOranizations.getSelectedItem() != null) {
			String cardOranization = Utils.getComponentValue(cardOranizations);

			Utils.setAutoComplete(
					textCardBrands, RS2Util.getCardBrands(cardOranization, getConnection()));
		}
	}

	public void refreshConnectionCombo() {
		try {
			boolean connectionChanged = isConnectionChanged();
			if (connectionChanged) {
				initConnection();
				refreshtextCardBrands();
				Utils.setAutoComplete(textServiceTypes, RS2Util.getCardServicesList(getConnection()));

				cardOranizations.addItemListener(new ItemListener() {
					public void itemStateChanged(ItemEvent arg0) {
						refreshtextCardBrands();
					}
				});

				Utils.setAutoComplete(textCountry, RS2Util.getCountryList(getConnection()));

			}

		} catch (Exception ex) {
			LogManager.getLogger().error(ex);
		}
	}

	public void cancel() {
		saveToExcel();
	}

	public void saveToExcel() {
		Utils.exportListDBQueryToExcel(listAllQueryDefinitions);
	}

	/**
	 * Checks if the card is valid
	 * 
	 * @param card
	 *             {@link String} card number
	 * @return result {@link boolean} true of false
	 */
	public static boolean luhnCheck(String card) {

		if (card == null || card.isEmpty())
			return false;

		char checkDigit = card.charAt(card.length() - 1);
		String digit = calculateCheckDigit(card.substring(0, card.length() - 1));
		return checkDigit == digit.charAt(0);
	}

	/**
	 * Calculates the last digits for the card number received as parameter
	 * 
	 * @param card
	 *             {@link String} number
	 * @return {@link String} the check digit
	 */
	public static String calculateCheckDigit(String card) {
		if (card == null)
			return null;
		String digit;
		/* convert to array of int for simplicity */
		int[] digits = new int[card.length()];
		for (int i = 0; i < card.length(); i++) {
			digits[i] = Character.getNumericValue(card.charAt(i));
		}

		/* double every other starting from right - jumping from 2 in 2 */
		for (int i = digits.length - 1; i >= 0; i -= 2) {
			digits[i] += digits[i];

			/* taking the sum of digits grater than 10 - simple trick by substract 9 */
			if (digits[i] >= 10) {
				digits[i] = digits[i] - 9;
			}
		}
		int sum = 0;
		for (int i = 0; i < digits.length; i++) {
			sum += digits[i];
		}
		/* multiply by 9 step */
		sum = sum * 9;

		/* convert to string to be easier to take the last digit */
		digit = sum + "";
		return digit.substring(digit.length() - 1);
	}

	public void genVisaFunction1(String[] cards) {
		for (int i = 0; i < cards.length - 1; i++) {
			String line = cards[i];
			if (line.trim().startsWith("visa_")) {
				String[] tmp = line.trim().replace("  ", " ").split(" ");
				if (tmp.length >= 3) {
					tmp[0] = "addMessage(new ";
					if (!tmp[1].endsWith(")"))
						tmp[1] = tmp[1] + "()";
					tmp[2] = "); // " + tmp[2];
					line = "\t\t" + String.join(" ", tmp);
				}
			} else if (line.trim().startsWith("local ")) {
				line = line.replace("local ", "");
			} else if (line.trim().startsWith("Sprintf")) {
				line = line.replace("Sprintf", Utils.substringBetween(line, "Sprintf(", ",").trim() + " = Sprintf");
			} else if (line.trim().startsWith("char ") && line.trim().contains("[") && line.trim().endsWith("];")) {
				line = line.replace("char ", "String ");
				String size = Utils.substringBetween(line, "[", "]");
				line = line.substring(0, line.indexOf("[")) + "; // char[" + size + "]";
			}

			line = line.replace("string ", "String ")
					.replace("int64 ", "int ")
					.replace("uchar ", "char ");
			cards[i] = line;
		}
		String result = String.join("\n", cards);
		textCard.setText(result);
		Utils.saveFile("C:\\_Works\\tools\\rs2-ba-tools\\src\\rs2\\files\\visa\\tmp.txt",
				result);

		Utils.copyToClipboard(result);
	}

	public void genVisaFile(String[] cards) {
		String content = "";
		String className = "", fieldName = "", fieldDesc = "", desc = "";
		String result1 = "", result12 = "", result2 = "", result3 = "", column = "", length = "", result4 = "";

		String tmp = "";
		boolean openStatement = false;
		for (String line : cards) {
			line = line.trim();
			if (line.isEmpty())
				continue;
			if (!line.startsWith(tmp) && !tmp.isEmpty() && !line.startsWith("BYTE"))
				line = tmp + " " + line;

			if (line.equals("}") && !openStatement) {
				tmp = line;
				continue;
			}
			tmp = "";

			if (line.startsWith("BYTE")) {
				fieldName = Utils.substringBetween(line, " ", "[").trim();
				length = Utils.substringBetween(line, "[", "]").trim();

				line = line.replace("[", "; //[").replace("BYTE ", "public char[] ");
				result1 += "\tpublic AsciiField " + FilesService.getAttributeName(fieldName) + ";\n";
				result12 += "\t" + line + "\n";
				result2 += "\t\taddElement(\"" + fieldName + "\", " + length + "); \n"; // addElement("trans_code", 2);
				result3 += "\t\t" + fieldName + " = getElement(\"" + fieldName + "\"); \n"; // trans_code =
																							// getElement("trans_code");
			} else if (line.startsWith("if")) {
				result2 += "\t\t" + line + "\n";
				openStatement = true;
				continue;
			} else if (line.startsWith("}")) {
				if (openStatement) {
					result2 += "\t\t" + line + "\n";
					openStatement = false;
					continue;
				}
				tmp = line;

				className = Utils.substringBetween(line, "}", "_STR").trim();
				result4 += "\t\tpublic " + className + " " + className + "; \n";

				if (line.contains("//"))
					desc = Utils.substringBetween(line, "//", "//").trim();
				else
					desc = "";
				content += "package com.java.files.visa.structures;\n\n";
				content += "import com.java.files.isoparser.elements.ascii.AsciiField;\n";
				content += "import com.java.files.isoparser.elements.ascii.AsciiMessage;\n\n";
				content += "public class {class} extends AsciiMessage {\n";
				content += "{result1}\n\n";
				content += "\tpublic {class}(int offset, String content) { \n\t\tsuper(offset, content); \n \t}\n \tpublic {class}(String content) {\n \t\tsuper(content); \n \t} \n \tpublic {class}() { \n \t\tsuper(); \n \t} \n \tpublic {class}(int ifReturn) { \n \t\tsuper(ifReturn); \n \t} \n";
				content += "\n\t@Override\n \tpublic void initFields(int ifReturn) {\n {result2} \n \t} \n\n ";
				content += "\n\tpublic void setDataString(String content) {\n\t\tsuper.setDataString(content);\n {result3} \n \t} \n";
				content += "\n\t@Override\n \tpublic String getDescription() {\n\t\treturn \"{desc}\"; \n \t} ";
				content += "}";

				content = content.replace("{result1}", result1 + result12).replace("{result2}", result2)
						.replace("{result3}", result3).replace(
								"{desc}", desc)
						.replace("{class}", className);

				Utils.saveFile(
						"C:\\_Works\\tools\\rs2-ba-tools\\src\\rs2\\files\\visa\\structures\\" + className + ".java",
						content.trim());
				content = "";
				desc = "";
				className = "";
				result1 = "";
				result12 = "";
				result2 = "";
				result3 = "";
				length = "";
				tmp = "";
				openStatement = false;
				fieldName = "";
			} else {
				result2 += "\t //" + line + "; \n";
			}

		}

		Utils.saveFile("C:\\_Works\\tools\\rs2-ba-tools\\src\\rs2\\files\\visa\\tmp.txt",
				result4);

	}

	public void genIPMFile(String[] cards) {
		String content = "";
		String className = "", fieldName = "";
		String result1 = "", result2 = "", result3 = "", column = "", length = "";

		for (String card : cards) {

			if (card.isEmpty() || card.contains("}") || card.contains("{"))
				continue;
			if (card.contains(";")) {
				String[] arr = card.split(";");
				// <field num="1" type="NUMERIC" length="8"></field>
				// <field num="2" type="LLVAR"></field>
				LogManager.getLogger().debug("<field num=\"" + arr[0].trim()
						+ "\" type=\""
						+ (arr[2].trim().equalsIgnoreCase("FIXED") ? ("ALPHA length=\"" + arr[3].trim() + "\"")
								: arr[2].trim())
						+ "></field>");
				continue;
			}

			if (card.toUpperCase().contains("BYTE ")) {
				String tmp = card.trim(); // Utils.substringBetween(card, "BYTE ", "] <");
				String[] tmpArr = tmp.split(" ");

				column = tmpArr[1];
				length = tmpArr[2];
				if (length.contains("["))
					length = length.substring(1, length.length() - 1);
				else
					length = "10";

				result1 = result1 + "\tpublic String " + column + ";\n";
				result2 = result2 + "\t\taddElement(\"" + column + "\", " + length + ");\n";
				result3 = result3 + "\t\t" + column + " = getElement(\"" + column + "\");\n";
			} else {
				// TextMessage item = FilesService.createByteArray(card.trim());
				// if (item != null)
				// result1 = result1 + item.toString() + "\n";

			}

		}
	}

}