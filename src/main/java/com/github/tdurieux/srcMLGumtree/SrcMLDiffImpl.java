package com.github.tdurieux.srcMLGumtree;

import com.github.gumtreediff.actions.ActionGenerator;
import com.github.gumtreediff.gen.srcML.SrcMLTreeGenerator;
import com.github.gumtreediff.matchers.CompositeMatchers;
import com.github.gumtreediff.matchers.MappingStore;
import com.github.gumtreediff.tree.ITree;
import com.github.gumtreediff.tree.TreeContext;
import com.github.gumtreediff.tree.TreeUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class SrcMLDiffImpl implements SrcMLDiff {
	private ActionGenerator actionGenerator;
	private CompositeMatchers.ClassicGumtree matcher;

	@Override
	public SrcMLDiffResult compare(String f1, String f2) {
		return compare(new File(f1), new File(f2));
	}

	@Override
	public SrcMLDiffResult compare(File f1, File f2) {
		try {
			return compare(new FileInputStream(f1), new FileInputStream(f2));
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public SrcMLDiffResult compare(InputStream oldVersion, InputStream newVersion) {
		TreeContext oldTree = getTreeContext(oldVersion);
		TreeContext newTree = getTreeContext(newVersion);
		return compare(oldTree, newTree);
	}

	public void prepare(ITree node) {
		node.refresh();
		TreeUtils.postOrderNumbering(node);
	}

	private SrcMLDiffResult compare(TreeContext oldTree, TreeContext newTree) {

		prepare(oldTree.getRoot());
		prepare(newTree.getRoot());

		MappingStore mappingsComp = new MappingStore();
		this.matcher = new CompositeMatchers.ClassicGumtree(oldTree.getRoot(), newTree.getRoot(), mappingsComp);
		matcher.match();

		this.actionGenerator = new ActionGenerator(oldTree.getRoot(), newTree.getRoot(), matcher.getMappings());
		actionGenerator.generate();
		return new SrcMLDiffResultImpl(oldTree, newTree, matcher, actionGenerator);
	}

	private TreeContext getTreeContext(InputStream stream) {
		try {
			TreeContext treeContext = new SrcMLTreeGenerator().generateFromStream(stream);
			return treeContext;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
