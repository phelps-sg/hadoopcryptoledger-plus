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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.io.Writable;
import org.zuinnote.hadoop.bitcoin.format.littleendian.EpochDatetime;
import org.zuinnote.hadoop.bitcoin.format.littleendian.UInt32;
import org.zuinnote.hadoop.bitcoin.format.util.Bytes;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class BitcoinTransaction implements Serializable, Writable {

    private UInt32 version;
    private byte marker;
    private byte flag;
    private byte[] inCounter;
    private byte[] outCounter;
    private List<BitcoinTransactionInput> inputs;
    private List<BitcoinTransactionOutput> outputs;
    private List<BitcoinScriptWitnessItem> scriptWitnessItems;
    private EpochDatetime lockTime;

	private static transient final Log LOG = LogFactory.getLog(BitcoinTransaction.class.getName());

    public BitcoinTransaction() {
        this.version = new UInt32(0);
        this.marker = 1;
        this.flag = 0;
        this.inCounter = new byte[0];
        this.outCounter = new byte[0];
        this.inputs = new ArrayList<>();
        this.outputs = new ArrayList<>();
        this.scriptWitnessItems = new ArrayList<>();
        this.lockTime = new EpochDatetime(0);
    }

    /**
     * Creates a traditional Bitcoin Transaction without ScriptWitness
     *
     * @param version
     * @param inCounter
     * @param listOfInputs
     * @param outCounter
     * @param listOfOutputs
     * @param lockTime
     */
    public BitcoinTransaction(long version, byte[] inCounter, List<BitcoinTransactionInput> listOfInputs, byte[] outCounter, List<BitcoinTransactionOutput> listOfOutputs, long lockTime) {
        this.marker = 1;
        this.flag = 0;
        this.version = new UInt32(version);
        this.inCounter = inCounter;
        this.inputs = listOfInputs;
        this.outCounter = outCounter;
        this.outputs = listOfOutputs;
        this.scriptWitnessItems = new ArrayList<>();
        this.lockTime = new EpochDatetime(lockTime);
    }

    /**
     * Creates a Bitcoin Transaction with Segwitness
     *
     * @param marker
     * @param flag
     * @param version
     * @param inCounter
     * @param listOfInputs
     * @param outCounter
     * @param listOfOutputs
     * @param listOfScriptWitnessItem
     * @param lockTime
     */
    public BitcoinTransaction(byte marker, byte flag, long version, byte[] inCounter, List<BitcoinTransactionInput> listOfInputs, byte[] outCounter, List<BitcoinTransactionOutput> listOfOutputs, List<BitcoinScriptWitnessItem> listOfScriptWitnessItem, long lockTime) {
        this.marker = marker;
        this.flag = flag;
        this.version = new UInt32(version);
        this.inCounter = inCounter;
        this.inputs = listOfInputs;
        this.outCounter = outCounter;
        this.outputs = listOfOutputs;
        this.scriptWitnessItems = listOfScriptWitnessItem;
        this.lockTime = new EpochDatetime(lockTime);
    }

    public long getVersion() {
        return this.version.longValue();
    }

    public byte getMarker() {
        return this.marker;
    }

    public byte getFlag() {
        return this.flag;
    }

    public byte[] getInCounter() {
        return this.inCounter;
    }

    public List<BitcoinTransactionInput> getListOfInputs() {
        return this.inputs;
    }

    public byte[] getOutCounter() {
        return this.outCounter;
    }

    public List<BitcoinTransactionOutput> getListOfOutputs() {
        return this.outputs;
    }

    public List<BitcoinScriptWitnessItem> getBitcoinScriptWitness() {
        return this.scriptWitnessItems;
    }

    public long getLockTime() {
        return this.lockTime.getValue();
    }

    public void set(BitcoinTransaction newTransaction) {
        this.version = new UInt32(newTransaction.getVersion());
        this.marker = newTransaction.getMarker();
        this.flag = newTransaction.getFlag();
        this.inCounter = newTransaction.getInCounter();
        this.inputs = newTransaction.getListOfInputs();
        this.outCounter = newTransaction.getOutCounter();
        this.outputs = newTransaction.getListOfOutputs();
        this.scriptWitnessItems = newTransaction.getBitcoinScriptWitness();
        this.lockTime = new EpochDatetime(newTransaction.getLockTime());
    }

    @Override
    public void write(DataOutput dataOutput) {
        throw new UnsupportedOperationException("write unsupported");
    }

    @Override
    public void readFields(DataInput dataInput) {
        throw new UnsupportedOperationException("readFields unsupported");
    }

	/**
	 * Calculates the double SHA256-Hash of a transaction in little endian format.
	 * It corresponds to the Bitcoin specification of txid (https://bitcoincore.org/en/segwit_wallet_dev/).
	 * Note that this can be compared to a prevTransactionHash. However, if you want to search for it in
	 * popular blockchain explorers then you need to apply the function BitcoinUtil.reverseByteArray to it!
     *
	 * @return byte array containing the hash of the transaction.
	 */
    public byte[] getTransactionHash() {
        return new Bytes(version, inCounter, inputs, outCounter, outputs, lockTime).hashTwice();
    }

	/**
	 * <p>
	 * Calculates the double SHA256-Hash of a transaction in little endian format.
	 * It corresponds to the Bitcoin specification of wtxid (https://bitcoincore.org/en/segwit_wallet_dev/).
	 * Note that this can be compared to a prevTransactionHash.
	 * However, if you want to search for it in popular blockchain explorers then you need to
	 * apply the function BitcoinUtil.reverseByteArray to it.
     * </p>
	 *
	 * @return byte array containing the hash of the transaction.
	 */
	public byte[] getTransactionHashSegwit() {
			Bytes buffer = new Bytes();
			buffer.write(version);
			// check if segwit
			boolean segwit = false;
			if ((getMarker() == 0) && (getFlag() != 0)) {
				segwit = true;
				// we still need to check the case that all witness script stack items for all input transactions are
				// of size 0 => traditional transaction hash calculation
				// cf. https://github.com/bitcoin/bips/blob/master/bip-0141.mediawiki
				// A non-witness program (defined hereinafter) txin MUST be associated with an empty witness field,
				// represented by a 0x00. If all txins are not witness program, a transaction's
				// wtxid is equal to its txid.
				boolean emptyWitness = true;
				for (BitcoinScriptWitnessItem currentItem: scriptWitnessItems) {
					if (currentItem.getStackItemCounter().length > 1
                        || ((currentItem.getStackItemCounter().length == 1)
                                && (currentItem.getStackItemCounter()[0] != 0x00))) {
						emptyWitness = false;
						break;
					}
				}
				if (emptyWitness) {
					return getTransactionHash();
				}
				buffer.write(marker, flag);
			}
			buffer.write(inCounter, inputs, outCounter, outputs);
			if (segwit) {
			    buffer.write(scriptWitnessItems);
            }
			buffer.write(lockTime);
			return buffer.hashTwice();
	}

}
