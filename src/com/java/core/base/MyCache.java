package com.java.core.base;

import java.util.Map.Entry;
import java.util.*;

import com.java.modules.db.DBService;
import com.java.Main;
import com.java.core.data.DBLookup;
import com.java.core.data.DBParam;
import com.java.core.data.DBQuery;
import com.java.core.logs.LogManager;
import com.java.core.settings.Settings;

public class MyCache {
	public static Map<String, Map<String, String>> CachedData = new TreeMap<String, Map<String, String>>();
	public static Map<String, Map<String, String>> LookupMapping = new TreeMap<String, Map<String, String>>();

	public static void setupCachedData(List<DBQuery> plans, List<DBLookup> lookupColumns, String connectionName,
			List<DBParam> queryParams) {

		setupCachedData(lookupColumns, connectionName, queryParams);

		if (plans != null) {
			for (DBQuery plan : plans) {
				if (plan.getType().equalsIgnoreCase(Settings.QueryTypeLookup) && !plan.getTableName().isEmpty()) {
					String cacheKey = getCachedKey(connectionName, plan.getTableName());
					addCachedData(connectionName, cacheKey, plan.getSqlQuery(), "", "", queryParams);
				}
			}
		}
	}

	public static void setupCachedData(List<DBLookup> lookupColumns, String connectionName, List<DBParam> queryParams) {
		setupCachedData(lookupColumns, connectionName, queryParams, null);
	}

	public static void setupCachedData(List<DBLookup> lookupColumns, String connectionName, List<DBParam> queryParams,
			List<String> columns) {
		Main.startThread("Setup Cache Data");
		if (lookupColumns != null) {
			for (DBLookup lookupDef : lookupColumns) {
				if (columns == null || columns.contains(lookupDef.getFieldLookup().toLowerCase()))
					addCachedData(lookupDef, connectionName, queryParams);
			}
		}
		Main.stopThread();
	}

	public static void clearCachedData() {
		clearCachedData(null);
	}

	public static void clearCachedData(String connectionName) {
		if (connectionName == null || connectionName.isEmpty())
			CachedData = new TreeMap<String, Map<String, String>>();

		if (CachedData != null) {
			Iterator<Entry<String, Map<String, String>>> entryIt = CachedData.entrySet().iterator();
			// Iterate over all the elements
			while (entryIt.hasNext()) {
				Entry<String, Map<String, String>> entry = entryIt.next();
				// Check if Value associated with Key is 10
				if (entry.getKey().toLowerCase().startsWith(connectionName.toLowerCase())) {
					// Remove the element
					entryIt.remove();
				}
			}
		}
	}

	public static Map<String, String> addCachedData(DBLookup lookupDef, String connectionName,
			List<DBParam> queryParams) {
		if (connectionName == null)
			connectionName = "";

		String fieldLookup = lookupDef.getFieldLookup();
		String fieldKey = lookupDef.getFieldKey();
		String fieldValue = lookupDef.getFieldValue();
		String tableName = lookupDef.getTableName();
		String lookupTable = lookupDef.getLookupTable();
		String sql = lookupDef.getSql();

		if (tableName == null)
			tableName = "";
		if (lookupTable == null)
			lookupTable = "";

		String cacheKey = getCachedKey(connectionName, lookupTable);
		String mappingKey = "";

		Map<String, String> lookupData = null;
		if (fieldLookup != null && !fieldLookup.isEmpty()) {
			String[] fields = fieldLookup.split(",");
			for (String field : fields) {
				field = field.trim();
				mappingKey = getCachedKey(connectionName, field);
				if (!LookupMapping.containsKey(mappingKey)) {
					Map<String, String> mappingValues = new HashMap<String, String>();
					mappingValues.put("cacheKey", cacheKey);
					mappingValues.put("sql", sql);
					mappingValues.put("table", lookupTable);
					LookupMapping.put(mappingKey, mappingValues);
				}
			}
		}

		if (DBService.isQueryHasParams(sql, queryParams) == -1 // sql does not have any param, add 'global' cached first
				|| (DBService.isQueryHasParams(sql, queryParams) == 1
						&& (mappingKey.isEmpty() || LookupMapping.containsKey(mappingKey)))) // or sql has mapping key
																								// then ignore the first
																								// time
		{
			try {
				lookupData = addCachedData(connectionName, cacheKey, sql, fieldKey, fieldValue, queryParams);
			} catch (Exception e) {
				LogManager.getLogger().error(e);
			}
		}

		lookupData = CachedData.get(cacheKey);
		return lookupData;
	}

