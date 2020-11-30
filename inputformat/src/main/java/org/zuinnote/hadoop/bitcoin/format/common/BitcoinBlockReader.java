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
import org.zuinnote.hadoop.bitcoin.format.exception.BitcoinBlockReadException;
import org.zuinnote.hadoop.bitcoin.format.littleendian.*;
import org.zuinnote.hadoop.bitcoin.format.util.Bytes;
import org.zuinnote.hadoop.ethereum.format.common.EthereumUtil;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This class reads Bitcoin blocks (in raw network format) from an input stream and returns Java objects of the
 * class BitcoinBlock. It reuses code from the LineRecordReader due to its robustness and well-tested functionality.
 **/
public class BitcoinBlockReader {

    private static final Log LOG = LogFactory.getLog(BitcoinBlockReader.class.getName());

    private int maxSizeBitcoinBlock = 0;
    private boolean useDirectBuffer = false;
    private boolean readAuxPow = false;
    private boolean filterSpecificMagic = false;
    private byte[][] specificMagicByteArray;
    private ByteBuffer preAllocatedDirectByteBuffer;
    private InputStream bin;

    /**
     * Create a BitcoinBlock reader that reads from the given stream and uses the given parameters for configuration.
     * Note that it is assumed that the validity of this configuration is checked by BitcoinBlockRecordReader.
     *
     * @param in                     Input stream to read from
     * @param maxSizeBitcoinBlock    Maximum size of a Bitcoinblock.
     * @param bufferSize             size of the memory buffer for the givenInputStream
     * @param specificMagicByteArray filters by specific block magic numbers if not null.
     * @param useDirectBuffer        experimental feature to use a DirectByteBuffer instead of a HeapByteBuffer
     **/
    public BitcoinBlockReader(InputStream in, int maxSizeBitcoinBlock, int bufferSize, byte[][] specificMagicByteArray, boolean useDirectBuffer) {
        this(in, maxSizeBitcoinBlock, bufferSize, specificMagicByteArray, useDirectBuffer, false);
    }

    /**
     * Create a BitcoinBlock reader that reads from the given stream and uses the given parameters for configuration. Note it assumed that the validity of this configuration is checked by BitcoinBlockRecordReader
     *
     * @param in                     Input stream to read from
     * @param maxSizeBitcoinBlock    Maximum size of a Bitcoinblock.
     * @param bufferSize             size of the memory buffer for the givenInputStream
     * @param specificMagicByteArray filters by specific block magic numbers if not null.
     * @param useDirectBuffer        experimental feature to use a DirectByteBuffer instead of a HeapByteBuffer
     * @param readAuxPow             true if auxPow information should be parsed, false if not
     **/
    public BitcoinBlockReader(InputStream in, int maxSizeBitcoinBlock, int bufferSize, byte[][] specificMagicByteArray, boolean useDirectBuffer, boolean readAuxPow) {
        this.maxSizeBitcoinBlock = maxSizeBitcoinBlock;
        this.specificMagicByteArray = specificMagicByteArray;
        this.useDirectBuffer = useDirectBuffer;
        if (specificMagicByteArray != null) {
            this.filterSpecificMagic = true;
        }
        if (bufferSize == 0) { // use original stream
            this.bin = in;
        } else {
            this.bin = new BufferedInputStream(in, bufferSize);
        }
        if (this.useDirectBuffer) { // in case of a DirectByteBuffer we do allocation only once for the maximum size of one block, otherwise we will have a high cost for reallocation
            preAllocatedDirectByteBuffer = ByteBuffer.allocateDirect(this.maxSizeBitcoinBlock);
        }
        this.readAuxPow = readAuxPow;
    }

