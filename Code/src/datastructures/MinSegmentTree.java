package datastructures;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

public class MinSegmentTree {
	TreeNode root;
	int current_range; // root.r
	int used_leaves; // the one attached with values
	public MinSegmentTree(int[] input) {
		current_range = input.length;
		--current_range;
		current_range |= current_range >> 1;
		current_range |= current_range >> 2;
		current_range |= current_range >> 4;
		current_range |= current_range >> 8;
		current_range |= current_range >> 16;
		root = new TreeNode(0, current_range); // inclusive of [0, len];
		build(root, 0, current_range, input);
	}

	public MinSegmentTree() {
		root = null;
		current_range = -1;
		used_leaves = 0;
	}

	/**
	 * append value at the tail of segment tree
	 * 
	 * @param val
	 */
	public void append(int val) {
		if (root == null) {
			root = new TreeNode(0, 0);
			current_range = 0;
			used_leaves = 1;
			root.setMin(val);
		} else {
			if(current_range + 1 > used_leaves) {
				updateAt(used_leaves, val);
				used_leaves++;
			} else {
				//expand the tree by doubling
				TreeNode rightroot = new TreeNode(current_range + 1, current_range * 2 +1);
				buildEmpty(rightroot, current_range+1, current_range * 2 +1);
				current_range = current_range * 2 + 1;
				TreeNode newroot = new TreeNode(0, current_range);
				newroot.setLeft(root);
				newroot.setRight(rightroot);
				root.setParent(newroot);
				rightroot.setParent(newroot);
				root = newroot;
//				root.setSplitPoint(root.getLeft().getR());
				updateAt(used_leaves,val);
				used_leaves++;
			}
		}
	}

	private void buildEmpty(TreeNode current, int l, int r) {
		if(l == r) {
			current.setMin(-1);
			return;
		}
		int mid = (l + r) / 2;
		TreeNode left = new TreeNode(l, mid);
		TreeNode right = new TreeNode(mid + 1, r);
		// doubly linked
		left.setParent(current);
		right.setParent(current);
		current.setLeft(left);
		current.setRight(right);
//		current.setSplitPoint(mid);
		current.setMin(-1);
		// recursive build
		buildEmpty(left, l, mid);
		buildEmpty(right, mid + 1,r);
		
	}

	private void build(TreeNode current, int l, int r, int[] input) {
		if (l == r) {
			if(l < input.length) {
				current.setMin(input[l]);
				used_leaves++;
			} else {
				current.setMin(-1);
			}
		} else {
			int mid = (l + r) / 2;
			TreeNode left = new TreeNode(l, mid);
			TreeNode right = new TreeNode(mid + 1, r);
			// doubly linked
			left.setParent(current);
			right.setParent(current);
			current.setLeft(left);
			current.setRight(right);
//			current.setSplitPoint(mid);
			// recursive build
			build(left, l, mid, input);
			build(right, mid + 1, r, input);
			current.setMin(min(left.getMin(), right.getMin()));
		}
	}

	public int getMin(int l, int r) {
		if (l < root.getL() || r > root.getR() || l > r) {
			return -1;
		} else {
			return getMin(root, l, r);
		}
	}
	
	public int getMinAt(int pos) {
		if(pos > root.getR()) {
			return -1;
		}
		return getMin(pos,pos);
	}

	/**
	 * find the min value in range l,r
	 * 
	 * @param root2
	 *            , the current node
	 * @param l
	 *            left
	 * @param r
	 *            right, both inclusive
	 * @return
	 */
	private int getMin(TreeNode root2, int l, int r) {
		if (root2.getL() == l && root2.getR() == r) {
			return root2.getMin();
		} else {
			// check range containment
			int sp = root2.getSplitPoint();
			if (l > sp) {
				return getMin(root2.getRight(), l, r);
			} else if (r <= sp) {
				return getMin(root2.getLeft(), l, r);
			} else {
				return min(getMin(root2.getLeft(), l, sp),
						getMin(root2.getRight(), sp + 1, r));
			}
		}
	}

	public void updateAt(int pos, int val) {
		// find the correct Node
		TreeNode updateNode = findNode(root, pos);
		// if(updateNode.getMin() > val) {
		updateNode.setMin(val);
		// propagation
		TreeNode father = updateNode.father;
		while (father != null) {
			father.setMin(min(father.getLeft().getMin(), father.getRight()
					.getMin()));
			father = father.father;
		}
	}

