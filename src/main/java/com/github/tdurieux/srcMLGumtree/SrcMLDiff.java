package com.github.tdurieux.srcMLGumtree;

import java.io.File;
import java.io.InputStream;

public interface SrcMLDiff {
	/**
	 * Compare two srcML files
	 *
	 * @param f1
	 * @param f2
	 * @return the current instance
	 */
	SrcMLDiffResult compare(String f1, String f2);

	/**
	 * Compare two srcML files
	 *
	 * @param f1
	 * @param f2
	 * @return the current instance
	 */
	SrcMLDiffResult compare(File f1, File f2);

	/**
	 * Compare two srcML files
	 *
	 * @param oldVersion
	 * @param newVersion
	 * @return the current instance
	 */
	SrcMLDiffResult compare(InputStream oldVersion, InputStream newVersion);
}
