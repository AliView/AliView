/*
 * The MIT License
 *
 * Copyright 2013 Joos Kiener <Joos.Kiener@gmail.com>.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.bitbucket.kienerj.io;

import java.io.EOFException;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UTFDataFormatException;

/**
 * <p> Wrapper of {@link java.io.RandomAccessFile
 * <code>RandomAccessFile</code>} that has an readLine method performing similar
 * to {@link java.io.BufferedReader#readLine()} while keeping the random access
 * functionality. </p>
 *
 * <p> {@link java.io.RandomAccessFile#readLine()} is very slow as it reads a
 * file byte by byte. This here will perform 2 orders of magnitude faster. 
 * For thread-safety all read and write methods are synchronized. </p>
 * </p>
 * If the underlying {@link java.nio.channels.FileChannel
 * <code>FileChannel</code>} is manipulated, the behavior is unpredictable. 
 * Therefore this class does not expose the <code>getFileChannel()</code> method
 * of <code>RandomAccessFile</code>. </p>
 *
 * @author Joos Kiener <Joos.Kiener@gmail.com>
 */
public class OptimizedRandomAccessFile {

    private static final int BUFFER_SIZE = 8192;
    private static int defaultExpectedLineLength = 80;
    private RandomAccessFile raf;
    private Long actualFilePointer;
    private char[] charBuffer;
    private int nChars, nextChar;
    private int bufferSize;
    private long lastOffset;
    private boolean skipLF;

    /**
     * see {@link RandomAccessFile#RandomAccessFile(String,String)}
     *
     * @param name path to the text file
     * @param mode r, rw, rws, rwd
     * @throws FileNotFoundException
     */
    public OptimizedRandomAccessFile(String name, String mode)
            throws FileNotFoundException {
        this(name != null ? new File(name) : null, mode);
    }

    /**
     *
     * see {@link RandomAccessFile#RandomAccessFile(File,String)}
     *
     * @param file
     * @param mode
     * @throws FileNotFoundException
     */
    public OptimizedRandomAccessFile(File file, String mode)
            throws FileNotFoundException {
        this.raf = new RandomAccessFile(file, mode);
        actualFilePointer = null;
        this.bufferSize = BUFFER_SIZE;
        charBuffer = new char[bufferSize];
    }

    /**
     * <p> Returns the opaque file descriptor object associated with this
     * stream. </p>
     *
     * @return the file descriptor object associated with this stream.
     * @exception IOException if an I/O error occurs.
     * @see java.io.FileDescriptor
     */
    public final FileDescriptor getFD() throws IOException {
        return raf.getFD();
    }

    /**
     * Reads a byte of data from this file. The byte is returned as an integer
     * in the range 0 to 255 (
     * <code>0x00-0x0ff</code>). This method behaves similar to the
     * {@link java.io.BufferedReader#read()} method of
     * <code>BufferedReader</code>.
     *
     * @return the next byte of data, or <code>-1</code> if the end of the file
     * has been reached.
     * @exception IOException if an I/O error occurs. Not thrown if end-of-file
     * has been reached.
     */
    public synchronized int read() throws IOException {
        //resetPosition();
        for (;;) {
            if (nextChar >= nChars) {
                fill();
                if (nextChar >= nChars) {
                    return -1;
                }
            }
            if (skipLF) {
                skipLF = false;
                if (charBuffer[nextChar] == '\n') {
                    nextChar++;
                    continue;
                }
            }
            int result = charBuffer[nextChar++];
            actualFilePointer++;
            return result;
        }
    }

