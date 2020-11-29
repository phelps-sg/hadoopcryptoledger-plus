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

import org.zuinnote.hadoop.bitcoin.format.littleendian.UIntVar;
import org.zuinnote.hadoop.bitcoin.format.util.Byteable;
import org.zuinnote.hadoop.bitcoin.format.util.Bytes;

import java.nio.ByteBuffer;

public class BitcoinScript implements Byteable {

    private UIntVar length;
    private Bytes script;

    public BitcoinScript(ByteBuffer input) {
        length = new UIntVar(input);
        byte[] scriptBytes = new byte[length.intValue()];
        input.get(scriptBytes, 0, length.intValue());
        script = new Bytes(scriptBytes);
    }

    public BitcoinScript() {
    }

    public Bytes getScript() {
        return script;
    }

    public long getLength() {
        return length.longValue();
    }

    public void setLength(UIntVar length) {
        this.length = length;
    }

    public void setScript(Bytes script) {
        this.script = script;
    }

    @Override
    public byte[] getBytes() {
        return new Bytes(length, script).getBytes();
    }

}
