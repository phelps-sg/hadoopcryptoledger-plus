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

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * An implementation of an unsigned 32-bit integer in Java.
 * <p>
 * The value is stored in a raw little-endian four-byte buffer, and then converted
 * to a 64-bit signed long on-demand.  This allows UINT32 values  to be stored efficiently
 * for big-data computations while retaining the full range of positive values.
 */
public class UInt32 extends Number implements Byteable {

    public static final int NUM_BYTES = 4;
    public static final long MAX = (1L << (NUM_BYTES * 8)) - 1;

    /**
     * The raw data stored as 4 bytes in little-endian order.
     */
    protected ByteBuffer rawData;

    public UInt32() {
        this.rawData = ByteBuffer.allocate(NUM_BYTES);
        this.rawData.order(ByteOrder.LITTLE_ENDIAN);
    }

    public UInt32(byte[] value) {
        this();
        setValue(value);
    }

    public UInt32(long value) {
        this();
        setValue(value);
    }

    public UInt32(ByteBuffer value) {
        this();
        setValue(value);
    }

    public long getValue() {
        return rawData.getInt(0) & MAX;
    }

    public ByteBuffer getRawData() {
        return rawData;
    }

    public byte[] getBytes() {
        return getRawData().array();
    }

    public void setValue(long value) {
        rawData.putInt(0, (int) value);
    }

    public void setValue(byte[] value) {
        rawData.put(value, 0, NUM_BYTES);
    }

    public void setValue(ByteBuffer buffer) {
        for (int i = 0; i < NUM_BYTES; i++) {
            rawData.put(i, buffer.get());
        }
    }

    @Override
    public int intValue() {
        int result = (int) getValue();
        if (result < 0) throw new IllegalArgumentException("signed int overflow");
        return result;
    }

    @Override
    public long longValue() {
        return getValue();
    }

    @Override
    public float floatValue() {
        return (float) getValue();
    }

    @Override
    public double doubleValue() {
        return (double) getValue();
    }

    @Override
    public boolean equals(Object other) {
        return ((Number) other).longValue() == this.longValue();
    }

}
