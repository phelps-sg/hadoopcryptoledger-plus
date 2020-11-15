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

import java.io.Serializable;

public class BitcoinScriptWitness implements Serializable, Byteable {

    private static final long serialVersionUID = 273511143914429994L;

    private UIntVar witnessScriptLength;
    private byte[] witnessScript;

    public BitcoinScriptWitness(UIntVar witnessScriptLength, byte[] witnessScript) {
        this.witnessScriptLength = witnessScriptLength;
        this.witnessScript = witnessScript;
    }

    public UIntVar getWitnessScriptLength() {
        return this.witnessScriptLength;
    }

    public byte[] getWitnessScript() {
        return this.witnessScript;
    }

    public byte[] getBytes() {
        return new Bytes(witnessScriptLength, witnessScript).getBytes();
    }
}
