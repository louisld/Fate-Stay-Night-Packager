package fr.bloomenetwork.fatestaynight.packager;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class Utils {

	private static final String[] numbers = {"", "一", "二", "三", "四", "五", "六", "七", "八", "九"};
    private static final int DEFAULT_BUFFER_SIZE = 1024;
	
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
	
	 public static void writeInputStreamToFile(InputStream inputStream, java.io.File file)
	            throws IOException {

	        // append = false
	        try (FileOutputStream outputStream = new FileOutputStream(file)) {
	            int read;
	            byte[] bytes = new byte[DEFAULT_BUFFER_SIZE];
	            while ((read = inputStream.read(bytes)) != -1) {
	                outputStream.write(bytes, 0, read);
	            }
	        }

	    }
}
