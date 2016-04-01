package main;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import com.sun.xml.internal.bind.v2.runtime.unmarshaller.XsiNilLoader.Array;

/**
 * this program should be pretty small so i'll just put everything in here
 * @author max
 *
 */
public class Main {

	//public static String PATH_TO_TRAINING = "data/hepatitis-training.dat";
	//public static String PATH_TO_TESTING = "data/hepatitis-test.dat";
	//public static String PATH_TO_TRAINING = "data/golf-training.dat";
	//public static String PATH_TO_TESTING = "data/golf-test.dat";
	//public static String PATH_TO_TRAINING = "data/hepatitis-training-run1.dat";
	//public static String PATH_TO_TESTING = "data/hepatitis-test-run1.dat"; 23 success 14 fail
	//public static String PATH_TO_TRAINING = "data/hepatitis-training-run2.dat"; 28 9
	//public static String PATH_TO_TESTING = "data/hepatitis-test-run2.dat";
	//public static String PATH_TO_TRAINING = "data/hepatitis-training-run3.dat"; 30 7
	//public static String PATH_TO_TESTING = "data/hepatitis-test-run3.dat";
	//public static String PATH_TO_TRAINING = "data/hepatitis-training-run4.dat";
	//public static String PATH_TO_TESTING = "data/hepatitis-test-run4.dat"; 29 8
	//public static String PATH_TO_TRAINING = "data/hepatitis-training-run5.dat";
	//public static String PATH_TO_TESTING = "data/hepatitis-test-run5.dat"; 29 8
	//public static String PATH_TO_TRAINING = "data/hepatitis-training-run6.dat"; 28 9
	//public static String PATH_TO_TESTING = "data/hepatitis-test-run6.dat";
	//public static String PATH_TO_TRAINING = "data/hepatitis-training-run7.dat"; 32 5
	//public static String PATH_TO_TESTING = "data/hepatitis-test-run7.dat";
	//public static String PATH_TO_TRAINING = "data/hepatitis-training-run8.dat"; 30 7
	//public static String PATH_TO_TESTING = "data/hepatitis-test-run8.dat";
	//public static String PATH_TO_TRAINING = "data/hepatitis-training-run9.dat"; 29 8
	//public static String PATH_TO_TESTING = "data/hepatitis-test-run9.dat";
	public static String PATH_TO_TRAINING = "data/hepatitis-training-run10.dat"; //27 10
	public static String PATH_TO_TESTING = "data/hepatitis-test-run10.dat";


	public static void main(String[] args) throws IOException{
		ArrayList<HepInstance> instances = loadInstancesFromFile(PATH_TO_TRAINING);
		ArrayList<String> attributes = getCategories(PATH_TO_TRAINING);
	/*	for(HepInstance each: instances){
			System.out.println("---------------- \n");
			for(String eachKey: each.fields.keySet()){
				System.out.println(eachKey + " : " + each.fields.get(eachKey));
			}
		}
		for(String each: attributes){
			System.out.println(each);
		}*/
		Node root = buildTree(instances, attributes, 0);

		//test out that attribute goodness method:
/*		ArrayList<HepInstance> testInstances = new ArrayList<>();
		//make the three instances with true attr and class True
		HashMap<String, Boolean> trueAttrTrueClass = new HashMap<>();
		trueAttrTrueClass.put("attribute", true);
		trueAttrTrueClass.put("class", true);
		testInstances.add(new HepInstance(trueAttrTrueClass));
		testInstances.add(new HepInstance(trueAttrTrueClass));
		testInstances.add(new HepInstance(trueAttrTrueClass));
		testInstances.add(new HepInstance(trueAttrTrueClass));
		//make the four instances with true attr and class False
		HashMap<String, Boolean> trueAttrFalseClass = new HashMap<>();
		trueAttrFalseClass.put("attribute", true);
		trueAttrFalseClass.put("class", false); //should be false. testing that more purity is lower value
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
		testInstances.add(new HepInstance(falseAttrFalseClass));*/



	//	assert(testInstances.size() == 10);
	/*	double goodness = calculateAttributeGoodness("attribute", testInstances);
		assert(goodness == 0.23809523809523808):"it should be 0.238, but it is:" + goodness;*/

		//WE BUILT OUR TREE HOPEFULLY, SO DO THE TESTS
		ArrayList<HepInstance> testInstances = loadInstancesFromFile(PATH_TO_TESTING);
		for(HepInstance each: testInstances){
			System.out.println("---------------- \n");
			for(String eachKey: each.fields.keySet()){
				System.out.println(eachKey + " : " + each.fields.get(eachKey));
			}
		}
		classifyInstances(testInstances, root);
		breadthFirstSearch(root);

	}


	public static void breadthFirstSearch(Node startPoint){
		Queue<Node> queue = new LinkedList<Node>();
		queue.add(startPoint);
		while(!queue.isEmpty()){
			Node visitingNode = queue.poll();
			if(visitingNode instanceof LeafNode){
				LeafNode leaf = (LeafNode)visitingNode;
				System.out.println(leaf.depth + "Leaf" + leaf.outcomeClass);
			}else{
				InternalNode internal = (InternalNode)visitingNode;
				System.out.println(internal.depth + "Internal" + internal.attributeName);
				for(Node eachChild: internal.getChildren()){
					queue.add(eachChild);
				}
			}
		}
	}


