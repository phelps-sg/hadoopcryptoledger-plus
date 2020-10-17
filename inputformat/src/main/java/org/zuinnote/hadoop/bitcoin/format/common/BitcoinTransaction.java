/**
* Copyright 2016 ZuInnoTe (Jörn Franke) <zuinnote@gmail.com>
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
**/

package org.zuinnote.hadoop.bitcoin.format.common;

import java.io.*;

import org.apache.commons.io.output.ThresholdingOutputStream;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.io.Writable;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.ArrayList;


public class BitcoinTransaction implements Serializable, Writable {


	private static transient final Log LOG = LogFactory.getLog(BitcoinTransaction.class.getName());

private int version;
private byte marker;
private byte flag;
private byte[] inCounter;
private byte[] outCounter;
private List<BitcoinTransactionInput> listOfInputs;
private List<BitcoinTransactionOutput> listOfOutputs;
private List<BitcoinScriptWitnessItem> listOfScriptWitnessItem;
private int lockTime;

public BitcoinTransaction() {
	this.version=0;
	this.marker=1;
	this.flag=0;
	this.inCounter=new byte[0];
	this.outCounter=new byte[0];
	this.listOfInputs=new ArrayList<>();
	this.listOfOutputs=new ArrayList<>();
	this.listOfScriptWitnessItem=new ArrayList<>();
	this.lockTime=0;
}

/***
 * Creates a traditional Bitcoin Transaction without ScriptWitness
 * 
 * @param version
 * @param inCounter
 * @param listOfInputs
 * @param outCounter
 * @param listOfOutputs
 * @param lockTime
 */
public BitcoinTransaction(int version, byte[] inCounter, List<BitcoinTransactionInput> listOfInputs, byte[] outCounter, List<BitcoinTransactionOutput> listOfOutputs, int lockTime) {

	this.marker=1;
	this.flag=0;
	this.version=version;
	this.inCounter=inCounter;
	this.listOfInputs=listOfInputs;
	this.outCounter=outCounter;
	this.listOfOutputs=listOfOutputs;
	this.listOfScriptWitnessItem=new ArrayList<>();
	this.lockTime=lockTime;
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
public BitcoinTransaction(byte marker, byte flag, int version, byte[] inCounter, List<BitcoinTransactionInput> listOfInputs, byte[] outCounter, List<BitcoinTransactionOutput> listOfOutputs, List<BitcoinScriptWitnessItem> listOfScriptWitnessItem, int lockTime) {
	this.marker=marker;
	this.flag=flag;
	this.version=version;
	this.inCounter=inCounter;
	this.listOfInputs=listOfInputs;
	this.outCounter=outCounter;
	this.listOfOutputs=listOfOutputs;
	this.listOfScriptWitnessItem=listOfScriptWitnessItem;
	this.lockTime=lockTime;
}

public int getVersion() {
	return this.version;
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
	return this.listOfInputs;
}

public byte[] getOutCounter() {
	return this.outCounter;
}

public List<BitcoinTransactionOutput> getListOfOutputs() {
	return this.listOfOutputs;
}

public List<BitcoinScriptWitnessItem> getBitcoinScriptWitness() {
	return this.listOfScriptWitnessItem;
}

public int getLockTime() {
	return this.lockTime;
}

public void set(BitcoinTransaction newTransaction) {
	this.version=newTransaction.getVersion();
	this.marker=newTransaction.getMarker();
	this.flag=newTransaction.getFlag();
	this.inCounter=newTransaction.getInCounter();
	this.listOfInputs=newTransaction.getListOfInputs();
	this.outCounter=newTransaction.getOutCounter();
	this.listOfOutputs=newTransaction.getListOfOutputs();
	this.listOfScriptWitnessItem=newTransaction.getBitcoinScriptWitness();
	this.lockTime=newTransaction.getLockTime();
	
}

/** Writable **/

  @Override
  public void write(DataOutput dataOutput) throws IOException {
    throw new UnsupportedOperationException("write unsupported");
  }

  @Override
  public void readFields(DataInput dataInput) throws IOException {
    throw new UnsupportedOperationException("readFields unsupported");
  }


	/**
	 * Calculates the double SHA256-Hash of a transaction in little endian format. This could be used for certain analysis scenario where one want to investigate the referenced transaction used as an input for a Transaction. Furthermore, it can be used as a unique identifier of the transaction
	 * <p>
	 * It corresponds to the Bitcoin specification of txid (https://bitcoincore.org/en/segwit_wallet_dev/)
	 *
	 * @return byte array containing the hash of the transaction. Note: This one can be compared to a prevTransactionHash. However, if you want to search for it in popular blockchain explorers then you need to apply the function BitcoinUtil.reverseByteArray to it!
	 * @throws java.io.IOException in case of errors reading from the InputStream
	 */
	public byte[] getTransactionHash() throws IOException {
		// convert transaction to byte array
		ByteArrayOutputStream transactionBAOS = new ByteArrayOutputStream();

		byte[] version = BitcoinUtil.reverseByteArray(BitcoinUtil.convertIntToByteArray(getVersion()));
		transactionBAOS.write(version);
		byte[] inCounter = getInCounter();
		transactionBAOS.write(inCounter);
		for (int i = 0; i < getListOfInputs().size(); i++) {
			transactionBAOS.write(getListOfInputs().get(i).getPrevTransactionHash());
			transactionBAOS.write(BitcoinUtil.reverseByteArray(BitcoinUtil.convertIntToByteArray((int) (getListOfInputs().get(i).getPreviousTxOutIndex()))));
			transactionBAOS.write(getListOfInputs().get(i).getTxInScriptLength());
			transactionBAOS.write(getListOfInputs().get(i).getTxInScript());
			transactionBAOS.write(BitcoinUtil.reverseByteArray(BitcoinUtil.convertIntToByteArray((int) (getListOfInputs().get(i).getSeqNo()))));
		}
		byte[] outCounter = getOutCounter();
		transactionBAOS.write(outCounter);
		for (int j = 0; j < getListOfOutputs().size(); j++) {
			transactionBAOS.write(BitcoinUtil.convertBigIntegerToByteArray(getListOfOutputs().get(j).getValue(), 8));
			transactionBAOS.write(getListOfOutputs().get(j).getTxOutScriptLength());
			transactionBAOS.write(getListOfOutputs().get(j).getTxOutScript());
		}
		byte[] lockTime = BitcoinUtil.reverseByteArray(BitcoinUtil.convertIntToByteArray(getLockTime()));
		transactionBAOS.write(lockTime);
		byte[] transactionByteArray = transactionBAOS.toByteArray();
		byte[] firstRoundHash;
		byte[] secondRoundHash;
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			firstRoundHash = digest.digest(transactionByteArray);
			secondRoundHash = digest.digest(firstRoundHash);
		} catch (NoSuchAlgorithmException nsae) {
			LOG.error(nsae);
			return new byte[0];
		}
		return secondRoundHash;
	}