    /**
     * Seek for a valid block start according to the following algorithm:
     * (1) find the magic of the block
     * (2) Check that the block can be fully read and that block size is smaller than maximum block size
     * This functionality is particularly useful for file processing in Big Data systems,
     * such as Hadoop and Co where we work indepently on different filesplits and cannot expect that the
     * Bitcoin block starts directly at the beginning of the stream.
     *
     * @throws org.zuinnote.hadoop.bitcoin.format.exception.BitcoinBlockReadException in case of format errors of the Bitcoin Blockchain data
     **/
    public void seekBlockStart() throws IOException {
        if (!(this.filterSpecificMagic)) {
            throw new BitcoinBlockReadException("Error: Cannot seek to a block start, because no magic(s) are defined.");
        }
        findMagic();
        // validate it is a full block
        checkFullBlock();
    }

    /**
     * Read a block into a Java object of the class Bitcoin Block. This makes analysis very easy, but might be slower for some type of analytics where you are only interested in small parts of the block. In this case it is recommended to use {@link #readRawBlock}
     *
     * @return an instance of BitcoinBlock if another block is available, otherwise null.
     * @throws java.io.IOException in case of errors of reading the Bitcoin Blockchain data
     */
    public BitcoinBlock readBlock() throws IOException {
        ByteBuffer buffer = readRawBlock();
        if (buffer == null) {
            return null;
        }
        Magic magicNo = new Magic(buffer);
        UInt32 blockSize = new UInt32(buffer);
        UInt32 version = new UInt32(buffer);
        HashSHA256 hashPrevBlock = new HashSHA256(buffer);
        HashSHA256 hashMerkleRoot = new HashSHA256(buffer);
        EpochDatetime time = new EpochDatetime(buffer);
        UInt32 bits = new UInt32(buffer);
        UInt32 nonce = new UInt32(buffer);
        BitcoinAuxPOW auxPOW = parseAuxPow(buffer);
        List<BitcoinTransaction> transactions = parseTransactions(buffer);
        return new BitcoinBlock(blockSize, magicNo, version, time, bits, nonce, hashPrevBlock,
                                        hashMerkleRoot, transactions, auxPOW);
    }

