package dataStructures;

import utilitiies.Tree;

public abstract class TreeBase<K> extends Object implements Tree<K> {
	
	public TreeBase() {
		initializeTree();
	}
	
	protected  abstract void initializeTree();
}