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
package org.zuinnote.hadoop.ethereum.format.common;

import java.io.Serializable;
import java.math.BigInteger;

/**
 *
 *
 */
public class EthereumBlockHeader implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 2446091414374317679L;
    private byte[] parentHash;
    private byte[] uncleHash;
    private byte[] coinBase;
    private byte[] stateRoot;
    private byte[] txTrieRoot;
    private byte[] receiptTrieRoot;
    private byte[] logsBloom;
    private byte[] difficulty;
    private long timestamp;
    private BigInteger number;
    private byte[] numberRaw;
    private BigInteger gasLimit;
    private byte[] gasLimitRaw;
    private BigInteger gasUsed;
    private byte[] gasUsedRaw;
    private byte[] mixHash;
    private byte[] extraData;
    private byte[] nonce;


    public EthereumBlockHeader() {
        // please use the set method to modify the data
    }


    public byte[] getParentHash() {
        return parentHash;
    }


    public void setParentHash(byte[] parentHash) {
        this.parentHash = parentHash;
    }


    public byte[] getUncleHash() {
        return uncleHash;
    }


    public void setUncleHash(byte[] uncleHash) {
        this.uncleHash = uncleHash;
    }


    public byte[] getCoinBase() {
        return coinBase;
    }


    public void setCoinBase(byte[] coinBase) {
        this.coinBase = coinBase;
    }


    public byte[] getStateRoot() {
        return stateRoot;
    }


    public void setStateRoot(byte[] stateRoot) {
        this.stateRoot = stateRoot;
    }


    public byte[] getTxTrieRoot() {
        return txTrieRoot;
    }


    public void setTxTrieRoot(byte[] txTrieRoot) {
        this.txTrieRoot = txTrieRoot;
    }


    public byte[] getReceiptTrieRoot() {
        return receiptTrieRoot;
    }


    public void setReceiptTrieRoot(byte[] receiptTrieRoot) {
        this.receiptTrieRoot = receiptTrieRoot;
    }


    public byte[] getLogsBloom() {
        return logsBloom;
    }


    public void setLogsBloom(byte[] logsBloom) {
        this.logsBloom = logsBloom;
    }


    public byte[] getDifficulty() {
        return difficulty;
    }


    public void setDifficulty(byte[] difficulty) {
        this.difficulty = difficulty;
    }


    public long getTimestamp() {
        return timestamp;
    }


    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }


    public BigInteger getNumber() {
        if (number == null) {
            this.number = EthereumUtil.convertVarNumberToBigInteger(this.numberRaw);
        }
        return number;
    }


    public void setNumber(BigInteger number) {
        this.number = number;
    }


    public BigInteger getGasLimit() {
        if (gasLimit == null) {
            this.gasLimit = EthereumUtil.convertVarNumberToBigInteger(this.gasLimitRaw);
        }
        return gasLimit;
    }


    public void setGasLimit(BigInteger gasLimit) {
        this.gasLimit = gasLimit;
    }


    public BigInteger getGasUsed() {
        if (gasUsed == null) {
            this.gasUsed = EthereumUtil.convertVarNumberToBigInteger(this.gasUsedRaw);
        }
        return gasUsed;
    }


    public void setGasUsed(BigInteger gasUsed) {
        this.gasUsed = gasUsed;
    }


    public byte[] getMixHash() {
        return mixHash;
    }


    public void setMixHash(byte[] mixHash) {
        this.mixHash = mixHash;
    }


    public byte[] getExtraData() {
        return extraData;
    }


    public void setExtraData(byte[] extraData) {
        this.extraData = extraData;
    }


    public byte[] getNonce() {
        return nonce;
    }


    public void setNonce(byte[] nonce) {
        this.nonce = nonce;
    }


    public void set(EthereumBlockHeader newEthereumBlockHeader) {
        this.parentHash = newEthereumBlockHeader.getParentHash();
        this.uncleHash = newEthereumBlockHeader.getUncleHash();
        this.coinBase = newEthereumBlockHeader.getCoinBase();
        this.stateRoot = newEthereumBlockHeader.getStateRoot();
        this.txTrieRoot = newEthereumBlockHeader.getTxTrieRoot();
        this.receiptTrieRoot = newEthereumBlockHeader.getReceiptTrieRoot();
        this.logsBloom = newEthereumBlockHeader.getLogsBloom();
        this.difficulty = newEthereumBlockHeader.getDifficulty();
        this.timestamp = newEthereumBlockHeader.getTimestamp();
        this.number = newEthereumBlockHeader.getNumber();
        this.numberRaw = newEthereumBlockHeader.getNumberRaw();
        this.gasLimit = newEthereumBlockHeader.getGasLimit();
        this.gasLimitRaw = newEthereumBlockHeader.getGasLimitRaw();
        this.gasUsed = newEthereumBlockHeader.getGasUsed();
        this.gasUsedRaw = newEthereumBlockHeader.getGasUsedRaw();
        this.mixHash = newEthereumBlockHeader.getMixHash();
        this.extraData = newEthereumBlockHeader.getExtraData();
        this.nonce = newEthereumBlockHeader.getNonce();
    }


    public byte[] getNumberRaw() {
        return numberRaw;
    }


    public void setNumberRaw(byte[] numberRaw) {
        this.numberRaw = numberRaw;
    }


    public byte[] getGasLimitRaw() {
        return gasLimitRaw;
    }


    public void setGasLimitRaw(byte[] gasLimitRaw) {
        this.gasLimitRaw = gasLimitRaw;
    }


    public byte[] getGasUsedRaw() {
        return gasUsedRaw;
    }


    public void setGasUsedRaw(byte[] gasUsedRaw) {
        this.gasUsedRaw = gasUsedRaw;
    }


}
