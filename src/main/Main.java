package main;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;

import com.sun.xml.internal.bind.v2.runtime.unmarshaller.XsiNilLoader.Array;

/**
 * this program should be pretty small so i'll just put everything in here
 * @author max
 *
 */
public class Main {
	
	public static String PATH_TO_TRAINING = "data/hepatitis-training.dat";

	public static void main(String[] args) throws IOException{
		ArrayList<HepInstance> instances = loadInstancesFromFile(PATH_TO_TRAINING);
	}

	private static ArrayList<HepInstance> loadInstancesFromFile(String path) throws IOException {
		ArrayList<HepInstance> instances = new ArrayList<HepInstance>();
		
		
		//read in the categories and then remove those lines
		ArrayList<String> lines = (ArrayList<String>) Files.readAllLines(Paths.get(path), Charset.forName("utf8"));
		lines.remove(0);
		String[] categories = lines.get(0).split("\t");
		assert(categories.length == 16):"it should be 16" + categories.length;
		lines.remove(0);
		
		//now read each individual instance and fill out its fields (its only field is a map that is dataName -> bool)
		for(String eachLine: lines){
			//there are some blank empty lines in the text files
			if(!eachLine.equals("")){
				//System.out.println(eachLine);
				//put the line into an array of strings
				String[] instanceData = eachLine.split("   |  | ");//the values are separated by either 1 or 2 or three blank spaces. This is kinda a hack but id rather not have to get tokens out of a stream
				assert(instanceData.length == 17):"that should be length 17..." + instanceData.length;
				//build the map that this entry will use for its fields
				HashMap<String, Boolean> fields = new HashMap<String, Boolean>();
				//fill in the class
				if(instanceData[0].equals("live")){
					fields.put("class", true);
				}else{
					fields.put("class", false);
				}
				for(int i = 0; i < categories.length; i++){// WE USE <= BECAUSE THERE IS ONE MORE ENTRY IN EACH LINE THAN THEIR ARE AMOUNT OF FIELDS. I.E. THE CLASS OF THAT ENTRY
					fields.put(categories[i], Boolean.valueOf(instanceData[i + 1]));
				}
				//now create the HepInstance with the map we made for its fields
				instances.add(new HepInstance(fields));
		}
		
	}
		return instances;
	
	
	
	
	



}

	
	
	
	
	
	
	
	
}