	private static void classifyInstances(ArrayList<HepInstance> testInstances, Node root) {
		int success = 0;
		int fail = 0;
		for(HepInstance eachInstance: testInstances){
			Boolean actualClass = eachInstance.fields.get("class");
			Boolean predictedClass = descendTree(eachInstance, root);
			if(actualClass == predictedClass){
				success++;
			}else{
				fail++;
			}
		}
		System.out.println("success: " + success);
		System.out.println("fail: " + fail);
	}


	private static Boolean descendTree(HepInstance instance, Node root) {
		//if we reached a leaf, we should return the outcome:
		if(root instanceof LeafNode){
			LeafNode leaf = (LeafNode) root;
			System.out.println("i reached a leaf and I'm classifying as " + leaf.outcomeClass + " and my actual class is: " + instance.fields.get("class"));
			return leaf.outcomeClass;
		}
		//descend the branch that is appropriate for this instance
		InternalNode internal =  (InternalNode) root;
		//if this attribute is true, go left, else go right
		if(instance.fields.get(internal.attributeName)){
			System.out.println("my attribute:" + internal.attributeName + " is " + instance.fields.get(internal.attributeName) + " so im going left");
			return descendTree(instance, internal.getChildren().get(0));
		}else{
			System.out.println("my attribute:" + internal.attributeName + " is " + instance.fields.get(internal.attributeName) + " so im going right");
			return descendTree(instance, internal.getChildren().get(1));
		}

	}


	/**
	 * recursively builds our decision tree.
	 * @param instances all of the instances that will belong in this node or its future child nodes
	 * @param categories all of the attributes that we can possibly use at this point to divide up our instances
	 * @return a node. Interior nodes represent a category that is used to split the instances. A leaf node is used to represent a certain outcome class that should be taken when it is reached.
	 */
	public static Node buildTree(ArrayList<HepInstance> instances, ArrayList<String> attributes, int depth) {

		//if our list of instances is empty, we should return a leaf node that maps to the overall most likely class //this is basically "no applicable training data available"
		if(instances.isEmpty()){
			System.out.println("creating a leaf node with value: " + true + " cause no more instances");
			return new LeafNode(true, depth);//true i.e. live is the baseline class
		}

		//if our list of instances are all of the same class, we should return a leaf node that maps to that class
		boolean firstInstanceClass = instances.get(0).fields.get("class");
		boolean allSameClass = true;
		for(HepInstance eachInstance: instances){
			if(eachInstance.fields.get("class") != firstInstanceClass){
				allSameClass = false;
			}
		}
		if(allSameClass){
			System.out.println("creating a leaf node with value: " + firstInstanceClass + " because we are pure");
			return new LeafNode(firstInstanceClass, depth);//if first instance's class is q, and all instances are of class q, then q must be majority class obvs
		}

		//if our set of attributes is empty (we ran out), then we should return a leaf node that maps to the majority class of the instances in this node
		if(attributes.isEmpty()){
			//count which class is the majority at this point
			int liveClassCount = 0;
			int dieClassCount = 0;
			for(HepInstance eachInstance: instances){
				if(eachInstance.fields.get("class")){
					liveClassCount++;
				}else{
					dieClassCount++;
				}
			}
			if(liveClassCount > dieClassCount){
				System.out.println("creating a leaf node with value: " + true);
				return new LeafNode(true, depth);
			}else if(liveClassCount < dieClassCount){
				System.out.println("creating a leaf node with value: " + false);
				return new LeafNode(false, depth);
			}else{
				assert(false):"we have an equal split of class outcomes in this point with no attributes. should choose a random one.";
			}


		}


		//if we reached here without returning, then we are going to create an internal node.

		//find the attribute that results in best purity measure value
		double bestAttributePurity = Double.POSITIVE_INFINITY;
		String bestAttributeName = "no attribute selected";
		for(String eachAttribute: attributes){
			double tempGoodness = calculateAttributeGoodness(eachAttribute, instances);
			if(tempGoodness < bestAttributePurity){
				bestAttributePurity = tempGoodness;
				bestAttributeName = eachAttribute;
			}
		}
		assert(!bestAttributeName.equals("no attribute selected")): "the amount of attributes to choose from at this point is: " + attributes.size();
		//sort the instances into two groups, those that this attribute is true for and the others.
		ArrayList<HepInstance> bestAttrTrueInstances = new ArrayList<>();
		ArrayList<HepInstance> bestAttrFalseInstances = new ArrayList<>();
		for(HepInstance each: instances){
			if(each.fields.get(bestAttributeName)){
				bestAttrTrueInstances.add(each);
			}else{
				assert(!each.fields.get(bestAttributeName)):"This should be true or false";
				bestAttrFalseInstances.add(each);
			}
		}
		assert(bestAttrTrueInstances.size() + bestAttrFalseInstances.size() == instances.size());
		//create a new internal node with the name of the attribute that has the best purity measure
		System.out.println("creating an internal node with the attribute: " + bestAttributeName);
		InternalNode internal = new InternalNode(bestAttributeName, depth);
		//side effects are cool. Have to remove from attributes list here
		int debug = attributes.size();
		ArrayList<String> newAttributes = new ArrayList<>();
		for(String each: attributes){
			if(!each.equals(bestAttributeName)){
				newAttributes.add(each);
			}
		}
		assert(debug - 1 == newAttributes.size());
		//first add the left/true/index0 child
		internal.addChild(buildTree(bestAttrTrueInstances, newAttributes, depth + 1));
		//now add the right/false/index1 child
		internal.addChild(buildTree(bestAttrFalseInstances, newAttributes, depth + 1));
		return internal;
	}














