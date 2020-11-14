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

import org.zuinnote.hadoop.bitcoin.format.common.BitcoinUtil;
import org.zuinnote.hadoop.bitcoin.format.util.Byteable;
import org.zuinnote.hadoop.bitcoin.format.util.Bytes;

import java.nio.ByteBuffer;

public class HashSHA256 implements Byteable {

    public static final int NUM_BYTES = 32;

    protected byte[] rawData;

    public HashSHA256(Bytes input) {
        this.rawData = input.hashTwice();
    }

    public HashSHA256(ByteBuffer buffer) {
        rawData = new byte[NUM_BYTES];
        setValue(buffer);
    }

    public HashSHA256(byte[] rawData) {
        this.rawData = rawData;
        assert rawData.length == 32;
    }

    public void setValue(ByteBuffer buffer) {
        for (int i = 0; i < NUM_BYTES; i++) {
            rawData[i] = buffer.get();
        }
    }

    public String toString() {
        return BitcoinUtil.convertByteArrayToHexString(BitcoinUtil.reverseByteArray(rawData));
    }

    @Override
    public byte[] getBytes() {
        return rawData;
    }
}
