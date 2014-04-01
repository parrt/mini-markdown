/** Parser subset of Markdown copied from:
	http://daringfireball.net/projects/markdown/basics

	# Header 1
	## Header 2

	paragraphs are lines of text separated by blank lines. No indent.

	*   Candy.
	*   Gum.
	*   Booze. might
	    be 2 lines

	> This is a blockquote. No markdown allowed inside
	>
	> This is the second paragraph in the blockquote.

	This is an [example link](http://example.com/).

	Some of these words *are emphasized*.
    Some of these words _are emphasized also_.
*/
parser grammar MarkdownParser;

//@header {package org.antlr.md;}

options {tokenVocab=CharVocab;}

file:	'\n'* elem+ EOF ;

elem
@init {System.err.println(_input.LT(1));} //With predicates, it seems debugging the grammar helps where is normally does not.
	:	header
	|	para
	|	quote
	|	list
	|	'\n'
	;

header : '#'+ ~'\n'* '\n' ;

para:	'\n'* paraContent '\n' (nl|EOF) ; // if \n\n, exists loop. if \n not \n, stays in loop.

paraContent : (text|bold|italics|link|astericks|underscore|{_input.LA(2)!='\n'&&_input.LA(2)!=Token.EOF}? '\n')+ ;

bold:	'*' ~('\n'|' ') text'*' ;

astericks : {_input.LT(1).getCharPositionInLine()!=0}? ws '*' ws ;

underscore : ws '_' ws ;

italics : '_' ~('\n'|' ') text '_' ;

link : '[' text ']' '(' ~')'* ')' ;

quote : quoteElem+ nl ;

quoteElem : {_input.LT(1).getCharPositionInLine()==0}? '>' ~'\n'* '\n' ;

list:	listElem+ nl nl ;

listElem : {_input.LT(1).getCharPositionInLine()==0}? (' ' (' ' ' '?)?)? '*' ws paraContent ;

text:	~('#'|'*'|'>'|'['|']'|'_'|'\n')+ ;

ws	:	(' '|'\t')+ ;

nl	:	'\r'? '\n' ;