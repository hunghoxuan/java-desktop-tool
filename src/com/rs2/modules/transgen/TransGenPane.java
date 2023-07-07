package com.rs2.modules.transgen;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.sql.Connection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.rs2.core.components.MyCmdStream;
import com.rs2.core.components.MyDialog;
import com.rs2.core.components.MyFileBrowser;
import com.rs2.core.components.MyInputDialog;
import com.rs2.core.utils.RS2Util;
import com.rs2.core.utils.Utils;
import java.awt.event.*;
import java.awt.Component;
import javax.swing.JSplitPane;

import com.rs2.core.components.MyTree;
import com.rs2.core.logs.LogManager;
import com.rs2.core.settings.Settings;
import com.rs2.modules.db.DBService;
import com.rs2.core.base.MyPane;
import com.rs2.core.base.MyService;

import java.awt.BorderLayout;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.edge.EdgeDriver;

import io.github.bonigarcia.wdm.WebDriverManager;

public class TransGenPane extends MyPane {
	private static final long serialVersionUID = 1L;
	public static String ServiceTitle = "Trans Generator";
	JCheckBox isGenParamFile;
	JCheckBox isGenCardNumbers;
	JCheckBox isGenAcquirerBatchInput;
	JCheckBox isTransactionGeneratorTool;

	private static PrintStream out;

	public static String[] pre_processor_visa_fields = new String[] { "TEST_CASE_ID", "TEST_DESCRIPTION", "SMS",
			"MER_COUNTRY", "TERMINAL_ID", "MERCHANT_ID", "MCC", "TRAN_TYPE", "TIMELINESS", "TRAN_CURR", "TRAN_AMOUNT",
			"SETT_CURR", "SETT_AMOUNT", "CASHBACK", "INSTALLMENT_TYPE", "INSTALLMENT_COUNT", "FIRST_INSTALLMENT_AMOUNT",
			"TC", "CM", "PTC", "PEM", "CIM", "MEI", "ATI", "PENV", "AUTH_RESPONSE", "AUTH", "UCAF_IND", "CARD_NUMBER",
			"BIN_COUNTRY", "PRODUCT_ID", "SUB_PRODUCT", "FUNDING_SOURCE", "B2B_ID", "PREPAID_PROGRAM_INDICATOR",
			"CC_FLAG", "AUTH_IND", "SPEND_IND", "MARKET_DATA", "BAI", "VISA_TOKEN_PROGRAM_VALUE", "WALLET_ID",
			"NATIONAL_TAX_INDICATOR_SUMM", "MERCHANT_VAT_REGISTRATION_SUMM", "MGIS_VGIS", "AIRLINE_ADDENDUM",
			"PAYFAC_ADDENDUM", "FEE_RULE", "FEE_RATE", "FEE_IDENTIFIER", "FEE_BASE", "FEE_MINIMUM", "FEE_MAXIMUM",
			"AREA_OF_EVENT" };
	public static String[] pre_processor_mc_fields = new String[] { "TEST_CASE_ID", "TEST_DESCRIPTION", "SMS",
			"MER_COUNTRY", "TERMINAL_ID", "MERCHANT_ID", "MCC", "BIN_COUNTRY", "PRODUCT_ID", "SUB_PRODUCT",
			"CARD_SERVICE", "PAYPASS", "TRAN_TYPE", "TRAN_CURR", "TRAN_AMOUNT", "INSTALLMENT_TYPE", "INSTALLMENT_COUNT",
			"FIRST_INSTALLMENT_AMOUNT", "TIMELINESS", "SETT_CURR", "SETT_AMOUNT", "CASHBACK", "TC", "CM", "DE22.01",
			"DE22.02", "DE22.03", "DE22.04", "DE22.05", "DE22.06", "DE22.07", "DE22.08", "DE22.09", "DE22.10",
			"DE22.11", "DE22.12", "PDS0023", "AUTH_RESPONSE", "AUTH", "UCAF_IND", "WALLET_ID", "CARD_NUMBER",
			"AUTH_IND", "SPEND_IND", "MARKET_DATA", "BAI", "MGIS_VGIS", "AIRLINE_ADDENDUM", "PAYFAC_ADDENDUM",
			"FEE_RULE", "FEE_RATE", "FEE_IDENTIFIER", "FEE_BASE", "FEE_MINIMUM", "FEE_MAXIMUM" };

