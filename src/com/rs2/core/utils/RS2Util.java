package com.rs2.core.utils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.rs2.core.components.MyDialog;
import com.rs2.core.logs.LogManager;
import com.rs2.modules.cardsgen.CardsGenService;
import com.rs2.modules.db.DBService;

public class RS2Util {
	public static DBService DBService = new DBService();

	public static String CARD_BRAND_MAST = "002";
	public static String CARD_BRAND_VISA = "003";
	public static String CARD_BRAND_DINNER = "006";
	public static String CARD_BRAND_DISCOVER = "023";
	public static String CARD_BRAND_JCB = "000";
	public static String CARD_BRAND_AMEX = "004";
	public static String[] CARD_BRANDS = new String[] { "002 : MAST", "003 : VISA", "006 : DINER", "004 : AMEX",
			"023 : DISCOVER", "000 : JCB" };
	public static String[] TRAN_TYPES = new String[] { "CASH", "PURCHASE", "SALE", "UNIQUE",
			"CFT", "CRYPTO", "REFUND" };
	public static String[] ENABLED = new String[] { "Y", "N" };

	public static boolean isValidInstitutionNumber(String inst) {
		return inst != null && inst.length() == 8;
	}

	public static List<String> getCardBrands(String cardOranization, Connection conn) {
		String table = getTableNameByOrganization(cardOranization);
		try {
			return DBService.executeSqlAsLookupCollections(
					"select index_field,card_brand from cht_card_brand where institution_number = 0 and card_organization = '"
							+ cardOranization
							+ "' and index_field in (select distinct card_brand from " + table
							+ " where card_organization = '" + cardOranization + "') order by 1",
					conn,
					-1);
		} catch (SQLException e) {
			LogManager.getLogger().error(e);
			return new ArrayList<String>();
		}
	}

	public static List<String> getCardServicesList(Connection conn) {
		return getLookupDataFromTable("cht_service_type", conn);
	}

	public static List<String> getInstitutionsList(Connection conn) {
		try {
			return DBService.executeSqlAsLookupCollections(
					"select institution_number, institution_name from sys_institution_licence order by 1",
					conn, -1);
		} catch (SQLException e) {
			LogManager.getLogger().error(e);
			return new ArrayList<String>();
		}
	}

	public static List<String> getClientNumbersList(String institutionNumber, String country, Connection conn) {
		try {
			String sql = "select client_number, trade_name, client_city from cis_client_details where client_status = '001' ";
			if (institutionNumber != null && !institutionNumber.isEmpty())
				sql += " and institution_number in (" + cleanSqlValue(institutionNumber, 8, "'") + ") ";
			if (country != null && !country.isEmpty())
				sql += " and client_country in (" + cleanSqlValue(country, 3, "'") + ") ";
			sql += " order by client_number";
			return DBService.executeSqlAsLookupCollections(
					sql,
					conn, -1);
		} catch (SQLException e) {
			LogManager.getLogger().error(e);
			return new ArrayList<String>();
		}
	}

	public static String cleanSqlValue(String value, int length) {
		return cleanSqlValue(value, length, "");
	}

	public static String cleanSqlValue(String value, String quotation) {
		return cleanSqlValue(value, 0, quotation);
	}

	public static String cleanSqlValue(String value, int length, String quotation) {
		if (value == null)
			value = "";
		if (length > 0 && value.length() != length && !value.contains(","))
			value = Utils.getFixLengthValue(String.valueOf(length), value);

		if (value != null && ((length == 0 || value.length() == length) && !value.contains(",")))
			value = quotation + value + quotation;
		return value;
	}

	public static List<String> getTerminalIdsList(String institutionNumber, String clientNumber, Connection conn) {
		try {
			String sql = "select terminal_id, serial_number, merchant_id, terminal_currency from cis_device_link where terminal_status = '001' ";
			if (institutionNumber != null && !institutionNumber.isEmpty()) {
				sql += " and institution_number in (" + cleanSqlValue(institutionNumber, 8, "'") + ") ";
			}
			if (clientNumber != null && !clientNumber.isEmpty()) {
				sql += " and client_number in (" + cleanSqlValue(clientNumber, 8, "'") + ") ";
			}
			sql += " order by client_number";
			return DBService.executeSqlAsLookupCollections(
					sql,
					conn, -1);
		} catch (SQLException e) {
			LogManager.getLogger().error(e);
			return new ArrayList<String>();
		}
	}

