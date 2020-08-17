package fr.bloomenetwork.fatestaynight.packager;

public class Utils {

	private static final String[] numbers = {"", "一", "二", "三", "四", "五", "六", "七", "八", "九"};

	public static String numberToJapaneseString(int number) {

		String strNumber = String.format("%02d", number);
		String dix = "十";

		if(strNumber.charAt(0) == '0')
			dix = "";

		return numbers[Integer.parseInt(String.valueOf(strNumber.charAt(0)))] + dix + numbers[Integer.parseInt(String.valueOf(strNumber.charAt(1)))];
	}
}
