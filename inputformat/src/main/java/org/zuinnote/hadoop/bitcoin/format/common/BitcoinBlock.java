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

import org.apache.hadoop.io.Writable;
import org.zuinnote.hadoop.bitcoin.format.littleendian.EpochDatetime;
import org.zuinnote.hadoop.bitcoin.format.littleendian.HashSHA256;
import org.zuinnote.hadoop.bitcoin.format.littleendian.Magic;
import org.zuinnote.hadoop.bitcoin.format.littleendian.UInt32;
import org.zuinnote.hadoop.bitcoin.format.util.Bytes;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * This class is an object storing relevant fields of a Bitcoin Block.
 * </p>
 *
 * <p>
 * It contains modified code from
 * <a href="https://github.com/bitcoinj/bitcoinj/blob/master/core/src/main/java/org/bitcoinj/core/Block.java">Block.java</a>
 * by Andreas Schildbach.
 * </p>
 **/
public class BitcoinBlock implements Serializable, Writable {

    private UInt32 blockSize;
    private Magic magicNo;
    private UInt32 version;
    private EpochDatetime time;
    private UInt32 bits;
    private UInt32 nonce;
    private HashSHA256 hashPrevBlock;
    private HashSHA256 hashMerkleRoot;
    private List<BitcoinTransaction> transactions;
    private BitcoinAuxPOW auxPOW;

    public BitcoinBlock(UInt32 blockSize, Magic magicNo, UInt32 version, EpochDatetime time, UInt32 bits,
                            UInt32 nonce, HashSHA256 hashPrevBlock, HashSHA256 hashMerkleRoot,
                            List<BitcoinTransaction> transactions, BitcoinAuxPOW auxPOW) {
        this.blockSize = blockSize;
        this.magicNo = magicNo;
        this.version = version;
        this.time = time;
        this.bits = bits;
        this.nonce = nonce;
        this.hashPrevBlock = hashPrevBlock;
        this.hashMerkleRoot = hashMerkleRoot;
        this.transactions = transactions;
        this.auxPOW = auxPOW;
    }

    public BitcoinBlock() {
    }

    public UInt32 getBlockSize() {
        return this.blockSize;
    }

    public void setBlockSize(UInt32 blockSize) {
        this.blockSize = blockSize;
    }

    public Magic getMagicNo() {
        return this.magicNo;
    }

    public void setMagicNo(Magic magicNo) {
        this.magicNo = magicNo;
    }

    public UInt32 getVersion() {
        return this.version;
    }

    public void setVersion(UInt32 version) {
        this.version = version;
    }

    public EpochDatetime getTime() {
        return this.time;
    }

    public void setTime(EpochDatetime time) {
        this.time = time;
    }

    public UInt32 getBits() {
        return this.bits;
    }

    public void setBits(UInt32 bits) {
        this.bits = bits;
    }

    public UInt32 getNonce() {
        return this.nonce;
    }

    public void setNonce(UInt32 nonce) {
        this.nonce = nonce;
    }

    public HashSHA256 getHashPrevBlock() {
        return this.hashPrevBlock;
    }

    public String getHashPrevBlockString() {
        return getHashPrevBlock().toString();
    }

    public void setHashPrevBlock(HashSHA256 hashPrevBlock) {
        this.hashPrevBlock = hashPrevBlock;
    }

    public HashSHA256 getHashMerkleRoot() {
        return this.hashMerkleRoot;
    }

    public void setHashMerkleRoot(HashSHA256 hashMerkleRoot) {
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
     * @return A positive 64-bit integer representing the number of seconds elapsed since the Epoch.
     */
    public long getEpochTime() {
        return getTime().longValue();
    }

    /**
     * Get the time field as a Java Date.
     *
     * @return The time-stamp for the block as a java.util.Date object.
     */
    public Date getDate() {
        return getTime().getDate();
    }

    /**
     * Build the Merkle Tree for this block.
     * <p>
     * This code is a modified version of the method of the same name from
     * https://github.com/bitcoinj/bitcoinj/blob/master/core/src/main/java/org/bitcoinj/core/Block.java
     * by Andreas Schildbach.
     *
     * @return The Merkle Tree for this block.
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

    public HashSHA256 getHash() {
        return new HashSHA256(getHeader());
    }

    public String getHashString() {
        return getHash().toString();
    }

    public Bytes getHeader() {
        return new Bytes(version, hashPrevBlock, BitcoinUtil.reverseByteArray(calculateMerkleRoot()),
                            time, bits, nonce);
    }
}
