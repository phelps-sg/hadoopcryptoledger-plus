/*
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
 */

package org.zuinnote.hadoop.bitcoin.format.common;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.Writable;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;

/**
* This class is an object storing relevant fields of a Bitcoin Block. 
*/

public class BitcoinBlock implements Serializable, Writable {

    private LittleEndianUInt32 blockSize;
    private byte[] magicNo;
    private LittleEndianUInt32 version;
    private LittleEndianUInt32 time;
    private byte[] bits;
    private LittleEndianUInt32 nonce;
    private byte[] hashPrevBlock;
    private byte[] hashMerkleRoot;
    private long transactionCounter;
    private List<BitcoinTransaction> transactions;
    private BitcoinAuxPOW auxPOW;

    public BitcoinBlock() {
        this.magicNo = new byte[0];
        this.bits = new byte[0];
        this.transactionCounter = 0;
        this.hashPrevBlock = new byte[0];
        this.hashMerkleRoot = new byte[0];
        this.transactions = new ArrayList<>();
        this.auxPOW = new BitcoinAuxPOW();
    }


    public LittleEndianUInt32 getBlockSize() {
        return this.blockSize;
    }

    public void setBlockSize(LittleEndianUInt32 blockSize) {
        this.blockSize = blockSize;
    }


    public byte[] getMagicNo() {
        return this.magicNo;
    }

    public void setMagicNo(byte[] magicNo) {
        this.magicNo = magicNo;
    }

    public LittleEndianUInt32 getVersion() {
        return this.version;
    }

    public void setVersion(LittleEndianUInt32 version) {
        this.version = version;
    }

    public LittleEndianUInt32 getTime() {
        return this.time;
    }

    public void setTime(LittleEndianUInt32 time) {
        this.time = time;
    }

    public byte[] getBits() {
        return this.bits;
    }

    public void setBits(byte[] bits) {
        this.bits = bits;
    }

    public LittleEndianUInt32 getNonce() {
        return this.nonce;
    }

    public void setNonce(LittleEndianUInt32 nonce) {
        this.nonce = nonce;
    }

    public long getTransactionCounter() {
        return this.transactionCounter;
    }


    public void setTransactionCounter(long transactionCounter) {
        this.transactionCounter = transactionCounter;
    }

    public byte[] getHashPrevBlock() {
        return this.hashPrevBlock;
    }

    public void setHashPrevBlock(byte[] hashPrevBlock) {
        this.hashPrevBlock = hashPrevBlock;
    }

    public byte[] getHashMerkleRoot() {
        return this.hashMerkleRoot;
    }

    public void setHashMerkleRoot(byte[] hashMerkleRoot) {
        this.hashMerkleRoot = hashMerkleRoot;
    }

    public List<BitcoinTransaction> getTransactions() {
        return this.transactions;
    }

    public void setTransactions(List<BitcoinTransaction> transactions) {
        this.transactions = transactions;
    }

    public BitcoinAuxPOW getAuxPOW() {
        return this.auxPOW;
    }


    public void setAuxPOW(BitcoinAuxPOW auxPOW) {
        this.auxPOW = auxPOW;
    }

    public void set(BitcoinBlock newBitcoinBlock) {
        this.blockSize = newBitcoinBlock.getBlockSize();
        this.magicNo = newBitcoinBlock.getMagicNo();
        this.version = newBitcoinBlock.getVersion();
        this.time = newBitcoinBlock.getTime();
        this.bits = newBitcoinBlock.getBits();
        this.nonce = newBitcoinBlock.getNonce();
        this.transactionCounter = newBitcoinBlock.getTransactionCounter();
        this.hashPrevBlock = newBitcoinBlock.getHashPrevBlock();
        this.hashMerkleRoot = newBitcoinBlock.getHashMerkleRoot();
        this.transactions = newBitcoinBlock.getTransactions();
        this.auxPOW = newBitcoinBlock.getAuxPOW();
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

    public long getEpochTime() {
        return getTime().longValue();
    }

    public Date getDate() {
        return new Date(getEpochTime() * 1000L);
    }

}
