package torrentweb;

import java.io.StringWriter;
import java.nio.charset.*;
import java.nio.*;

public class Conversion {

	public static final String AsIsCharactersSigns = ".-~_/"; //The '/' character isn't AsIs usually, but won't harm and may save us.
	public static final String AsIsHTML = "+-*/=()%:";
	public static final String defaultSpecialCharacters = "?&%#";

	public static final String QueryStringCompliant(String queryVar,
			String specialCharacters) {
		StringWriter compliantString = new StringWriter();
		for (int i = 0; i < queryVar.length(); i++) {
			if (specialCharacters.contains(String.valueOf(queryVar.charAt(i)))) {
				compliantString.write("%");

				char[] hex = { (char) (queryVar.charAt(i) / 16 + 48),
						(char) (queryVar.charAt(i) % 16 + 48) };
				for (int n = 0; n < 2; n++) {
					if (hex[n] > 57) {
						hex[n] += 7;
					}
					compliantString.write(hex[n]);
				}
			} else {
				compliantString.write(queryVar.charAt(i));
			}
		}
		compliantString.flush();
		return compliantString.toString();
	}

	public static String VeryCompatibleQueryStringCompliant(String queryVar) {
		StringWriter compliantString = new StringWriter();
		ByteBuffer toConvert = Charset.defaultCharset().encode(queryVar);

		for (int i = 0; i < toConvert.remaining(); i++) {
			char currentByte = ByteToChar(toConvert.get(i));
			if (!( currentByte >= 'a' && currentByte <= 'z')
					&& !(currentByte >= 'A' && currentByte <= 'Z')
					&& !(currentByte >= '0' && currentByte <= '9')
					&& !AsIsCharactersSigns.contains(String.valueOf(currentByte))) {

				compliantString.write(CharToURLHex(currentByte));

			} else {
				compliantString.write(currentByte);
			}
		}
		compliantString.flush();
		return compliantString.toString();
	}

	public static String QueryStringCompliant(String queryVar) {
		return QueryStringCompliant(queryVar, defaultSpecialCharacters);
	}

	/*public static String CompatibleQueryStringCompliant(String queryVar) {
		return QueryStringCompliant(queryVar, defaultSpecialCharacters + " ");
	}*/

	public static String CharToURLHex(char toConvert) {
		String out = "";
		int loop = 0;
		while (toConvert > 0 || loop < 1 || loop % 2 != 0) {

			char writeChar = (char) (toConvert % 16 + 48);
			if (writeChar > 57) {
				writeChar += 7;
			}
			out = writeChar + out;
			if (loop % 2 == 1) {
				out = "%" + out;
			}

			toConvert /= 16;
			loop++;
		}

		return out;

	}
	
	public static char ByteToChar(byte in)
	{
		
		if (in<0)
		{
			return (char)(in+256);
		}
		else {return (char)in;}
	}
	public static String HTMLString(String text) {
		StringWriter compliantString = new StringWriter();

		for (int i = 0; i < text.length(); i++) {
			char currentByte = text.charAt(i);
			if (!( currentByte >= 'a' && currentByte <= 'z')
					&& !(currentByte >= 'A' && currentByte <= 'Z')
					&& !(currentByte >= '0' && currentByte <= '9')
					&& !AsIsHTML.contains(String.valueOf(currentByte))) {

				compliantString.write("&#" + (int)currentByte + ";");

			} else {
				compliantString.write(currentByte);
			}
		}
		compliantString.flush();
		return compliantString.toString();
	}
}
