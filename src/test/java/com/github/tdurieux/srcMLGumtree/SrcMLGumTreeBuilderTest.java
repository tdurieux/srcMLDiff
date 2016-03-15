package com.github.tdurieux.srcMLGumtree;

import com.github.gumtreediff.actions.model.Action;
import org.junit.Test;

import java.io.InputStream;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class SrcMLGumTreeBuilderTest {

	@Test
	public void testExample1() {
		InputStream oldVersion = getClass()
				.getResourceAsStream("/java/example1/old.xml");
		InputStream newVersion = getClass()
				.getResourceAsStream("/java/example1/new.xml");

		SrcMLDiff diff = new SrcMLDiffImpl();
		diff.compare(oldVersion, newVersion);

		List<Action> rootActions = diff.getRootActions();

		assertEquals(5, rootActions.size());
	}

	@Test
	public void testExample2() {
		InputStream oldVersion = getClass()
				.getResourceAsStream("/java/example2/old.xml");
		InputStream newVersion = getClass()
				.getResourceAsStream("/java/example2/new.xml");

		SrcMLDiff diff = new SrcMLDiffImpl();
		diff.compare(oldVersion, newVersion);

		List<Action> rootActions = diff.getRootActions();

		assertEquals(1, rootActions.size());
	}

	@Test
	public void testExample3() {
		InputStream oldVersion = getClass()
				.getResourceAsStream("/java/example3/old.xml");
		InputStream newVersion = getClass()
				.getResourceAsStream("/java/example3/new.xml");

		SrcMLDiff diff = new SrcMLDiffImpl();
		diff.compare(oldVersion, newVersion);

		List<Action> rootActions = diff.getRootActions();

		assertEquals(22, rootActions.size());
	}

	@Test
	public void testExample4() {
		InputStream oldVersion = getClass()
				.getResourceAsStream("/java/example4/old.xml");
		InputStream newVersion = getClass()
				.getResourceAsStream("/java/example4/new.xml");

		SrcMLDiff diff = new SrcMLDiffImpl();
		diff.compare(oldVersion, newVersion);

		List<Action> rootActions = diff.getRootActions();

		assertEquals(3, rootActions.size());
	}

	@Test
	public void testExample5() {
		InputStream oldVersion = getClass()
				.getResourceAsStream("/java/example5/old.xml");
		InputStream newVersion = getClass()
				.getResourceAsStream("/java/example5/new.xml");

		SrcMLDiff diff = new SrcMLDiffImpl();
		diff.compare(oldVersion, newVersion);

		List<Action> rootActions = diff.getRootActions();

		assertEquals(2, rootActions.size());
	}
}
