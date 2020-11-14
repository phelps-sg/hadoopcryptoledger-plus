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
package org.zuinnote.hadoop.bitcoin.hive.udf;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.BytesWritable;
import org.apache.hadoop.io.Writable;

public class TestBitcoinScriptWitness implements Writable {

	private BytesWritable witnessScriptLength;
	private BytesWritable witnessScript;
	
	public TestBitcoinScriptWitness(byte[] witnessScriptLength,byte[] witnessScript) {
		this.witnessScriptLength=new BytesWritable(witnessScriptLength);
		this.witnessScript=new BytesWritable(witnessScript);
	}
	
	public BytesWritable getWitnessScriptLength() {
		return this.witnessScriptLength;
	}
	
	public BytesWritable getWitnessScript() {
		return this.witnessScript;
	}
	
	@Override
	public void write(DataOutput out) throws IOException {
		 throw new UnsupportedOperationException("write unsupported");
		
	}

	@Override
	public void readFields(DataInput in) throws IOException {
		 throw new UnsupportedOperationException("read unsupported");
		
	}

	
}
