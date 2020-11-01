package fr.bloomenetwork.fatestaynight.packager;

public class Utils {

	private static final String[] numbers = {"", "一", "二", "三", "四", "五", "六", "七", "八", "九"};
	
	//Retourne l'entier, compris entre 1 et 99, fournit en paramètre
	//en un String écrit en japonais
	public static String numberToJapaneseString(int number) {

		String strNumber = String.format("%02d", number);
		String dix = "十";

		if(strNumber.charAt(0) == '0')
			dix = "";
		if(strNumber.charAt(0) == '1')
			strNumber = "0" + strNumber.charAt(1);

		return numbers[Integer.parseInt(String.valueOf(strNumber.charAt(0)))] + dix + numbers[Integer.parseInt(String.valueOf(strNumber.charAt(1)))];
	}
}
