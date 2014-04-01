package org.antlr.md;

import org.antlr.v4.codegen.Target;
import org.antlr.v4.misc.CharSupport;
import org.antlr.v4.runtime.CommonTokenFactory;
import org.antlr.v4.runtime.WritableToken;
import org.antlr.v4.runtime.misc.Pair;
import org.antlr.v4.tool.Grammar;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonToken;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenFactory;
import org.antlr.v4.runtime.TokenSource;

import java.util.LinkedHashMap;
import java.util.Map;

public class CharsAsTokens implements TokenSource {
    CharStream input;
    String[] tokenNames;
    int line=1;
    int charPosInLine;
    Map<Integer, Integer> charToTokenType = new LinkedHashMap<Integer, Integer>();

    public CharsAsTokens(CharStream input, String[] tokenNames) {
        this.input = input;
        this.tokenNames = tokenNames;
        int ttype = 0;
        for (String tname : tokenNames) {
            if ( tname!=null && tname.charAt(0)=='\'' ) {
				int charVal = CharSupport.getCharValueFromGrammarCharLiteral(tname);
				charToTokenType.put(charVal, ttype);
            }
            ttype++;
        }
//        System.out.println(charToTokenType);
    }

	@Override
	public TokenFactory<?> getTokenFactory() {
		return CommonTokenFactory.DEFAULT;
	}

	public Token nextToken() {
		Token t = null;
		consumeUnknown();
		int c = input.LA(1);
		int i = input.index();
		if ( c == CharStream.EOF ) {
			t = getTokenFactory().create(Token.EOF, "<EOF>");
		}
		else {
			Integer ttypeI = charToTokenType.get(c);
			t = getTokenFactory().create(
					new Pair<TokenSource,CharStream>(this,input),
					ttypeI, String.valueOf((char)c), Token.DEFAULT_CHANNEL, i,  i,
					line, charPosInLine);
		}
//		System.out.println(t.getText());
		consume();
		return t;
	}

	protected void consumeUnknown() {
		int c = input.LA(1);
        Integer ttypeI = charToTokenType.get(c);
        while ( ttypeI==null && c != CharStream.EOF ) {
            System.err.println("no token type for char '"+(char)c+"'");
            c = consume();
            ttypeI = charToTokenType.get(c);
        }
    }

    protected int consume() {
        int c = input.LA(1);
        if ( c==-1 ) {
			return CharStream.EOF;
		}
		input.consume();
		charPosInLine++;
        if ( c == '\n' ) { charPosInLine = 0; line++; }
        return input.LA(1);
    }

    public String getSourceName() {
        return null;
    }

	@Override
	public int getCharPositionInLine() {
		return 0;
	}

	@Override
	public int getLine() {
		return 0;
	}

	@Override
	public CharStream getInputStream() { return input; }

	@Override
	public void setTokenFactory(TokenFactory<?> factory) {
	}
}
