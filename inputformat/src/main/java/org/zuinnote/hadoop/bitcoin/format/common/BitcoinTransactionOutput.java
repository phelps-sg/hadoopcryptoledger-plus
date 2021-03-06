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

import org.bitcoinj.core.Address;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.params.MainNetParams;
import org.bitcoinj.script.Script;
import org.zuinnote.hadoop.bitcoin.format.littleendian.UIntVar;
import org.zuinnote.hadoop.bitcoin.format.util.Byteable;
import org.zuinnote.hadoop.bitcoin.format.util.Bytes;

import java.io.Serializable;
import java.math.BigInteger;

public class BitcoinTransactionOutput implements Serializable, Byteable {

    private static final long serialVersionUID = 2854570630540937753L;

    private BigInteger value;
    private BitcoinScript outScript;

    //TODO Configure from magic?
    protected static NetworkParameters networkParameters = MainNetParams.get();

    public BitcoinTransactionOutput(BigInteger value, BitcoinScript outScript) { //UIntVar txOutScriptLength, byte[] txOutScript) {
        this.value = value;
        this.outScript = outScript;
//        this.txOutScriptLength = txOutScriptLength;
//        this.txOutScript = txOutScript;
    }

    public BigInteger getValue() {
        return this.value;
    }

    public UIntVar getTxOutScriptLength() {
        return getTxOutScriptLength();
    }

    public byte[] getTxOutScript() {
        return this.outScript.getScript().getBytes();
    }

    public Address getToAddress(NetworkParameters params) {
        Script script = new Script(outScript.getScript().getBytes());
        return script.getToAddress(params);
    }

    public Address getToAddress() {
        return getToAddress(networkParameters);
    }

    public String getToAddressString() {
        return getToAddress().toString();
    }

    @Override
    public byte[] getBytes() {
        return new Bytes(BitcoinUtil.convertBigIntegerToByteArray(getValue(), 8),
                                outScript).getBytes();
    }

}