    /**
     * Reads characters into a portion of an array, reading from the underlying
     * stream if necessary.
     */
    private int read1(byte[] cbuf, int off, int len) throws IOException {
        if (nextChar >= nChars) {
            /* If the requested length is at least as large as the buffer and
             if line feeds are not being skipped, do not bother to copy the
             characters into the local buffer.  In this way buffered streams
             will cascade harmlessly. */
            if (len >= charBuffer.length && !skipLF) {
                actualFilePointer = null;
                return raf.read(cbuf, off, len);
            }
            fill();
        }
        if (nextChar >= nChars) {
            return -1;
        }
        if (skipLF) {
            skipLF = false;
            if (charBuffer[nextChar] == '\n') {
                nextChar++;
                if (nextChar >= nChars) {
                    fill();
                }
                if (nextChar >= nChars) {
                    return -1;
                }
            }
        }
        int n = Math.min(len, nChars - nextChar);
        for (int i = 0; i < n; i++) {
            cbuf[off + i] = (byte) charBuffer[nextChar + i];
        }
        //System.arraycopy(charBuffer, nextChar, cbuf, off, n);
        nextChar += n;
        actualFilePointer += n;
        return n;
    }

    /**
     * Reads up to
     * <code>len</code> bytes of data from this file into an array of bytes.
     * This method behaves similar to the
     * {@link java.io.BufferedReader#read(byte[],int,int)} method of
     * <code>BufferedReader</code>.
     *
     * @param b the buffer into which the data is read.
     * @param off the start offset in array <code>b</code> at which the data is
     * written.
     * @param len the maximum number of bytes read.
     * @return the total number of bytes read into the buffer, or
     * <code>-1</code> if there is no more data because the end of the file has
     * been reached.
     * @exception IOException If the first byte cannot be read for any reason
     * other than end of file, or if the random access file has been closed, or
     * if some other I/O error occurs.
     * @exception NullPointerException If <code>b</code> is <code>null</code>.
     * @exception IndexOutOfBoundsException If <code>off</code> is negative,
     * <code>len</code> is negative, or <code>len</code> is greater than
     * <code>b.length - off</code>
     */
    public synchronized int read(byte b[], int off, int len) throws IOException {
        //resetPosition();
        if ((off < 0) || (off > b.length) || (len < 0)
                || ((off + len) > b.length) || ((off + len) < 0)) {
            throw new IndexOutOfBoundsException();
        } else if (len == 0) {
            return 0;
        }

        int n = read1(b, off, len);
        if (n <= 0) {
            return n;
        }
        while (n < len) {
            int n1 = read1(b, off + n, len - n);
            if (n1 <= 0) {
                break;
            }
            n += n1;
        }
        return n;
        //return raf.read(b, off, len);
    }

    /**
     * Reads up to
     * <code>b.length</code> bytes of data from this file into an array of
     * bytes. This method behaves similar to the
     * {@link java.io.BufferedReader#read(byte[])} method of
     * <code>BufferedReader</code>.
     *
     * @param b the buffer into which the data is read.
     * @return the total number of bytes read into the buffer, or
     * <code>-1</code> if there is no more data because the end of this file has
     * been reached.
     * @exception IOException If the first byte cannot be read for any reason
     * other than end of file, or if the random access file has been closed, or
     * if some other I/O error occurs.
     * @exception NullPointerException If <code>b</code> is <code>null</code>.
     */
    public synchronized int read(byte b[]) throws IOException {
        return read(b, 0, b.length);
    }

    /**
     * Reads
     * <code>b.length</code> bytes from this file into the byte array, starting
     * at the current file pointer. This method reads repeatedly from the file
     * until the requested number of bytes are read. This method blocks until
     * the requested number of bytes are read, the end of the stream is
     * detected, or an exception is thrown.
     *
     * @param b the buffer into which the data is read.
     * @exception EOFException if this file reaches the end before reading all
     * the bytes.
     * @exception IOException if an I/O error occurs.
     */
    public synchronized final void readFully(byte b[]) throws IOException {
        resetPosition();
        raf.readFully(b);
    }

    /**
     * Reads exactly
     * <code>len</code> bytes from this file into the byte array, starting at
     * the current file pointer. This method reads repeatedly from the file
     * until the requested number of bytes are read. This method blocks until
     * the requested number of bytes are read, the end of the stream is
     * detected, or an exception is thrown.
     *
     * @param b the buffer into which the data is read.
     * @param off the start offset of the data.
     * @param len the number of bytes to read.
     * @exception EOFException if this file reaches the end before reading all
     * the bytes.
     * @exception IOException if an I/O error occurs.
     */
    public synchronized final void readFully(byte b[], int off, int len) throws IOException {
        resetPosition();
        raf.readFully(b, off, len);
    }

