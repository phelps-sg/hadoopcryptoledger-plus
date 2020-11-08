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

import java.io.*;

import org.apache.hadoop.io.Writable;
import org.apache.hadoop.io.file.tfile.ByteArray;

import java.nio.ByteBuffer;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;

/**
 * <p>
 * This class is an object storing relevant fields of a Bitcoin Block.
 * </p>
 *
 * <p>
 * It contains modified code from
 *  <a href="https://github.com/bitcoinj/bitcoinj/blob/master/core/src/main/java/org/bitcoinj/core/Block.java">Block.java</a>
 *  by Andreas Schildbach.
 *  </p>
 **/
public class BitcoinBlock implements Serializable, Writable {

    public static final int HEADER_SIZE_BYTES = 6*32;

    private LittleEndianUInt32 blockSize;
    private byte[] magicNo;
    private LittleEndianUInt32 version;
    private LittleEndianUInt32 time;
    private LittleEndianUInt32 bits;
    private LittleEndianUInt32 nonce;
    private byte[] hashPrevBlock;
    private byte[] hashMerkleRoot;
    private long transactionCounter;
    private List<BitcoinTransaction> transactions;
    private BitcoinAuxPOW auxPOW;

    public BitcoinBlock() {
        this.magicNo = new byte[0];
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

    public LittleEndianUInt32 getBits() {
        return this.bits;
    }

    public void setBits(LittleEndianUInt32 bits) {
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

    public byte[] getHashPrevBlock() { return this.hashPrevBlock; }

    public String getHashPrevBlockString() {
        return BitcoinUtil.convertByteArrayToHexString(getHashPrevBlock());
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

    @Override
    public void write(DataOutput dataOutput) throws IOException {
        throw new UnsupportedOperationException("write unsupported");
    }

    @Override
    public void readFields(DataInput dataInput) throws IOException {
        throw new UnsupportedOperationException("readFields unsupported");
    }

    /**
     * Get the time field as a Unix epoch time.
     *
     * @return  A positive 64-bit integer representing the number of seconds elapsed since the Epoch.
     */
    public long getEpochTime() {
        return getTime().longValue();
    }

    /**
     * Get the time field as a Java Date.
     *
     * @return  The time-stamp for the block as a java.util.Date object.
     */
    public Date getDate() {
        return new Date(getEpochTime() * 1000L);
    }

    /**
     * Build the Merkle Tree for this block.
     *
     * This code is a modified version of the method of the same name from
     *  https://github.com/bitcoinj/bitcoinj/blob/master/core/src/main/java/org/bitcoinj/core/Block.java
     *  by Andreas Schildbach.
     * @return  The Merkle Tree for this block.
     */
    public List<byte[]> buildMerkleTree() {
        // The Merkle root is based on a tree of hashes calculated from the transactions:
        //
        //     root
        //      / \
        //   A      B
        //  / \    / \
        // t1 t2 t3 t4
        //
        // The tree is represented as a list: t1,t2,t3,t4,A,B,root where each
        // entry is a hash.
        //
        // The hashing algorithm is double SHA-256. The leaves are a hash of the serialized contents of the transaction.
        // The interior nodes are hashes of the concatenation of the two child hashes.
        //
        // This structure allows the creation of proof that a transaction was included into a block without having to
        // provide the full block contents. Instead, you can provide only a Merkle branch. For example to prove tx2 was
        // in a block you can just provide tx2, the hash(tx1) and B. Now the other party has everything they need to
        // derive the root, which can be checked against the block header. These proofs aren't used right now but
        // will be helpful later when we want to download partial block contents.
        //
        // Note that if the number of transactions is not even the last tx is repeated to make it so (see
        // tx3 above). A tree with 5 transactions would look like this:
        //
        //         root
        //        /     \
        //       1        5
        //     /   \     / \
        //    2     3    4  4
        //  / \   / \   / \
        // t1 t2 t3 t4 t5 t5
        ArrayList<byte[]> tree = new ArrayList<>(transactions.size());
        // Start by adding all the hashes of the transactions as leaves of the tree.
        for (BitcoinTransaction tx : transactions) {
            byte[] id;
            id = BitcoinUtil.reverseByteArray(tx.getTransactionHash());
            tree.add(id);
        }
        int levelOffset = 0; // Offset in the list where the currently processed level starts.
        // Step through each level, stopping when we reach the root (levelSize == 1).
        for (int levelSize = transactions.size(); levelSize > 1; levelSize = (levelSize + 1) / 2) {
            // For each pair of nodes on that level:
            for (int left = 0; left < levelSize; left += 2) {
                // The right hand node can be the same as the left hand, in the case where we don't have enough
                // transactions.
                int right = Math.min(left + 1, levelSize - 1);
                byte[] leftBytes = BitcoinUtil.reverseByteArray(tree.get(levelOffset + left));
                byte[] rightBytes = BitcoinUtil.reverseByteArray(tree.get(levelOffset + right));
                tree.add(BitcoinUtil.reverseByteArray(BitcoinUtil.hashTwice(leftBytes, rightBytes)));
            }
            // Move to the next level.
            levelOffset += levelSize;
        }
        return tree;
    }

    public byte[] calculateMerkleRoot() {
        List<byte[]> tree = buildMerkleTree();
        return tree.get(tree.size() - 1);
    }

    public byte[] getHash() {
        return BitcoinUtil.reverseByteArray(BitcoinUtil.hashTwice(getHeader()));
    }

    public String getHashString() {
        return BitcoinUtil.convertByteArrayToHexString(getHash());
    }

    public byte[] getHeader() {
        ByteArrayOutputStream header = new ByteArrayOutputStream();
        try {
            header.write(version.getBytes());
            header.write(hashPrevBlock);
            header.write(BitcoinUtil.reverseByteArray(calculateMerkleRoot()));
            header.write(time.getBytes());
            header.write(bits.getBytes());
            header.write(nonce.getBytes());
            return header.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);  // Never happens
        }
    }
}
