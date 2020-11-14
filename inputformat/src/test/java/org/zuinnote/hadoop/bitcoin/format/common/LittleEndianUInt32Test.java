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

package org.zuinnote.hadoop.bitcoin.format.common;

import org.junit.jupiter.api.Test;
import org.zuinnote.hadoop.bitcoin.format.littleendian.UInt32;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class LittleEndianUInt32Test {

    @Test
    public void testZero() {
        byte[] zeroBytes = { 0x00, 0x00, 0x00, 0x00 };
        UInt32 zero = new UInt32(zeroBytes);
        assertEquals(0L, zero.getValue());
    }

    @Test
    public void testOne() {
        byte[] oneBytes = { 0x01, 0x00, 0x00, 0x00 };
        UInt32 one = new UInt32(oneBytes);
        assertEquals(1L, one.getValue());
    }

    @Test
    public void testMaxUInt() {
        int[] allOnes = { 0xff, 0xff, 0xff, 0xff };
        UInt32 maxUInt = new UInt32(asByte(allOnes));
        assertEquals(4294967295L, maxUInt.getValue());
    }

    @Test
    public void testBig() {
        long testValue = (long) Integer.MAX_VALUE + 5L;
        UInt32 big = new UInt32(testValue);
        assertEquals(big.getValue(), testValue);
    }

    public byte[] asByte(int[] bytes) {
        byte[] result = new byte[bytes.length];
        for(int i=0; i<bytes.length; i++) {
            result[i] = (byte) bytes[i];
        }
        return result;
    }
}
