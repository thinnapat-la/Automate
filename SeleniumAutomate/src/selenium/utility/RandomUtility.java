package selenium.utility;

import static org.apache.commons.text.CharacterPredicates.DIGITS;
import static org.apache.commons.text.CharacterPredicates.LETTERS;

import org.apache.commons.text.RandomStringGenerator;

public class RandomUtility {
	
	private RandomUtility() {
		throw new IllegalStateException("Utility class");
	}
	
	private static String randomAll(int txtRg) {
		return new RandomStringGenerator.Builder().withinRange('0', 'z').filteredBy(LETTERS, DIGITS).build().generate(txtRg);
	}
	
	private static String randomText(int txtRg) {
		return new RandomStringGenerator.Builder().withinRange('a', 'z').filteredBy(LETTERS).build().generate(txtRg);
	}
	
	private static String randomNum(int txtRg) {
		return new RandomStringGenerator.Builder().withinRange('0', '9').filteredBy(DIGITS).build().generate(txtRg);
	}

	public static String randomStr(int len) {
		return new RandomStringGenerator.Builder().withinRange('0', 'z').filteredBy(LETTERS, DIGITS).build().generate(len);
	}
	
	public static String randomInput(String typeRm, int txtRg) {
		
		String txtRMResult = null;

		  if(typeRm.equals("ALL")) {
			  txtRMResult = randomAll(txtRg);
		  } else if(typeRm.equals("TEXT")) {
			  txtRMResult = randomText(txtRg);
		  }	else if(typeRm.equals("NUMBER")) {
			  txtRMResult = randomNum(txtRg);
		  }		
		return txtRMResult;			
	}
	
//    @Test
//    public void tTest() {
//    	log.info("All is {} \n Text is {} \n Num is {}", randomAll("35"), randomText("35"), randomNum("35"));
//    	
//    	log.info("RANDOM ------> {}", randomInput("num", "10"));
//    }
	
}
