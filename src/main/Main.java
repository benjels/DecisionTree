package main;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
		String[] categories = getCategories(PATH_TO_TRAINING);
		for(HepInstance each: instances){
			System.out.println("---------------- \n");
			for(String eachKey: each.fields.keySet()){
				System.out.println(eachKey + " : " + each.fields.get(eachKey));
			}
		}
		for(String each: categories){
			System.out.println(each);
		}
		Node root = buildTree(instances, categories);
		//test out that attribute goodness method:
		ArrayList<HepInstance> testInstances = new ArrayList<>();
		//make the three instances with true attr and class True
		HashMap<String, Boolean> trueAttrTrueClass = new HashMap<>();
		trueAttrTrueClass.put("attribute", true);
		trueAttrTrueClass.put("class", true);
		testInstances.add(new HepInstance(trueAttrTrueClass));
		testInstances.add(new HepInstance(trueAttrTrueClass));
		testInstances.add(new HepInstance(trueAttrTrueClass));
		//make the four instances with true attr and class False
		HashMap<String, Boolean> trueAttrFalseClass = new HashMap<>();
		trueAttrFalseClass.put("attribute", true);
		trueAttrFalseClass.put("class", false);
		testInstances.add(new HepInstance(trueAttrFalseClass));
		testInstances.add(new HepInstance(trueAttrFalseClass));
		testInstances.add(new HepInstance(trueAttrFalseClass));
		testInstances.add(new HepInstance(trueAttrFalseClass));
		//make the two instances with false attr and class True
		HashMap<String, Boolean> falseAttrTrueClass = new HashMap<>();
		falseAttrTrueClass.put("attribute", false);
		falseAttrTrueClass.put("class", true);
		testInstances.add(new HepInstance(falseAttrTrueClass));
		testInstances.add(new HepInstance(falseAttrTrueClass));
		//make the one instance with fase attr and class False
		HashMap<String, Boolean> falseAttrFalseClass = new HashMap<>();
		falseAttrFalseClass.put("attribute", false);
		falseAttrFalseClass.put("class", false);
		testInstances.add(new HepInstance(falseAttrFalseClass));



		assert(testInstances.size() == 10);
		double goodness = calculateAttributeGoodness("attribute", testInstances);
		assert(goodness == 0.23809523809523808):"it should be 0.238, but it is:" + goodness;
	}


	/**
	 * recursively builds our decision tree.
	 * @param instances all of the instances that will belong in this node or its future child nodes
	 * @param categories all of the categories that we can possibly use at this point to divide up our instances
	 * @return a node. Interior nodes represent a category that is used to split the instances. A leaf node is used to represent a certain outcome class that should be taken when it is reached.
	 */
	public static Node buildTree(ArrayList<HepInstance> instances, String[] categories) {
		//if our list of instances is empty, we should return a leaf node that maps to the overall most likely class

		//if our list of instances are all of the same class, we should return a leaf node that maps to that class

		//if our set of attributes is empty (we ran out), then we should return a leaf node that maps to the majority class of the instances in this node

		//if we reached here without returning, then we are going to create an internal node.

		//-find the attribute that results in best purity measure value

		//-create a new internal node with the name of the attribute that has the best purity measure

		//-add child nodes to this node by recursively calling this method (will need to divide the instances into two lists, one for which the chosen attribute is true and the other for which it is false)
		^ACTUALLY COULD JUST GET THE calculateAttributeGoodness method to return this as well in a pojo seeing that we are already calcing it there
			//leftChild = recurse(trueInstances, attributes - bestAtt)
			//rightChild = recurse(falseInstances, attributes - bestAtt)

	}














	//TODO: if this needs to be generalised for more than one attribute outcomes and/or more classes, it's really just a matter of gathering q^r different double values (where q is the amount of outcomes for the attribute and r is the amount of outcome classes) for the final calculation. Can just use arrays.
	public static double calculateAttributeGoodness(String attributeName, List<HepInstance> instances){
		//TODO: could generalise this by using an array that is the length of the amount of possible attribute outcomes (e.g. in this case it is always binary, the attr is either true or false) IDK if this is really necessary here because we always only have two outcomes for each attr

		//first we should find the probabilities of visiting each of the nodes
		//int trueAttrCount = 0;
		ArrayList<HepInstance> trueAttributes = new ArrayList<>();
		//int falseAttrCount = 0;
		ArrayList<HepInstance> falseAttributes = new ArrayList<>();
		for(HepInstance eachInstance: instances){
			if(eachInstance.fields.get(attributeName).equals(true)){
				trueAttributes.add(eachInstance);
			}else if(eachInstance.fields.get(attributeName).equals(false)){
				falseAttributes.add(eachInstance);
			}else{
				assert false;
			}
		}
		assert(trueAttributes.size() + falseAttributes.size() == instances.size());
		assert(trueAttributes.size() == 7);
		double trueAttrProb = (double)trueAttributes.size()/(double)(instances.size());
		double falseAttrProb = (double)falseAttributes.size()/(double)(instances.size());
		//now we have our probabilities, we want to calculate the "purity" of each possible node
		//TODO: similarly, this should be generalised by having an array of the length of the amount of possible child nodes/attribute outcomes

		//find the purity of the trueNode
		int attrTrueClassTrueCount = 0;
		int attrTrueClassFalseCount = 0;
		for(HepInstance eachInstance: trueAttributes){
			if(eachInstance.fields.get("class")){
				attrTrueClassTrueCount ++;
			}else if(!eachInstance.fields.get("class")){
				attrTrueClassFalseCount ++;
			}else{
				assert false;
			}
		}
		assert(attrTrueClassTrueCount == 3);
		assert(attrTrueClassFalseCount == 4);
		double attrTruePurity = ((double)attrTrueClassTrueCount / (double)trueAttributes.size()) * ((double)attrTrueClassFalseCount / (double)trueAttributes.size());
		assert(attrTruePurity == 0.24489795918367344): "It should be " + 0.24489795918 + " but it's:" + attrTruePurity;


		//find the purity of the falseNode
		int attrFalseClassTrueCount = 0;
		int attrFalseClassFalseCount = 0;
		for(HepInstance eachInstance: falseAttributes){
			if(eachInstance.fields.get("class")){
				attrFalseClassTrueCount ++;
			}else if(!eachInstance.fields.get("class")){
				attrFalseClassFalseCount ++;
			}else{
				assert false;
			}
		}
		assert(attrFalseClassTrueCount == 2);
		assert(attrFalseClassFalseCount == 1);
		double attrFalsePurity = ((double)attrFalseClassTrueCount / (double)falseAttributes.size()) * ((double)attrFalseClassFalseCount / (double)falseAttributes.size());
		assert(attrFalsePurity == 0.2222222222222222): attrFalsePurity;

		//now we are ready to find the weighted purity of these potential child nodes
		double weightedAveragePurity = (trueAttrProb * attrTruePurity) + (falseAttrProb * attrFalsePurity);
		System.out.println(weightedAveragePurity);
		return weightedAveragePurity;
	}























	private static String[] getCategories(String path) throws IOException {
		ArrayList<String> lines = (ArrayList<String>) Files.readAllLines(Paths.get(path), Charset.forName("utf8"));
		lines.remove(0);
		String[] categories = lines.get(0).split("\t");
		return categories;
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



	public static void algorithmTbh(){

	}





}
