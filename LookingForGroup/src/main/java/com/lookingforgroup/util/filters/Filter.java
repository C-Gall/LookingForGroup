package com.lookingforgroup.util.filters;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public abstract class Filter {
	private final String regex = "[a-zA-Z0-9]+[=<>][=]?[a-zA-Z0-9\s+&.,!?%_\"]+";
	private final Map<String, String> numericalCompares = new HashMap<String, String>();
	{
		numericalCompares.put("<", "<");
		numericalCompares.put("<=", "<=");
		numericalCompares.put(">=", ">=");
		numericalCompares.put(">", ">");
		numericalCompares.put("==", "=");
	}
	private final Map<String, String> keyPairs = new HashMap<String, String>();
	{
		generateKeys(keyPairs);
	}
	private final List<String> booleanKeys = new LinkedList<String>();
	{
		generateBooleanKeys(booleanKeys);
	}
	private final List<String> integerKeys = new LinkedList<String>(); {
		generateIntegerKeys(integerKeys);
	}

	public FilterResult generateFilterResult(String input) {
		// Create instance of empty FilterResult
		FilterResult result = new FilterResult();

		// Variables to be used throughout process
		List<Object> values = new LinkedList<Object>();
		String SQL = sqlBase();
		String prevKey = "";
		String paramToBeAdded = "";
		
		System.out.println("Acceptable Keys-to-SQLKeys: " + keyPairs.toString());
		System.out.println("Integer Keys: " + integerKeys.toString());
		System.out.println("Boolean Keys: " + booleanKeys.toString());

		String[] tokens = input.substring(1, input.length() - 1).split("\\)\s\\(");
		System.out.println(Arrays.toString(tokens));

		for (String token : tokens) {
			if (token.matches(regex)) {
				String param = token; // token.substring(1, token.length() - 1);
				System.out.println("Initial Param: " + param);

				boolean isGreatLessEqualTo = false;

				String key = "";
				String compare = "";
				String value = "";

				for (int i = 0; i < param.length(); i++) {
					if (!Character.isDigit(param.charAt(i)) && !Character.isLetter(param.charAt(i))) {
						if (!Character.isDigit(param.charAt(i + 1)) && !Character.isLetter(param.charAt(i + 1))) {
							isGreatLessEqualTo = true;
						}

						key = param.substring(0, i);
						if (isGreatLessEqualTo) {
							compare = param.substring(i, i + 2);
							value = param.substring(i + 2);
						} else {
							compare = param.substring(i, i + 1);
							value = param.substring(i + 1);
						}

						i = param.length();
					}
				}

				System.out.println("Key: " + key + " | Compare: " + compare + " | value: " + value);

				if (keyPairs.containsKey(key)) { //Check to see if the key to be searched for is actually a key
					key = keyPairs.get(key); //Assign the simplified input to match the SQL query
					
					//If the key holds integer values...
					if(integerKeys.contains(key)) {
						//System.out.println(key + " will require integer values!");
						
						if (prevKey.equals(key)) {
							paramToBeAdded += "OR " + key + " ";
						} else {
							paramToBeAdded += "AND " + key + " ";
						}
						
						if (isInteger(value) && numericalCompares.containsKey(compare)) {
							// paramToBeAdded += compare + " " + value + " ";
							paramToBeAdded += numericalCompares.get(compare) + " ? ";
							
							values.add(Integer.parseInt(value));
							prevKey = key;
						}
						//Invalid value, reset paramToBeAdded!
						else {
							paramToBeAdded = "";
						}
					}
					//If the key holds boolean values...
					else if(booleanKeys.contains(key)) {
						//System.out.println(key + " will require boolean values!");
						
						if (prevKey.equals(key)) {
							paramToBeAdded += "OR " + key + " ";
						} else {
							paramToBeAdded += "AND " + key + " ";
						}
						
						char val = value.toLowerCase().charAt(0);
						if(val == 't' || val == 'f') {
							paramToBeAdded += "= ? ";
							
							if(val == 't') {
								values.add(true);
							}
							else {
								values.add(false);
							}

							prevKey = key;
						}
						//Invalid value, reset paramToBeAdded!
						else {
							paramToBeAdded = "";
						}
					}
					else {
						System.out.println(key + " will require string values!");
						
						if (prevKey.equals(key)) {
							paramToBeAdded += "OR LOWER(" + key + ") ";
						} else {
							paramToBeAdded += "AND LOWER(" + key + ") ";
						}
						
						if (value.contains("_") || value.contains("%")) {
							paramToBeAdded += "LIKE LOWER(?) ";
						} else {
							paramToBeAdded += "= " + " LOWER(?) ";
						}
						values.add(value);
					}						
				}
			}
			SQL += paramToBeAdded;
			paramToBeAdded = "";
		}
		SQL += ")";
		result.setSQL(SQL);
		result.setValues(values);
		
		System.out.println(SQL);
		System.out.println(values.toString());

		return result;
	}

	public abstract String sqlBase();
	
	protected abstract void generateKeys(Map<String, String> keyList);
	
	protected abstract void generateBooleanKeys(List<String> booleanKeyList);
	
	protected abstract void generateIntegerKeys(List<String> integerKeyList);

	// Credit where credit is due, as this is the most efficient way to check for an Integer:
	// Jonas K @
	// https://stackoverflow.com/questions/237159/whats-the-best-way-to-check-if-a-string-represents-an-integer-in-java
	private static boolean isInteger(String str) {
		if (str == null) {
			return false;
		}
		int length = str.length();
		if (length == 0) {
			return false;
		}
		int i = 0;
		if (str.charAt(0) == '-') {
			if (length == 1) {
				return false;
			}
			i = 1;
		}
		for (; i < length; i++) {
			char c = str.charAt(i);
			if (c < '0' || c > '9') {
				return false;
			}
		}
		return true;
	}
}
