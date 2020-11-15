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

public abstract class UInt extends Number implements Byteable {

    /**
     * The raw data stored in little-endian order.
     */
    protected ByteBuffer rawData;

    public UInt() {
        this.rawData = ByteBuffer.allocate(getNumBytes());
        this.rawData.order(ByteOrder.LITTLE_ENDIAN);
    }

    public UInt(byte[] value) {
        this();
        setValue(value);
    }

    public UInt(long value) {
        this();
        setValue(value);
    }

    public UInt(ByteBuffer value) {
        this();
        setValue(value);
    }

    public UInt(DataInput input) throws IOException {
        this();
        setValue(input);
    }

    public UInt(InputStream input) throws IOException {
        this();
        setValue(input);
    }


    public abstract long getValue();

    public abstract int getNumBytes();

    public ByteBuffer getRawData() {
        return rawData;
    }

    public byte[] getBytes() {
        return getRawData().array();
    }

    public abstract void setValue(long value);

    public void setValue(byte[] value) {
        rawData.put(value, 0, getNumBytes());
    }

    public void setValue(ByteBuffer buffer) {
        for (int i = 0; i < getNumBytes(); i++) {
            rawData.put(i, buffer.get());
        }
    }

    public void setValue(DataInput input) throws IOException  {
        for (int i = 0; i < getNumBytes(); i++) {
            input.readByte();
        }
    }

    public void setValue(InputStream input) throws IOException {
        int totalByteRead = 0;
        int readByte = 0;
        while ((readByte = input.read(rawData.array(), totalByteRead, getNumBytes() - totalByteRead)) > -1) {
            totalByteRead += readByte;
            if (totalByteRead >= getNumBytes()) {
                break;
            }
        }
        if (totalByteRead != getNumBytes()) {
            //TODO?
        }
    }

    public long getMaxValue() {
        return (1L << (getNumBytes() * 8)) - 1;
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