    /**
     * Attempts to skip over
     * <code>n</code> bytes of input discarding the skipped bytes. <p>
     *
     * This method may skip over some smaller number of bytes, possibly zero.
     * This may result from any of a number of conditions; reaching end of file
     * before
     * <code>n</code> bytes have been skipped is only one possibility. This
     * method never throws an
     * <code>EOFException</code>. The actual number of bytes skipped is
     * returned. If
     * <code>n</code> is negative, no bytes are skipped.
     *
     * @param n the number of bytes to be skipped.
     * @return the actual number of bytes skipped.
     * @exception IOException if an I/O error occurs.
     */
    public synchronized int skipBytes(int n) throws IOException {

        if (n < 0L) {
            throw new IllegalArgumentException("skip value is negative");
        }

        int r = n;
        while (r > 0) {
            if (nextChar >= nChars) {
                fill();
            }
            if (nextChar >= nChars) /* EOF */ {
                break;
            }
            if (skipLF) {
                skipLF = false;
                if (charBuffer[nextChar] == '\n') {
                    nextChar++;
                }
            }
            long d = nChars - nextChar;
            if (r <= d) {
                nextChar += r;
                r = 0;
                break;
            } else {
                r -= d;
                nextChar = nChars;
            }
        }
        int skipped = n - r;
        actualFilePointer += skipped;
        return skipped;

//        resetPosition();
//        return raf.skipBytes(n);
    }

    // 'Write' primitives
    /**
     * Writes the specified byte to this file. The write starts at the current
     * file pointer.
     *
     * @param b the <code>byte</code> to be written.
     * @exception IOException if an I/O error occurs.
     */
    public synchronized void write(int b) throws IOException {
        resetPosition();
        raf.write(b);
    }

    /**
     * Writes
     * <code>b.length</code> bytes from the specified byte array to this file,
     * starting at the current file pointer.
     *
     * @param b the data.
     * @exception IOException if an I/O error occurs.
     */
    public synchronized void write(byte b[]) throws IOException {
        resetPosition();
        raf.write(b, 0, b.length);
    }

    /**
     * Writes
     * <code>len</code> bytes from the specified byte array starting at offset
     * <code>off</code> to this file.
     *
     * @param b the data.
     * @param off the start offset in the data.
     * @param len the number of bytes to write.
     * @exception IOException if an I/O error occurs.
     */
    public synchronized void write(byte b[], int off, int len) throws IOException {
        resetPosition();
        raf.write(b, off, len);
    }

    // 'Random access' stuff
    /**
     * Returns the current offset in this file.
     *
     * @return the offset from the beginning of the file, in bytes, at which the
     * next read or write occurs.
     * @exception IOException if an I/O error occurs.
     */
    public synchronized long getFilePointer() throws IOException {
        if (actualFilePointer == null) {
            return raf.getFilePointer();
        } else {
            return this.actualFilePointer;
        }
    }

    /**
     * Sets the file-pointer offset, measured from the beginning of this file,
     * at which the next read or write occurs. The offset may be set beyond the
     * end of the file. Setting the offset beyond the end of the file does not
     * change the file length. The file length will change only by writing after
     * the offset has been set beyond the end of the file.
     *
     * @param pos the offset position, measured in bytes from the beginning of
     * the file, at which to set the file pointer.
     * @exception IOException if <code>pos</code> is less than <code>0</code> or
     * if an I/O error occurs.
     */
    public synchronized void seek(long pos) throws IOException {
        actualFilePointer = null;
        resetPosition();
        raf.seek(pos);
    }

    /**
     * Returns the length of this file.
     *
     * @return the length of this file, measured in bytes.
     * @exception IOException if an I/O error occurs.
     */
    public synchronized long length() throws IOException {
        return raf.length();
    }

