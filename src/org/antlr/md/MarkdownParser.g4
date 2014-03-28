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

@header {package org.antlr.md;}

options {tokenVocab=CharVocab;}

file:	'\n'* elem+ EOF ;

elem:	header
	|	para
	|	quote
	|	list
	|	'\n'
	;

header : '#'+ ~'\n'* '\n' ;

para:	'\n'* paraContent '\n' nl ; // if \n\n, exists loop. if \n not \n, stays in loop.

paraContent : (text|bold|italics|link|astericks|underscore|{_input.LA(2)!='\n'}? '\n')+ ;

bold:	'*' text '*' ;

astericks : ws '*' ws ;

underscore : ws '_' ws ;

italics : '_' text '_' ;

link : '[' text ']' '(' ~')'* ')' ;

quote : quoteElem+ nl ;

quoteElem : '>' ~'\n'* '\n' ;

list:	listElem+ nl nl ;

listElem : (' ' (' ' ' '?)?)? '*' ws paraContent ;

text:	~('#'|'*'|'>'|'['|'\n')+ ;

ws	:	(' '|'\t')+ ;

nl	:	'\r'? '\n' ;