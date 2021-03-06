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
import java.util.List;

/**
 *
 */
public class BitcoinScriptWitnessItem implements Serializable, Byteable {

    /**
     *
     */
    private static final long serialVersionUID = -8500521021303513414L;
    private UIntVar stackItemCounter;
    private List<BitcoinScriptWitness> scriptWitnessList;

    public BitcoinScriptWitnessItem(UIntVar stackItemCounter, List<BitcoinScriptWitness> scriptWitnessList) {
        this.stackItemCounter = stackItemCounter;
        this.scriptWitnessList = scriptWitnessList;
    }

    public UIntVar getStackItemCounter() {
        return this.stackItemCounter;
    }

    public List<BitcoinScriptWitness> getScriptWitnessList() {
        return this.scriptWitnessList;
    }

    @Override
    public byte[] getBytes() {
        return new Bytes(stackItemCounter, scriptWitnessList).getBytes();
    }
}
