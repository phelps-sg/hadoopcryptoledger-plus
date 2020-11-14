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
package org.zuinnote.hadoop.ethereum.hive.udf;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.hive.ql.exec.Description;
import org.apache.hadoop.hive.ql.exec.UDFArgumentException;
import org.apache.hadoop.hive.ql.exec.UDFArgumentLengthException;
import org.apache.hadoop.hive.ql.metadata.HiveException;
import org.apache.hadoop.hive.ql.udf.generic.GenericUDF;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.StructObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.PrimitiveObjectInspectorFactory;
import org.apache.hadoop.io.LongWritable;
import org.zuinnote.hadoop.ethereum.format.common.EthereumTransaction;
import org.zuinnote.hadoop.ethereum.format.common.EthereumUtil;

@Description(
		name = "hclEthereumGetChainId",
		value = "_FUNC_(Struct<EthereumTransaction>) - calculates the chainId of a EthereumTransaction and returns a long",
		extended = "Example:\n" +
		"  > SELECT hclEthereumGetChainId(ethereumTransactions[0]) FROM EthereumBlockChain LIMIT 1;\n")
public class EthereumGetChainIdUDF extends GenericUDF {
	private static final Log LOG = LogFactory.getLog(EthereumGetChainIdUDF.class.getName());
	private EthereumUDFUtil ethereumUDFUtil;
	
	@Override
	public ObjectInspector initialize(ObjectInspector[] arguments) throws UDFArgumentException {
		if (arguments==null) {
      		throw new UDFArgumentLengthException("ethereumGetChainId only takes one argument: Struct<EthereumTransction> ");
		}
		if (arguments.length != 1) {
      		throw new UDFArgumentLengthException("ethereumGetChainId only takes one argument: Struct<EthereumTransction> ");
		}
		if (!(arguments[0] instanceof StructObjectInspector)) { 
		throw new UDFArgumentException("first argument must be a Struct containing a EthereumTransction");
		}
		this.ethereumUDFUtil=new EthereumUDFUtil((StructObjectInspector) arguments[0]);
		return PrimitiveObjectInspectorFactory.writableIntObjectInspector;
	}

	@Override
	public Object evaluate(DeferredObject[] arguments) throws HiveException {
		if ((arguments==null) || (arguments.length!=1)) { 
			return null;
		}
		EthereumTransaction eTrans = this.ethereumUDFUtil.getEthereumTransactionFromObject(arguments[0].get());
		Long chainId=EthereumUtil.calculateChainId(eTrans);
		if (chainId==null) {
			return null;
		}
		return new LongWritable(chainId);
	}

	@Override
	public String getDisplayString(String[] children) {
		return "hclEthereumGetChainId()";
	}
	
	
}
