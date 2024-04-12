package com.arm.legv8simulator.client.memory;

import java.util.HashMap;

/**
 * <code>Memory</code> is the class used to implement the virtual address space of a LEGv8 program.
 * <p>
 * <code>Memory</code> is byte addressable and data is stored big-endian.
 * <p>
 * Segment sizes and offsets are as specified in Patterson and Hennessy ARM Edition.  
 * Segment boundaries are strictly enforced with rogue accesses resulting in <code>SegmentFaultException</code>s.
 * <p>
 * All load and store operations return and require 8-byte <code>long</code>s respectively. This is to mimic the 
 * use of 64-bit registers.
 *  
 * @author Jonathan Wright, 2016
 *
 */

/* When data is stored in memory, it is put into the hashmap, keyed by its address.
 * Values at addresses not in the hashmap are assumed to be 0.
 */
public class Memory {
	
	// the stack base has been made quadword aligned, whereas the Patterson specifies an 0x7ffffffffc address
	public static final long	STACK_BASE = 0x7ffffffffcL + 0x4L;
	public static final long	DYNAMIC_DATA_SEGMENT_OFFSET = 0x10000000L;
	public static final long	TEXT_SEGMENT_OFFSET = 0x400000L;
	public static final int		BYTE_SIZE = 1;
	public static final int		HALFWORD_SIZE = BYTE_SIZE * 2;
	public static final int		WORD_SIZE = HALFWORD_SIZE * 2;
	public static final int		DOUBLEWORD_SIZE = WORD_SIZE * 2;
	public static final int		BITS_IN_BYTE = 8;
	
	private ByteBuffer buffer;
	private final long staticDataSegmentOffset;
	// Java data structures cannot contain more than Integer.MAX_VALUE amount of elements, this means that in theory one cannot use this program to truly
	// simulate the entire address space indicated by the Patterson as it would require more than 2^32 addresses, i.e. entries into the HashMap or ArrayList
	private HashMap<Long, Byte> memory;					
	
	/**
	 * Memory constructor with a specified number of instructions.
	 * @param numInstructions	the number of instructions in the LEGv8 program being compiled/executed.
	 */
	public Memory(int numInstructions) {
		staticDataSegmentOffset = TEXT_SEGMENT_OFFSET + numInstructions * WORD_SIZE;
		memory = new HashMap<Long, Byte>();
		buffer = new ByteBuffer(DOUBLEWORD_SIZE);
	}

	/**
	 * @param address	the address from which to retrieve data.
	 * @return			the doubleword stored at <code>address</code>
	 * @throws SegmentFaultException
	 */
	public long loadDoubleword(long address) throws SegmentFaultException {
		boundsCheck(address, DOUBLEWORD_SIZE);
		for (int i=0; i<DOUBLEWORD_SIZE; i++) {
			Byte b = memory.get(address+i);
			if (b == null) {
				buffer.putByte(i, (byte) 0);
			} else {
				buffer.putByte(i, b);
			}
		}
		return buffer.getDoubleWord(0);
	}
	
	/**
	 * @param address	the address to store data.
	 * @param value		the data to be stored at <code>address</code>.
	 * @throws SegmentFaultException
	 */
	public void storeDoubleword(long address, long value) throws SegmentFaultException {
		boundsCheck(address, DOUBLEWORD_SIZE);
		buffer.putDoubleWord(0, value);
		for (int i=0; i<DOUBLEWORD_SIZE; i++) {
			memory.put(address+i, buffer.getByte(i));
		}
	}
	
	/**
	 * @param address	the address from which to retrieve data.
	 * @return			the signed word stored at <code>address</code>
	 * @throws SegmentFaultException
	 */
	public int loadSignedWord(long address) throws SegmentFaultException {
		boundsCheck(address, WORD_SIZE);
		for (int i=0; i<WORD_SIZE; i++) {
			Byte b = memory.get(address+i);
			if (b == null) {
				buffer.putByte(i+WORD_SIZE, (byte) 0);
			} else {
				buffer.putByte(i+WORD_SIZE, b);
			}
		}
		// sign extend;
		if (buffer.getWord(4) < 0) {
			return (int) (buffer.getDoubleWord(0) | 0xffffffff00000000L);
		} else {
			return (int) (buffer.getDoubleWord(0) & 0x00000000ffffffffL);
		}
	}
	