	//TODO: if this needs to be generalised for more than one attribute outcomes and/or more classes, it's really just a matter of gathering q^r different double values (where q is the amount of outcomes for the attribute and r is the amount of outcome classes) for the final calculation. Can just use arrays.
	public static double calculateAttributeGoodness(String attributeName, List<HepInstance> instances){
		//TODO: could generalise this by using an array that is the length of the amount of possible attribute outcomes (e.g. in this case it is always binary, the attr is either true or false) IDK if this is really necessary here because we always only have two outcomes for each attr
		//assert(instances.size() == 10): instances.size();
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
		//assert(trueAttributes.size() + falseAttributes.size() == instances.size());
		//assert(trueAttributes.size() == 7): trueAttributes.size();
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
		//assert(attrTrueClassTrueCount == 3);
		//assert(attrTrueClassFalseCount == 4);
		if(trueAttributes.size() == 0){
			assert(falseAttributes.size() == instances.size());
			//System.out.println(falseAttributes.size());
			//System.out.println(instances.size());
		}
		//assert((double)trueAttributes.size() != 0): "failing to generate purity measures because this value is 0 which gives us a NaN. This is 0 in cases where all of the instances are e.g. false for this attribute i.e. all in the right-side branch";
		double attrTruePurity;
		if(trueAttributes.size() == 0){
			attrTruePurity = 0;
		}else{
			attrTruePurity = ((double)attrTrueClassTrueCount / (double)trueAttributes.size()) * ((double)attrTrueClassFalseCount / (double)trueAttributes.size());
		}

		//assert(attrTruePurity == 0.24489795918367344): "It should be " + 0.24489795918 + " but it's:" + attrTruePurity;


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
		//assert(attrFalseClassTrueCount == 2);
		//assert(attrFalseClassFalseCount == 1);
		//assert((double)falseAttributes.size() != 0): "failing to generate purity measures because this value is 0 which gives us a NaN";
		double attrFalsePurity;
		if(falseAttributes.size() == 0){
			attrFalsePurity = 0;
		}else{
			attrFalsePurity = ((double)attrFalseClassTrueCount / (double)falseAttributes.size()) * ((double)attrFalseClassFalseCount / (double)falseAttributes.size());
		}

		//assert(attrFalsePurity == 0.2222222222222222): attrFalsePurity;

		//now we are ready to find the weighted purity of these potential child nodes
		double weightedAveragePurity = (trueAttrProb * attrTruePurity) + (falseAttrProb * attrFalsePurity);
		//System.out.println(weightedAveragePurity);

		return weightedAveragePurity;
	}























	private static ArrayList<String> getCategories(String path) throws IOException {
		ArrayList<String> lines = (ArrayList<String>) Files.readAllLines(Paths.get(path), Charset.forName("utf8"));
		lines.remove(0);
		String[] categories = lines.get(0).split("\t");
		ArrayList<String> attributes = new ArrayList<>(Arrays.asList(categories));
		return attributes;
	}

	private static ArrayList<HepInstance> loadInstancesFromFile(String path) throws IOException {
		ArrayList<HepInstance> instances = new ArrayList<HepInstance>();


		//read in the categories and then remove those lines
		ArrayList<String> lines = (ArrayList<String>) Files.readAllLines(Paths.get(path), Charset.forName("utf8"));
		lines.remove(0);
		String[] categories = lines.get(0).split("\t");
		//assert(categories.length == 16):"it should be 16" + categories.length;
		lines.remove(0);

		//now read each individual instance and fill out its fields (its only field is a map that is dataName -> bool)
		for(String eachLine: lines){
			//there are some blank empty lines in the text files
			if(!eachLine.equals("")){
				//System.out.println(eachLine);
				//put the line into an array of strings
				String[] instanceData = eachLine.split("\t");//eachLine.split("   |  | ")the values are separated by either 1 or 2 or three blank spaces. This is kinda a hack but id rather not have to get tokens out of a stream
				//assert(instanceData.length == 17):"that should be length 17...  THIS IS PROBABLY DUE TO THE INCONSISTENT FORMATTING OF THE PROVIDED FILES. I.E. SOME OF THEM REQUIRE TO BE SPLIT ON TABS AND OTHERS REQUIRE TO BE SPLIT ON SPACES. JUST SWAP OUT THE REGEX PATTERN IN THE LINE ABOVE THIS ONE" + instanceData.length;
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
