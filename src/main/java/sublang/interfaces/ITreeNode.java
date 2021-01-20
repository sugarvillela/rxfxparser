package sublang.interfaces;

import sublang.treenode.TreeNodeBase;

import java.util.ArrayList;

public interface ITreeNode {
    boolean split(char delim);

    boolean negate();

    /**Public starting point for unwrapping.
     * If not a leaf (nodes != null) then call recursively on children
     * Else call private version to unwrap self
     * @param first Opening parentheses
     * @param last Closing parentheses
     * @return true if something got changed
     */
    boolean unwrap(char first, char last);

    /**Publicly exposed helper. Checks for wrapping and also unbalanced wraps */
    boolean isWrapped(char first, char last);

    boolean unquote(char quote);

    //=======Rebuild====================================================

    void addChild(TreeNodeBase node);

    //=======Access functions===========================================

    int treeDepth();

    int treeDepth(int max);

    void leaves(ArrayList<TreeNodeBase> leaves);

    void breadthFirst(ArrayList<TreeNodeBase>[] levels);

    void preOrder(ArrayList<TreeNodeBase> leaves);
}
