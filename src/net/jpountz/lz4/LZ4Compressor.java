package net.jpountz.lz4;

/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * LZ4 compressor.
 * <p>
 * Instances of this class are thread-safe.
 */
public abstract class LZ4Compressor {

  /**
   * Returns the maximum compressed length for an input of size <code>length</code>.
   *
   * @param length the input size in bytes
   * @return the maximum compressed length in bytes
   */
  @SuppressWarnings("static-method")
  public final int maxCompressedLength(int length) {
    return LZ4Utils.maxCompressedLength(length);
  }

  /**
   * Compresses <code>src[srcOff:srcOff+srcLen]</code> into
   * <code>dest[destOff:destOff+maxDestLen]</code> and returns the compressed
   * length.
   *
   * This method will throw a {@link LZ4Exception} if this compressor is unable
   * to compress the input into less than <code>maxDestLen</code> bytes. To
   * prevent this exception to be thrown, you should make sure that
   * <code>maxDestLen &gt;= maxCompressedLength(srcLen)</code>.
   *
   * @param src the source data
   * @param srcOff the start offset in src
   * @param srcLen the number of bytes to compress
   * @param dest the destination buffer
   * @param destOff the start offset in dest
   * @param maxDestLen the maximum number of bytes to write in dest
   * @throws LZ4Exception if maxDestLen is too small
   * @return the compressed size
   */
  public abstract int compress(byte[] src, int srcOff, int srcLen, byte[] dest, int destOff, int maxDestLen);

  /**
   * Compresses <code>src[srcOff:srcOff+srcLen]</code> into
   * <code>dest[destOff:destOff+maxDestLen]</code> and returns the compressed
   * length.
   *
   * This method will throw a {@link LZ4Exception} if this compressor is unable
   * to compress the input into less than <code>maxDestLen</code> bytes. To
   * prevent this exception to be thrown, you should make sure that
   * <code>maxDestLen &gt;= maxCompressedLength(srcLen)</code>.
   *
   * {@link ByteBuffer} positions remain unchanged.
   *
   * @param src the source data
   * @param srcOff the start offset in src
   * @param srcLen the number of bytes to compress
   * @param dest the destination buffer
   * @param destOff the start offset in dest
   * @param maxDestLen the maximum number of bytes to write in dest
   * @throws LZ4Exception if maxDestLen is too small
   * @return the compressed size
   */
  public abstract int compress(ByteBuffer src, int srcOff, int srcLen, ByteBuffer dest, int destOff, int maxDestLen);

  /**
   * Convenience method, equivalent to calling
   * {@link #compress(byte[], int, int, byte[], int, int) compress(src, srcOff, srcLen, dest, destOff, dest.length - destOff)}.
   *
   * @param src the source data
   * @param srcOff the start offset in src
   * @param srcLen the number of bytes to compress
   * @param dest the destination buffer
   * @param destOff the start offset in dest
   * @throws LZ4Exception if dest is too small
   * @return the compressed size
   */
  public final int compress(byte[] src, int srcOff, int srcLen, byte[] dest, int destOff) {
    return compress(src, srcOff, srcLen, dest, destOff, dest.length - destOff);
  }

  /**
   * Convenience method, equivalent to calling
   * {@link #compress(byte[], int, int, byte[], int) compress(src, 0, src.length, dest, 0)}.
   *
   * @param src the source data
   * @param dest the destination buffer
   * @throws LZ4Exception if dest is too small
   * @return the compressed size
   */
  public final int compress(byte[] src, byte[] dest) {
    return compress(src, 0, src.length, dest, 0);
  }

  /**
   * Convenience method which returns <code>src[srcOff:srcOff+srcLen]</code>
   * compressed.
   * <p><b><span style="color:red">Warning</span></b>: this method has an
   * important overhead due to the fact that it needs to allocate a buffer to
   * compress into, and then needs to resize this buffer to the actual
   * compressed length.</p>
   * <p>Here is how this method is implemented:</p>
   * <pre>
   * final int maxCompressedLength = maxCompressedLength(srcLen);
   * final byte[] compressed = new byte[maxCompressedLength];
   * final int compressedLength = compress(src, srcOff, srcLen, compressed, 0);
   * return Arrays.copyOf(compressed, compressedLength);
   * </pre>
   *
   * @param src the source data
   * @param srcOff the start offset in src
   * @param srcLen the number of bytes to compress
   * @return the compressed data
   */
  public final byte[] compress(byte[] src, int srcOff, int srcLen) {
    final int maxCompressedLength = maxCompressedLength(srcLen);
    final byte[] compressed = new byte[maxCompressedLength];
    final int compressedLength = compress(src, srcOff, srcLen, compressed, 0);
    return Arrays.copyOf(compressed, compressedLength);
  }

  /**
   * Convenience method, equivalent to calling
   * {@link #compress(byte[], int, int) compress(src, 0, src.length)}.
   *
   * @param src the source data
   * @return the compressed data
   */
  public final byte[] compress(byte[] src) {
    return compress(src, 0, src.length);
  }

  /**
   * Compresses <code>src</code> into <code>dest</code>. Calling this method
   * will update the positions of both {@link ByteBuffer}s.
   *
   * @param src the source data
   * @param dest the destination buffer
   * @throws LZ4Exception if dest is too small
   */
  public final void compress(ByteBuffer src, ByteBuffer dest) {
    final int cpLen = compress(src, src.position(), src.remaining(), dest, dest.position(), dest.remaining());
    src.position(src.limit());
    dest.position(dest.position() + cpLen);
  }

  @Override
  public String toString() {
    return getClass().getSimpleName();
  }

}
