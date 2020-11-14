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

package org.zuinnote.hadoop.bitcoin.format.mapred;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.io.BytesWritable;
import org.apache.hadoop.mapred.*;
import org.zuinnote.hadoop.bitcoin.format.common.BitcoinBlock;
import org.zuinnote.hadoop.bitcoin.format.exception.BitcoinBlockReadException;
import org.zuinnote.hadoop.bitcoin.format.exception.HadoopCryptoLedgerConfigurationException;

import java.io.IOException;

public class BitcoinBlockFileInputFormat extends AbstractBitcoinFileInputFormat<BytesWritable, BitcoinBlock> {

    private static final Log LOGFI = LogFactory.getLog(BitcoinBlockFileInputFormat.class.getName());

    @Override
    public RecordReader<BytesWritable, BitcoinBlock> getRecordReader(InputSplit split, JobConf job, Reporter reporter) throws IOException {
        /** Create reader **/
        try {
            return new BitcoinBlockRecordReader((FileSplit) split, job, reporter);
        } catch (HadoopCryptoLedgerConfigurationException | BitcoinBlockReadException e) {
            // log
            LOGFI.error(e);
        }
        return null;
    }


}
