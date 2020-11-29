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

package org.zuinnote.hadoop.bitcoin.format.mapreduce;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.BytesWritable;
import org.apache.hadoop.yarn.webapp.hamlet.Hamlet;
import org.zuinnote.hadoop.bitcoin.format.common.BitcoinBlock;
import org.zuinnote.hadoop.bitcoin.format.exception.BitcoinBlockReadException;
import org.zuinnote.hadoop.bitcoin.format.exception.HadoopCryptoLedgerConfigurationException;

import java.io.IOException;

/**
 * Reads records as blocks of the bitcoin blockchain. Note that it can be tricky to find the start of a block in a split. The BitcoinBlockReader provides a method (seekBlockStart) for this.
 */

public class BitcoinBlockRecordReader extends AbstractBitcoinRecordReader<BytesWritable, BitcoinBlock> {
    private static final Log LOG = LogFactory.getLog(BitcoinBlockRecordReader.class.getName());
    private BytesWritable currentKey = new BytesWritable();
    private BitcoinBlock currentValue = new BitcoinBlock();

    public BitcoinBlockRecordReader(Configuration conf) throws HadoopCryptoLedgerConfigurationException {
        super(conf);
    }

    /**
     * get current key after calling next()
     *
     * @return key is a 64 byte array (hashMerkleRoot and prevHashBlock)
     */
    @Override
    public BytesWritable getCurrentKey() {
        return this.currentKey;
    }

    /**
     * get current value after calling next()
     *
     * @return is a deserialized Java object of class BitcoinBlock
     */
    @Override
    public BitcoinBlock getCurrentValue() {
        return this.currentValue;
    }

    /**
     * Read a next block.
     *
     * @return true if next block is available, false if not
     */
    @Override
    public boolean nextKeyValue() throws IOException {
        BitcoinBlock block = getBbr().readBlock();
        if (block == null) {
            return false;
        }
        byte[] hashMerkleRoot = block.getHashMerkleRoot().getBytes();
        byte[] hashPrevBlock = block.getHashPrevBlock().getBytes();
        byte[] newKey = new byte[hashMerkleRoot.length + hashPrevBlock.length];
        System.arraycopy(hashMerkleRoot, 0, newKey, 0, hashMerkleRoot.length);
        System.arraycopy(hashPrevBlock, 0, newKey, 0 + hashMerkleRoot.length, hashPrevBlock.length);
        currentKey.set(newKey, 0, newKey.length);
        currentValue.set(block);
        return true;
    }

}
