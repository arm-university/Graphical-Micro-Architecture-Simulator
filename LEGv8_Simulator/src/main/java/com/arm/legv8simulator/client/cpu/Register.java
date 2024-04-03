package com.arm.legv8simulator.client.cpu;

import com.arm.legv8simulator.client.memory.Memory;

public final class Register {
	
	private final RegisterType type; // maybe useless
	private final int size;
	private long content;
	
	public Register(RegisterType type) {
		
		this.type = type;
		
		switch(type) {
		case X:
		case D:		this.size = Memory.DOUBLEWORD_SIZE;
					break;
		case S:		this.size = Memory.WORD_SIZE;
					break;
		default:	this.size = Memory.DOUBLEWORD_SIZE;
		}
		
		content = 0L;		// S registers are just the lower part of the D registers
	}
	
	public void writeDoubleWord(long bits) {
		this.content = bits;
	}
	
	public long readDoubleWord() {
		return this.content;
	}
	
	public void writeWord(int bits) {
		this.content = (this.content & 0xffffffff00000000L) | (bits & 0x00000000ffffffffL);
	}
	
	public int readWord() {
		return (int) this.content;
	}

	public RegisterType getType() {
		return type;
	}

	public int getSize() {
		return size;
	}


}