	public static String getCachedKey(String tableName) {
		if (tableName == null)
			return "";
		if (tableName.startsWith(Settings.paramPrefix))
			tableName = tableName.replace(Settings.paramPrefix, "");
		if (tableName.startsWith(Settings.paramPrefix1))
			tableName = tableName.replace(Settings.paramPrefix1, "");
		if (tableName.contains(Settings.paramParamValueSeparator))
			tableName = tableName.substring(0, tableName.indexOf(Settings.paramParamValueSeparator));
		if (tableName.contains(Settings.paramRequiredBy))
			tableName = tableName.substring(0, tableName.indexOf(Settings.paramRequiredBy));

		return tableName;
	}

	public static String getCachedKey(String connectionName, String tableName) {
		if (tableName == null)
			tableName = "";
		if (connectionName == null)
			connectionName = "";

		// normalize cache key
		tableName = getCachedKey(tableName);

		return (connectionName.toLowerCase() + " " + Settings.lookupKeyValueSeperator + " " + tableName.toLowerCase())
				.trim();
	}

	public static String getCachedKey(String connectionName, String tableName, List<DBParam> queryParams) {
		if (tableName == null)
			tableName = "";
		if (connectionName == null)
			connectionName = "";
		return (connectionName.toLowerCase() + " " + Settings.lookupKeyValueSeperator + " " + tableName.toLowerCase())
				.trim();
	}

	public static Map<String, String> addCachedData(String connectionName, String cacheKey,
			Map<String, String> lookupData) {

		if (lookupData == null || lookupData.size() == 0)
			return null;
		if (!CachedData.containsKey(cacheKey)
		// && lookupData.size() > 0
		) {
			CachedData.put(cacheKey, lookupData);
		}
		return lookupData;
	}

	public static Map<String, String> addCachedData(String connectionName, String cacheKey, String sql, String fieldKey,
			String fieldValue) {
		return addCachedData(connectionName, cacheKey, sql, fieldKey, fieldValue, null);
	}

	public static Map<String, String> addCachedData(String connectionName, String cacheKey, String sql, String fieldKey,
			String fieldValue, List<DBParam> queryParams) {
		Map<String, String> lookupData = null;
		if (CachedData == null)
			CachedData = new TreeMap<String, Map<String, String>>();

		if (!CachedData.containsKey(cacheKey)) {
			if (sql == null || sql.isEmpty())
				return null;
			if (queryParams != null) {
				for (DBParam param : queryParams) {
					if (param.getKey() == null || param.getKey().isEmpty())
						continue;
					String keyOriginal = param.getKey();
					String valueOriginal = param.getValue();
					// if sql contains a null param value then no need to implement, return null
					// immediately
					if ((valueOriginal == null || valueOriginal.isEmpty()) && keyOriginal != null && sql != null
							&& sql.toLowerCase().contains(keyOriginal.toLowerCase()))
						return null;
				}
			}
			lookupData = MyService.getLookupData(connectionName, sql, fieldKey, fieldValue, queryParams);
			addCachedData(connectionName, cacheKey, lookupData);
		} else {
			lookupData = CachedData.get(cacheKey);
		}
		return lookupData;
	}

	public static Map<String, String> getCachedData(String connectionName, String fieldLookup) {
		return getCachedData(connectionName, fieldLookup, null);
	}

	public static Map<String, String> getCachedData(String connectionName, String fieldLookup,
			List<DBParam> queryParams) {
		if (connectionName == null)
			connectionName = "";

		String mappingKey = getCachedKey(connectionName, fieldLookup);
		Map<String, String> mappingValues = LookupMapping.containsKey(mappingKey) ? LookupMapping.get(mappingKey)
				: null;
		String cacheKey = mappingValues != null && mappingValues.containsKey("cacheKey") ? mappingValues.get("cacheKey")
				: mappingKey;
		String sql = mappingValues != null && mappingValues.containsKey("sql") ? mappingValues.get("sql") : "";
		String fieldKey = mappingValues != null && mappingValues.containsKey("fieldKey") ? mappingValues.get("fieldKey")
				: "";
		String fieldValue = mappingValues != null && mappingValues.containsKey("fieldValue")
				? mappingValues.get("fieldValue")
				: "";

		Map<String, String> lookupData = new HashMap<String, String>();

		if (CachedData != null && cacheKey != null && !cacheKey.isEmpty()) {
			if (CachedData.containsKey(cacheKey)) {
				return CachedData.get(cacheKey);
			}
			// if

			if (!sql.isEmpty()) {
				lookupData = MyService.getLookupData(connectionName, sql, fieldKey, fieldValue,
						queryParams);
				addCachedData(connectionName, cacheKey, lookupData);
			}
		}
		return lookupData;
	}
}