	public static List<Map<String, String>> getTerminalIdsRecord(String institutionNumber, String clientNumber,
			Connection conn, int maxRowNum) {
		try {
			String sql = "select terminal_id, serial_number, merchant_id, terminal_currency from cis_device_link where terminal_status = '001' ";
			if (institutionNumber != null && !institutionNumber.isEmpty()) {
				sql += " and institution_number in (" + cleanSqlValue(institutionNumber, 8, "'") + ") ";
			}
			if (clientNumber != null && !clientNumber.isEmpty())
				sql += " and client_number in (" + cleanSqlValue(clientNumber, 8, "'") + ") ";
			sql += " order by client_number";
			return DBService.executeSqlAsListMapRows(sql, conn, maxRowNum);
		} catch (SQLException e) {
			LogManager.getLogger().error(e);
			return new LinkedList<Map<String, String>>();
		}
	}

	// public static List<String> getInstitutionsList(String connectionName, String
	// field) {
	// List<String> lookupWords = MyService.getCachedDataAsList(connectionName,
	// DBService.getDBFieldFromParamName(field),
	// null);
	// return lookupWords;
	// }

	public static List<String> getCountryList(Connection conn) {
		return getLookupDataFromTable("cht_country", conn);
	}

	public static List<String> getTerminalCompability(Connection conn) {
		return getLookupDataFromTable("CHT_TERMINAL_CAPABILITY", conn);
	}

	public static List<String> getCaptureMethod(Connection conn) {
		return getLookupDataFromTable("CHT_CAPTURE_METHOD", conn);
	}

	public static List<String> getCurrencyList(Connection conn) {
		return getLookupDataFromSQL(
				"select iso_code, swift_code from CHT_CURRENCY where institution_number = 0 and language = 'USA' order by 1",
				conn);
	}

	public static List<String> getCardOrganizationList(Connection conn) {
		return getLookupDataFromTable("cht_card_organization", conn);
	}

	public static List<String> getLookupDataFromTable(String table, Connection conn) {
		return getLookupDataFromSQL(
				"select * from " + table + " where institution_number = 0 and language = 'USA' order by 1", conn);
	}

	public static List<String> getLookupDataFromSQL(String sql, Connection conn) {
		try {
			return DBService.executeSqlAsLookupCollections(
					sql,
					conn, -1);
		} catch (SQLException e) {
			LogManager.getLogger().error(e);
			return new ArrayList<String>();
		}
	}

	public static String getTableNameByOrganization(String cardOrganization) {
		String table = "";
		if (cardOrganization.equals(CARD_BRAND_MAST)) {
			table = "SYS_MAST_INTER_BIN_TABLE";
		} else if (cardOrganization.equals(CARD_BRAND_VISA)) {
			table = "SYS_VISA_INTER_BIN_TABLE";
		} else if (cardOrganization.equals(CARD_BRAND_MAST)) {
			table = "SYS_MAST_INTER_BIN_TABLE";
		} else if (cardOrganization.equals(CARD_BRAND_AMEX)) {
			table = "SYS_AMEX_BIN_TABLE";
		} else if (cardOrganization.equals(CARD_BRAND_JCB)) {
			table = "SYS_JCB_INTER_BIN_TABLE";
		} else if (cardOrganization.equals(CARD_BRAND_DINNER)) {
			table = "SYS_DINERS_BIN_TABLE";
		} else if (cardOrganization.equals(CARD_BRAND_DISCOVER)) {
			table = "SYS_DISCOVER_INTER_BIN_TABLE";
		}
		return table;
	}

	public static String GETNEXTSEQNUMBER(Connection conn, String sequenceId, String institution_number,
			boolean update) {
		String text = null;
		String sql = "SELECT * FROM CBR_SEQUENCE_NUMBERS where institution_number = '" + institution_number
				+ "' and sequence_id = '" + sequenceId + "'";
		try {
			ResultSet rs = DBService.executeQuery(sql, conn);
			if (rs != null) {
				rs.next();

				text = rs.getString("sequence_value");
				int value = rs.getInt("sequence_value");
				int len = rs.getInt("sequence_length");
				int increment = rs.getInt("sequence_increment");
				value = value + increment;
				text = Utils.getFixLengthValue(String.valueOf(len), String.valueOf(value));
				String updateSQL = "UPDATE CBR_SEQUENCE_NUMBERS SET sequence_value = '" + text
						+ "', last_amendment_date = '" + Utils.formatDate() + "' WHERE institution_number = '"
						+ institution_number
						+ "' and sequence_id = '" + sequenceId + "'";
				if (update) {
					int result = DBService.executeSql(updateSQL, null, conn);
					System.out.println(result);
				}
				// System.out.println(updateSQL);
				rs.close();
			}
		} catch (SQLException ex) {
			LogManager.getLogger().error(ex);
			text = null;
		}

		return text;
	}

