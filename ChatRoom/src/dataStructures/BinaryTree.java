package dataStructures;

import java.util.Objects;
import java.util.function.Consumer;

import exceptions.NotComparableObjectException;
import utilitiies.DoubleIterator;

@SuppressWarnings("unchecked")
public class BinaryTree<K> extends TreeBase<K> {
	
	protected class TreeNode extends Object {
		private TreeNode parent;
		private TreeNode left;
		private TreeNode right;
		private K key;
		
		protected TreeNode(K key) {
			parent = NULL_GUARD;
			left = NULL_GUARD;
			right = NULL_GUARD;
			this.key = key;
		}
		
		protected void setParent(TreeNode parent) {
			this.parent = parent;
		}
		
		protected TreeNode getParent() {
			return parent;
		}
		
		protected void setLeft(TreeNode left){
			this.left = left;
		}
		
		protected TreeNode getLeft() {
			return left;
		}
		
		protected void setRight(TreeNode right) {
			this.right = right;
		}
		
		protected TreeNode getRight() {
			return right;
		}
		
		protected void setKey(K key) {
			this.key = key;
		}
		
		protected K getKey() {
			return key;
		}
	}
	
	private TreeNode NULL_GUARD;
	private TreeNode root;
	private int size;
	
	protected void setNULL_GUARD(TreeNode NULL_GUARD) {
		this.NULL_GUARD = NULL_GUARD;
	}
	
	protected TreeNode getNULL_GUARD() {
		return NULL_GUARD;
	}
	
	protected void setRoot(TreeNode root) {
		this.root = root;
	}
	
	protected TreeNode getRoot() {
		return root;
	}
	
	protected void incrementSize() {
		size += 1;
	}
	
	protected void decrementSize() {
		size -= 1;
	}
	
	protected void setSize(int size) {
		this.size = size;
	}
	
	protected TreeNode add(TreeNode node) {
		TreeNode parent = NULL_GUARD;
		TreeNode root = this.root;
		boolean isLeftChild = false;
		
		while(root != NULL_GUARD) {
			parent = root;
			
			if(((Comparable<K>)node.key).compareTo(root.key) < 0) {
				root = root.left;
				isLeftChild = true;
			}
			else {
				root = root.right;
				isLeftChild = false;
			}
		}
		
		node.parent = parent;
		
		if(parent == NULL_GUARD)
			this.root = node;
		else if(isLeftChild)
			parent.left = node;
		else
			parent.right = node;
		
		return node;
	}
	
	protected TreeNode searchTreeNode(Object key) {
		TreeNode root = this.root;
		
		while(root != NULL_GUARD && !key.equals(root.key)) {
			if(((Comparable<K>)key).compareTo(root.key) < 0)
				root = root.left;
			else
				root = root.right;
		}
		
		return root;
	}
	
	protected TreeNode first(TreeNode root) {
		while(root.left != NULL_GUARD)
			root = root.left;
		return root;
	}
	
	protected TreeNode last(TreeNode root) {
		while(root.right != NULL_GUARD)
			root = root.right;
		return root;
	}
	
	protected TreeNode previous(TreeNode root) {
		if(root.left != NULL_GUARD)
			return last(root.left);
		else {
			TreeNode parent = root.parent;
			while(parent != NULL_GUARD && root == parent.left) {
				root = root.parent;
				parent = parent.parent;
			}
			return parent;
		}
	}
	
	protected TreeNode next(TreeNode root) {
		if(root.right != NULL_GUARD)
			return first(root.right);
		else {
			TreeNode parent = root.parent;
			while(parent != NULL_GUARD && root == parent.right) {
				root = root.parent;
				parent = parent.parent;
			}
			return parent;
		}
	}
	
	protected TreeNode removeNode(TreeNode root) {
		TreeNode current = NULL_GUARD;
		TreeNode child = NULL_GUARD;
		
		if(root.left == NULL_GUARD || root.right == NULL_GUARD)
			current = root;
		else
			current = first(root.right);
		
		if(current.right == NULL_GUARD)
			child = current.left;
		else
			child = current.right;
		
		//if(child != NULL_GUARD)
			child.parent = current.parent;
			
		if(current.parent == NULL_GUARD)
			this.root = child;
		else if(current == current.parent.left)
			current.parent.left = child;
		else
			current.parent.right = child;
		
		if(root != current)
			root.key = current.key;
		
		TreeNode parent = current.parent;
		current.key = null;
		current = current.parent = current.left = current.right = null;
		
		return parent;
	}
	
	protected void clear(TreeNode root) {
		if(root == NULL_GUARD)
			return;
		
		clear(root.left);
		clear(root.right);
		root.key = null;
		root = root.parent = root.left = root.right = null;
	}
	
	protected int height(TreeNode root) {
		if(root == NULL_GUARD)
			return -1;
		
		int hLeft = height(root.left) ;
		int hRight = height(root.right);
		
		return (hLeft > hRight ? hLeft : hRight) + 1;
	}
	
	protected int balanceFactor(TreeNode root) {
		return (height(root.right) + 1) - (height(root.left) + 1);
	}
	