    /**
     * Sets the length of this file.
     *
     * <p> If the present length of the file as returned by the
     * <code>length</code> method is greater than the
     * <code>newLength</code> argument then the file will be truncated. In this
     * case, if the file offset as returned by the
     * <code>getFilePointer</code> method is greater than
     * <code>newLength</code> then after this method returns the offset will be
     * equal to
     * <code>newLength</code>.
     *
     * <p> If the present length of the file as returned by the
     * <code>length</code> method is smaller than the
     * <code>newLength</code> argument then the file will be extended. In this
     * case, the contents of the extended portion of the file are not defined.
     *
     * @param newLength The desired length of the file
     * @exception IOException If an I/O error occurs
     * @since 1.2
     */
    public synchronized void setLength(long newLength) throws IOException {
        if (newLength < raf.length()) {
            resetPosition();
        }
        raf.setLength(newLength);
    }

    /**
     * Closes this random access file stream and releases any system resources
     * associated with the stream. A closed random access file cannot perform
     * input or output operations and cannot be reopened.
     *
     * <p> If this file has an associated channel then the channel is closed as
     * well.
     *
     * @exception IOException if an I/O error occurs.
     *
     * @revised 1.4
     * @spec JSR-51
     */
    public void close() throws IOException {
        raf.close();
    }

    //
    //  Some "reading/writing Java data types" methods stolen from
    //  DataInputStream and DataOutputStream.
    //
    /**
     * Reads a
     * <code>boolean</code> from this file. This method reads a single byte from
     * the file, starting at the current file pointer. A value of
     * <code>0</code> represents
     * <code>false</code>. Any other value represents
     * <code>true</code>. This method blocks until the byte is read, the end of
     * the stream is detected, or an exception is thrown.
     *
     * @return the <code>boolean</code> value read.
     * @exception EOFException if this file has reached the end.
     * @exception IOException if an I/O error occurs.
     */
    public synchronized final boolean readBoolean() throws IOException {
        resetPosition();
        return raf.readBoolean();
    }

    /**
     * Reads a signed eight-bit value from this file. This method reads a byte
     * from the file, starting from the current file pointer. If the byte read
     * is
     * <code>b</code>, where
     * <code>0&nbsp;&lt;=&nbsp;b&nbsp;&lt;=&nbsp;255</code>, then the result is:
     * null null null null null null null null null null null null null null     <blockquote><pre>
     *     (byte)(b)
     * </pre></blockquote> <p> This method blocks until the byte is read, the
     * end of the stream is detected, or an exception is thrown.
     *
     * @return the next byte of this file as a signed eight-bit
     * <code>byte</code>.
     * @exception EOFException if this file has reached the end.
     * @exception IOException if an I/O error occurs.
     */
    public synchronized final byte readByte() throws IOException {
        resetPosition();
        return raf.readByte();
    }

    /**
     * Reads an unsigned eight-bit number from this file. This method reads a
     * byte from this file, starting at the current file pointer, and returns
     * that byte. <p> This method blocks until the byte is read, the end of the
     * stream is detected, or an exception is thrown.
     *
     * @return the next byte of this file, interpreted as an unsigned eight-bit
     * number.
     * @exception EOFException if this file has reached the end.
     * @exception IOException if an I/O error occurs.
     */
    public synchronized final int readUnsignedByte() throws IOException {
        resetPosition();
        return raf.readUnsignedByte();
    }

    /**
     * Reads a signed 16-bit number from this file. The method reads two bytes
     * from this file, starting at the current file pointer. If the two bytes
     * read, in order, are
     * <code>b1</code> and
     * <code>b2</code>, where each of the two values is between
     * <code>0</code> and
     * <code>255</code>, inclusive, then the result is equal to: null null null
     * null null null null null null null null null null null     <blockquote><pre>
     *     (short)((b1 &lt;&lt; 8) | b2)
     * </pre></blockquote> <p> This method blocks until the two bytes are read,
     * the end of the stream is detected, or an exception is thrown.
     *
     * @return the next two bytes of this file, interpreted as a signed 16-bit
     * number.
     * @exception EOFException if this file reaches the end before reading two
     * bytes.
     * @exception IOException if an I/O error occurs.
     */
    public synchronized final short readShort() throws IOException {
        resetPosition();
        return raf.readShort();
    }

