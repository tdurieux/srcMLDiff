package com.github.tdurieux.srcMLGumtree;

import com.github.gumtreediff.actions.ActionGenerator;
import com.github.gumtreediff.actions.model.Action;
import com.github.gumtreediff.actions.model.Update;
import com.github.gumtreediff.gen.srcML.SrcMLTreeGenerator;
import com.github.gumtreediff.matchers.CompositeMatchers;
import com.github.gumtreediff.tree.ITree;
import com.github.gumtreediff.tree.TreeContext;
import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Node;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class SrcMLDiffResultImpl implements SrcMLDiffResult {
	private final TreeContext oldTree;
	private final TreeContext newTree;
	private final ActionGenerator actionGenerator;
	private final CompositeMatchers.ClassicGumtree matcher;

	public SrcMLDiffResultImpl(
			TreeContext oldTree, TreeContext newTree,
			CompositeMatchers.ClassicGumtree matcher,
			ActionGenerator actionGenerator) {

		this.oldTree = oldTree;
		this.newTree = newTree;
		this.actionGenerator = actionGenerator;
		this.matcher = matcher;
	}

	public List<Action> getAllActions() {
		return null;
	}

	public List<Action> getRootActions() {
		ActionClassifier actionClassifier = new ActionClassifier();
		return actionClassifier.getRootActions(matcher.getMappingSet(), actionGenerator.getActions());
	}

	private TreeContext getTreeContext(InputStream stream) {
		try {
			TreeContext treeContext = new SrcMLTreeGenerator().generateFromStream(stream);
			return treeContext;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private String toDebugString() {
		String result = "";
		for (Action action : getRootActions()) {
			ITree node = action.getNode();
			String label = "\"" + node.getLabel() + "\"";
			if (action instanceof Update) {
				label += " to \"" + ((Update) action).getValue() + "\"";
			}
			Node srcML = (Node) node.getMetadata("srcML");
			String nodeType = oldTree.getTypeLabel(node.getType());
			result +=
					"\"" + action.getClass().getSimpleName() + "\","
							+ " " + "\"" + nodeType + "\","
							+ " " + srcML.getTextContent()
							+ " (size: " + node.getDescendants().size() + ") \n";
		}
		return result;
	}

	@Override
	public String toString() {
		return toDebugString();
	}

	@Override
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
		String nodeType = oldTree.getTypeLabel(node.getType());
		jsonNode.accumulate("nodeType", nodeType);
		jsonNode.accumulate("label", node.getLabel());
		jsonNode.accumulate("value", ((Node) node.getMetadata("srcML")).getTextContent());
		JSONObject actionPositionJSON = new JSONObject();
		actionPositionJSON.put("positionStart", node.getPos());
		actionPositionJSON.put("positionEnd", node.getPos() + node.getSize());
		jsonNode.put("position", actionPositionJSON);

		List<ITree> descendants = node.getDescendants();
		for (int i = 0; i < descendants.size(); i++) {
			ITree child = descendants.get(i);
			jsonNode.append("descendants", iTreeToJSSON(child));
		}
		return jsonNode;
	}
}