	public static String[] generateCards(Connection conn, String cardOrganization, String cardBrand, int howMany,
			String startBin, String country, String service) {
		return generateCards(conn, cardOrganization, cardBrand, howMany, startBin, country, service, null, null, null,
				null);
	}

	public static String[] generateCards(Connection conn, String cardOrganization, String product_id,
			String sub_product, String pay_pass, String country, String funding_source, String b2b_id,
			String technology, String institutionNumber, int howMany) {
		List<String> result = new LinkedList<String>();
		for (int i = 0; i < howMany; i++) {
			Object card = DBService.executeStoredProcedure("GENERATECARD",
					new String[] {
							cardOrganization,
							product_id,
							sub_product,
							pay_pass,
							country,
							funding_source,
							b2b_id,
							technology,
							institutionNumber },
					new int[] { 10 }, conn).get(0);
			if (card != null)
				result.add(String.valueOf(card));
		}
		return Utils.convertToStringArray(result);
	}

	public static String[] generateCards(Connection conn, String cardOrganization, String cardBrand, int howMany,
			String startBin, String country, String service, String product_id, String sub_product, String pay_pass,
			String funding_source) {
		if (cardOrganization.contains("-"))
			cardOrganization = cardOrganization.substring(0, cardOrganization.indexOf("-")).trim();
		if (cardOrganization.contains(":"))
			cardOrganization = cardOrganization.substring(0, cardOrganization.indexOf(":")).trim();

		String[] cards = new String[] {};
		List<String> cardsList = new LinkedList<String>();
		String sql = "";
		String table = "";
		String columnBinLength = "card_length";
		String columnStartBin = "start_bin_value";
		String columnEndBin = "end_bin_value";
		table = getTableNameByOrganization(cardOrganization);
		if (table.isEmpty())
			return cards;

		if (conn != null) {
			sql = "select * from " + table + " where card_organization in (" +
					cardOrganization + ")";
			if (cardBrand != null && !cardBrand.isEmpty())
				sql += " and card_brand in (" + cleanSqlValue(cardBrand, 3, "'") + ")";
			if (startBin != null && !startBin.isEmpty())
				sql += " and start_bin_value like '" + startBin + "%'";
			if (service != null && !service.isEmpty())
				sql += " and service_type in (" + cleanSqlValue(service, 3, "'") + ")";

			if (country != null && !country.isEmpty() && (cardOrganization.equals(CARD_BRAND_VISA) || cardOrganization
					.equals(CARD_BRAND_MAST) || cardOrganization.equals(CARD_BRAND_DISCOVER)))
				sql += " and bin_country in (" + cleanSqlValue(country, 3, "'") + ")";

			if (cardOrganization.equals(CARD_BRAND_MAST)) {
				if (product_id != null && !product_id.isEmpty())
					sql += " and card_product_code in (" + cleanSqlValue(product_id, 0, "'") + ")";
				if (sub_product != null && !sub_product.isEmpty())
					sql += " and acceptance_brand in (" + cleanSqlValue(sub_product, 0, "'") + ")";
				if (pay_pass != null && !pay_pass.isEmpty())
					sql += " and paypass_enabled in (" + cleanSqlValue(pay_pass, 0, "'") + ")";
				sql += " and product_priority_code = '01' ";
			} else if (cardOrganization.equals(CARD_BRAND_VISA)) {
				if (product_id != null && !product_id.isEmpty())
					sql += " and product_id in (" + cleanSqlValue(product_id, 0, "'") + ")";

				if (sub_product != null && !sub_product.isEmpty())
					sql += " and product_subtype in (" + cleanSqlValue(sub_product, 0, "'") + ")";
			}
			sql += " and ROWNUM < 3 order by 1";
			ResultSet rs = DBService.executeQuery(sql, conn);
			List<String> startBins = new LinkedList<String>();

			try {
				if (rs != null) {
					while (rs.next()) {
						String start_bin = rs.getString(columnStartBin);
						String end_bin = rs.getString(columnEndBin);

						int len = 0;

						try {
							len = rs.getInt(columnBinLength);
						} catch (SQLException ex) {
							LogManager.getLogger().error(ex);
						}

						if (len == 0) {
							len = start_bin.length();
						}

						if (start_bin.indexOf("000") > 0) {
							if (end_bin.indexOf("999") > 0)
								start_bin = start_bin.substring(0, end_bin.indexOf("999"));
							else
								start_bin = start_bin.substring(0, start_bin.indexOf("000"));
						}

						startBins.add(start_bin);
						cards = CardsGenService.generateCardNumbers(Utils.convertToStringArray(startBins), len,
								howMany);
						break;
					}
					// System.out.println(updateSQL);
					rs.close();
				}
			} catch (SQLException ex) {
				LogManager.getLogger().error(ex);
			}
		} else {
			if (cardOrganization.equals(CARD_BRAND_MAST)) {
				cards = CardsGenService.generateMasterCardNumbers(howMany);
			} else if (cardOrganization.equals(CARD_BRAND_VISA)) {
				cards = CardsGenService.generateVisaCardNumbers(howMany);
			} else if (cardOrganization.equals(CARD_BRAND_AMEX)) {
				cards = CardsGenService.generateAmexCardNumbers(howMany);
			} else if (cardOrganization.equals(CARD_BRAND_JCB)) {
				cards = CardsGenService.generateJCBCardNumbers(howMany);
			}
		}

		// String[] _cards = CardsGenerator.generate(10);
		return cards;
	}

