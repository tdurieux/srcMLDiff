package com.github.tdurieux.srcMLGumtree;

import com.github.gumtreediff.tree.ITree;
import com.github.gumtreediff.tree.TreeContext;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class SrcMLGumTreeBuilder {
	TreeContext gtContext = new TreeContext();
	private Stack<ITree> nodes;
	public ITree root;
	public static List<String> typesId = new ArrayList<String>();
	private boolean isToPop = false;

	public SrcMLGumTreeBuilder() {
		init();
	}

	// cleans all nodes
	public void init() {
		nodes = new Stack<ITree>();
		root = gtContext.createTree(-1, "", "root");
		getGtContext().setRoot(root);
		nodes.push(root);
		isToPop = false;
	}

	public void visit(Node node) {
		if (node.getNodeName().equals("pos:position")) {
			int column = Integer.parseInt(node.getAttributes().getNamedItem("pos:column").getNodeValue());
			int line = Integer.parseInt(node.getAttributes().getNamedItem("pos:line").getNodeValue());
			nodes.peek().setPos(line * 256 + column);
			nodes.peek().setSize(node.getParentNode().getTextContent().length());
			return;
		}

		enter(node);
		if ("comment".equals(node.getNodeName())
				|| "literal".equals(node.getNodeName())
				|| "operator".equals(node.getNodeName())
				|| ("name".equals(node.getNodeName())
				&& node.getFirstChild().getNodeValue() != null)) {
			nodes.peek().setLabel(node.getFirstChild().getNodeValue());
		} else {
			visitChildren(node);
		}
		exit(node);
	}

	private void visitChildren(Node node) {
		NodeList list = node.getChildNodes();
		for (int i = 0; i < list.getLength(); i++) {
			Node childNode = list.item(i);
			visit(childNode);
		}
	}

	private void enter(Node node) {
		if (node instanceof Document) {
			nodes = new Stack<ITree>();
			root = gtContext.createTree(-1, "", "root");
			getGtContext().setRoot(root);
			nodes.push(root);
			return;
		}
		ITree iTree = createITree(node);
		if (iTree != null) {
			if (node.hasAttributes()) {
				Node column = node.getAttributes().getNamedItem("pos:column");
				Node line = node.getAttributes().getNamedItem("pos:line");
				if (line != null) {
					iTree.setPos(Integer.parseInt(line.getNodeValue()) * 256
							+ Integer.parseInt(column.getNodeValue()));
					iTree.setSize(
							node.getParentNode().getTextContent().length());
				}
			}
			addNodeToTree(iTree);
			isToPop = true;
		} else {
			isToPop = false;
		}
	}

	private void exit(Node node) {
		if (!isToPop) {
			return;
		}
		nodes.pop();
	}

	public int resolveTypeId(String typeClass) {
		if (!typesId.contains(typeClass)) {
			typesId.add(typeClass);
		}
		return typesId.indexOf(typeClass);
	}

	private ITree createNode(String label, String typeLabel) {
		int typeId = resolveTypeId(typeLabel);
		ITree node = gtContext.createTree(typeId, label, typeLabel);
		return node;
	}

	private ITree createITree(Node node) {
		String label = "";
		if (node.getNodeValue() != null) {
			label = node.getNodeValue().replaceAll("[\n\r \t]", "");
		}
		if (label.length() == 0) {
			if ("#text".equals(node.getNodeName())) {
				return null;
			}
		}

		String typeLabel = node.getNodeName();
		ITree iTree = createNode(label, typeLabel);
		//iTree.setMetadata("srcML", node);
		return iTree;
	}

	private void addNodeToTree(ITree node) {
		ITree parent = nodes.peek();
		if (parent != null) {// happens when nodes.push(null)
			parent.addChild(node);
		}
		nodes.push(node);
	}

	public TreeContext getGtContext() {
		return gtContext;
	}
}