    /**
     * Reads an unsigned 16-bit number from this file. This method reads two
     * bytes from the file, starting at the current file pointer. If the bytes
     * read, in order, are
     * <code>b1</code> and
     * <code>b2</code>, where
     * <code>0&nbsp;&lt;=&nbsp;b1, b2&nbsp;&lt;=&nbsp;255</code>, then the
     * result is equal to: null null null null null null null null null null
     * null null null null     <blockquote><pre>
     *     (b1 &lt;&lt; 8) | b2
     * </pre></blockquote> <p> This method blocks until the two bytes are read,
     * the end of the stream is detected, or an exception is thrown.
     *
     * @return the next two bytes of this file, interpreted as an unsigned
     * 16-bit integer.
     * @exception EOFException if this file reaches the end before reading two
     * bytes.
     * @exception IOException if an I/O error occurs.
     */
    public synchronized final int readUnsignedShort() throws IOException {
        resetPosition();
        return raf.readUnsignedShort();
    }

    /**
     * Reads a character from this file. This method reads two bytes from the
     * file, starting at the current file pointer. If the bytes read, in order,
     * are
     * <code>b1</code> and
     * <code>b2</code>, where
     * <code>0&nbsp;&lt;=&nbsp;b1,&nbsp;b2&nbsp;&lt;=&nbsp;255</code>, then the
     * result is equal to: null null null null null null null null null null
     * null null null null     <blockquote><pre>
     *     (char)((b1 &lt;&lt; 8) | b2)
     * </pre></blockquote> <p> This method blocks until the two bytes are read,
     * the end of the stream is detected, or an exception is thrown.
     *
     * @return the next two bytes of this file, interpreted as a
     * <code>char</code>.
     * @exception EOFException if this file reaches the end before reading two
     * bytes.
     * @exception IOException if an I/O error occurs.
     */
    public synchronized final char readChar() throws IOException {
        resetPosition();
        return raf.readChar();
    }

    /**
     * Reads a signed 32-bit integer from this file. This method reads 4 bytes
     * from the file, starting at the current file pointer. If the bytes read,
     * in order, are
     * <code>b1</code>,
     * <code>b2</code>,
     * <code>b3</code>, and
     * <code>b4</code>, where
     * <code>0&nbsp;&lt;=&nbsp;b1, b2, b3, b4&nbsp;&lt;=&nbsp;255</code>, then
     * the result is equal to: null null null null null null null null null null
     * null null null null     <blockquote><pre>
     *     (b1 &lt;&lt; 24) | (b2 &lt;&lt; 16) + (b3 &lt;&lt; 8) + b4
     * </pre></blockquote> <p> This method blocks until the four bytes are read,
     * the end of the stream is detected, or an exception is thrown.
     *
     * @return the next four bytes of this file, interpreted as an
     * <code>int</code>.
     * @exception EOFException if this file reaches the end before reading four
     * bytes.
     * @exception IOException if an I/O error occurs.
     */
    public synchronized final int readInt() throws IOException {
        resetPosition();
        return raf.readInt();
    }

