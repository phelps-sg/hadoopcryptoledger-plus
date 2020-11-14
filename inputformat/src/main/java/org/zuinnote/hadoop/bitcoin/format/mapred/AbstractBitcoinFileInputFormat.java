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

import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.compress.CompressionCodec;
import org.apache.hadoop.io.compress.CompressionCodecFactory;
import org.apache.hadoop.io.compress.SplittableCompressionCodec;
import org.apache.hadoop.mapred.*;

import java.io.IOException;


public abstract class AbstractBitcoinFileInputFormat<K, V> extends FileInputFormat<K, V> implements JobConfigurable {
    public static final String CONF_ISSPLITABLE = org.zuinnote.hadoop.bitcoin.format.mapreduce.AbstractBitcoinFileInputFormat.CONF_ISSPLITABLE;
    public static final boolean DEFAULT_ISSPLITABLE = org.zuinnote.hadoop.bitcoin.format.mapreduce.AbstractBitcoinFileInputFormat.DEFAULT_ISSPLITABLE;


    private boolean isSplitable = DEFAULT_ISSPLITABLE;
    private CompressionCodecFactory compressionCodecs = null;

    @Override
    public abstract RecordReader<K, V> getRecordReader(InputSplit split, JobConf job, Reporter reporter) throws IOException;


    @Override
    public void configure(JobConf conf) {
        this.compressionCodecs = new CompressionCodecFactory(conf);
        this.isSplitable = conf.getBoolean(AbstractBitcoinFileInputFormat.CONF_ISSPLITABLE, AbstractBitcoinFileInputFormat.DEFAULT_ISSPLITABLE);
    }

    /**
     * This method is experimental and derived from TextInputFormat. It is not necessary and not recommended to compress the blockchain files. Instead it is recommended to extract relevant data from the blockchain files once and store them in a format suitable for analytics (including compression), such as ORC or Parquet.
     */
    @Override
    protected boolean isSplitable(FileSystem fs, Path file) {
        if (!(this.isSplitable)) {
            return false;
        }
        final CompressionCodec codec = compressionCodecs.getCodec(file);
        if (null == codec) {
            return true;
        }
        return codec instanceof SplittableCompressionCodec;

    }


}
