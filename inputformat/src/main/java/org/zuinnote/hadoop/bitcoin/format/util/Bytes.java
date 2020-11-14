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

package org.zuinnote.hadoop.bitcoin.format.util;

import org.zuinnote.hadoop.bitcoin.format.common.BitcoinUtil;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

public class Bytes implements Byteable {

    protected ByteArrayOutputStream out;

    public Bytes(int initialCapacity) {
        this.out = new ByteArrayOutputStream(initialCapacity);
    }

    public Bytes() {
        this.out = new ByteArrayOutputStream();
    }

    public Bytes(Object... items) {
        this();
        write(items);
    }

    public void write(int b) {
        out.write(b);
    }

    public void write(byte[] b, int off, int len) {
        out.write(b, off, len);
    }

    public void reset() {
        out.reset();
    }

    public int size() {
        return out.size();
    }

    public void write(byte[] b) {
        try {
            out.write(b);
        } catch (IOException e) {
            throw new RuntimeException(e); // Never happens
        }
    }

    public byte[] hash() {
        return BitcoinUtil.hash(getBytes());
    }

    public byte[] hashTwice() {
        return BitcoinUtil.hashTwice(getBytes());
    }

    public byte[] getBytesReversed() {
        return BitcoinUtil.reverseByteArray(getBytes());
    }

    @Override
    public byte[] getBytes() {
        return out.toByteArray();
    }

    public void write(List items) {
        for(Object item : items) {
           assert(item instanceof Byteable);
           write(((Byteable) item).getBytes());
        }
    }

    public void write(Object... items) {
        for(Object item : items) {
            if (item instanceof Byteable) {
                write(((Byteable) item).getBytes());
            } else if (item instanceof byte[]) {
                write((byte[]) item);
            } else if (item instanceof Byte) {
                write((byte) item);
            } else if (item instanceof List) {
                write((List) item);
            } else {
                throw new IllegalArgumentException(item.toString());
            }
        }
    }

    public void write(Byteable byteable) {
        write(byteable.getBytes());
    }
}