    /**
     * Parses AuxPOW information (cf. https://en.bitcoin.it/wiki/Merged_mining_specification)
     *
     * @param buffer
     * @return
     */
    public BitcoinAuxPOW parseAuxPow(ByteBuffer buffer) {
        if (!this.readAuxPow) {
            return null;
        }
        // in case it does not contain auxpow we need to reset
        buffer.mark();
        UInt32 version = new UInt32(buffer);
        UIntVar inCounter = new UIntVar(buffer);
        byte[] currentTransactionInputPrevTransactionHash = new byte[32];
        buffer.get(currentTransactionInputPrevTransactionHash, 0, 32);
        byte[] prevTxOutIdx = new byte[4];
        buffer.get(prevTxOutIdx, 0, 4);
        // detect auxPow
        buffer.reset();
        byte[] expectedPrevTransactionHash = new byte[]{0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};
        byte[] expectedPrevOutIdx = new byte[]{(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF};

        if ((!(Arrays.equals(prevTxOutIdx, expectedPrevOutIdx)) || (!(Arrays.equals(currentTransactionInputPrevTransactionHash, expectedPrevTransactionHash))))) {
            return null;
        }
        // continue reading auxPow
        // txIn (for all of them)
        version = new UInt32(buffer);

        inCounter = new UIntVar(buffer);
        List<BitcoinTransactionInput> inputs = parseTransactionInputs(buffer, inCounter.intValue());

        // txOut (for all of them)
        UIntVar outCounter = new UIntVar(buffer);
//        byte[] currentOutCounterVarInt = BitcoinUtil.convertVarIntByteBufferToByteArray(buffer);
//        long currentNoOfOutput = BitcoinUtil.getVarInt(currentOutCounterVarInt);
        List<BitcoinTransactionOutput> outputs = parseTransactionOutputs(buffer, outCounter.intValue());
        EpochDatetime lockTime = new EpochDatetime(buffer);
        BitcoinTransaction coinbaseTransaction =
                new BitcoinTransaction(version, inCounter, outCounter, inputs, outputs, null, lockTime);

        // read branches
        // coinbase branch
        byte[] coinbaseParentBlockHeaderHash = new byte[32];
        buffer.get(coinbaseParentBlockHeaderHash, 0, 32);

        BitcoinAuxPOWBranch coinbaseBranch = parseAuxPOWBranch(buffer);

        // auxchain branch
        BitcoinAuxPOWBranch auxChainBranch = parseAuxPOWBranch(buffer);

        // parent Block header

        byte[] parentBlockBits = new byte[4];
        byte[] parentBlockHashMerkleRoot = new byte[32];
        byte[] parentBlockHashPrevBlock = new byte[32];

        // version
        int parentBlockVersion = buffer.getInt();
        // hashPrevBlock
        buffer.get(parentBlockHashPrevBlock, 0, 32);
        // hashMerkleRoot
        buffer.get(parentBlockHashMerkleRoot, 0, 32);
        // time
        int parentBlockTime = buffer.getInt();
        // bits/difficulty
        buffer.get(parentBlockBits, 0, 4);
        // nonce
        int parentBlockNonce = buffer.getInt();
        BitcoinAuxPOWBlockHeader parentBlockheader = new BitcoinAuxPOWBlockHeader(parentBlockVersion, parentBlockHashPrevBlock, parentBlockHashMerkleRoot, parentBlockTime, parentBlockBits, parentBlockNonce);

        return new BitcoinAuxPOW(version, coinbaseTransaction, coinbaseParentBlockHeaderHash, coinbaseBranch, auxChainBranch, parentBlockheader);
    }

    /**
     * Parse an AUXPowBranch
     *
     * @param rawByteBuffer ByteBuffer from which the AuxPOWBranch should be parsed
     * @return AuxPOWBranch
     */
    public BitcoinAuxPOWBranch parseAuxPOWBranch(ByteBuffer rawByteBuffer) {

        byte[] noOfLinksVarInt = BitcoinUtil.convertVarIntByteBufferToByteArray(rawByteBuffer);
        long currentNoOfLinks = BitcoinUtil.getVarInt(noOfLinksVarInt);
        ArrayList<byte[]> links = new ArrayList((int) currentNoOfLinks);
        for (int i = 0; i < currentNoOfLinks; i++) {
            byte[] currentLink = new byte[32];
            rawByteBuffer.get(currentLink, 0, 32);
            links.add(currentLink);
        }
        byte[] branchSideBitmask = new byte[4];
        rawByteBuffer.get(branchSideBitmask, 0, 4);
        return new BitcoinAuxPOWBranch(noOfLinksVarInt, links, branchSideBitmask);
    }

    /**
     * Parses the Bitcoin transactions in a byte buffer.
     *
     * @param buffer    ByteBuffer from which the transactions have to be parsed
     * @return Array of transactions
     */
    public List<BitcoinTransaction> parseTransactions(ByteBuffer buffer) {
        long noOfTransactions = new UIntVar(buffer).longValue();
        ArrayList<BitcoinTransaction> result = new ArrayList<>((int) noOfTransactions);
        for (int k = 0; k < noOfTransactions; k++) {
            UInt32 version = new UInt32(buffer);
            UIntVar inCounter = new UIntVar(buffer);

            boolean segwit = false;
            byte marker = 1;
            byte flag = 0;
            // check segwit marker
            if (inCounter.intValue() == 0) {
                // this seems to be segwit - lets be sure
                // check segwit flag
                buffer.mark();
                byte segwitFlag = buffer.get();
                if (segwitFlag != 0) {
                    // load the real number of inputs
                    segwit = true;
                    marker = 0;
                    flag = segwitFlag;
                    inCounter = new UIntVar(buffer);
                } else {
                    //TODO Exception/assertion?
                    LOG.warn("It seems a block with 0 transaction inputs was found");
                    buffer.reset();
                }
            }

            List<BitcoinTransactionInput> inputs = parseTransactionInputs(buffer, inCounter.intValue());
            UIntVar outCounter = new UIntVar(buffer);
            List<BitcoinTransactionOutput> outputs = parseTransactionOutputs(buffer, outCounter.intValue());

            List<BitcoinScriptWitnessItem> scriptWitnessItems;
            if (segwit) {
                scriptWitnessItems = new ArrayList<>();
                for (int i = 0; i < inCounter.intValue(); i++) {
                    UIntVar witnessCounter = new UIntVar(buffer);
                    List<BitcoinScriptWitness> currentTransactionSegwit = new ArrayList<>(witnessCounter.intValue());
                    for (int j = 0; j < witnessCounter.intValue(); j++) {
                        UIntVar segwitScriptLength = new UIntVar(buffer);
                        // read segwit script
                        byte[] currentTransactionInSegwitScript = new byte[segwitScriptLength.intValue()];
                        buffer.get(currentTransactionInSegwitScript, 0, segwitScriptLength.intValue());
                        currentTransactionSegwit.add(new BitcoinScriptWitness(segwitScriptLength, currentTransactionInSegwitScript));
                    }
                    scriptWitnessItems.add(new BitcoinScriptWitnessItem(witnessCounter, currentTransactionSegwit));
                }
            } else {
                scriptWitnessItems = new ArrayList<>();
            }
            EpochDatetime lockTime = new EpochDatetime(buffer);
            result.add(new BitcoinTransaction(version, marker, flag, inCounter, outCounter,
                                                                inputs, outputs, scriptWitnessItems, lockTime));
        }
        return result;
    }

    /**
     * Parses the Bitcoin transaction inputs in a byte buffer.
     *
     * @param buffer         ByteBuffer from which the transaction inputs are to be parsed.
     * @param numInputs      Number of transaction inputs to parse.
     * @return               List of parsed transaction inputs.
     */
    public List<BitcoinTransactionInput> parseTransactionInputs(ByteBuffer buffer, long numInputs) {
        ArrayList<BitcoinTransactionInput> inputs = new ArrayList<>((int) numInputs);
        for (int i = 0; i < numInputs; i++) {
            HashSHA256 prevTransactionHash = new HashSHA256(buffer);
            UInt32 prevTxOutIdx = new UInt32(buffer);

            // read inScript
            UIntVar inScriptLength = new UIntVar(buffer);
            byte[] inScript = new byte[inScriptLength.intValue()];
            buffer.get(inScript, 0, inScriptLength.intValue());

            UInt32 seqNo = new UInt32(buffer);

            inputs.add(new BitcoinTransactionInput(prevTransactionHash, prevTxOutIdx, inScriptLength, inScript, seqNo));
        }
        return inputs;
    }

    /**
     * Parses the Bitcoin transaction outputs in a byte buffer.
     *
     * @param buffer ByteBuffer from which the transaction outputs have to be parsed
     * @param numOutputs Number of expected transaction outputs
     *
     * @return Array of transactions
     *
     */
    public List<BitcoinTransactionOutput> parseTransactionOutputs(ByteBuffer buffer, long numOutputs) {

        ArrayList<BitcoinTransactionOutput> outputs = new ArrayList<>((int) (numOutputs));
        for (int i = 0; i < numOutputs; i++) {

            // read value
            byte[] currentTransactionOutputValueArray = new byte[8];
            buffer.get(currentTransactionOutputValueArray);
            BigInteger value =
                    new BigInteger(1, BitcoinUtil.reverseByteArray(currentTransactionOutputValueArray));

            BitcoinScript outScript = new BitcoinScript(buffer);

            outputs.add(new BitcoinTransactionOutput(value, outScript));
        }

        return outputs;
    }

    /**
     * Reads a raw Bitcoin block into a ByteBuffer. This method is recommended if you are only interested in a small part of the block and do not need the deserialization of the full block, ie in case you generally skip a lot of blocks
     *
     *
     * @return ByteBuffer containing the block
     *
     * @throws org.zuinnote.hadoop.bitcoin.format.exception.BitcoinBlockReadException in case of format errors of the Bitcoin Blockchain data
     **/
    public ByteBuffer readRawBlock() throws IOException {
        Long rawBlockSize = null;
        while (rawBlockSize == null) { // in case of filtering by magic no we skip blocks until we reach a valid magicNo or end of Block
            // check if more to read
            if (this.bin.available() < 1) {
                return null;
            }
            rawBlockSize = skipBlocksNotInFilter();
        }
        int blockSize = rawBlockSize.intValue() + 8;
        assert blockSize > 0;
        if (blockSize > this.maxSizeBitcoinBlock) {
            throw new BitcoinBlockReadException("Error: Block size is larger then defined in configuration - Please increase it if this is a valid block");
        }

        // read full block into ByteBuffer
        byte[] fullBlock = new byte[blockSize];
        int totalByteRead = 0;
        int readByte;
        while ((readByte = this.bin.read(fullBlock, totalByteRead, blockSize - totalByteRead)) > -1) {
            totalByteRead += readByte;
            if (totalByteRead >= blockSize) {
                break;
            }
        }
        if (totalByteRead != blockSize) {
            throw new BitcoinBlockReadException("Error: Could not read full block");
        }

        ByteBuffer result;
        if (!(this.useDirectBuffer)) {
            result = ByteBuffer.wrap(fullBlock);
        } else {
            preAllocatedDirectByteBuffer.clear(); // clear out old bytebuffer
            preAllocatedDirectByteBuffer.limit(fullBlock.length); // limit the bytebuffer
            result = preAllocatedDirectByteBuffer;
            result.put(fullBlock);
            result.flip(); // put in read mode
        }
        result.order(ByteOrder.LITTLE_ENDIAN);
        return result;
    }

    /**
     * This function is used to read from a raw Bitcoin block some identifier. Note: Does not change ByteBuffer position
     *
     * @param rawByteBuffer ByteBuffer as read by readRawBlock
     * @return byte array containing hashMerkleRoot and prevHashBlock
     */
    public byte[] getKeyFromRawBlock(ByteBuffer rawByteBuffer) {
        rawByteBuffer.mark();

        Magic magicNo = new Magic(rawByteBuffer);
        rawByteBuffer.getInt();
        rawByteBuffer.getInt();
        HashSHA256 hashPrevBlock = new HashSHA256(rawByteBuffer);
        HashSHA256 hashMerkleRoot = new HashSHA256(rawByteBuffer);

        rawByteBuffer.reset();

        return new Bytes(hashMerkleRoot, hashPrevBlock).getBytes();
    }

    /**
     * Closes the reader
     *
     * @throws java.io.IOException in case of errors reading from the InputStream
     */
    public void close() throws IOException {
        this.bin.close();
    }

    /**
     * Finds the start of a block by looking for the specified magics in the current InputStream
     *
     * @throws org.zuinnote.hadoop.bitcoin.format.exception.BitcoinBlockReadException in case of errors reading Blockchain data
     *
     */
    private void findMagic() throws IOException {
        // search if first byte of any magic matches
        // search up to maximum size of a bitcoin block
        int currentSeek = 0;
        while (currentSeek != this.maxSizeBitcoinBlock) {
            int firstByte = -1;
            this.bin.mark(4); // magic is always 4 bytes
            firstByte = this.bin.read();
            if (firstByte == -1) {
                throw new BitcoinBlockReadException("Error: Did not find defined magic within current stream");
            }
            if (checkForMagicBytes(firstByte)) {
                return;
            }
            if (currentSeek == this.maxSizeBitcoinBlock) {
                throw new BitcoinBlockReadException("Error: Cannot seek to a block start, because no valid block found within the maximum size of a Bitcoin block. Check data or increase maximum size of Bitcoin block.");
            }
            // increase by one byte if magic not found yet
            this.bin.reset();
            if (this.bin.skip(1) != 1) {
                //TODO: Exception?
                LOG.error("Error cannot skip 1 byte in InputStream");
            }
            currentSeek++;
        }
    }

    /**
     * Checks if there is a full Bitcoin Block at the current position of the InputStream
     *
     * @throws org.zuinnote.hadoop.bitcoin.format.exception.BitcoinBlockReadException in case of errors reading Blockchain data
     *
     */
    private void checkFullBlock() throws IOException {
        // now we can check that we have a full block
        this.bin.mark(this.maxSizeBitcoinBlock);
        // skip maigc
        long skipMagic = this.bin.skip(4);
        if (skipMagic != 4) {
            throw new BitcoinBlockReadException("Error: Cannot seek to a block start, because no valid block found. Cannot skip forward magic");
        }

        UInt32 blockSize = new UInt32(bin);
        if (blockSize.getValue() > this.maxSizeBitcoinBlock) {
            throw new BitcoinBlockReadException("Error: Cannot seek to a block start, because no valid block found. Max bitcoin block size is smaller than current block size.");
        }

        int readByte, totalByteRead = 0;
        byte[] blockRead = new byte[blockSize.intValue()];
        while ((readByte = this.bin.read(blockRead, totalByteRead, blockSize.intValue() - totalByteRead)) > -1) {
            totalByteRead += readByte;
            if (totalByteRead >= blockSize.intValue()) {
                break;
            }
        }
        if (totalByteRead != blockSize.intValue()) {
            throw new BitcoinBlockReadException("Error: Cannot seek to a block start, because no valid block found. Cannot skip to end of block");
        }
        this.bin.reset();
        // it is a full block
    }

    /**
     * Read the magic and blockSize fields in the header and return the raw block size as Long,
     * or null iff filterSpecificMagic is true, and the magic read does not match any of those specified
     * in specificMagicByteArray.
     *
     * @return null, or the raw size of the block excluding the magic and blockSize fields.
     *
     * @throws java.io.IOException in case of errors reading from InputStream
     */
    private Long skipBlocksNotInFilter() throws IOException {
        bin.mark(8);
        Magic magicNo = new Magic(bin);
        UInt32 blockSize = new UInt32(bin);
        bin.reset();
        if (filterSpecificMagic) {
            for (byte[] filter : specificMagicByteArray) {
                if (new Magic(filter).equals(magicNo)) {
                    return blockSize.getValue();
                }
            }
            bin.skip(blockSize.getValue());
            return null;
        } else {
            return blockSize.getValue();
        }
    }

    /**
     * Checks in BufferedInputStream (bin) for the magic(s) specified in specificMagicByteArray
     *
     * @param firstByte first byte (as int) of the byteBuffer
     * @throws java.io.IOException in case of issues reading from BufferedInputStream
     * @retrun true if one of the magics has been identified, false if not
     */
    private boolean checkForMagicBytes(int firstByte) throws IOException {
        byte[] fullMagic = null;
        for (int i = 0; i < this.specificMagicByteArray.length; i++) {
            // compare first byte and decide if we want to read full magic
            int currentMagicFirstbyte = this.specificMagicByteArray[i][0] & 0xFF;
            if (firstByte == currentMagicFirstbyte) {
                if (fullMagic == null) { // read full magic
                    fullMagic = new byte[4];
                    fullMagic[0] = this.specificMagicByteArray[i][0];
                    int maxByteRead = 4;
                    int totalByteRead = 1;
                    int readByte;
                    while ((readByte = this.bin.read(fullMagic, totalByteRead, maxByteRead - totalByteRead)) > -1) {
                        totalByteRead += readByte;
                        if (totalByteRead >= maxByteRead) {
                            break;
                        }
                    }
                    if (totalByteRead != maxByteRead) {
                        return false;
                    }
                }
                // compare full magics
                if (new Magic(fullMagic).equals(new Magic(specificMagicByteArray[i]))) {
                    this.bin.reset();
                    return true;
                }
            }

        }
        return false;
    }
}
