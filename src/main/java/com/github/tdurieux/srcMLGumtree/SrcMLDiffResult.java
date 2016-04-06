package com.github.tdurieux.srcMLGumtree;

import com.github.gumtreediff.actions.model.Action;
import org.json.JSONObject;

import java.util.List;

public interface SrcMLDiffResult {
	/**
	 * lists all actions (move,insert, deletes)
	 */
	List<Action> getAllActions();

	/**
	 * lists all actions such that the parent is not involved in the diff
	 */
	List<Action> getRootActions();

	JSONObject toJSON();
}
