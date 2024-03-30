package com.arm.legv8simulator.client.parser;

import java.util.ArrayList;

import com.arm.legv8simulator.client.instruction.Mnemonic;
import com.arm.legv8simulator.client.lexer.Token;
import com.arm.legv8simulator.client.lexer.TokenType;

/**
 * <code>Parser</code> is used to test the legality of LEGv8 source code token sequences. 
 * 
 * @author Jonathan Wright, 2016
 */
public class Parser {
	
	/**
	 * @param tokens	list of lexical tokens from a line of LEGv8 source code.
	 * @return			parser error message; <code>null</code> if no error present.
	 */
	public static String parseLine(ArrayList<Token> tokens) {
		ParserState currentParserState = ParserState.INIT;
		int i=0;
		try {
			for (i=0; i<tokens.size(); i++) {
				currentParserState = currentParserState.transition(tokens.get(i));
			}
		} catch (UnsupportedInstructionException uie) {
			return handleUnsupportedInstructionException(uie, currentParserState, tokens, i);
		} catch (InvalidTokenException ite) {
			return handleInvalidTokenException(ite, currentParserState, tokens, i);
		} catch (UnexpectedTokenException ute) {
			return unexpectedTokenMsg(tokens.get(i));
		}
		if (currentParserState.accepting) {
			return null;
		} else {
			return missingTokensMsg(currentParserState, 0);
		}
	}
	
	/*
	 * The methods below return error messages detailing why parsing failed.
	 * In order to provide correct error messages with sufficient detail, a number of corner cases have to be handled. 
	 * This is the reason for the code being rather ugly.
	 */
	
	private static String handleUnsupportedInstructionException(UnsupportedInstructionException uie, 
			ParserState parserState, ArrayList<Token> tokens, int tokenIndex) {
		// attempt to match mnemonic. If matched, set message containing missing args
		Mnemonic m = null;
		try {
			if (tokenIndex != tokens.size()-1 && tokens.get(tokenIndex).getType() != TokenType.IDENTIFIER) {
				return unsupportedInstructionMsg(tokens.get(tokenIndex), tokens.get(tokenIndex+1));
			}
			m = Mnemonic.fromString(tokens.get(tokenIndex).getData());
		} catch (IllegalArgumentException iae) {
			return unsupportedInstructionMsg(tokens.get(tokenIndex));
		}
		try {
			parserState = parserState.transition(new Token(m.type, null));
			// will never fail
		} catch (UnsupportedInstructionException e) {} 
		catch (InvalidTokenException e) {} 
		catch (UnexpectedTokenException e) {}
		return missingTokensMsg(parserState, 0);
	}
	
	private static String handleInvalidTokenException(InvalidTokenException ite, 
			 ParserState parserState, ArrayList<Token> tokens, int tokenIndex) {
		Mnemonic expectedMne = ite.getExpectedMnemonic();
		Token currentToken = tokens.get(tokenIndex);
		if (expectedMne == null) {
			return invalidTokenMsg(ite.getExpectedTokenType().toString(), currentToken.getData());
		} else {
			if (currentToken.getData().equals(Mnemonic.LSL.nameUpper) 
					|| currentToken.getData().equals(Mnemonic.LSL.nameLower)) {
				if (tokenIndex == tokens.size()-1) {
					return missingTokensMsg(parserState, 1);
				}
				if (tokenIndex < tokens.size()-1) {
					return invalidTokenMsg(expectedMne.toString(), 
						currentToken.getData() + tokens.get(tokenIndex+1).getData());
				} else {
					return invalidTokenMsg(ite.getExpectedTokenType().toString(), currentToken.getData());
				}
			}
			return invalidTokenMsg(expectedMne.toString(), tokens.get(tokenIndex).getData());
		}
	}
	
	private static String unsupportedInstructionMsg(Token t) {
		return "Unsupported instruction: '" + t.getData() + "'";
	}
	
	private static String unsupportedInstructionMsg(Token t1, Token t2) {
		return "Unsupported instruction: '" + t1.getData() + t2.getData() + "'";
	}
	
	private static String unexpectedTokenMsg(Token t) {
		return "Unexpected token: '" + t.getData() + "'. Remove this token.";
	}
	
	private static String invalidTokenMsg(String expected, String found) {
		return "Invalid token: expected '" + expected + "', found '" + found + "'";
	}
	
	private static String missingTokensMsg(ParserState parserState, int index) {
		String missingTokens = "";
		for (int i=index; i<parserState.expected.length; i++) {
			if (parserState.expected[i].toString().equals("MNEMONIC")) {
				missingTokens += "'LSL' ";
			} else {
				missingTokens += "'" + parserState.expected[i].toString() + "' ";
			}
		}
		if (parserState.expected.length == 1) {
			return "Missing token: " + missingTokens;
		}
		return "Missing tokens: " + missingTokens;
	}
}
