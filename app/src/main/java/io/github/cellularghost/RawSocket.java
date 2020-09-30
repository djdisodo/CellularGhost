package io.github.cellularghost;

public class RawSocket implements AutoCloseable{
	private long self; //raw self pointer of rust struct

	public RawSocket() {
		init();
	}

	private native void init();

	public native void close();

	public native void write(byte[] buffer, int start, int len);

	public native int read(byte[] buffer, int start);
}
