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
            	case XMNEMONIC_R : return XR1;
            	case XMNEMONIC_RR : return XRR1;
            	case SMNEMONIC_RR : return SRR1;
            	case DMNEMONIC_RR : return DRR1;
            	case XMNEMONIC_RRR : return XRRR1;
            	case SMNEMONIC_RRR : return SRRR1;
            	case DMNEMONIC_RRR : return DRRR1;
            	case XMNEMONIC_RI : return XRI1;
            	case XMNEMONIC_RRI : return XRRI1;
            	case XMNEMONIC_RM : return XRM1;
            	case SMNEMONIC_RM : return SRM1;
            	case DMNEMONIC_RM : return DRM1;
            	case XMNEMONIC_RRM : return XRRM1;
            	case XMNEMONIC_RISI : return XRISI1;
            	case MNEMONIC_L : return L1;
            	case XMNEMONIC_RL : return XRL1;
            	case LABEL : return G1;
            	default : throw new UnsupportedInstructionException(INIT);
            }
        }
    },
    G1(true, null) { @Override
    	public ParserState transition(Token t) throws UnsupportedInstructionException {
            switch (t.getType()) {
            	case XMNEMONIC_R : return XR1;
            	case XMNEMONIC_RR : return XRR1;
            	case SMNEMONIC_RR : return SRR1;
            	case DMNEMONIC_RR : return DRR1;
            	case XMNEMONIC_RRR : return XRRR1;
            	case SMNEMONIC_RRR : return SRRR1;
            	case DMNEMONIC_RRR : return DRRR1;
            	case XMNEMONIC_RI : return XRI1;
            	case XMNEMONIC_RRI : return XRRI1;
            	case XMNEMONIC_RM : return XRM1;
            	case SMNEMONIC_RM : return SRM1;
            	case DMNEMONIC_RM : return DRM1;
            	case XMNEMONIC_RRM : return XRRM1;
            	case XMNEMONIC_RISI : return XRISI1;
            	case MNEMONIC_L : return L1;
            	case XMNEMONIC_RL : return XRL1;
            	default : throw new UnsupportedInstructionException(G1);
            }
        }
    },
    XR1(false, new TokenType[]{TokenType.XREGISTER}) { @Override
    	public ParserState transition(Token t) throws InvalidTokenException {
            switch (t.getType()) {
            	case 		XREGISTER : return XR2;
            	default : throw new InvalidTokenException(expected[0]);
            }
        }
    },
    XR2(true, null) { @Override
    	public ParserState transition(Token t) throws UnexpectedTokenException {
            throw new UnexpectedTokenException();
        }
    },
    XRR1(false, new TokenType[]{TokenType.XREGISTER, TokenType.COMMA, 
    		TokenType.XREGISTER}) { @Override
    	public ParserState transition(Token t) throws InvalidTokenException {
            switch (t.getType()) {
            	case XREGISTER : return XRR2;
            	default : throw new InvalidTokenException(expected[0]);
            }
        }
    },
    XRR2(false, new TokenType[]{TokenType.COMMA, TokenType.XREGISTER}) { @Override
    	public ParserState transition(Token t) throws InvalidTokenException {
            switch (t.getType()) {	
            	case COMMA : return XRR3;
            	default : throw new InvalidTokenException(expected[0]);
            }
        }
    },
    XRR3(false, new TokenType[]{TokenType.XREGISTER}) { @Override
    	public ParserState transition(Token t) throws InvalidTokenException {
            switch (t.getType()) {
            	case XREGISTER : return XRR4;
            	default : throw new InvalidTokenException(expected[0]);
            }
        }
    },
    XRR4(true, null) { @Override
    	public ParserState transition(Token t) throws UnexpectedTokenException {
            throw new UnexpectedTokenException();
        }
    },
    SRR1(false, new TokenType[]{TokenType.SREGISTER, TokenType.COMMA, 
    		TokenType.SREGISTER}) { @Override
    	public ParserState transition(Token t) throws InvalidTokenException {
            switch (t.getType()) {
            	case SREGISTER : return SRR2;
            	default : throw new InvalidTokenException(expected[0]);
            }
        }
    },
    SRR2(false, new TokenType[]{TokenType.COMMA, TokenType.SREGISTER}) { @Override
    	public ParserState transition(Token t) throws InvalidTokenException {
            switch (t.getType()) {	
            	case COMMA : return SRR3;
            	default : throw new InvalidTokenException(expected[0]);
            }
        }
    },
    SRR3(false, new TokenType[]{TokenType.SREGISTER}) { @Override
    	public ParserState transition(Token t) throws InvalidTokenException {
            switch (t.getType()) {
            	case SREGISTER : return SRR4;
            	default : throw new InvalidTokenException(expected[0]);
            }
        }
    },
    SRR4(true, null) { @Override
    	public ParserState transition(Token t) throws UnexpectedTokenException {
            throw new UnexpectedTokenException();
        }
    },
    DRR1(false, new TokenType[]{TokenType.DREGISTER, TokenType.COMMA, 
    		TokenType.DREGISTER}) { @Override
    	public ParserState transition(Token t) throws InvalidTokenException {
            switch (t.getType()) {
            	case DREGISTER : return DRR2;
            	default : throw new InvalidTokenException(expected[0]);
            }
        }
    },
    DRR2(false, new TokenType[]{TokenType.COMMA, TokenType.DREGISTER}) { @Override
    	public ParserState transition(Token t) throws InvalidTokenException {
            switch (t.getType()) {	
            	case COMMA : return DRR3;
            	default : throw new InvalidTokenException(expected[0]);
            }
        }
    },
    DRR3(false, new TokenType[]{TokenType.DREGISTER}) { @Override
    	public ParserState transition(Token t) throws InvalidTokenException {
            switch (t.getType()) {
            	case DREGISTER : return DRR4;
            	default : throw new InvalidTokenException(expected[0]);
            }
        }
    },
    DRR4(true, null) { @Override
    	public ParserState transition(Token t) throws UnexpectedTokenException {
            throw new UnexpectedTokenException();
        }
    },
    XRRR1(false, new TokenType[]{TokenType.XREGISTER, TokenType.COMMA, 
    		TokenType.XREGISTER, TokenType.COMMA, TokenType.XREGISTER}) { @Override
    	public ParserState transition(Token t) throws InvalidTokenException {
            switch (t.getType()) {
            	case XREGISTER : return XRRR2;
            	default : throw new InvalidTokenException(expected[0]);
            }
        }
    },
    XRRR2(false, new TokenType[]{TokenType.COMMA, TokenType.XREGISTER, 
    		TokenType.COMMA, TokenType.XREGISTER}) { @Override
    	public ParserState transition(Token t) throws InvalidTokenException {
            switch (t.getType()) {
            	case COMMA : return XRRR3;
            	default : throw new InvalidTokenException(expected[0]);
            }
        }
    },
    XRRR3(false, new TokenType[]{TokenType.XREGISTER, TokenType.COMMA, 
    		TokenType.XREGISTER}) { @Override
    	public ParserState transition(Token t) throws InvalidTokenException {
            switch (t.getType()) {
            	case XREGISTER : return XRRR4;
            	default : throw new InvalidTokenException(expected[0]);
            }
        }
    },
    XRRR4(false, new TokenType[]{TokenType.COMMA, TokenType.XREGISTER}) { @Override
    	public ParserState transition(Token t) throws InvalidTokenException {
            switch (t.getType()) {
            	case COMMA : return XRRR5;
            	default : throw new InvalidTokenException(expected[0]);
            }
        }
    },
    XRRR5(false, new TokenType[]{TokenType.XREGISTER}) { @Override
    	public ParserState transition(Token t) throws InvalidTokenException {
            switch (t.getType()) {
            	case XREGISTER : return RRR6;
            	default : throw new InvalidTokenException(expected[0]);
            }
        }
    },
    RRR6(true, null) { @Override
    	public ParserState transition(Token t) throws UnexpectedTokenException {
            throw new UnexpectedTokenException();
        }
    },
    SRRR1(false, new TokenType[]{TokenType.SREGISTER, TokenType.COMMA, 
    		TokenType.SREGISTER, TokenType.COMMA, TokenType.SREGISTER}) { @Override
    	public ParserState transition(Token t) throws InvalidTokenException {
            switch (t.getType()) {
            	case SREGISTER : return SRRR2;
            	default : throw new InvalidTokenException(expected[0]);
            }
        }
    },
    SRRR2(false, new TokenType[]{TokenType.COMMA, TokenType.SREGISTER, 
    		TokenType.COMMA, TokenType.SREGISTER}) { @Override
    	public ParserState transition(Token t) throws InvalidTokenException {
            switch (t.getType()) {
            	case COMMA : return SRRR3;
            	default : throw new InvalidTokenException(expected[0]);
            }
        }
    },
    SRRR3(false, new TokenType[]{TokenType.SREGISTER, TokenType.COMMA, 
    		TokenType.SREGISTER}) { @Override
    	public ParserState transition(Token t) throws InvalidTokenException {
            switch (t.getType()) {
            	case SREGISTER : return SRRR4;
            	default : throw new InvalidTokenException(expected[0]);
            }
        }
    },
    SRRR4(false, new TokenType[]{TokenType.COMMA, TokenType.SREGISTER}) { @Override
    	public ParserState transition(Token t) throws InvalidTokenException {
            switch (t.getType()) {
            	case COMMA : return SRRR5;
            	default : throw new InvalidTokenException(expected[0]);
            }
        }
    },
    SRRR5(false, new TokenType[]{TokenType.SREGISTER}) { @Override
    	public ParserState transition(Token t) throws InvalidTokenException {
            switch (t.getType()) {
            	case SREGISTER : return RRR6;
            	default : throw new InvalidTokenException(expected[0]);
            }
        }
    },
    DRRR1(false, new TokenType[]{TokenType.DREGISTER, TokenType.COMMA, 
    		TokenType.DREGISTER, TokenType.COMMA, TokenType.DREGISTER}) { @Override
    	public ParserState transition(Token t) throws InvalidTokenException {
            switch (t.getType()) {
            	case DREGISTER : return DRRR2;
            	default : throw new InvalidTokenException(expected[0]);
            }
        }
    },
    DRRR2(false, new TokenType[]{TokenType.COMMA, TokenType.DREGISTER, 
    		TokenType.COMMA, TokenType.DREGISTER}) { @Override
    	public ParserState transition(Token t) throws InvalidTokenException {
            switch (t.getType()) {
            	case COMMA : return DRRR3;
            	default : throw new InvalidTokenException(expected[0]);
            }
        }
    },
    DRRR3(false, new TokenType[]{TokenType.DREGISTER, TokenType.COMMA, 
    		TokenType.DREGISTER}) { @Override
    	public ParserState transition(Token t) throws InvalidTokenException {
            switch (t.getType()) {
            	case DREGISTER : return DRRR4;
            	default : throw new InvalidTokenException(expected[0]);
            }
        }
    },
    DRRR4(false, new TokenType[]{TokenType.COMMA, TokenType.DREGISTER}) { @Override
    	public ParserState transition(Token t) throws InvalidTokenException {
            switch (t.getType()) {
            	case COMMA : return DRRR5;
            	default : throw new InvalidTokenException(expected[0]);
            }
        }
    },
    DRRR5(false, new TokenType[]{TokenType.DREGISTER}) { @Override
    	public ParserState transition(Token t) throws InvalidTokenException {
            switch (t.getType()) {
            	case DREGISTER : return RRR6;
            	default : throw new InvalidTokenException(expected[0]);
            }
        }
    },
    XRI1(false, new TokenType[]{TokenType.XREGISTER, TokenType.COMMA, 
    		TokenType.IMMEDIATE}) { @Override
    	public ParserState transition(Token t) throws InvalidTokenException {
            switch (t.getType()) {
            	case XREGISTER : return XRI2;
            	default : throw new InvalidTokenException(expected[0]);
            }
        }
    },
    XRI2(false, new TokenType[]{TokenType.COMMA, TokenType.IMMEDIATE}) { @Override
    	public ParserState transition(Token t) throws InvalidTokenException {
            switch (t.getType()) {
            	case COMMA : return XRI3;
            	default : throw new InvalidTokenException(expected[0]);
            }
        }
    },
    XRI3(false, new TokenType[]{TokenType.IMMEDIATE}) { @Override
    	public ParserState transition(Token t) throws InvalidTokenException {
            switch (t.getType()) {
            	case IMMEDIATE : return XRI4;
            	default : throw new InvalidTokenException(expected[0]);
            }
        }
    },
    XRI4(true, null) { @Override
    	public ParserState transition(Token t) throws UnexpectedTokenException {
            throw new UnexpectedTokenException();
        }
    },
    XRRI1(false, new TokenType[]{TokenType.XREGISTER, TokenType.COMMA, 
    		TokenType.XREGISTER, TokenType.COMMA, TokenType.IMMEDIATE}) { @Override
    	public ParserState transition(Token t) throws InvalidTokenException {
            switch (t.getType()) {
            	case XREGISTER : return XRRI2;
            	default : throw new InvalidTokenException(expected[0]);
            }
        }
    },
    XRRI2(false, new TokenType[]{TokenType.COMMA, TokenType.XREGISTER, 
    		TokenType.COMMA, TokenType.IMMEDIATE}) { @Override
    	public ParserState transition(Token t) throws InvalidTokenException {
            switch (t.getType()) {
            	case COMMA : return XRRI3;
            	default : throw new InvalidTokenException(expected[0]);
            }
        }
    },
    XRRI3(false, new TokenType[]{TokenType.XREGISTER, TokenType.COMMA, 
    		TokenType.IMMEDIATE}) { @Override
    	public ParserState transition(Token t) throws InvalidTokenException {
            switch (t.getType()) {
            	case XREGISTER : return XRRI4;
            	default : throw new InvalidTokenException(expected[0]);
            }
        }
    },
    XRRI4(false, new TokenType[]{TokenType.COMMA, TokenType.IMMEDIATE}) { @Override
    	public ParserState transition(Token t) throws InvalidTokenException {
            switch (t.getType()) {
            	case COMMA : return XRRI5;
            	default : throw new InvalidTokenException(expected[0]);
            }
        }
    },
    XRRI5(false, new TokenType[]{TokenType.IMMEDIATE}) { @Override
    	public ParserState transition(Token t) throws InvalidTokenException {
            switch (t.getType()) {
            	case IMMEDIATE : return XRRI6;
            	default : throw new InvalidTokenException(expected[0]);
            }
        }
    },
    XRRI6(true, null) { @Override
    	public ParserState transition(Token t) throws UnexpectedTokenException {
            throw new UnexpectedTokenException();
        }
    },
    XRM1(false, new TokenType[]{TokenType.XREGISTER, TokenType.COMMA, 
    		TokenType.LBRACKET, TokenType.XREGISTER, TokenType.COMMA, 
    		TokenType.IMMEDIATE, TokenType.RBRACKET}) { @Override
    	public ParserState transition(Token t) throws InvalidTokenException {
            switch (t.getType()) {
            	case XREGISTER : return RM2;
            	default : throw new InvalidTokenException(expected[0]);
            }
        }
    },
    SRM1(false, new TokenType[]{TokenType.SREGISTER, TokenType.COMMA, 
    		TokenType.LBRACKET, TokenType.XREGISTER, TokenType.COMMA, 
    		TokenType.IMMEDIATE, TokenType.RBRACKET}) { @Override
    	public ParserState transition(Token t) throws InvalidTokenException {
            switch (t.getType()) {
            	case SREGISTER : return RM2;
            	default : throw new InvalidTokenException(expected[0]);
            }
        }
    },
    DRM1(false, new TokenType[]{TokenType.DREGISTER, TokenType.COMMA, 
    		TokenType.LBRACKET, TokenType.XREGISTER, TokenType.COMMA, 
    		TokenType.IMMEDIATE, TokenType.RBRACKET}) { @Override
    	public ParserState transition(Token t) throws InvalidTokenException {
            switch (t.getType()) {
            	case DREGISTER : return RM2;
            	default : throw new InvalidTokenException(expected[0]);
            }
        }
    },
    RM2(false, new TokenType[]{TokenType.COMMA, TokenType.LBRACKET, TokenType.XREGISTER, 
    		TokenType.COMMA, TokenType.IMMEDIATE, TokenType.RBRACKET}) { @Override
    	public ParserState transition(Token t) throws InvalidTokenException {
            switch (t.getType()) {
            	case COMMA : return RM3;
            	default : throw new InvalidTokenException(expected[0]);
            }
        }
    },
    RM3(false, new TokenType[]{TokenType.LBRACKET, TokenType.XREGISTER, 
    		TokenType.COMMA, TokenType.IMMEDIATE, TokenType.RBRACKET}) { @Override
    	public ParserState transition(Token t) throws InvalidTokenException {
            switch (t.getType()) {
            	case LBRACKET : return RM4;
            	default : throw new InvalidTokenException(expected[0]);
            }
        }
    },
    RM4(false, new TokenType[]{TokenType.XREGISTER, TokenType.COMMA, 
    		TokenType.IMMEDIATE, TokenType.RBRACKET}) { @Override
    	public ParserState transition(Token t) throws InvalidTokenException {
            switch (t.getType()) {
            	case XREGISTER : return RM5;
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
    XRRM1(false, new TokenType[]{TokenType.XREGISTER, TokenType.COMMA, TokenType.XREGISTER, 
    		TokenType.COMMA, TokenType.LBRACKET, TokenType.XREGISTER, TokenType.COMMA, 
    		TokenType.IMMEDIATE, TokenType.RBRACKET}) { @Override
    	public ParserState transition(Token t) throws InvalidTokenException {
            switch (t.getType()) {
            	case XREGISTER : return XRRM2;
            	default : throw new InvalidTokenException(expected[0]);
            }
        }
    },
    XRRM2(false, new TokenType[]{TokenType.COMMA, TokenType.XREGISTER, 
    		TokenType.COMMA, TokenType.LBRACKET, TokenType.XREGISTER, 
    		TokenType.COMMA, TokenType.IMMEDIATE, TokenType.RBRACKET}) { @Override
    	public ParserState transition(Token t) throws InvalidTokenException {
            switch (t.getType()) {
            	case COMMA : return XRRM3;
            	default : throw new InvalidTokenException(expected[0]);
            }
        }
    },
    XRRM3(false, new TokenType[]{TokenType.XREGISTER, TokenType.COMMA, 
    		TokenType.LBRACKET, TokenType.XREGISTER, TokenType.COMMA, 
    		TokenType.IMMEDIATE, TokenType.RBRACKET}) { @Override
    	public ParserState transition(Token t) throws InvalidTokenException {
            switch (t.getType()) {
            	case XREGISTER : return XRRM4;
            	default : throw new InvalidTokenException(expected[0]);
            }
        }
    },
    XRRM4(false, new TokenType[]{TokenType.COMMA, TokenType.LBRACKET, TokenType.XREGISTER, 
    		TokenType.COMMA, TokenType.IMMEDIATE, TokenType.RBRACKET}) { @Override
    	public ParserState transition(Token t) throws InvalidTokenException {
            switch (t.getType()) {
            	case COMMA : return XRRM5;
            	default : throw new InvalidTokenException(expected[0]);
            }
        }
    },
    XRRM5(false, new TokenType[]{TokenType.LBRACKET, TokenType.XREGISTER, 
    		TokenType.COMMA, TokenType.IMMEDIATE, TokenType.RBRACKET}) { @Override
    	public ParserState transition(Token t) throws InvalidTokenException {
            switch (t.getType()) {
            	case LBRACKET : return XRRM6;
            	default : throw new InvalidTokenException(expected[0]);
            }
        }
    },
    XRRM6(false, new TokenType[]{TokenType.XREGISTER, TokenType.COMMA, 
    		TokenType.IMMEDIATE, TokenType.RBRACKET}) { @Override
    	public ParserState transition(Token t) throws InvalidTokenException {
            switch (t.getType()) {
            	case XREGISTER : return XRRM7;
            	default : throw new InvalidTokenException(expected[0]);
            }
        }
    },
    XRRM7(false, new TokenType[]{TokenType.COMMA, TokenType.IMMEDIATE, 
    		TokenType.RBRACKET}) { @Override
    	public ParserState transition(Token t) throws InvalidTokenException {
            switch (t.getType()) {
            	case RBRACKET : return XRRM8;
            	case COMMA : return XRRM9;
            	default : throw new InvalidTokenException(expected[0]);
            }
        }
    },
    XRRM8(true, null) { @Override
    	public ParserState transition(Token t) throws UnexpectedTokenException {
            throw new UnexpectedTokenException();
        }
    },
    XRRM9(false, new TokenType[]{TokenType.IMMEDIATE, TokenType.RBRACKET}) { @Override
    	public ParserState transition(Token t) throws InvalidTokenException {
            switch (t.getType()) {
            	case IMMEDIATE : return XRRM10;
            	default : throw new InvalidTokenException(expected[0]);
            }
        }
    },
    XRRM10(false, new TokenType[]{TokenType.RBRACKET}) { @Override
    	public ParserState transition(Token t) throws InvalidTokenException {
            switch (t.getType()) {
            	case RBRACKET : return XRRM8;
            	default : throw new InvalidTokenException(expected[0]);
            }
        }
    },
    XRISI1(false, new TokenType[]{TokenType.XREGISTER, TokenType.COMMA, TokenType.IMMEDIATE, 
    		TokenType.COMMA, TokenType.XMNEMONIC_RRI, TokenType.IMMEDIATE}) { @Override
    	public ParserState transition(Token t) throws InvalidTokenException {
            switch (t.getType()) {
            	case XREGISTER : return XRISI2;
            	default : throw new InvalidTokenException(expected[0]);
            }
        }
    },
    XRISI2(false, new TokenType[]{TokenType.COMMA, TokenType.IMMEDIATE, 
    		TokenType.COMMA, TokenType.XMNEMONIC_RRI, TokenType.IMMEDIATE}) { @Override
    	public ParserState transition(Token t) throws InvalidTokenException {
            switch (t.getType()) {
            	case COMMA : return XRISI3;
            	default : throw new InvalidTokenException(expected[0]);
            }
        }
    },
    XRISI3(false, new TokenType[]{TokenType.IMMEDIATE, TokenType.COMMA, 
    		TokenType.XMNEMONIC_RRI, TokenType.IMMEDIATE}) { @Override
    	public ParserState transition(Token t) throws InvalidTokenException {
            switch (t.getType()) {
            	case IMMEDIATE : return XRISI4;
            	default : throw new InvalidTokenException(expected[0]);
            }
        }
    },
    XRISI4(true, new TokenType[]{TokenType.COMMA, TokenType.XMNEMONIC_RRI, TokenType.IMMEDIATE}) { @Override
    	public ParserState transition(Token t) throws InvalidTokenException {
            switch (t.getType()) {
            	case COMMA : return XRISI5;
            	default : throw new InvalidTokenException(expected[0]);
            }
        }
    },
   XRISI5(false, new TokenType[]{TokenType.XMNEMONIC_RRI, TokenType.IMMEDIATE}) { @Override
    	public ParserState transition(Token t) throws InvalidTokenException {
            switch (t.getType()) {
            	case XMNEMONIC_RRI : 
            		if (t.getData().trim().equalsIgnoreCase("lsl") && !t.getData().trim().equals(t.getData())) {
            			return XRISI6;
            		} else {
            			throw new InvalidTokenException(expected[0], Mnemonic.LSL);
            		}
            	default : throw new InvalidTokenException(expected[0], Mnemonic.LSL);
            }
        }
    },
    XRISI6(false, new TokenType[]{TokenType.IMMEDIATE}) { @Override
    	public ParserState transition(Token t) throws InvalidTokenException {
            switch (t.getType()) {
            	case IMMEDIATE : return XRISI7;
            	default : throw new InvalidTokenException(expected[0]);
            }
        }
    },
    XRISI7(true, null) { @Override
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
    /*L3(true, null) { @Override											// THIS SEEMS TO BE AN UNREACHABLE LOOP?	
    	public ParserState transition(Token t) { return L3; }
    },*/
    XRL1(false, new TokenType[]{TokenType.XREGISTER, TokenType.COMMA, 
    		TokenType.IDENTIFIER}) { @Override
    	public ParserState transition(Token t) throws InvalidTokenException {
            switch (t.getType()) {
            	case XREGISTER : return XRL2;
            	default : throw new InvalidTokenException(expected[0]);
            }
        }
    },
    XRL2(false, new TokenType[]{TokenType.COMMA, TokenType.IDENTIFIER}) { @Override
    	public ParserState transition(Token t) throws InvalidTokenException {
            switch (t.getType()) {
            	case COMMA : return XRL3;
            	default : throw new InvalidTokenException(expected[0]);
            }
        }
    },
    XRL3(false, new TokenType[]{TokenType.IDENTIFIER}) { @Override
    	public ParserState transition(Token t) throws InvalidTokenException {
            switch (t.getType()) {
            	case IDENTIFIER : return XRL4;
            	default : throw new InvalidTokenException(expected[0]);
            }
        }
    },
    XRL4(true, null) { @Override
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
