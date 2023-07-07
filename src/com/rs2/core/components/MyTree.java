package com.rs2.core.components;

import javax.swing.JTree;
import javax.swing.tree.TreeModel;

import com.rs2.core.components.MyTree;
import com.rs2.core.base.MyPane;

public class MyTree extends JTree { // using Treemap: always order by Key while HashMap is randomly ordered
    public MyPane myPane;

    public MyTree() {
        super();
    }

    public MyTree(TreeModel model) {
        super(model);
    }
}