	/**
	 * find the child node with l = pos, right = pos
	 * 
	 * @param root2
	 * @param pos
	 * @return
	 */
	private TreeNode findNode(TreeNode root2, int pos) {
		if (root2.getL() == pos && root2.getR() == pos) {
			return root2;
		} else {
			int sp = root2.getSplitPoint();
			if (sp < pos) {
				return findNode(root2.getRight(), pos);
			} else {
				return findNode(root2.getLeft(), pos);
			}
		}
	}
	
	@Override
	public String toString() {
		Queue<TreeNode> q = new LinkedList<TreeNode>();
		
		String result  = root +",";
		q.add(root.left);
		q.add(root.right);
		while(!q.isEmpty()) {
			TreeNode tn = q.poll();
			if(tn != null) {
				result = result+ tn + ",";
				q.add(tn.left);
				q.add(tn.right);
			} 
		}
		
		return result;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
//		MinSegmentTree mst = new MinSegmentTree(new int[] { 9, 2, 6, 3, 1, 5,
//				0, 7, 3 });
//		MinSegmentTree mst2 = new MinSegmentTree();
//		mst2.append(9);
//		mst2.append(2);
//		mst2.append(6);
//		mst2.append(3);
//		mst2.append(1);
//		mst2.append(5);
//		mst2.append(0);
//		mst2.append(7);
//		mst2.append(3);
//		for(int i = 0 ; i < 11; i++) {
//			for(int j = i; j <11; j++) {
//				System.out.println(mst.getMin(i, j) + "\t" + mst2.getMin(i, j));
//			}
//		}
//		
//		MinSegmentTree mst3 = new MinSegmentTree(new int[] { 1, -1, 1, -1, -1,-1,
//				-1, 7, 3 });
//		System.out.println(mst3.getMin(1, 8));
		
		MinSegmentTree mst = new MinSegmentTree();
		Random r = new Random(System.currentTimeMillis());
		int queries = 1000000;
		long timer = System.currentTimeMillis();
		long time_used =0;
		for(int i = 0; i < queries; i++) {
			timer = System.currentTimeMillis();
			mst.append(r.nextInt(2000));
			time_used +=  System.currentTimeMillis() - timer;
		}
		for(int i = 0 ; i < queries; i++) {
			int a=r.nextInt(2000), b = r.nextInt(2000); 
			if(a > b) {
				int t = b;
				b = a;
				a = t;
			}
			timer = System.currentTimeMillis();
			mst.getMin(a, b);
			time_used += System.currentTimeMillis() - timer;
		}
		System.out.println("Query:" + queries + "\tTime:"+time_used + "ms\tThroughput:" + (queries/time_used) +"K/s");
	}

	private static int min(int a, int b) {
//		if(a == -1) {
//			if(b == -1) {
//				return -1;
//			} else {
//				return b;
//			}
//		} else {
//			if(b == -1) {
//				return a;
//			} else {
//				return a < b ? a : b;		
//			}
//		}
		return a < b ? a : b;
	}
}

/**
 * it is doubly linked
 * 
 * @author a0048267
 * 
 */
class TreeNode {
	protected TreeNode father;
	protected TreeNode left;
	protected TreeNode right;
	int min, l, r; // stands for the min value at the rang of <l,r> inclusive
//	int split_point; // = left.getR();

	/**
	 * this creates a root, or empty node
	 */
	public TreeNode(int l, int r) {
		father = null;
		left = null;
		right = null;
		this.l = l;
		this.r = r;
	}

	public void setParent(TreeNode current) {
		this.father = current;
	}

	public void setLeft(TreeNode left) {
		this.left = left;
	}

//	public void setSplitPoint(int sp) {
//		split_point = sp;
//	}

	public int getSplitPoint() {
		return (l+r)>>1;
	}

	public void setRight(TreeNode right) {
		this.right = right;
	}

	public TreeNode getLeft() {
		return left;
	}

	public TreeNode getRight() {
		return right;
	}

	public int getL() {
		return l;
	}

	public int getR() {
		return r;
	}

	public int getMin() {
		return min;
	}

	public void setMin(int min) {
		this.min = min;
	}

	@Override
	public String toString() {
		return String.format("[%d,%d]->%d", l, r, min);
	}
}