	private void preOrderDisplay(TreeNode root, Consumer<? super K> action) {
		if(root == NULL_GUARD)
			return;
		
		action.accept(root.key);
		preOrderDisplay(root.left, action);
		preOrderDisplay(root.right, action);
	}
	
	private void inOrderDisplay(TreeNode root, Consumer<? super K> action) {
		if(root == NULL_GUARD)
			return;
		
		inOrderDisplay(root.left, action);
		action.accept(root.key);
		inOrderDisplay(root.right, action);
	}
	
	
	private void postOrderDisplay(TreeNode root, Consumer<? super K> action) {
		if(root == NULL_GUARD)
			return;
		
		postOrderDisplay(root.left, action);
		postOrderDisplay(root.right, action);
		action.accept(root.key);
	}
	
	
	@Override
	protected void initializeTree() {
		NULL_GUARD = root = new TreeNode(null);
		NULL_GUARD.parent = NULL_GUARD;
		NULL_GUARD.left = NULL_GUARD;
		NULL_GUARD.right = NULL_GUARD;
		
		size = 0;
	}
	
	public BinaryTree() {
		super();
	}
	
	@Override
	public synchronized boolean add(K key) {
		if(!(key instanceof Comparable)) {
			throw new NotComparableObjectException("Class " 
					+ key.getClass().getCanonicalName()
					+ " must implement the Comparable Interface") ;
		}
		
		add(new TreeNode(key));
		size += 1;
		
		return true;
	}
	
	@Override
	public synchronized boolean search(Object key) {
		return searchTreeNode(key) == NULL_GUARD ? false : true;
	}
	
	@Override
	public synchronized boolean remove(Object key) {
		TreeNode node = searchTreeNode(key);
		
		if(node == null)
			return false;
		else {
			removeNode(node);
			size -= 1;
			return true;
		}
	}
	
	@Override
	public synchronized K first() {
		return first(root).key;
	}
	
	@Override
	public synchronized K last() {
		return last(root).key;
	}
	
	@Override
	public synchronized K previous(K key) {
		TreeNode node = searchTreeNode(key);
		TreeNode root = (node == NULL_GUARD) ? NULL_GUARD : previous(node);
		return root.key;
	}
	
	@Override
	public synchronized K next(K key) {
		TreeNode node = searchTreeNode(key);
		TreeNode root = (node == NULL_GUARD) ? NULL_GUARD : next(node);
		return root.key;
	}
	
	@Override
	public synchronized K popFirst() {
		TreeNode root = first(this.root);
		K key = root.key;
		
		removeNode(root);
		size -= 1;
		
		return key;
	}
	
	@Override
	public synchronized K popLast() {
		TreeNode root = last(this.root);
		K key = root.key;
		
		removeNode(root);
		size -= 1;
		
		return key;
	}
	
	@Override
	public synchronized void clear() {
		clear(root);
		root = NULL_GUARD;
		size = 0;
	}
	
	@Override
	public synchronized int size() {
		return size;
	}
	
	@Override
	public synchronized boolean isEmpty() {
		return size == 0;
	}
	
	@Override
	public synchronized void preOrderDisplay(Consumer<? super K> action) {
		Objects.requireNonNull(action);
		preOrderDisplay(root, action);
		System.out.println();
	}
	
	@Override
	public synchronized void inOrderDisplay(Consumer<? super K> action) {
		Objects.requireNonNull(action);
		inOrderDisplay(root, action);
		System.out.println();
	}
	
	@Override
	public synchronized void postOrderDisplay(Consumer<? super K> action) {
		Objects.requireNonNull(action);
		postOrderDisplay(root, action);
		System.out.println();
	}
	
	@Override
	public synchronized int height() {
		return height(root);
	}
	
	@Override
	public synchronized int balanceFactor() {
		return balanceFactor(root);
	}
	
	@Override
	public synchronized K getTreeRoot() {
		return root.key;
	}
	
	@Override
	public synchronized DoubleIterator<K> iterator() {
		return new DoubleIterator<K>() {
			TreeNode iterator = first(root);
			
			@Override
			public boolean hasNext() {
				return iterator == NULL_GUARD ? false : true;
			}
			
			@Override
			public K next() {
				K key = iterator.key;
				iterator = BinaryTree.this.next(iterator);
				return key;
			}
			
			@Override
			public boolean hasPrevious() {
				return iterator == NULL_GUARD ? false : true;
			}
			
			@Override
			public K previous() {
				K key = iterator.key;
				iterator = BinaryTree.this.previous(iterator);
				return key;
			}
			
			@Override
			public DoubleIterator<K> resetToBegin() {
				iterator = first(root);
				return this;
			}
			
			@Override
			public DoubleIterator<K> resetToEnd() {
				iterator = last(root);
				return this;
			}
		};
	}
	
	@Override
	public synchronized String toString() {
		if(size == 0)
			return "{}";
		else if(size == 1) {
			return "{" + root.key + "}";
		}
		else {
			StringBuilder sb = new StringBuilder("{");
			for(K key : this)
				sb.append(key + ", ");
			sb.setLength(sb.length() - 2);
			sb.append("}");
			return sb.toString();
		}
	}
}