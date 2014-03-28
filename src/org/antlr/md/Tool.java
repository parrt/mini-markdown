package org.antlr.md;

import org.antlr.v4.runtime.ANTLRFileStream;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ParserRuleContext;

import java.util.Arrays;

public class Tool {
	public static void main(String[] args) throws Exception {
		ANTLRInputStream chars = new ANTLRFileStream(args[0]);
		CharsAsTokens charTokens = new CharsAsTokens(chars, MarkdownParser.tokenNames);
		CommonTokenStream tokens = new CommonTokenStream(charTokens);
		MarkdownParser parser = new MarkdownParser(tokens);
		ParserRuleContext t = parser.file();
		t.inspect(Arrays.asList(MarkdownParser.ruleNames));
	}
}
