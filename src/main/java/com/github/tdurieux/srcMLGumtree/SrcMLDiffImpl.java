package com.github.tdurieux.srcMLGumtree;

import com.github.gumtreediff.actions.ActionGenerator;
import com.github.gumtreediff.actions.model.Action;
import com.github.gumtreediff.actions.model.Update;
import com.github.gumtreediff.matchers.CompositeMatchers;
import com.github.gumtreediff.matchers.MappingStore;
import com.github.gumtreediff.tree.ITree;
import com.github.gumtreediff.tree.TreeUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;

public class SrcMLDiffImpl implements SrcMLDiff {
	private SrcMLGumTreeBuilder treeBuilder = new SrcMLGumTreeBuilder();
	private ActionGenerator actionGenerator;
	private CompositeMatchers.ClassicGumtree matcher;

	public List<Action> getAllActions() {
		return null;
	}

	public List<Action> getRootActions() {
		ActionClassifier actionClassifier = new ActionClassifier();
		return actionClassifier.getRootActions(matcher.getMappingSet(), actionGenerator.getActions());
	}

	public SrcMLDiff compare(String f1, String f2) {
		return compare(new File(f1), new File(f2));
	}

	public SrcMLDiff compare(File f1, File f2) {
		try {
			return compare(new FileInputStream(f1), new FileInputStream(f2));
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	public SrcMLDiff compare(InputStream oldVersion, InputStream newVersion) {
		ITree oldTree = getTree(oldVersion);
		ITree newTree = getTree(newVersion);
		return compare(oldTree, newTree);
	}

	public void prepare(ITree node) {
		node.refresh();
		TreeUtils.postOrderNumbering(node);
		TreeUtils.computeHeight(node);
	}

	private SrcMLDiffImpl compare(ITree oldTree, ITree newTree) {

		prepare(oldTree);
		prepare(newTree);

		MappingStore mappingsComp = new MappingStore();
		this.matcher = new CompositeMatchers.ClassicGumtree(oldTree, newTree, mappingsComp);
		matcher.match();

		this.actionGenerator = new ActionGenerator(oldTree, newTree, matcher.getMappings());
		actionGenerator.generate();
		return this;
	}

	private ITree getTree(InputStream stream) {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db;
		Document doc;
		try {
			db = dbf.newDocumentBuilder();
			doc = db.parse(stream);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		treeBuilder.init();
		treeBuilder.visit(doc);
		return treeBuilder.root;
	}

	private String toDebugString() {
		String result = "";
		for (Action action : getRootActions()) {
			ITree node = action.getNode();
			String label = "\"" + node.getLabel() + "\"";
			if (action instanceof Update) {
				label += " to \"" + ((Update) action).getValue() + "\"";
			}
			String nodeType = treeBuilder.getGtContext().getTypeLabel(node.getType());
			result +=
					"\"" + action.getClass().getSimpleName() + "\","
							+ " " + "\"" + nodeType + "\","
							+ " " + label
							+ " (size: " + node.getDescendants().size() + ") \n"
							+ printTree("", node);
		}
		return result;
	}

	public String printTree(String tab, ITree t) {
		StringBuffer b = new StringBuffer();
		b.append(treeBuilder.getGtContext().getTypeLabel(t.getType()) + ":" + t.getLabel() + " \n");
		Iterator<ITree> cIt = t.getChildren().iterator();
		while (cIt.hasNext()) {
			ITree c = cIt.next();
			b.append(tab + " " + printTree("\t" + tab, c));
		}
		return b.toString();
	}

	@Override
	public String toString() {
		return toDebugString();
	}

	public JSONObject toJSON() {
		JSONObject output = new JSONObject();
		JSONArray jsonActions = new JSONArray();
		output.put("actions", jsonActions);

		List<Action> actions = this.getRootActions();
		if (actions.size() == 0) {
			return output;
		}

		for (Action action : actions) {
			ITree node = action.getNode();
			JSONObject jsonAction = iTreeToJSSON(node);
			// action name
			jsonAction.accumulate("action", action.getClass().getSimpleName());

			jsonActions.put(jsonAction);
		}

		return output;
	}

	private JSONObject iTreeToJSSON(ITree node) {
		JSONObject jsonNode = new JSONObject();
		// node type
		String nodeType = treeBuilder.getGtContext().getTypeLabel(node.getType());
		jsonNode.accumulate("nodeType", nodeType);
		jsonNode.accumulate("label", node.getLabel());
		jsonNode.accumulate("value", ((Node) node.getMetadata("srcML")).getTextContent());
		JSONObject actionPositionJSON = new JSONObject();
		actionPositionJSON.put("line", node.getPos() / 256);
		actionPositionJSON.put("column", node.getPos() % 256);
		actionPositionJSON.put("size", node.getSize());
		jsonNode.put("position", actionPositionJSON);

		List<ITree> descendants = node.getDescendants();
		for (int i = 0; i < descendants.size(); i++) {
			ITree child = descendants.get(i);
			jsonNode.append("descendants", iTreeToJSSON(child));
		}
		return jsonNode;
	}
}