    /**
     * Reads a signed 64-bit integer from this file. This method reads eight
     * bytes from the file, starting at the current file pointer. If the bytes
     * read, in order, are
     * <code>b1</code>,
     * <code>b2</code>,
     * <code>b3</code>,
     * <code>b4</code>,
     * <code>b5</code>,
     * <code>b6</code>,
     * <code>b7</code>, and
     * <code>b8,</code> where: null null null null null null null null null null
     * null null null null     <blockquote><pre>
     *     0 &lt;= b1, b2, b3, b4, b5, b6, b7, b8 &lt;=255,
     * </pre></blockquote> <p> then the result is equal to: null null null null
     * null null null null null null null null null null     <p><blockquote><pre>
     *     ((long)b1 &lt;&lt; 56) + ((long)b2 &lt;&lt; 48)
     *     + ((long)b3 &lt;&lt; 40) + ((long)b4 &lt;&lt; 32)
     *     + ((long)b5 &lt;&lt; 24) + ((long)b6 &lt;&lt; 16)
     *     + ((long)b7 &lt;&lt; 8) + b8
     * </pre></blockquote> <p> This method blocks until the eight bytes are
     * read, the end of the stream is detected, or an exception is thrown.
     *
     * @return the next eight bytes of this file, interpreted as a
     * <code>long</code>.
     * @exception EOFException if this file reaches the end before reading eight
     * bytes.
     * @exception IOException if an I/O error occurs.
     */
    public synchronized final long readLong() throws IOException {
        resetPosition();
        return raf.readLong();
    }

    /**
     * Reads a
     * <code>float</code> from this file. This method reads an
     * <code>int</code> value, starting at the current file pointer, as if by
     * the
     * <code>readInt</code> method and then converts that
     * <code>int</code> to a
     * <code>float</code> using the
     * <code>intBitsToFloat</code> method in class
     * <code>Float</code>. <p> This method blocks until the four bytes are read,
     * the end of the stream is detected, or an exception is thrown.
     *
     * @return the next four bytes of this file, interpreted as a
     * <code>float</code>.
     * @exception EOFException if this file reaches the end before reading four
     * bytes.
     * @exception IOException if an I/O error occurs.
     * @see java.io.RandomAccessFile#readInt()
     * @see java.lang.Float#intBitsToFloat(int)
     */
    public synchronized final float readFloat() throws IOException {
        resetPosition();
        return raf.readFloat();
    }

    /**
     * Reads a
     * <code>double</code> from this file. This method reads a
     * <code>long</code> value, starting at the current file pointer, as if by
     * the
     * <code>readLong</code> method and then converts that
     * <code>long</code> to a
     * <code>double</code> using the
     * <code>longBitsToDouble</code> method in class
     * <code>Double</code>. <p> This method blocks until the eight bytes are
     * read, the end of the stream is detected, or an exception is thrown.
     *
     * @return the next eight bytes of this file, interpreted as a
     * <code>double</code>.
     * @exception EOFException if this file reaches the end before reading eight
     * bytes.
     * @exception IOException if an I/O error occurs.
     * @see java.io.RandomAccessFile#readLong()
     * @see java.lang.Double#longBitsToDouble(long)
     */
    public synchronized final double readDouble() throws IOException {
        resetPosition();
        return raf.readDouble();
    }

    /**
     * <p> Read the file line by line omitting the line separator. </p> <p> see
     * {@link java.io.RandomAccessFile#readLine() readLine()} and see
     * {@link java.io.BufferedReader#readLine(boolean) readLine(boolean ignoreLF)}.
     * <p>
     *
     * <p> Subsequent calls of this method are buffered. If certain other
     * methods that are affected by the current position of the reader in the
     * file is called after this method, the position is set to the start of the
     * next line and the buffer is invalidated. </p>
     *
     * <p> This method is copied from
     * {@link java.io.BufferedReader BufferedReader} with minor changes like
     * tracking position (offset) were next line starts. </p>
     *
     * @return the next line of text from this file, or null if end of file is
     * encountered before even one byte is read.
     * @exception IOException if an I/O error occurs.
     */
    public synchronized final String readLine(boolean ignoreLF) throws IOException {

        StringBuilder s = null;
        int startChar;
        int separatorIndex = 0;

        boolean omitLF = ignoreLF || skipLF;

        bufferLoop:
        for (;;) {

            if (nextChar >= nChars) {
                fill();
            }
            if (nextChar >= nChars) { /* EOF */
                if (s != null && s.length() > 0) {
                    //EOF -> hence no need to adjust position in file
                    // changed by fill()
                    return s.toString();
                } else {
                    return null;
                }
            }
            boolean eol = false;
            char c = 0;
            int i;

            /* Skip a leftover '\n', if necessary */
            if (omitLF && (charBuffer[nextChar] == '\n')) {
                nextChar++;
            }
            skipLF = false;
            omitLF = false;

            charLoop:
            for (i = nextChar; i < nChars; i++) {
                c = charBuffer[i];
                if ((c == '\n') || (c == '\r')) {
                    eol = true;
                    break charLoop;
                }
            }

            startChar = nextChar;
            nextChar = i;

            if (eol) {
                String str;
                if (s == null) {
                    str = new String(charBuffer, startChar, i - startChar);
                } else {
                    s.append(charBuffer, startChar, i - startChar);
                    str = s.toString();
                }
                nextChar++;
                if (c == '\r') {
                    skipLF = true;
                    if (nextChar >= nChars) {
                        fill();
                    }
                    if (charBuffer[nextChar] == '\n') {
                        separatorIndex = 1;
                    }
                }
                actualFilePointer = lastOffset + nextChar + separatorIndex;
                return str;
            }

            if (s == null) {
                s = new StringBuilder(defaultExpectedLineLength);
            }
            s.append(charBuffer, startChar, i - startChar);
        }
    }