	JSplitPane splitPane, splitPane1;

	private JComboBox comboCardBrands, comboTranType, comboTC, comboCM, comboPaypassEnabled;
	private JTextField textClientNumber, textInstitutionNumber;
	private JTextField textHowMany, textStartBin, textCountry, textAuditTrail, textMerchantId, textTerminalId;
	private JTextField textAmountMin, textAmountMax, textCurrency, textProduct, textSubProduct, textFundingSource,
			textB2B;
	private JComboBox comboCardOrganizations;
	private JComboBox comboServiceTypes;
	private JTextField textCardBrands;

	private JPanel myPanel;

	// BSchema schema;
	MyInputDialog inputDialog = new MyInputDialog(this);

	public String getId() {
		if (panelId.isEmpty())
			panelId = ServiceTitle;
		return super.getId();
	}

	public TransGenPane(Component view) {
		super(view);
	}

	public TransGenPane() {
		super();
		progressBar.timerDelay = 1;
	}

	@Override
	public void refreshLayout() {
		// if (theBrowseTree == null) {
		// theBrowseTree = createJTree("theBrowseTree");
		// }

		// splitPane1 = Utils.createSplitPane(JSplitPane.HORIZONTAL_SPLIT, new
		// JScrollPane(theBrowseTree),
		// getMainJPanel());
		// splitPane1.setDividerLocation(250);

		splitPane = Utils.createSplitPane(JSplitPane.VERTICAL_SPLIT, getTopJPanel(),
				getMainJPanel());
		splitPane.setDividerLocation(200);
		getJPanel().add(splitPane, BorderLayout.CENTER);
		getBottomJPanel().setVisible(!isHideLog);
	}

	public void initMenuPanel() {
		getMenuJpanel().add(btnOk);
		// getMenuJpanel().add(btnAdd);
		// getMenuJpanel().add(btnCollapse);
		getMenuJpanel().add(btnHideLog);
		// getMenuJpanel().add(btnClearLog);
		// getMenuJpanel().add(btnEdit);
		// btnCancel.setText("Save SQL");
		// getMenuJpanel().add(btnCancel);
		getMenuJpanel().add(btnClose);
	}

