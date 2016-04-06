package com.github.gumtreediff.gen.srcML;

import com.github.gumtreediff.gen.Register;
import com.github.gumtreediff.gen.TreeGenerator;
import com.github.gumtreediff.tree.TreeContext;

import java.io.IOException;
import java.io.Reader;

@Register(id = "srcML", accept = {"\\.xml"})
public class SrcMLTreeGenerator extends TreeGenerator {
	@Override
	protected TreeContext generate(Reader r) throws IOException {
		SrcMLGumTreeVisitor srcMLGumTreeVisitor = new SrcMLGumTreeVisitor(r);
		return srcMLGumTreeVisitor.getTreeContext();
	}
}
