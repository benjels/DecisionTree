package main;

//just a node that can't have any children :(
public class LeafNode extends Node{

	public final boolean outcomeClass;

	public LeafNode(boolean outcomeClass, int depth){
		super(depth);
		this.outcomeClass = outcomeClass;
	}

}
