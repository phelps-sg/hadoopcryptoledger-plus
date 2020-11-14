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
package org.zuinnote.flink.ethereum;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.flink.api.common.io.CheckpointableInputFormat;
import org.apache.flink.core.fs.FileInputSplit;
import org.zuinnote.hadoop.ethereum.format.common.EthereumBlock;
import org.zuinnote.hadoop.ethereum.format.exception.EthereumBlockReadException;

public class EthereumBlockFlinkInputFormat extends AbstractEthereumFlinkInputFormat<EthereumBlock> implements CheckpointableInputFormat<FileInputSplit, Long> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8852751104331531470L;
	private static final Log LOG = LogFactory.getLog(EthereumBlockFlinkInputFormat.class.getName());
	private boolean isEndReached;
	
	/***
	 * 
	 * 
	 * @param maxSizeEthereumBlock
	 * @param useDirectBuffer
	 */
	public EthereumBlockFlinkInputFormat(int maxSizeEthereumBlock, boolean useDirectBuffer) {
		super(maxSizeEthereumBlock, useDirectBuffer);
		this.isEndReached=false;
	}



	/*
	 * Reopens the stream at a specific previously stored position and initializes the BitcoinBlockReader
	 * 
	 * @param split FileInputSplit
	 * @param state position in the stream
	 * 
	 * (non-Javadoc)
	 * @see org.apache.flink.api.common.io.CheckpointableInputFormat#reopen(org.apache.flink.core.io.InputSplit, java.io.Serializable)
	 */
	@Override
	public void reopen(FileInputSplit split, Long state) throws IOException {
		try {
			this.open(split);
		} finally {
			this.stream.seek(state);
		}
	}
	
	@Override
	public EthereumBlock nextRecord(EthereumBlock reuse) throws IOException {
		EthereumBlock dataBlock=null;
		if ((this.currentSplit.getLength()<0) ||(this.stream.getPos()<=this.currentSplit.getStart()+this.currentSplit.getLength())) {
		
				try {
					dataBlock=this.getEbr().readBlock();
				} catch (EthereumBlockReadException e) {
					LOG.error(e);
					throw new RuntimeException(e.toString());
				}
			if (dataBlock==null) {
				this.isEndReached=true;
			}
		} else {
			this.isEndReached=true;
		}
		return dataBlock;
}



	@Override
	public boolean reachedEnd() throws IOException {
		return this.isEndReached;
	}


	/*
	 * Saves the current state of the stream
	 *  
	 *  @return current position in stream
	 *  
	 * (non-Javadoc)
	 * @see org.apache.flink.api.common.io.CheckpointableInputFormat#getCurrentState()
	 */
	@Override
	public Long getCurrentState() throws IOException {
		return this.stream.getPos();
	}

	

}
