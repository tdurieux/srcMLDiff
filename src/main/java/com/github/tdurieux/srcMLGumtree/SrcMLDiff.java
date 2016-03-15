package com.github.tdurieux.srcMLGumtree;

import com.github.gumtreediff.actions.model.Action;

import java.io.File;
import java.io.InputStream;
import java.util.List;

public interface SrcMLDiff {
	/**
	 * lists all actions (move,insert, deletes)
	 */
	public List<Action> getAllActions();

	/**
	 * lists all actions such that the parent is not involved in the diff
	 */
	public List<Action> getRootActions();

	/**
	 * Compare two srcML files
	 *
	 * @param f1
	 * @param f2
	 * @return the current instance
	 */
	SrcMLDiff compare(String f1, String f2);

	/**
	 * Compare two srcML files
	 *
	 * @param f1
	 * @param f2
	 * @return the current instance
	 */
	SrcMLDiff compare(File f1, File f2);

	/**
	 * Compare two srcML files
	 *
	 * @param oldVersion
	 * @param newVersion
	 * @return the current instance
	 */
	SrcMLDiff compare(InputStream oldVersion, InputStream newVersion);
}
