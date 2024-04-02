package com.arm.legv8simulator.client.memory;

/**
 * <code>ByteBuffer</code> is a byte buffer of client specified length, used to store data in 
 * big-endian format. Data - <code>bytes</code>, <code>ints</code> and <code>longs</code> - 
 * can be read and written from arbitrary locations within the buffer. No bounds checking is 
 * performed so attempting to read or write from an index outside the buffer will result in 
 * a <code>NullPointerException</code>.   
 * <p>
 * This class has been written because java.nio.ByteBuffer is not supported by GWT.
 * 
 * @author Jonathan Wright, 2016
 */

public class ByteBuffer {
	
	/**
	 * @param capacity	the number of bytes in this ByteBuffer 
	 */
	public ByteBuffer(int capacity) {
		buffer = new byte[capacity];
	}
	
	/**
	 * @param index	the location to insert data in this ByteBuffer
	 * @param value	the byte to be inserted into the ByteBuffer  
	 */
	public void put(int index, byte value) {
		buffer[index] = value;
	}
	
	/**
	 * @param index	the location to retrieve data
	 * @return		the byte at <code>index</code> in this ByteBuffer
	 */
	public byte get(int index) {
		return buffer[index];
	}
	
	/**
	 * Data is inserted in big-endian format
	 * 
	 * @param index	the location to insert data in this ByteBuffer
	 * @param value the four bytes to be inserted into the ByteBuffer 
	 */
	public void putInt(int index, int value) {
		for (int i=index; i<Memory.WORD_SIZE; i++) {
			buffer[i] = (byte) (value >>> (Memory.WORD_SIZE-1-i)*Memory.BITS_IN_BYTE);
		}
	}
	
	/**
	 * Data in the ByteBuffer is interpreted in big-endian format
	 * 
	 * @param index the location to retrieve data
	 * @return		the <code>int</code> formed by concatenating the four bytes starting at <code>index</code> in this ByteBuffer
	 */
	public int getInt(int index) {
		int result = 0;
		for (int i=index; i<Memory.WORD_SIZE; i++) {
			result = result << Memory.BITS_IN_BYTE;
			result = result | (buffer[i] & 0x000000ff);
		}
		return result;
	}
	
	/**
	 * Data is inserted in big-endian format
	 * 
	 * @param index	the location to insert data in this ByteBuffer
	 * @param value	the eight bytes to be inserted into the ByteBuffer
	 */
	public void putLong(int index, long value) {
		for (int i=index; i<Memory.DOUBLEWORD_SIZE; i++) {
			buffer[i] = (byte) (value >>> (Memory.DOUBLEWORD_SIZE-1-i)*Memory.BITS_IN_BYTE);
		}
	}
	
	/**
	 * Data in the ByteBuffer is interpreted in big-endian format
	 * 
	 * @param index	the location to retrieve data
	 * @return		the <code>long</code> formed by concatenating the eight bytes starting at <code>index</code> in this ByteBuffer
	 */
	public long getLong(int index) {
		long result = 0L;
		for (int i=index; i<Memory.DOUBLEWORD_SIZE; i++) {
			result = result << Memory.BITS_IN_BYTE;
			result = result | (buffer[i] & 0x00000000000000FFL);
		}
		return result;
	}
	
	private byte[] buffer;
}