	public void initUI(Component view) {

		super.initUI(view);
		isHideLog = true;

		getMenuJpanel().setVisible(true);
		getMainJPanel().setVisible(true);
		getBottomJPanel().setVisible(!isHideLog);
		getParamsPanel().setVisible(true);
		getSQLJpanel().setVisible(true);

		initComboConns();

		refreshConnectionCombo();

		comboCardOrganizations = Utils.createComboBox(RS2Util.CARD_BRANDS, null, "CARD_ORGANIZATION");
		comboServiceTypes = Utils.createComboBox(RS2Util.getCardServicesList(conn), "001 : Credit Card",
				"card_service");

		textCardBrands = Utils.createTextField("", "card_brand");
		textHowMany = Utils.createTextField("2", "transactions No");
		textStartBin = Utils.createTextField("", "start bin");
		textCountry = Utils.createTextField("280", "country");
		textCurrency = Utils.createTextField("978", "currency");
		textTerminalId = Utils.createTextField("", "terminal_id");
		textMerchantId = Utils.createTextField("", "merchant_id");
		textProduct = Utils.createTextField("MCC", "product_id");
		textSubProduct = Utils.createTextField("MCC", "sub_product");
		textB2B = Utils.createTextField("", "B2B_ID");
		textFundingSource = Utils.createTextField("", "funding_source");
		comboPaypassEnabled = Utils.createComboBox(RS2Util.ENABLED, "N", "paypass_enabled");
		comboTranType = Utils.createComboBox(RS2Util.TRAN_TYPES, "PURCHASE", "tran_type");

		comboCM = Utils.createComboBox(RS2Util.getCaptureMethod(conn), "004", "capture_method");
		comboTC = Utils.createComboBox(RS2Util.getTerminalCompability(conn), "001", "terminal_capability");

		textAmountMin = Utils.createTextField("500", "amount_min");
		textAmountMax = Utils.createTextField("1000", "amount_max");

		textInstitutionNumber = Utils.createTextField("", "institution_number");
		textInstitutionNumber.addFocusListener(
				new FocusListener() {
					@Override
					public void focusGained(FocusEvent e) {
					};

					@Override
					public void focusLost(FocusEvent e) {
						// textInstitutionNumber.setText(RS2Util.cleanSqlValue(
						// textInstitutionNumber.getText().trim(), 8));
						if (!e.isTemporary() && isEnabled()) {
							if (!textInstitutionNumber.getText().trim().isEmpty()) {
								Utils.setAutoComplete(
										textClientNumber, RS2Util.getClientNumbersList(
												textInstitutionNumber.getText().trim(), textCountry.getText().trim(),
												getConnection()));
							}
						}
					}
				});
		textClientNumber = Utils.createTextField("", "client_number");
		textClientNumber.addFocusListener(
				new FocusListener() {
					@Override
					public void focusGained(FocusEvent e) {
					};

					@Override
					public void focusLost(FocusEvent e) {
						// textClientNumber.setText(RS2Util.cleanSqlValue(
						// textClientNumber.getText().trim(), 8));
						if (!e.isTemporary() && isEnabled()) {
							Connection conn = getConnection();

							if (!textClientNumber.getText().trim().isEmpty()) {
								List<Map<String, String>> data = RS2Util.getTerminalIdsRecord(
										textInstitutionNumber.getText().trim(), textClientNumber.getText().trim(),
										conn, 1);
								Utils.setAutoComplete(
										textTerminalId, RS2Util.getTerminalIdsList(
												textInstitutionNumber.getText().trim(), textClientNumber.getText()
														.trim(),
												conn));
								if (data.size() > 0) {
									Map<String, String> row = data.get(0);
									if (row.get("TERMINAL_CURRENCY") != null)
										textCurrency.setText(row.get("TERMINAL_CURRENCY"));
									if (row.get("TERMINAL_ID") != null)
										textTerminalId.setText(row.get("TERMINAL_ID"));
									if (row.get("MERCHANT_ID") != null)
										textMerchantId.setText(row.get("MERCHANT_ID"));
								}
							}
						}
					}
				});

		textAuditTrail = Utils.createTextField("MANUAL_INPUT_" + Utils.formatDate(), "audit_trail");

		myPanel = Utils.createJPanelInput(
				new Component[] {
						textInstitutionNumber,
						textClientNumber,
						textMerchantId,
						textTerminalId,
						textCountry,

						comboCardOrganizations,
						// textCardBrands,
						// textStartBin,
						textProduct,
						textSubProduct,
						textFundingSource,
						textB2B,
						comboPaypassEnabled,

						comboServiceTypes,
						textCurrency,
						textAmountMin,
						textAmountMax,
						comboTranType,
						comboTC,
						comboCM,
						textAuditTrail,
						textHowMany
				// Utils.createLabel("")
				});

		if (view != null) {
			component = view;
			if (view instanceof MyTree)
				theGUITree = (MyTree) view;
		}
	}

	public void refreshUIMenu() {
		btnAdd.setVisible(viewOption == Settings.viewDetail);
		btnEdit.setText(getButtonText(btnEdit));
		btnEdit.setVisible(true);
		btnCancel.setVisible(true);
		btnCancel.setText("Export Excel");
		btnReset.setVisible(viewOption == Settings.viewDetail);
		btnCollapse.setVisible(viewOption == Settings.viewDetail);
		btnOk.setVisible(viewOption == Settings.viewDetail);
		btnHideLog.setText("Refresh");
		// btnOk.setText("Gen Cards");
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
		super.hideLog();
	}

