package com.arm.legv8simulator.client.parser;

import com.arm.legv8simulator.client.instruction.Mnemonic;
import com.arm.legv8simulator.client.lexer.Token;
import com.arm.legv8simulator.client.lexer.TokenType;

/**
 * The <code>ParserState</code> enumeration defines all states and transitions in the parser FSM
 * <p>
 * See the LEGv8_Grammar.ppt slides for a graphical description and explanation of the LEGv8 parser FSM 
 * 
 * @see Parser
 * @author Jonathan Wright, 2016
 */
public enum ParserState {
	INIT(false, null) { 
		@Override
        public ParserState transition(Token t) throws UnsupportedInstructionException {
            switch (t.getType()) {
            	case MNEMONIC_R : return R1;
            	case MNEMONIC_RR : return RR1;
            	case MNEMONIC_RRR : return RRR1;
            	case MNEMONIC_RI : return RI1;
            	case MNEMONIC_RRI : return RRI1;
            	case MNEMONIC_RM : return RM1;
            	case MNEMONIC_RRM : return RRM1;
            	case MNEMONIC_RISI : return RISI1;
            	case MNEMONIC_L : return L1;
            	case MNEMONIC_RL : return RL1;
            	case LABEL : return G1;
            	default : throw new UnsupportedInstructionException(INIT);
            }
        }
    },
    G1(true, null) { @Override
    	public ParserState transition(Token t) throws UnsupportedInstructionException {
            switch (t.getType()) {
            	case MNEMONIC_R : return R1;
            	case MNEMONIC_RR : return RR1;
            	case MNEMONIC_RRR : return RRR1;
            	case MNEMONIC_RI : return RI1;
            	case MNEMONIC_RRI : return RRI1;
            	case MNEMONIC_RM : return RM1;
            	case MNEMONIC_RRM : return RRM1;
            	case MNEMONIC_RISI : return RISI1;
            	case MNEMONIC_L : return L1;
            	case MNEMONIC_RL : return RL1;
            	default : throw new UnsupportedInstructionException(G1);
            }
        }
    },
    R1(false, new TokenType[]{TokenType.REGISTER}) { @Override
    	public ParserState transition(Token t) throws InvalidTokenException {
            switch (t.getType()) {
            	case REGISTER : return R2;
            	default : throw new InvalidTokenException(expected[0]);
            }
        }
    },
    R2(true, null) { @Override
    	public ParserState transition(Token t) throws UnexpectedTokenException {
            throw new UnexpectedTokenException();
        }
    },
    RR1(false, new TokenType[]{TokenType.REGISTER, TokenType.COMMA, 
    		TokenType.REGISTER}) { @Override
    	public ParserState transition(Token t) throws InvalidTokenException {
            switch (t.getType()) {
            	case REGISTER : return RR2;
            	default : throw new InvalidTokenException(expected[0]);
            }
        }
    },
    RR2(false, new TokenType[]{TokenType.COMMA, TokenType.REGISTER}) { @Override
    	public ParserState transition(Token t) throws InvalidTokenException {
            switch (t.getType()) {
            	case COMMA : return RR3;
            	default : throw new InvalidTokenException(expected[0]);
            }
        }
    },
    RR3(false, new TokenType[]{TokenType.REGISTER}) { @Override
    	public ParserState transition(Token t) throws InvalidTokenException {
            switch (t.getType()) {
            	case REGISTER : return RR4;
            	default : throw new InvalidTokenException(expected[0]);
            }
        }
    },
    RR4(true, null) { @Override
    	public ParserState transition(Token t) throws UnexpectedTokenException {
            throw new UnexpectedTokenException();
        }
    },
    RRR1(false, new TokenType[]{TokenType.REGISTER, TokenType.COMMA, 
    		TokenType.REGISTER, TokenType.COMMA, TokenType.REGISTER}) { @Override
    	public ParserState transition(Token t) throws InvalidTokenException {
            switch (t.getType()) {
            	case REGISTER : return RRR2;
            	default : throw new InvalidTokenException(expected[0]);
            }
        }
    },
    RRR2(false, new TokenType[]{TokenType.COMMA, TokenType.REGISTER, 
    		TokenType.COMMA, TokenType.REGISTER}) { @Override
    	public ParserState transition(Token t) throws InvalidTokenException {
            switch (t.getType()) {
            	case COMMA : return RRR3;
            	default : throw new InvalidTokenException(expected[0]);
            }
        }
    },
    RRR3(false, new TokenType[]{TokenType.REGISTER, TokenType.COMMA, 
    		TokenType.REGISTER}) { @Override
    	public ParserState transition(Token t) throws InvalidTokenException {
            switch (t.getType()) {
            	case REGISTER : return RRR4;
            	default : throw new InvalidTokenException(expected[0]);
            }
        }
    },
    RRR4(false, new TokenType[]{TokenType.COMMA, TokenType.REGISTER}) { @Override
    	public ParserState transition(Token t) throws InvalidTokenException {
            switch (t.getType()) {
            	case COMMA : return RRR5;
            	default : throw new InvalidTokenException(expected[0]);
            }
        }
    },
    RRR5(false, new TokenType[]{TokenType.REGISTER}) { @Override
    	public ParserState transition(Token t) throws InvalidTokenException {
            switch (t.getType()) {
            	case REGISTER : return RRR6;
            	default : throw new InvalidTokenException(expected[0]);
            }
        }
    },
    RRR6(true, null) { @Override
    	public ParserState transition(Token t) throws UnexpectedTokenException {
            throw new UnexpectedTokenException();
        }
    },
    RI1(false, new TokenType[]{TokenType.REGISTER, TokenType.COMMA, 
    		TokenType.IMMEDIATE}) { @Override
    	public ParserState transition(Token t) throws InvalidTokenException {
            switch (t.getType()) {
            	case REGISTER : return RI2;
            	default : throw new InvalidTokenException(expected[0]);
            }
        }
    },
    RI2(false, new TokenType[]{TokenType.COMMA, TokenType.IMMEDIATE}) { @Override
    	public ParserState transition(Token t) throws InvalidTokenException {
            switch (t.getType()) {
            	case COMMA : return RI3;
            	default : throw new InvalidTokenException(expected[0]);
            }
        }
    },
    RI3(false, new TokenType[]{TokenType.IMMEDIATE}) { @Override
    	public ParserState transition(Token t) throws InvalidTokenException {
            switch (t.getType()) {
            	case IMMEDIATE : return RI4;
            	default : throw new InvalidTokenException(expected[0]);
            }
        }
    },
    RI4(true, null) { @Override
    	public ParserState transition(Token t) throws UnexpectedTokenException {
            throw new UnexpectedTokenException();
        }
    },
    RRI1(false, new TokenType[]{TokenType.REGISTER, TokenType.COMMA, 
    		TokenType.REGISTER, TokenType.COMMA, TokenType.IMMEDIATE}) { @Override
    	public ParserState transition(Token t) throws InvalidTokenException {
            switch (t.getType()) {
            	case REGISTER : return RRI2;
            	default : throw new InvalidTokenException(expected[0]);
            }
        }
    },
    RRI2(false, new TokenType[]{TokenType.COMMA, TokenType.REGISTER, 
    		TokenType.COMMA, TokenType.IMMEDIATE}) { @Override
    	public ParserState transition(Token t) throws InvalidTokenException {
            switch (t.getType()) {
            	case COMMA : return RRI3;
            	default : throw new InvalidTokenException(expected[0]);
            }
        }
    },
    RRI3(false, new TokenType[]{TokenType.REGISTER, TokenType.COMMA, 
    		TokenType.IMMEDIATE}) { @Override
    	public ParserState transition(Token t) throws InvalidTokenException {
            switch (t.getType()) {
            	case REGISTER : return RRI4;
            	default : throw new InvalidTokenException(expected[0]);
            }
        }
    },
    RRI4(false, new TokenType[]{TokenType.COMMA, TokenType.IMMEDIATE}) { @Override
    	public ParserState transition(Token t) throws InvalidTokenException {
            switch (t.getType()) {
            	case COMMA : return RRI5;
            	default : throw new InvalidTokenException(expected[0]);
            }
        }
    },
    RRI5(false, new TokenType[]{TokenType.IMMEDIATE}) { @Override
    	public ParserState transition(Token t) throws InvalidTokenException {
            switch (t.getType()) {
            	case IMMEDIATE : return RRI6;
            	default : throw new InvalidTokenException(expected[0]);
            }
        }
    },
    RRI6(true, null) { @Override
    	public ParserState transition(Token t) throws UnexpectedTokenException {
            throw new UnexpectedTokenException();
        }
    },
    RM1(false, new TokenType[]{TokenType.REGISTER, TokenType.COMMA, 
    		TokenType.LBRACKET, TokenType.REGISTER, TokenType.COMMA, 
    		TokenType.IMMEDIATE, TokenType.RBRACKET}) { @Override
    	public ParserState transition(Token t) throws InvalidTokenException {
            switch (t.getType()) {
            	case REGISTER : return RM2;
            	default : throw new InvalidTokenException(expected[0]);
            }
        }
    },
    RM2(false, new TokenType[]{TokenType.COMMA, TokenType.LBRACKET, TokenType.REGISTER, 
    		TokenType.COMMA, TokenType.IMMEDIATE, TokenType.RBRACKET}) { @Override
    	public ParserState transition(Token t) throws InvalidTokenException {
            switch (t.getType()) {
            	case COMMA : return RM3;
            	default : throw new InvalidTokenException(expected[0]);
            }
        }
    },
    RM3(false, new TokenType[]{TokenType.LBRACKET, TokenType.REGISTER, 
    		TokenType.COMMA, TokenType.IMMEDIATE, TokenType.RBRACKET}) { @Override
    	public ParserState transition(Token t) throws InvalidTokenException {
            switch (t.getType()) {
            	case LBRACKET : return RM4;
            	default : throw new InvalidTokenException(expected[0]);
            }
        }
    },
    RM4(false, new TokenType[]{TokenType.REGISTER, TokenType.COMMA, 
    		TokenType.IMMEDIATE, TokenType.RBRACKET}) { @Override
    	public ParserState transition(Token t) throws InvalidTokenException {
            switch (t.getType()) {
            	case REGISTER : return RM5;
            	default : throw new InvalidTokenException(expected[0]);
            }
        }
    },
    RM5(false, new TokenType[]{TokenType.COMMA, TokenType.IMMEDIATE, 
    		TokenType.RBRACKET}) { @Override
    	public ParserState transition(Token t) throws InvalidTokenException {
            switch (t.getType()) {
            	case RBRACKET : return RM6;
            	case COMMA : return RM7;
            	default : throw new InvalidTokenException(expected[0]);
            }
        }
    },
    RM6(true, null) { @Override
    	public ParserState transition(Token t) throws UnexpectedTokenException {
            throw new UnexpectedTokenException();
        }
    },
    RM7(false, new TokenType[]{TokenType.IMMEDIATE, TokenType.RBRACKET}) { @Override
    	public ParserState transition(Token t) throws InvalidTokenException {
            switch (t.getType()) {
            	case IMMEDIATE : return RM8;
            	default : throw new InvalidTokenException(expected[0]);
            }
        }
    },
    RM8(false, new TokenType[]{TokenType.RBRACKET}) { @Override
    	public ParserState transition(Token t) throws InvalidTokenException {
            switch (t.getType()) {
            	case RBRACKET : return RM6;
            	default : throw new InvalidTokenException(expected[0]);
            }
        }
    },
    RRM1(false, new TokenType[]{TokenType.REGISTER, TokenType.COMMA, TokenType.REGISTER, 
    		TokenType.COMMA, TokenType.LBRACKET, TokenType.REGISTER, TokenType.COMMA, 
    		TokenType.IMMEDIATE, TokenType.RBRACKET}) { @Override
    	public ParserState transition(Token t) throws InvalidTokenException {
            switch (t.getType()) {
            	case REGISTER : return RRM2;
            	default : throw new InvalidTokenException(expected[0]);
            }
        }
    },
    RRM2(false, new TokenType[]{TokenType.COMMA, TokenType.REGISTER, 
    		TokenType.COMMA, TokenType.LBRACKET, TokenType.REGISTER, 
    		TokenType.COMMA, TokenType.IMMEDIATE, TokenType.RBRACKET}) { @Override
    	public ParserState transition(Token t) throws InvalidTokenException {
            switch (t.getType()) {
            	case COMMA : return RRM3;
            	default : throw new InvalidTokenException(expected[0]);
            }
        }
    },
    RRM3(false, new TokenType[]{TokenType.REGISTER, TokenType.COMMA, 
    		TokenType.LBRACKET, TokenType.REGISTER, TokenType.COMMA, 
    		TokenType.IMMEDIATE, TokenType.RBRACKET}) { @Override
    	public ParserState transition(Token t) throws InvalidTokenException {
            switch (t.getType()) {
            	case REGISTER : return RRM4;
            	default : throw new InvalidTokenException(expected[0]);
            }
        }
    },
    RRM4(false, new TokenType[]{TokenType.COMMA, TokenType.LBRACKET, TokenType.REGISTER, 
    		TokenType.COMMA, TokenType.IMMEDIATE, TokenType.RBRACKET}) { @Override
    	public ParserState transition(Token t) throws InvalidTokenException {
            switch (t.getType()) {
            	case COMMA : return RRM5;
            	default : throw new InvalidTokenException(expected[0]);
            }
        }
    },
    RRM5(false, new TokenType[]{TokenType.LBRACKET, TokenType.REGISTER, 
    		TokenType.COMMA, TokenType.IMMEDIATE, TokenType.RBRACKET}) { @Override
    	public ParserState transition(Token t) throws InvalidTokenException {
            switch (t.getType()) {
            	case LBRACKET : return RRM6;
            	default : throw new InvalidTokenException(expected[0]);
            }
        }
    },
    RRM6(false, new TokenType[]{TokenType.REGISTER, TokenType.COMMA, 
    		TokenType.IMMEDIATE, TokenType.RBRACKET}) { @Override
    	public ParserState transition(Token t) throws InvalidTokenException {
            switch (t.getType()) {
            	case REGISTER : return RRM7;
            	default : throw new InvalidTokenException(expected[0]);
            }
        }
    },
    RRM7(false, new TokenType[]{TokenType.COMMA, TokenType.IMMEDIATE, 
    		TokenType.RBRACKET}) { @Override
    	public ParserState transition(Token t) throws InvalidTokenException {
            switch (t.getType()) {
            	case RBRACKET : return RRM8;
            	case COMMA : return RRM9;
            	default : throw new InvalidTokenException(expected[0]);
            }
        }
    },
    RRM8(true, null) { @Override
    	public ParserState transition(Token t) throws UnexpectedTokenException {
            throw new UnexpectedTokenException();
        }
    },
    RRM9(false, new TokenType[]{TokenType.IMMEDIATE, TokenType.RBRACKET}) { @Override
    	public ParserState transition(Token t) throws InvalidTokenException {
            switch (t.getType()) {
            	case IMMEDIATE : return RRM10;
            	default : throw new InvalidTokenException(expected[0]);
            }
        }
    },
    RRM10(false, new TokenType[]{TokenType.RBRACKET}) { @Override
    	public ParserState transition(Token t) throws InvalidTokenException {
            switch (t.getType()) {
            	case RBRACKET : return RRM8;
            	default : throw new InvalidTokenException(expected[0]);
            }
        }
    },
    RISI1(false, new TokenType[]{TokenType.REGISTER, TokenType.COMMA, TokenType.IMMEDIATE, 
    		TokenType.COMMA, TokenType.MNEMONIC_RRI, TokenType.IMMEDIATE}) { @Override
    	public ParserState transition(Token t) throws InvalidTokenException {
            switch (t.getType()) {
            	case REGISTER : return RISI2;
            	default : throw new InvalidTokenException(expected[0]);
            }
        }
    },
    RISI2(false, new TokenType[]{TokenType.COMMA, TokenType.IMMEDIATE, 
    		TokenType.COMMA, TokenType.MNEMONIC_RRI, TokenType.IMMEDIATE}) { @Override
    	public ParserState transition(Token t) throws InvalidTokenException {
            switch (t.getType()) {
            	case COMMA : return RISI3;
            	default : throw new InvalidTokenException(expected[0]);
            }
        }
    },
    RISI3(false, new TokenType[]{TokenType.IMMEDIATE, TokenType.COMMA, 
    		TokenType.MNEMONIC_RRI, TokenType.IMMEDIATE}) { @Override
    	public ParserState transition(Token t) throws InvalidTokenException {
            switch (t.getType()) {
            	case IMMEDIATE : return RISI4;
            	default : throw new InvalidTokenException(expected[0]);
            }
        }
    },
    RISI4(true, new TokenType[]{TokenType.COMMA, TokenType.MNEMONIC_RRI, TokenType.IMMEDIATE}) { @Override
    	public ParserState transition(Token t) throws InvalidTokenException {
            switch (t.getType()) {
            	case COMMA : return RISI5;
            	default : throw new InvalidTokenException(expected[0]);
            }
        }
    },
    RISI5(false, new TokenType[]{TokenType.MNEMONIC_RRI, TokenType.IMMEDIATE}) { @Override
    	public ParserState transition(Token t) throws InvalidTokenException {
            switch (t.getType()) {
            	case MNEMONIC_RRI : 
            		if (t.getData().trim().equalsIgnoreCase("lsl") && !t.getData().trim().equals(t.getData())) {
            			return RISI6;
            		} else {
            			throw new InvalidTokenException(expected[0], Mnemonic.LSL);
            		}
            	default : throw new InvalidTokenException(expected[0], Mnemonic.LSL);
            }
        }
    },
    RISI6(false, new TokenType[]{TokenType.IMMEDIATE}) { @Override
    	public ParserState transition(Token t) throws InvalidTokenException {
            switch (t.getType()) {
            	case IMMEDIATE : return RISI7;
            	default : throw new InvalidTokenException(expected[0]);
            }
        }
    },
    RISI7(true, null) { @Override
    	public ParserState transition(Token t) throws UnexpectedTokenException {
            throw new UnexpectedTokenException();
        }
    },
    L1(false, new TokenType[]{TokenType.IDENTIFIER}) { @Override
    	public ParserState transition(Token t) throws InvalidTokenException {
            switch (t.getType()) {
            	case IDENTIFIER : return L2;
            	default : throw new InvalidTokenException(expected[0]);
            }
        }
    },
    L2(true, null) { @Override
    	public ParserState transition(Token t) throws UnexpectedTokenException {
            throw new UnexpectedTokenException();
        }
    },
    L3(true, null) { @Override
    	public ParserState transition(Token t) { return L3; }
    },
    RL1(false, new TokenType[]{TokenType.REGISTER, TokenType.COMMA, 
    		TokenType.IDENTIFIER}) { @Override
    	public ParserState transition(Token t) throws InvalidTokenException {
            switch (t.getType()) {
            	case REGISTER : return RL2;
            	default : throw new InvalidTokenException(expected[0]);
            }
        }
    },
    RL2(false, new TokenType[]{TokenType.COMMA, TokenType.IDENTIFIER}) { @Override
    	public ParserState transition(Token t) throws InvalidTokenException {
            switch (t.getType()) {
            	case COMMA : return RL3;
            	default : throw new InvalidTokenException(expected[0]);
            }
        }
    },
    RL3(false, new TokenType[]{TokenType.IDENTIFIER}) { @Override
    	public ParserState transition(Token t) throws InvalidTokenException {
            switch (t.getType()) {
            	case IDENTIFIER : return RL4;
            	default : throw new InvalidTokenException(expected[0]);
            }
        }
    },
    RL4(true, null) { @Override
    	public ParserState transition(Token t) throws UnexpectedTokenException {
            throw new UnexpectedTokenException();
        }
    };
    
	private ParserState(boolean accepting, TokenType[] expected) {
		this.expected = expected;
		this.accepting = accepting;
	}
	
	/**
	 * The transition function for this parser state
	 * 
	 * @param next	the next token read by the parser
	 * @return		the next parser state - the result of the transition function
	 * 
	 * @throws UnsupportedInstructionException
	 * @throws InvalidTokenException
	 * @throws UnexpectedTokenException
	 */
    public abstract ParserState transition(Token next) throws UnsupportedInstructionException, 
    							InvalidTokenException, UnexpectedTokenException;
    
    /**
     * Whether the this state of the parser FSM is accepting
     */
    public final boolean accepting;
    
    /**
     * The token sequence required for the parser to reach an accepting state from this state 
     */
    public final TokenType[] expected;
    
}