	/**
	 * Calculates the double SHA256-Hash of a transaction in little endian format. It serve as a unique identifier of a transaction, but cannot be used to link the outputs of other transactions as input
	 * <p>
	 * It corresponds to the Bitcoin specification of wtxid (https://bitcoincore.org/en/segwit_wallet_dev/)
	 *
	 * @return byte array containing the hash of the transaction. Note: This one can be compared to a prevTransactionHash. However, if you want to search for it in popular blockchain explorers then you need to apply the function BitcoinUtil.reverseByteArray to it!
	 * @throws java.io.IOException in case of errors reading from the InputStream
	 */
	public byte[] getTransactionHashSegwit() throws IOException {
		// convert transaction to byte array
		ByteArrayOutputStream transactionBAOS = new ByteArrayOutputStream();

		byte[] version = BitcoinUtil.reverseByteArray(BitcoinUtil.convertIntToByteArray(getVersion()));
		transactionBAOS.write(version);
		// check if segwit
		boolean segwit = false;
		if ((getMarker() == 0) && (getFlag() != 0)) {
			segwit = true;
			// we still need to check the case that all witness script stack items for all input transactions are of size 0 => traditional transaction hash calculation
			// cf. https://github.com/bitcoin/bips/blob/master/bip-0141.mediawiki
			// A non-witness program (defined hereinafter) txin MUST be associated with an empty witness field, represented by a 0x00. If all txins are not witness program, a transaction's wtxid is equal to its txid.
			boolean emptyWitness = true;
			for (int k = 0; k < getBitcoinScriptWitness().size(); k++) {
				BitcoinScriptWitnessItem currentItem = getBitcoinScriptWitness().get(k);
				if (currentItem.getStackItemCounter().length > 1) {
					emptyWitness = false;
					break;
				} else if ((currentItem.getStackItemCounter().length == 1) && (currentItem.getStackItemCounter()[0] != 0x00)) {
					emptyWitness = false;
					break;
				}
			}
			if (emptyWitness == true) {
				return getTransactionHashSegwit();
			}
			transactionBAOS.write(getMarker());
			transactionBAOS.write(getFlag());
		}
		byte[] inCounter = getInCounter();
		transactionBAOS.write(inCounter);
		for (int i = 0; i < getListOfInputs().size(); i++) {
			transactionBAOS.write(getListOfInputs().get(i).getPrevTransactionHash());
			transactionBAOS.write(BitcoinUtil.reverseByteArray(BitcoinUtil.convertIntToByteArray((int) (getListOfInputs().get(i).getPreviousTxOutIndex()))));
			transactionBAOS.write(getListOfInputs().get(i).getTxInScriptLength());
			transactionBAOS.write(getListOfInputs().get(i).getTxInScript());
			transactionBAOS.write(BitcoinUtil.reverseByteArray(BitcoinUtil.convertIntToByteArray((int) (getListOfInputs().get(i).getSeqNo()))));
		}
		byte[] outCounter = getOutCounter();
		transactionBAOS.write(outCounter);
		for (int j = 0; j < getListOfOutputs().size(); j++) {
			transactionBAOS.write(BitcoinUtil.convertBigIntegerToByteArray(getListOfOutputs().get(j).getValue(), 8));
			transactionBAOS.write(getListOfOutputs().get(j).getTxOutScriptLength());
			transactionBAOS.write(getListOfOutputs().get(j).getTxOutScript());
		}
		if (segwit) {
			for (int k = 0; k < getBitcoinScriptWitness().size(); k++) {
				BitcoinScriptWitnessItem currentItem = getBitcoinScriptWitness().get(k);
				transactionBAOS.write(currentItem.getStackItemCounter());
				for (int l = 0; l < currentItem.getScriptWitnessList().size(); l++) {
					transactionBAOS.write(currentItem.getScriptWitnessList().get(l).getWitnessScriptLength());
					transactionBAOS.write(currentItem.getScriptWitnessList().get(l).getWitnessScript());
				}
			}
		}
		byte[] lockTime = BitcoinUtil.reverseByteArray(BitcoinUtil.convertIntToByteArray(getLockTime()));
		transactionBAOS.write(lockTime);
		byte[] transactionByteArray = transactionBAOS.toByteArray();
		byte[] firstRoundHash;
		byte[] secondRoundHash;
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			firstRoundHash = digest.digest(transactionByteArray);
			secondRoundHash = digest.digest(firstRoundHash);
		} catch (NoSuchAlgorithmException nsae) {
			LOG.error(nsae);
			return new byte[0];
		}
		return secondRoundHash;
	}

}