	private void updateConfigurationIni() {
		String iniFile = Settings.getTransGenFolder() + "\\configuration.ini";
		String iniContent = Utils.getContentFromFile(iniFile);
		if (iniContent != null) {
			updateConnectionSettings();
			Map<String, String> connParams = new HashMap<String, String>();
			if (password == null || password.isEmpty())
				password = Settings.getDefaultDbPassword();
			connParams.put("user", userName);
			connParams.put("password", password);
			connParams.put("host", host);
			connParams.put("port", port);
			connParams.put("sid", serviceName);
			iniContent = Utils.replaceParams(iniContent, connParams, "", "=", "", "", "");
			Utils.saveFile(iniFile, iniContent);
			LogManager.getLogger().debug("Updated configuration.ini file !!");
			// System.out.println(iniContent);
		}
	}

	public void generateTransactions1() {
		updateConfigurationIni();
	}

	public void generateTransactions() {
		if (isGenCardNumbers == null)
			isGenCardNumbers = Utils.createCheckBox("Generate Card numbers ?", true);
		if (isGenParamFile == null)
			isGenParamFile = Utils.createCheckBox("Generate param file for Generator tool (excel) ?", true);
		if (isGenAcquirerBatchInput == null)
			isGenAcquirerBatchInput = Utils.createCheckBox("Generate Acquirer Batch Input SQL ?", true);
		if (isTransactionGeneratorTool == null)
			isTransactionGeneratorTool = Utils.createCheckBox("Generate Auths/CSF/Billable Events ?", true);

		Map<String, String> params = MyInputDialog.instance().showMapInput(null,
				"Which file would you like to generate ?",
				new Component[] {
						isGenCardNumbers, isGenParamFile, isGenAcquirerBatchInput, isTransactionGeneratorTool });
		if (params == null)
			return;

		params = MyInputDialog.instance().showMapInput(null, "Input Transactions Params", myPanel);
		if (params == null)
			return;

		if (textInstitutionNumber.getText().trim().isEmpty() || textClientNumber.getText().trim().isEmpty()) {
			MyDialog.showDialog("Institution Number / Client number is required !!", "Warning");
			return;
		}
		String institution_number = RS2Util.cleanSqlValue(textInstitutionNumber.getText().trim(), 8);
		String client_number = RS2Util.cleanSqlValue(textClientNumber.getText().trim(), 8);
		params.put("institution_number", institution_number);
		params.put("client_number", client_number);

		String cardOrganization = Utils.getComponentValue(comboCardOrganizations, true);
		String cardOrganizationStr = String.valueOf(comboCardOrganizations.getSelectedItem());
		String cardBrand = Utils.convertToStringValue(textCardBrands.getText().trim());
		String cardBrandStr = textCardBrands.getText();
		String country = textCountry.getText().trim();
		String cardservice = Utils.getComponentValue(comboServiceTypes, true);
		String cardServiceStr = comboServiceTypes.getSelectedItem().toString();
		cardServiceStr = cardServiceStr.substring(cardServiceStr.indexOf(":") + 1).trim();
		if (cardservice.equals("000"))
			cardservice = "";

		// String[] cards1 = RS2Util.generateCards(conn,
		// cardOrganization,
		// cardBrand,
		// Utils.convertToInt(textHowMany.getText().trim()),
		// textStartBin.getText().trim(),
		// country,
		// cardservice);
		if (isGenCardNumbers.isSelected()) {
			String[] cards1 = RS2Util.generateCards(conn,
					cardOrganization,
					Utils.getComponentValue(textProduct),
					Utils.getComponentValue(textSubProduct),
					Utils.getComponentValue(comboPaypassEnabled),
					country,
					Utils.getComponentValue(textFundingSource),
					Utils.getComponentValue(textB2B),
					"",
					institution_number,
					Utils.convertToInt(textHowMany.getText().trim()));
			if (cards1.length == 0) {
				LogManager.getLogger().error("Could not find any card numbers on given data criteria !!");
				return;
			}
			txtSQL.setText("");
			setSQL(String.join("\n", cards1));
		}

		String[] cards = txtSQL.getText().trim().split("\n");

		String auditTrail = textAuditTrail.getText().trim();
		String terminalId = textTerminalId.getText().trim();
		String merchantId = textMerchantId.getText().trim();

		String currency = Utils.getComponentValue(textCurrency, textCurrency.getText().trim());
		country = Utils.getComponentValue(textCountry, country);

		String amount = String.valueOf(Utils.randInt(
				Utils.convertToInt(textAmountMin.getText().trim()),
				Utils.convertToInt(textAmountMax.getText().trim())));

		if (isGenParamFile.isSelected()) {
			// prepare param excel file
			List<List<String>> data = new LinkedList<List<String>>();
			List<String> line = new LinkedList<String>();
			Map<String, String> map = new LinkedHashMap<String, String>();
			List<String> columns = new LinkedList<String>();

			if (cardOrganization.equals(RS2Util.CARD_BRAND_MAST)) {
				map.put("TEST_CASE_ID", "");
				map.put("TEST_DESCRIPTION", "");
				map.put("SMS", "");
				map.put("MER_COUNTRY", "");
				map.put("TERMINAL_ID", "");
				map.put("MERCHANT_ID", "");
				map.put("MCC", "");
				map.put("BIN_COUNTRY", "");
				map.put("PRODUCT_ID", "");
				map.put("SUB_PRODUCT", "");
				map.put("CARD_SERVICE", "");
				map.put("PAYPASS", "");
				map.put("TRAN_TYPE", "");
				map.put("TRAN_CURR", "");
				map.put("TRAN_AMOUNT", "");
				// map.put("INSTALLMENT_TYPE", "");
				// map.put("INSTALLMENT_COUNT", "");
				// map.put("FIRST_INSTALLMENT_AMOUNT", "");
				map.put("TIMELINESS", "");
				map.put("SETT_CURR", "");
				map.put("SETT_AMOUNT", "");
				map.put("CASHBACK", "");
				map.put("TC", "");
				map.put("CM", "");
				map.put("DE22.01", "");
				map.put("DE22.02", "");
				map.put("DE22.03", "");
				map.put("DE22.04", "");
				map.put("DE22.05", "");
				map.put("DE22.06", "");
				map.put("DE22.07", "");
				map.put("DE22.08", "");
				map.put("DE22.09", "");
				map.put("DE22.10", "");
				map.put("DE22.11", "");
				map.put("DE22.12", "");
				map.put("PDS0023", "");
				map.put("AUTH_RESPONSE", "");
				map.put("AUTH", "");
				map.put("UCAF_IND", "");
				map.put("WALLET_ID", "");
				map.put("CARD_NUMBER", "");
				map.put("AUTH_IND", "");
				map.put("SPEND_IND", "");
				map.put("MARKET_DATA", "");
				// map.put("BAI", "");
				map.put("MGIS_VGIS", "");
				map.put("AIRLINE_ADDENDUM", "");
				map.put("PAYFAC_ADDENDUM", "");
				map.put("FEE_RULE", "");
				map.put("FEE_RATE", "");
				map.put("FEE_IDENTIFIER", "");
				map.put("FEE_BASE", "");
				map.put("FEE_MINIMUM", "");
				map.put("FEE_MAXIMUM", "");

				map.put("REGISTRATION_REF_NO", "");
				map.put("RECURRING_PAYMENT_TYPE", "");
				map.put("PAYMENT_AMT_INDICATOR_PER_TXN", "");
				map.put("MAX_RECURRING_PAYMENT_AMT", "");
				map.put("RECURRING_PAYMENT_NO", "");
				map.put("RECURRING_PAYMENT_FREQ", "");
				map.put("POS_DATA", "");
			} else if (cardOrganization.equals(RS2Util.CARD_BRAND_VISA)) {
				map.put("TEST_CASE_ID", "");
				map.put("TEST_DESCRIPTION", "");
				map.put("SMS", "");
				map.put("MER_COUNTRY", "");
				map.put("TERMINAL_ID", "");
				map.put("MERCHANT_ID", "");
				map.put("MCC", "");
				map.put("TRAN_TYPE", "");
				map.put("TIMELINESS", "");
				map.put("TRAN_CURR", "");
				map.put("TRAN_AMOUNT", "");
				map.put("SETT_CURR", "");
				map.put("SETT_AMOUNT", "");
				map.put("CASHBACK", "");
				map.put("INSTALLMENT_TYPE", "");
				map.put("INSTALLMENT_COUNT", "");
				map.put("FIRST_INSTALLMENT_AMOUNT", "");
				map.put("TC", "");
				map.put("CM", "");
				map.put("PTC", "");
				map.put("PEM", "");
				map.put("CIM", "");
				map.put("MEI", "");
				map.put("ATI", "");
				map.put("PENV", "");
				map.put("AUTH_RESPONSE", "");
				map.put("AUTH", "");
				map.put("UCAF_IND", "");
				map.put("CARD_NUMBER", "");
				map.put("BIN_COUNTRY", "");
				map.put("PRODUCT_ID", "");
				map.put("SUB_PRODUCT", "");
				map.put("FUNDING_SOURCE", "");
				map.put("B2B_ID", "");
				map.put("PREPAID_PROGRAM_INDICATOR", "");
				map.put("CC_FLAG", "");
				map.put("AUTH_IND", "");
				map.put("SPEND_IND", "");
				map.put("MARKET_DATA", "");
				map.put("BAI", "");
				map.put("VISA_TOKEN_PROGRAM_VALUE", "");
				map.put("WALLET_ID", "");
				map.put("NATIONAL_TAX_INDICATOR_SUMM", "");
				map.put("MERCHANT_VAT_REGISTRATION_SUMM", "");
				map.put("MGIS_VGIS", "");
				map.put("AIRLINE_ADDENDUM", "");
				map.put("PAYFAC_ADDENDUM", "");
				map.put("FEE_RULE", "");
				map.put("FEE_RATE", "");
				map.put("FEE_IDENTIFIER", "");
				map.put("FEE_BASE", "");
				map.put("FEE_MINIMUM", "");
				map.put("FEE_MAXIMUM", "");
				map.put("AREA_OF_EVENT", "");
			}

			int j = 0;
			for (String card : cards) {
				j += 1;
				amount = String.valueOf(Utils.randInt(
						Utils.convertToInt(textAmountMin.getText().trim()),
						Utils.convertToInt(textAmountMax.getText().trim())));

				map.put("MER_COUNTRY", country);
				map.put("MERCHANT_ID", merchantId);
				map.put("TERMINAL_ID", terminalId);
				map.put("SETT_AMOUNT", amount);
				map.put("SETT_CURR", currency);

				map.put("TRAN_AMOUNT", amount);
				map.put("TRAN_CURR", currency);

				map.put("CARD_NUMBER", card);
				map.put("BIN_COUNTRY", country);
				map.put("SMS", "N");

				map.put("TIMELINESS", "1");

				map.put("TRAN_TYPE", String.valueOf(comboTranType.getSelectedItem()));
				map.put("TC", Utils.getComponentValue(comboTC, true));
				map.put("CM", Utils.getComponentValue(comboCM, true));

				map.put("PRODUCT_ID", "MCC");
				map.put("SUB_PRODUCT", "MCC");
				map.put("MCC", "5411");

				map.put("AUTH_RESPONSE", "00");
				map.put("AUTH", "Y");
				map.put("CASHBACK", "");

				map.put("FEE_RULE", "");

				if (cardOrganization.equals(RS2Util.CARD_BRAND_MAST)) {
					map.put("DE22.01", "2");
					map.put("DE22.02", "1");
					map.put("DE22.03", "0");
					map.put("DE22.04", "1");
					map.put("DE22.05", "0");
					map.put("DE22.06", "0");
					map.put("DE22.07", "B");
					map.put("DE22.08", "5");
					map.put("DE22.09", "4");
					map.put("DE22.10", "0");
					map.put("DE22.11", "4");
					map.put("DE22.12", "6");
					map.put("PDS0023", "POI");
					map.put("ECOMM_DATA", "220");
					map.put("POS_DATA", "02");
					map.put("UCAF_IND", "");
					map.put("CARD_SERVICE", cardServiceStr);
					map.put("PAYPASS", "N");
				} else if (cardOrganization.equals(RS2Util.CARD_BRAND_VISA)) {
					map.put("B2B_ID", "");
					map.put("CVV2 ", "");
					map.put("FUNDING_SOURCE ", "");
					map.put("CC_FLAG ", "");
				}

				map = MyInputDialog.instance().showMapInput(null, "Transaction #" + String.valueOf(j), map);
				if (columns == null || columns.size() == 0) {
					columns = Utils.convertToStringList(map.keySet());
					data.add(columns);
				}
				data.add(Utils.convertToStringList(map.values()));
			}

			Utils.exportListDataToFile(data, "params", cardOrganization);
		}

		String savedFolder = "";
		int selectedOption = -1;
		if (isTransactionGeneratorTool.isSelected()) {
			updateConfigurationIni();

			Utils.saveFile("transgen.bat",
					"cd " + Settings.getTransGenFolder() + " & client.exe");
			Utils.runCmd("transgen.bat");

			File file = Utils.openExternalFile(Settings.getTransGenFolder() + "\\client.exe");

			if (file != null && file.exists() && isGenAcquirerBatchInput.isSelected()) {
				savedFolder = getSavedFolder(100000);
				selectedOption = MyDialog.showDialog("Do you want to continue ?", "");
			} else {
				savedFolder = getSavedFolder();
			}
		}

		if (isGenAcquirerBatchInput.isSelected() && selectedOption == JOptionPane.OK_OPTION) {
			if (savedFolder.isEmpty())
				savedFolder = getSavedFolder();
			// int selectedOption = MyDialog.showDialog("Do you want to generate acq batch
			// input file ? ", "");
			// if (selectedOption == JOptionPane.OK_OPTION) {

			String filePath = Settings.getSqlsFolder() + "\\batch_input.sql";
			String content = Utils.getContentFromFile(filePath);
			if (content != null) {

				String cardStr = "";
				int i = 1;
				for (String card : cards) {
					cardStr += "v_card_numbers(" + String.valueOf(i) + ") := '" + card.trim() + "'; -- "
							+ cardOrganizationStr + ", " + cardBrandStr + "\n";
					i += 1;
				}

				params.put("v_card_numbers", cardStr.trim());

				// content = content.replace("card_numbers(1) := '';", cardStr);
				content = Utils.replaceParams(content, params);
				Utils.copyToClipboard(content);
			} else {
				LogManager.getLogger().debug("File not found: " + filePath);
			}

			filePath = savedFolder + "\\ACQ_BATCH_INPUT_" + Utils.formatDate("hhmmss") + ".sql";
			Utils.saveFile(filePath, content);
			// }
		}

		// browse saved folder
		if (!savedFolder.isEmpty()) {
			openFileBrowser(savedFolder);
			Utils.openExternalFile(new File(savedFolder));
		}
	}

