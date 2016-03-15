package com.github.tdurieux.srcMLGumtree;

import com.martiansoftware.jsap.JSAP;
import com.martiansoftware.jsap.JSAPException;
import com.martiansoftware.jsap.JSAPResult;
import com.martiansoftware.jsap.Switch;
import com.martiansoftware.jsap.UnflaggedOption;
import com.martiansoftware.jsap.stringparsers.FileStringParser;

import java.io.File;
import java.util.Iterator;

public class Main {

	private static JSAP jsap = new JSAP();

	public static void main(String[] args) throws Exception {
		initJSAP();
		JSAPResult arguments = parseArguments(args);
		if (arguments == null) {
			return;
		}
		File f1 = arguments.getFile("file1");
		File f2 = arguments.getFile("file2");

		SrcMLDiffImpl diff = new SrcMLDiffImpl();
		diff.compare(f1, f2);

		if (arguments.getBoolean("json")) {
			System.out.println(diff.toJSON().toString(4));
		} else {
			System.out.println(diff.toString());
		}
	}

	private static void showUsage() {
		System.err.println();
		System.err.println("Usage: java -jar srcMlDiff.jar");
		System.err.println("                          " + jsap.getUsage());
		System.err.println();
		System.err.println(jsap.getHelp());
	}

	private static JSAPResult parseArguments(String[] args) {
		JSAPResult config = jsap.parse(args);
		if (!config.success()) {
			System.err.println();
			for (Iterator<?> errs = config.getErrorMessageIterator(); errs
					.hasNext(); ) {
				System.err.println("Error: " + errs.next());
			}
			showUsage();
			return null;
		}

		return config;
	}

	private static void initJSAP() throws JSAPException {
		jsap = new JSAP();
		UnflaggedOption file1 = new UnflaggedOption("file1")
				.setStringParser(FileStringParser.getParser())
				.setUsageName("oldVersion")
				.setRequired(true)
				.setGreedy(false);
		jsap.registerParameter(file1);

		UnflaggedOption file2 = new UnflaggedOption("file2")
				.setStringParser(FileStringParser.getParser())
				.setUsageName("newVersion")
				.setRequired(true)
				.setGreedy(false);
		jsap.registerParameter(file2);

		Switch jsonOpt = new Switch("json")
				.setShortFlag('j')
				.setLongFlag("json");
		jsap.registerParameter(jsonOpt);

		Switch verboseOpt = new Switch("verbose")
				.setShortFlag('v')
				.setLongFlag("verbose");
		jsap.registerParameter(verboseOpt);
	}
}
