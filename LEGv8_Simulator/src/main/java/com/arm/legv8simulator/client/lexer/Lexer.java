package com.arm.legv8simulator.client.lexer;

import java.util.ArrayList;

import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;

/**
 * <code>Lexer</code> is used to process individual lines of LEGv8 source code from the
 * text editor. A list of lexical tokens is returned which can be parsed by the 
 * <code>Parser</code> class.
 * 
 * @see	Parser
 * @author Jonathan Wright, 2016
 *
 */
public class Lexer {

	/**
	 * @param input	a line of LEGv8 source code
	 * @return 		the list of lexical tokens found in the <code>input</code>. Whitespace tokens are omitted.
	 */
	public static ArrayList<Token> lex(String input) {
		
		ArrayList<Token> tokens = new ArrayList<Token>();

		// build single regular expression from the groups defined in TokenType class
		StringBuffer tokenPatternsBuffer = new StringBuffer();
		for (TokenType type : TokenType.values()) {
			tokenPatternsBuffer.append("|(" + type.pattern + ")");
		}
	
		// compile the regular expression into GWT's regex engine
		RegExp tokenPatterns = RegExp.compile(new String(tokenPatternsBuffer.substring(1)), "g");
		
		// every time the engine finds a match, add the token to the output arraylist. whitespace tokens are discarded
		for (MatchResult matcher = tokenPatterns.exec(input); matcher != null; matcher = tokenPatterns.exec(input)) {
			for (TokenType type: TokenType.values()) {
				if (type == TokenType.WHITESPACE) { continue; }
				if (matcher.getGroup(type.groupNumber) != null) {
					tokens.add(new Token(type, matcher.getGroup(type.groupNumber)));
					break;
				} 
			}
		}
		return tokens;
	}
}