	public void run(boolean overrideOldTree) {
		try {
			initData();
			conn = getConnection();
			generateTransactions();

		} catch (Exception e) {
			LogManager.getLogger().error(e);
		}
	}

	@Override
	public void showLog(boolean showLog) {
		refreshFileBrowser();
		isHideLog = true;
		btnHideLog.setText("Refresh");
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

	public String getSavedFolder() {
		return getSavedFolder(0);
	}

	public String getSavedFolder(int waitDuration) {
		int sleepTime = 1000;
		File lastFolder = null;
		String savedFolder = "";

		// catch new folder appeared
		if (waitDuration > 0) {
			lastFolder = Utils.getLastModifiedFolder(new File(Settings.getTransGenFolder() + "\\outward"));
			String oldLastFolder = lastFolder != null ? lastFolder.getAbsolutePath() : "";
			try {
				while (sleepTime < waitDuration) {
					Thread.sleep(sleepTime);
					lastFolder = Utils.getLastModifiedFolder(new File(Settings.getTransGenFolder() + "\\outward"));
					if (lastFolder != null && !lastFolder.getAbsolutePath().equals(oldLastFolder)) {
						sleepTime = waitDuration;
						savedFolder = lastFolder.getAbsolutePath();
						break;
					}
					sleepTime += sleepTime;
				}
			} catch (InterruptedException e) {
				LogManager.getLogger().error(e);
			}
		}

		if (savedFolder.isEmpty())
			savedFolder = Utils.createFolder(Settings.getTransGenFolder() + "\\outward\\" + "RUN_"
					+ Utils.formatDate("ddMMyy") + "_" + Utils.formatDate("HHmmss") + "_"
					+ Utils.formatDate("E"));

		return savedFolder;
	}

	public void refreshtextCardBrands() {
		return;
		// if (textCardBrands != null && comboCardOrganizations.getSelectedItem() !=
		// null) {
		// String cardOranization = Utils.getComponentValue(comboCardOrganizations,
		// true);
		// Utils.setAutoComplete(
		// textCardBrands, RS2Util.getCardBrands(cardOranization, getConnection()));
		// }
	}

	public void refreshConnectionCombo() {
		if (comboCardOrganizations == null)
			return;
		try {
			boolean connectionChanged = isConnectionChanged();
			if (connectionChanged) {
				initConnection();
				refreshtextCardBrands();

				Utils.setAutoComplete(textInstitutionNumber,
						RS2Util.getInstitutionsList(getConnection()));
				comboCardOrganizations.addItemListener(new ItemListener() {
					public void itemStateChanged(ItemEvent arg0) {
						refreshtextCardBrands();
					}
				});

				Utils.setAutoComplete(textCountry, RS2Util.getCountryList(getConnection()));
				Utils.setAutoComplete(textCurrency, RS2Util.getCurrencyList(getConnection()));
				Utils.setAutoComplete(comboCM, RS2Util.getCaptureMethod(conn));
				Utils.setAutoComplete(comboTC, RS2Util.getTerminalCompability(conn));
				Utils.setAutoComplete(comboServiceTypes, RS2Util.getCardServicesList(conn));
			}

		} catch (Exception ex) {
			LogManager.getLogger().error(ex);
		}
	}

	public void testAutomation() {
		System.setProperty("webdriver.chrome.driver", ".\\Driver\\chromedriver.exe");
		System.setProperty("webdriver.edge.driver", ".\\Driver\\msedgedriver.exe");
		System.setProperty("webdriver.chrome.whitelistedIps", "142.250.186.36");

		WebDriverManager.edgedriver().setup();
		WebDriver driver = new EdgeDriver(); // new ChromeDriver();
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
		driver.manage().window().maximize();
		driver.get("https://127.0.0.1");
		driver.close();
	}

	private void generateTransactions2() {
		String t = "";
		for (String i : pre_processor_mc_fields) {
			t = t + "map.put(\"" + i + "\", \"\");\n";
		}
		t = t + "-----\n";

		for (String i : pre_processor_visa_fields) {
			t = t + "map.put(\"" + i + "\", \"\");\n";
		}
		setSQL(t);
	}
}