	/**
	 * @param address	the address to store data.
	 * @param value		the data to be stored at <code>address</code>.
	 * @throws SegmentFaultException
	 */
	public void storeWord(long address, long value) throws SegmentFaultException {
		boundsCheck(address, WORD_SIZE);
		buffer.putDoubleWord(0, value);
		for (int i=0; i<WORD_SIZE; i++) {
			memory.put(address+i, buffer.getByte(i+WORD_SIZE));
		}
	}
	
	/**
	 * @param address	the address from which to retrieve data.
	 * @return			the unsigned halfword stored at <code>address</code>
	 * @throws SegmentFaultException
	 */
	public long loadHalfword(long address) throws SegmentFaultException {
		boundsCheck(address, HALFWORD_SIZE);
		for (int i=0; i<DOUBLEWORD_SIZE-HALFWORD_SIZE; i++) {
			buffer.putByte(i, (byte) 0);
		}
		for (int i=0; i<HALFWORD_SIZE; i++) {
			Byte b = memory.get(address+i);
			if (b == null) {
				buffer.putByte(i+DOUBLEWORD_SIZE-HALFWORD_SIZE, (byte) 0);
			} else {
				buffer.putByte(i+DOUBLEWORD_SIZE-HALFWORD_SIZE, b);
			}
		}
		return buffer.getDoubleWord(0);
	}
	
	/**
	 * @param address	the address to store data.
	 * @param value		the data to be stored at <code>address</code>.
	 * @throws SegmentFaultException
	 */
	public void storeHalfword(long address, long value) throws SegmentFaultException {
		boundsCheck(address, HALFWORD_SIZE);
		buffer.putDoubleWord(0, value);
		memory.put(address, buffer.getByte(DOUBLEWORD_SIZE-HALFWORD_SIZE));
		memory.put(address+1, buffer.getByte(DOUBLEWORD_SIZE-BYTE_SIZE));
	}
	
	/**
	 * @param address	the address from which to retrieve data.
	 * @return			the unsigned byte stored at <code>address</code>
	 * @throws SegmentFaultException
	 */
	public long loadByte(long address) throws SegmentFaultException {
		boundsCheck(address, BYTE_SIZE);
		for (int i=0; i<DOUBLEWORD_SIZE-BYTE_SIZE; i++) {
			buffer.putByte(i, (byte) 0);
		}
		Byte b = memory.get(address);
		if (b == null) {
			buffer.putByte(DOUBLEWORD_SIZE-BYTE_SIZE, (byte) 0);
		} else {
			buffer.putByte(DOUBLEWORD_SIZE-BYTE_SIZE, b);
		}
		return buffer.getDoubleWord(0);
	}
	
	/**
	 * @param address	the address to store data.
	 * @param value		the data to be stored at <code>address</code>.
	 * @throws SegmentFaultException
	 */
	public void storeByte(long address, long value) throws SegmentFaultException {
		boundsCheck(address, BYTE_SIZE);
		buffer.putDoubleWord(0, value);
		memory.put(address, buffer.getByte(DOUBLEWORD_SIZE-BYTE_SIZE));
	}
	
	/* Checks to make sure the memory access is within the stack or heap segments
	 * 
	 * @param address		the address from which to store or load data.
	 * @param figureSize	the number of bytes being loaded or stored.
	 * @throws SegmentFaultException
	 */
	private void boundsCheck(long address, int figureSize) throws SegmentFaultException {
		if (address > STACK_BASE-figureSize || address < DYNAMIC_DATA_SEGMENT_OFFSET) {
			throw new SegmentFaultException(address, "stack or heap");
		}
	}
	
	/**
	 * @return the offset of the static data segment - calculated as the text segment offset 
	 * added to the size of the text segment
	 */
	public long getStaticDataSegmentOffset() {
		return staticDataSegmentOffset;
	}
	
}
