package main;

import java.util.ArrayList;

public class InternalNode extends Node{
private ArrayList<Node> children = new ArrayList<>();
public final String attributeName;

public InternalNode(String attributeName, int depth) {
	super(depth);
	this.attributeName = attributeName;
}


public void addChild(Node child){
	this.children.add(child);
	assert(this.children.size() <= 2);
}

public ArrayList<Node> getChildren(){
	return this.children;
}

}