	public static String getTransactionSlipPrefix(String date) {
		Date d = Utils.convertToDate(date, "yyyyMMdd");
		return Utils.formatDate("Y", d).substring(3) + Utils.formatDate("ww", d);
	}

	public static String getTransactionSlipPrefix(Date d) {
		return Utils.formatDate("Y", d).substring(3) + Utils.formatDate("ww", d);
	}

	public static String getTransactionSlipPrefix() {
		return Utils.formatDate("Y").substring(3) + Utils.formatDate("ww");
	}

	public static String getAuditTrail(Connection conn, String text1, String text2) {
		String sql = String.format("select BW_CODE_LIBRARY.CreateAuditTrail('%s', '%s') FROM DUAL", text1,
				text2);
		ResultSet rs = DBService.executeQuery(sql, conn);
		String value = "";
		try {
			if (rs != null) {
				rs.next();
				value = rs.getString(1);
				rs.close();
			}
		} catch (SQLException ex) {
			LogManager.getLogger().error(ex);
			value = text1 + "-" + text2;
		}
		return value;
	}

	public static String getPostingDate(Connection conn, String institutionNumber, String station) {
		String sql = "SELECT POSTING_DATE FROM SYS_POSTING_DATE WHERE INSTITUTION_NUMBER = '" + institutionNumber
				+ "' AND STATION_NUMBER = '" + station + "'";
		ResultSet rs = DBService.executeQuery(sql, conn);
		String value = "";
		try {
			if (rs != null) {
				rs.next();
				value = rs.getString("POSTING_DATE");
				// System.out.println(updateSQL);
				rs.close();
			}
		} catch (SQLException ex) {
			LogManager.getLogger().error(ex);
			value = null;
		}
		return value;
	}

	public static String getLocalCurrency(Connection conn, String instNr) {
		String srcLocalCurr = "";
		String locCurr = "";
		try {
			Statement getLocalCurr = conn.createStatement();

			locCurr = "select distinct first_value (CONFIG_VALUE) over (order by INSTITUTION_NUMBER desc) as CONFIG_VALUE from sys_configuration "
					+
					"where CONFIG_SECTION ='Globals' and CONFIG_KEYWORD ='LocalCurrencyISO'  and INSTITUTION_NUMBER in ('00000000','"
					+ instNr + "')";
			ResultSet rsGetLocalCurr = getLocalCurr.executeQuery(locCurr);

			while (rsGetLocalCurr.next())
				srcLocalCurr = rsGetLocalCurr.getString("CONFIG_VALUE");

			rsGetLocalCurr.close();
			getLocalCurr.close();
		} catch (SQLException sq) {
			MyDialog.showException("SQL error.  " + sq + "\n"
					+ "Local currency of source institution could not be determined using this query: " +
					locCurr);
		}
		return srcLocalCurr;
	}

	public static String getSystemDate(Connection conn) {
		String recordDate = "";
		try {
			Statement rsGetRecDate = conn.createStatement();
			ResultSet rsRec = rsGetRecDate
					.executeQuery("Select to_char(sysdate, 'yyyymmdd') as record_date from dual");
			while (rsRec.next())
				recordDate = rsRec.getString("RECORD_DATE");
			rsGetRecDate.close();
			rsRec.close();
		} catch (SQLException sq) {
			MyDialog.showException("SQL error.  " + sq + "\n"
					+ "Select to_char(sysdate, 'yyyymmdd') as record_date from dual\n");
		}
		return recordDate;
	}
}
