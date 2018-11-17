package util;

import constant.AppConstants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class StringUtils {
	public static String toRawString(String content) {

		content = content.toLowerCase();
		HashMap<Character, Character> hashMap = new HashMap<>();

		char sa[] = {'a', 'á', 'à', 'ả', 'ạ', 'ă', 'ắ', 'ằ', 'ẳ', 'ẵ', 'ặ', 'â', 'ấ', 'ẩ', 'ẫ', 'ậ'};
		char su[] = {'u', 'ú', 'ủ', 'ũ', 'ụ', 'ư', 'ứ', 'ử', 'ữ', 'ự'};
		char so[] = {'o', 'ó', 'ỏ', 'õ', 'ọ', 'ơ', 'ớ', 'ở', 'ỡ', 'ợ', 'ô', 'ố', 'ổ', 'ỗ', 'ộ'};
		char si[] = {'i', 'í', 'ỉ', 'ĩ', 'ị'};
		char sy[] = {'y', 'ý', 'ỷ', 'ỹ', 'ỵ'};
		char se[] = {'e', 'é', 'ẻ', 'ẽ', 'ẹ', 'ê', 'ế', 'ể', 'ễ', 'ệ'};
		char sd[] = {'đ'};

		for (char x : sa) hashMap.put(x, 'a');
		for (char x : su) hashMap.put(x, 'u');
		for (char x : so) hashMap.put(x, 'o');
		for (char x : si) hashMap.put(x, 'i');
		for (char x : sy) hashMap.put(x, 'y');
		for (char x : se) hashMap.put(x, 'e');
		for (char x : sd) hashMap.put(x, 'd');


		String result = "";
		for (int i = 0; i < content.length(); i++) {
			if (hashMap.containsKey(content.charAt(i))) {
				result += hashMap.get(content.charAt(i));
			} else {
				result += content.charAt(i);
			}
		}

		return result;

	}

	public static int hashingString(String content) {
		int mod = 1000000007; // some big number
		int base = 3075; //random prime number

		int hashValue = 0;
		for (int i = 0; i < content.length(); i++) {
			hashValue = (int) (((long) hashValue * base + content.charAt(i)) % mod);
		}

		return hashValue;
	}


	public static double getNumberValueFromString(String input) {
		double result = 0;
		for (int i = 0; i < input.length(); i++) {
			if (input.charAt(i) >= '0' && input.charAt(i) <= '9') {
				int charValue = input.charAt(i) - '0';
				result = result * 10 + charValue;
			}
		}

		return result;
	}

	public static String beautifyUrl(String url, String domainURL) {
		String result = url;
		if (url.startsWith("/") && !url.startsWith("//")) {
			/*  /uploads/thaoptt09@gmail.com/February2720171130am_nguyen-ngoc-quang_thumb.jpg */
			result = domainURL + url;
		}
		return result;
	}
	public static List<Integer> toIntegerList(String integerListString, String delimiter) throws NumberFormatException {

		ArrayList<Integer> result = new ArrayList<>();

		if ( integerListString  == null  || integerListString.trim().isEmpty()) {
			return result;
		}
		String[] params = integerListString.split(",");
		for (String s : params) {
			result.add(Integer.parseInt(s.trim()));
		}
		return result;
	}

	public static List<Integer> toIntegerList(String integerListString) {
		return toIntegerList(integerListString, AppConstants.DEFAULT_URL_PARAM_LIST_DELIMITER);
	}

	public static String toDurationString(int duration) {
		if (duration < 0) {
			return "";
		}
		int hour = duration / 3600;
		int minute = (duration % 3600) / 60;
		int second = duration %60;
		return String.format("%02d:%02d:%02d", hour, minute, second);
	}
}
