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

import org.apache.hadoop.io.BytesWritable; 
import org.apache.hadoop.io.Text; 
import org.apache.hadoop.hive.ql.exec.Description;
import org.apache.hadoop.hive.ql.exec.UDF;



import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;


import org.zuinnote.hadoop.bitcoin.format.common.*;
/*
* UDF to extract the destination  (cf. https://en.bitcoin.it/wiki/Transaction#general_format_.28inside_a_block.29_of_each_input_of_a_transaction_-_Txin)
*
*
*/
@Description(
	name = "hclBitcoinScriptPattern",
	value = "_FUNC_(BINARY) - extracts information about the destination of a transaction based on txOutScript",
	extended = "Example:\n" +
	"  > SELECT hclBitcoinScriptPattern(expout.txoutscript) FROM (select * from BitcoinBlockchain LATERAL VIEW explode(transactions) exploded_transactions as exptran) transaction_table LATERAL VIEW explode (exptran.listofoutputs) exploded_outputs as expout;\n")

public class BitcoinScriptPaymentPatternAnalyzerUDF extends UDF {
private static final Log LOG = LogFactory.getLog(BitcoinScriptPaymentPatternAnalyzerUDF.class.getName());
 /**
 ** Analyzes txOutScript (ScriptPubKey) of an output of a Bitcoin Transaction to determine the payment destination
*
* @param input BytesWritable containing a txOutScript of a Bitcoin Transaction
*
* @return Text containing the type of the output of the transaction, cf. BitcoinScriptPatternParser of the hadoopcryptoledger inputformat
*
*/
  public Text evaluate(BytesWritable input) {
    if (input==null) { 
	LOG.debug("Input is null");
	return null;
    }
    String paymentDestination = BitcoinScriptPatternParser.getPaymentDestination(input.copyBytes());
    if (paymentDestination==null) {
	 LOG.debug("Payment destination is null");
	 return null;
    }
    return new Text(paymentDestination);
  }
}
