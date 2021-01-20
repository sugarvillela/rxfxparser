package sublang.interfaces;

import sublang.treenode.TreeNodeBase;

public interface ILogicTree {
    TreeNodeBase treeFromWordPattern(String text);
}
