package org.antlr.md;

import org.antlr.v4.runtime.ANTLRFileStream;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Tool {
	public static class VerboseListener extends BaseErrorListener {
	    @Override
	    public void syntaxError(Recognizer<?, ?> recognizer,
	                            Object offendingSymbol,
	                            int line, int charPositionInLine,
	                            String msg,
	                            RecognitionException e)
	    {
	        List<String> stack = ((Parser)recognizer).getRuleInvocationStack();
	        Collections.reverse(stack);
	        System.err.println("rule stack: "+stack);
	        System.err.println("line "+line+":"+charPositionInLine+" at "+
	                           offendingSymbol+": "+msg);
	    }

	}

	public static enum OptionArgType { NONE, STRING } // NONE implies boolean
	public static class Option {
		String fieldName;
		String name;
		OptionArgType argType;
		String description;

		public Option(String fieldName, String name, String description) {
			this(fieldName, name, OptionArgType.NONE, description);
		}

		public Option(String fieldName, String name, OptionArgType argType, String description) {
			this.fieldName = fieldName;
			this.name = name;
			this.argType = argType;
			this.description = description;
		}
	}

	public static Option[] optionDefs = {
		new Option("showGUI",	"-gui",		"load gui viewer of parse tree"),
		new Option("showTree",	"-tree",	"show parse tree in string form")
	};

	public String inputFileName;
	public boolean showGUI;
	public boolean showTree;

	public static void main(String[] args) throws Exception {
		new Tool().process(args);
	}

	public void process(String[] args) throws IOException {
		handleArgs(args);
		ANTLRInputStream chars;
		if ( inputFileName!=null ) chars = new ANTLRFileStream(inputFileName);
		else chars = new ANTLRInputStream(System.in);
		CharsAsTokens charTokens = new CharsAsTokens(chars, MarkdownParser.tokenNames);
		CommonTokenStream tokens = new CommonTokenStream(charTokens);
		MarkdownParser parser = new MarkdownParser(tokens);
//		parser.getInterpreter().setPredictionMode(PredictionMode.SLL);
//		parser.removeErrorListeners(); // remove ConsoleErrorListener
//		parser.addErrorListener(new VerboseListener()); // add ours
		ParserRuleContext t = parser.file();
		if ( showGUI ) t.inspect(Arrays.asList(MarkdownParser.ruleNames));
		if ( showTree ) System.out.println(t.toStringTree(parser));
	}

	public void handleArgs(String[] args) {
		int i=0;
		while ( args!=null && i<args.length ) {
			String arg = args[i];
			i++;
			if ( arg.charAt(0)!='-' ) { // file name
				inputFileName = arg;
				continue;
			}
			boolean found = false;
			for (Option o : optionDefs) {
				if ( arg.equals(o.name) ) {
					found = true;
					String argValue = null;
					if ( o.argType==OptionArgType.STRING ) {
						argValue = args[i];
						i++;
					}
					// use reflection to set field
					Class<? extends Tool> c = this.getClass();
					try {
						Field f = c.getField(o.fieldName);
						if ( argValue==null ) {
							if ( arg.startsWith("-no-") ) f.setBoolean(this, false);
							else f.setBoolean(this, true);
						}
						else f.set(this, argValue);
					}
					catch (Exception e) {
						System.err.println("can't access field "+o.fieldName);
					}
				}
			}
			if ( !found ) {
				System.err.println("invalid cmdline arg: " + arg);
			}
		}
	}

}
