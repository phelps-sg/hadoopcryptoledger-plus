/*
 * Copyright 2016 ZuInnoTe (Jörn Franke) <zuinnote@gmail.com>
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 **/

package org.zuinnote.hadoop.bitcoin.format.common;

import org.zuinnote.hadoop.bitcoin.format.littleendian.UInt32;
import org.zuinnote.hadoop.bitcoin.format.util.Byteable;
import org.zuinnote.hadoop.bitcoin.format.util.Bytes;

import java.io.Serializable;

public class BitcoinTransactionInput implements Serializable, Byteable {

    private static final long serialVersionUID = 283893453089295979L;

    private byte[] prevTransactionHash;
    private UInt32 previousTxOutIndex;
    private byte[] txInScriptLength;
    private byte[] txInScript;
    private UInt32 seqNo;

    public BitcoinTransactionInput(byte[] prevTransactionHash, long previousTxOutIndex, byte[] txInScriptLength, byte[] txInScript, long seqNo) {
        this.prevTransactionHash = prevTransactionHash;
        this.previousTxOutIndex = new UInt32(previousTxOutIndex);
        this.txInScriptLength = txInScriptLength;
        this.txInScript = txInScript;
        this.seqNo = new UInt32(seqNo);
    }

    public byte[] getPrevTransactionHash() {
        return this.prevTransactionHash;
    }

    public long getPreviousTxOutIndex() {
        return this.previousTxOutIndex.getValue();
    }

    public byte[] getTxInScriptLength() {
        return this.txInScriptLength;
    }

    public byte[] getTxInScript() {
        return this.txInScript;
    }

    public long getSeqNo() {
        return this.seqNo.getValue();
    }

    @Override
    public byte[] getBytes() {
        return new Bytes(prevTransactionHash, previousTxOutIndex, txInScriptLength,
                            txInScript, seqNo)
                .getBytes();
    }

}
