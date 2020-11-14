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


/**
 * Simple Mapper for counting the total number of Bitcoin transaction inputs of all Bitcoin transactions
 */
package org.zuinnote.hadoop.bitcoin.example.tasks;

/**
* Author: Jörn Franke <zuinnote@gmail.com>
*
*/
import java.io.IOException;
import org.apache.hadoop.mapreduce.*;
import org.apache.hadoop.io.*;
import org.zuinnote.hadoop.bitcoin.format.common.*;

import org.zuinnote.hadoop.bitcoin.format.mapreduce.*;

import java.util.*;

public  class BitcoinTransactionMap  extends  Mapper<BytesWritable, BitcoinTransaction, Text, IntWritable> {
private static final Text defaultKey = new Text("Transaction Input Count:");

@Override
public void setup(Context context) throws IOException, InterruptedException {
// no action required
}

@Override
public void map(BytesWritable key, BitcoinTransaction value, Context context) throws IOException, InterruptedException {
	// get the number of inputs to transaction
	context.write(defaultKey, new IntWritable(value.getListOfInputs().size()));
}
	    

@Override
public void cleanup(Context context) {
 // no action required
}

}
	 
