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

import java.io.Serializable;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * An unsigned integer stored in little-endian format with a variable-number of bytes (1, 3, 5, or 9) depending
 * on the magnitude being represented.
 * See <a href="https://en.bitcoin.it/wiki/Protocol_documentation#Variable_length_integer">the protocol documentation</a>.
 */
public class UIntVar extends UInt implements Serializable {

    /**
     * The total number of bytes used to store the integer (excluding the header byte).
     */
    protected int numBytes;

    public UIntVar(byte[] value) {
        super(value);
    }

    public UIntVar(long value) {
        super(value);
    }

    public UIntVar(ByteBuffer value) {
        super(value);
    }

    protected void allocateBytes() {
        if (getNumBytes() > 1) {
            rawData = ByteBuffer.allocate(getNumBytes() + 1);
        } else {
            rawData = ByteBuffer.allocate(1);
        }
        rawData.order(ByteOrder.LITTLE_ENDIAN);
    }

    public void setSizeFrom(long value) {
        if (value < 0xfd) {
            numBytes = 1;
        } else if (value < 0xffff) {
            numBytes = 2;
        } else if (value < 0xffffffff) {
            numBytes = 4;
        } else {
            numBytes = 8;
        }
    }

    public static int getVarIntSize(byte firstByteVarInt) {
        int unsignedByte = firstByteVarInt & 0xFF;
        switch (unsignedByte) {
            case 0xFD: return 2;
            case 0xFE: return 4;
            case 0xFF: return 8;
        }
        return 1;
    }

    public byte getSizeHeader(long value) {
        switch (numBytes) {
            case 1: return (byte) value;
            case 2: return (byte) 0xFD;
            case 4: return (byte) 0xFE;
            case 8: return (byte) 0xFF;
        }
        throw new IllegalArgumentException("Invalid size");
    }

    @Override
    public void setValue(ByteBuffer buffer) {
        byte firstByte = buffer.get();
        numBytes = getVarIntSize(firstByte);
        allocateBytes();
        rawData.put(firstByte);
        for(int i=1; i<rawData.capacity(); i++) {
            rawData.put(buffer.get());
        }
    }

    @Override
    public void setValue(byte[] buffer) {
        setValue(ByteBuffer.wrap(buffer));
    }

    @Override
    public long getValue() {
        long result = 0;
        switch (numBytes) {
            case 1: result = rawData.get(0); break;
            case 2: result = rawData.getShort(1); break;
            case 4: result = rawData.getInt(1); break;
            case 8: result = rawData.getLong(1); break;
        }
        return result & getMaxValue();
    }

    @Override
    public int getNumBytes() {
        return numBytes;
    }

    @Override
    public void setValue(long value) {
        setSizeFrom(value);
        allocateBytes();
        rawData.put(getSizeHeader(value));
        switch (numBytes) {
            case 2: rawData.putShort((short) value);
            case 4: rawData.putInt((int) value);
            case 8: rawData.putLong(value);
        }
    }
}
