/** Parser subset of Markdown copied from:
	http://daringfireball.net/projects/markdown/basics

	# Header 1
	## Header 2

	paragraphs are lines of text separated by blank lines. No indent.

	*   Candy.
	*   Gum.
	*   Booze.

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

file:	elem+ EOF ;

elem:	header
	|	para
	|	quote
	;

header : '#'+ ~'\n'* '\n' ;

para:	(text|bold|italics|link|'\n')+ '\n' '\n'+ ;

bold:	'*' text '*' ;

italics : '_' text '_' ;

link : '[' text ']' '(' ~')'* ')' ;

quote : '>' ~'\n'* '\n' ;

text:	~('#'|'*'|'>'|'['|'\n')+ ;