    /**
     * see {@link #readLine(boolean) readLine(boolean ignoreLF)}
     *
     * @return
     * @throws IOException
     */
    public synchronized String readLine() throws IOException {
        return readLine(false);
    }

    private void fill() throws IOException {

        lastOffset = raf.getFilePointer();
        actualFilePointer = lastOffset;
        byte[] buffer = new byte[bufferSize];
        int n = raf.read(buffer);
        if (n > 0) {
            nChars = n;
            nextChar = 0;
        }
        for (int i = 0; i < buffer.length; i++) {
            charBuffer[i] = (char) buffer[i];
        }
    }

    /**
     * Reads in a string from this file. The string has been encoded using a <a
     * href="DataInput.html#modified-utf-8">modified UTF-8</a> format. <p> The
     * first two bytes are read, starting from the current file pointer, as if
     * by
     * <code>readUnsignedShort</code>. This value gives the number of following
     * bytes that are in the encoded string, not the length of the resulting
     * string. The following bytes are then interpreted as bytes encoding
     * characters in the modified UTF-8 format and are converted into
     * characters. <p> This method blocks until all the bytes are read, the end
     * of the stream is detected, or an exception is thrown.
     *
     * @return a Unicode string.
     * @exception EOFException if this file reaches the end before reading all
     * the bytes.
     * @exception IOException if an I/O error occurs.
     * @exception UTFDataFormatException if the bytes do not represent valid
     * modified UTF-8 encoding of a Unicode string.
     * @see java.io.RandomAccessFile#readUnsignedShort()
     */
    public synchronized final String readUTF() throws IOException {
        resetPosition();
        return raf.readUTF();
    }

    /**
     * Writes a
     * <code>boolean</code> to the file as a one-byte value. The value
     * <code>true</code> is written out as the value
     * <code>(byte)1</code>; the value
     * <code>false</code> is written out as the value
     * <code>(byte)0</code>. The write starts at the current position of the
     * file pointer.
     *
     * @param v a <code>boolean</code> value to be written.
     * @exception IOException if an I/O error occurs.
     */
    public synchronized final void writeBoolean(boolean v) throws IOException {
        resetPosition();
        raf.writeBoolean(v);
    }

    /**
     * Writes a
     * <code>byte</code> to the file as a one-byte value. The write starts at
     * the current position of the file pointer.
     *
     * @param v a <code>byte</code> value to be written.
     * @exception IOException if an I/O error occurs.
     */
    public synchronized final void writeByte(int v) throws IOException {
        resetPosition();
        raf.writeByte(v);
    }

    /**
     * Writes a
     * <code>short</code> to the file as two bytes, high byte first. The write
     * starts at the current position of the file pointer.
     *
     * @param v a <code>short</code> to be written.
     * @exception IOException if an I/O error occurs.
     */
    public synchronized final void writeShort(int v) throws IOException {
        resetPosition();
        raf.writeShort(v);
    }

