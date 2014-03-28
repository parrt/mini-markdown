package org.antlr.md;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTree;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class MDTests {
	@Test
	public void testHeader() {
		check("# header\n", "(file (elem (header #   h e a d e r \\n)) <EOF>)");
	}

	public void check(String input, String expected) {
		ParseTree t = getParseTree(input);
		assertEquals(expected, asString(t));
	}

	public String asString(ParseTree t) {
		MarkdownParser parser = new MarkdownParser(null);
		return t.toStringTree(parser);
	}
	public ParserRuleContext getParseTree(String input) {
		ANTLRInputStream chars = new ANTLRInputStream(input);
		CharsAsTokens charTokens = new CharsAsTokens(chars, MarkdownParser.tokenNames);
		CommonTokenStream tokens = new CommonTokenStream(charTokens);
		MarkdownParser parser = new MarkdownParser(tokens);
		ParserRuleContext t = parser.file();
//		t.inspect(Arrays.asList(MarkdownParser.ruleNames));
		return t;
	}
}
