/*
 * Copyright 2016 ZuInnoTe (Jörn Franke) <zuinnote@gmail.com>
 *   <p>
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *   <p>
 *   http://www.apache.org/licenses/LICENSE-2.0
 *   <p>
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package org.zuinnote.hadoop.bitcoin.format.littleendian;

import org.zuinnote.hadoop.bitcoin.format.util.Byteable;

import java.io.DataInput;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * An implementation of an unsigned 32-bit integer in Java.
 * <p>
 * The value is stored in a raw little-endian four-byte buffer, and then converted
 * to a 64-bit signed long on-demand.  This allows UINT32 values  to be stored efficiently
 * for big-data computations while retaining the full range of positive values.
 */
public class UInt32 extends UInt {

    public static final int NUM_BYTES = 4;
    public static final long MAX = (1L << (NUM_BYTES * 8)) - 1;

    public UInt32() {
        super();
    }

    public UInt32(byte[] value) {
        super(value);
    }

    public UInt32(long value) {
        super(value);
    }

    public UInt32(ByteBuffer value) {
        super(value);
    }

    public UInt32(DataInput input) throws IOException {
        super(input);
    }

    public UInt32(InputStream input) throws IOException {
        super(input);
    }

    public int getNumBytes() {
        return NUM_BYTES;
    }

    public long getValue() {
        return rawData.getInt(0) & MAX;
    }

    public void setValue(long value) {
        rawData.putInt(0, (int) value);
    }
}