    /**
     * Writes a
     * <code>char</code> to the file as a two-byte value, high byte first. The
     * write starts at the current position of the file pointer.
     *
     * @param v a <code>char</code> value to be written.
     * @exception IOException if an I/O error occurs.
     */
    public synchronized final void writeChar(int v) throws IOException {
        resetPosition();
        raf.writeChar(v);
    }

    /**
     * Writes an
     * <code>int</code> to the file as four bytes, high byte first. The write
     * starts at the current position of the file pointer.
     *
     * @param v an <code>int</code> to be written.
     * @exception IOException if an I/O error occurs.
     */
    public synchronized final void writeInt(int v) throws IOException {
        resetPosition();
        raf.writeInt(v);
    }

    /**
     * Writes a
     * <code>long</code> to the file as eight bytes, high byte first. The write
     * starts at the current position of the file pointer.
     *
     * @param v a <code>long</code> to be written.
     * @exception IOException if an I/O error occurs.
     */
    public synchronized final void writeLong(long v) throws IOException {
        resetPosition();
        raf.writeLong(v);
    }

    /**
     * Converts the float argument to an
     * <code>int</code> using the
     * <code>floatToIntBits</code> method in class
     * <code>Float</code>, and then writes that
     * <code>int</code> value to the file as a four-byte quantity, high byte
     * first. The write starts at the current position of the file pointer.
     *
     * @param v a <code>float</code> value to be written.
     * @exception IOException if an I/O error occurs.
     * @see java.lang.Float#floatToIntBits(float)
     */
    public synchronized final void writeFloat(float v) throws IOException {
        resetPosition();
        raf.writeFloat(v);
    }

    /**
     * Converts the double argument to a
     * <code>long</code> using the
     * <code>doubleToLongBits</code> method in class
     * <code>Double</code>, and then writes that
     * <code>long</code> value to the file as an eight-byte quantity, high byte
     * first. The write starts at the current position of the file pointer.
     *
     * @param v a <code>double</code> value to be written.
     * @exception IOException if an I/O error occurs.
     * @see java.lang.Double#doubleToLongBits(double)
     */
    public synchronized final void writeDouble(double v) throws IOException {
        resetPosition();
        raf.writeDouble(v);
    }

    /**
     * Writes the string to the file as a sequence of bytes. Each character in
     * the string is written out, in sequence, by discarding its high eight
     * bits. The write starts at the current position of the file pointer.
     *
     * @param s a string of bytes to be written.
     * @exception IOException if an I/O error occurs.
     */
    public synchronized final void writeBytes(String s) throws IOException {
        resetPosition();
        raf.writeBytes(s);
    }

    /**
     * Writes a string to the file as a sequence of characters. Each character
     * is written to the data output stream as if by the
     * <code>writeChar</code> method. The write starts at the current position
     * of the file pointer.
     *
     * @param s a <code>String</code> value to be written.
     * @exception IOException if an I/O error occurs.
     * @see java.io.RandomAccessFile#writeChar(int)
     */
    public synchronized final void writeChars(String s) throws IOException {
        resetPosition();
        raf.writeChars(s);
    }

    /**
     * Writes a string to the file using <a
     * href="DataInput.html#modified-utf-8">modified UTF-8</a> encoding in a
     * machine-independent manner. <p> First, two bytes are written to the file,
     * starting at the current file pointer, as if by the
     * <code>writeShort</code> method giving the number of bytes to follow. This
     * value is the number of bytes actually written out, not the length of the
     * string. Following the length, each character of the string is output, in
     * sequence, using the modified UTF-8 encoding for each character.
     *
     * @param str a string to be written.
     * @exception IOException if an I/O error occurs.
     */
    public synchronized final void writeUTF(String str) throws IOException {
        resetPosition();
        raf.writeUTF(str);
    }

    private void resetPosition() throws IOException {
        if (actualFilePointer != null) {
            raf.seek(actualFilePointer);
            actualFilePointer = null;
        }
        nChars = 0;
        nextChar = 0;
    }
}
