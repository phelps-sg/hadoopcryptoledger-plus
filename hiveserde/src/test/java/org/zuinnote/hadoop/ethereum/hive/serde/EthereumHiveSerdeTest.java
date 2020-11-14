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
package org.zuinnote.hadoop.ethereum.hive.serde;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hive.serde2.SerDeException;
import org.junit.jupiter.api.Test;
import org.zuinnote.hadoop.ethereum.format.common.EthereumBlock;
import org.zuinnote.hadoop.ethereum.format.common.EthereumBlockReader;
import org.zuinnote.hadoop.ethereum.format.exception.EthereumBlockReadException;
import org.zuinnote.hadoop.ethereum.format.mapred.AbstractEthereumRecordReader;
import org.zuinnote.hadoop.ethereum.hive.datatypes.HiveEthereumBlock;

public class EthereumHiveSerdeTest {
	static final int DEFAULT_BUFFERSIZE=AbstractEthereumRecordReader.DEFAULT_BUFFERSIZE;
	static final int DEFAULT_MAXSIZE_ETHEREUMBLOCK=AbstractEthereumRecordReader. DEFAULT_MAXSIZE_ETHEREUMBLOCK;
	
	 @Test
	  public void checkTestDataBlock1346406Available() {
		ClassLoader classLoader = getClass().getClassLoader();
		String fileName="eth1346406.bin";
		String fileNameGenesis=classLoader.getResource("testdata/"+fileName).getFile();	
		assertNotNull(fileNameGenesis,"Test Data File \""+fileName+"\" is not null in resource path");
		File file = new File(fileNameGenesis);
		assertTrue( file.exists(),"Test Data File \""+fileName+"\" exists");
		assertFalse( file.isDirectory(),"Test Data File \""+fileName+"\" is not a directory");
	  }
	 
	 @Test
	  public void initializePositive() throws SerDeException {
		EthereumBlockSerde testSerde = new EthereumBlockSerde();
		Configuration conf = new Configuration();
		Properties tblProperties = new Properties();
		// just for testing purposes - these values may have no real meaning
		tblProperties.setProperty(EthereumBlockSerde.CONF_MAXBLOCKSIZE, String.valueOf(1));
		tblProperties.setProperty(EthereumBlockSerde.CONF_USEDIRECTBUFFER,"true");
		testSerde.initialize(conf,tblProperties);
		assertEquals(1,conf.getInt(EthereumBlockSerde.CONF_MAXBLOCKSIZE,2),"MAXBLOCKSIZE set correctly");	
		assertTrue(conf.getBoolean(EthereumBlockSerde.CONF_USEDIRECTBUFFER,false),"USEDIRECTBUFFER set correctly");	
	  }
	 
	 @Test
	  public void deserialize() throws IOException, EthereumBlockReadException, SerDeException{
		 EthereumBlockSerde testSerde = new EthereumBlockSerde();
		// create a BitcoinBlock based on the genesis block test data
		ClassLoader classLoader = getClass().getClassLoader();
		String fileName="eth1346406.bin";
		String fullFileNameString=classLoader.getResource("testdata/"+fileName).getFile();	
		File file = new File(fullFileNameString);
		FileInputStream fin = new FileInputStream(file);
		boolean direct=false;
		EthereumBlockReader ebr = new EthereumBlockReader(fin,EthereumHiveSerdeTest.DEFAULT_MAXSIZE_ETHEREUMBLOCK,EthereumHiveSerdeTest.DEFAULT_BUFFERSIZE,direct );
		EthereumBlock block = ebr.readBlock();
		Object deserializedObject = testSerde.deserialize(block);
		assertTrue( deserializedObject instanceof HiveEthereumBlock,"Deserialized Object is of type HiveEthereumBlock");
		HiveEthereumBlock deserializedBitcoinBlockStruct = (HiveEthereumBlock)deserializedObject;

		assertEquals( 6, deserializedBitcoinBlockStruct.getEthereumTransactions().size(),"Block contains 6 transactions");
		assertEquals(0, deserializedBitcoinBlockStruct.getUncleHeaders().size(),"Block contains 0 uncleHeaders");
		byte[] expectedParentHash = new byte[] {(byte)0xBA,(byte)0x6D,(byte)0xD2,(byte)0x60,(byte)0x12,(byte)0xB3,(byte)0x71,(byte)0x90,(byte)0x48,(byte)0xF3,(byte)0x16,(byte)0xC6,(byte)0xED,(byte)0xB3,(byte)0x34,(byte)0x9B,(byte)0xDF,(byte)0xBD,(byte)0x61,(byte)0x31,(byte)0x9F,(byte)0xA9,(byte)0x7C,(byte)0x61,(byte)0x6A,(byte)0x61,(byte)0x31,(byte)0x18,(byte)0xA1,(byte)0xAF,(byte)0x30,(byte)0x67};
		
		assertArrayEquals( expectedParentHash, deserializedBitcoinBlockStruct.getEthereumBlockHeader().getParentHash(),"Block contains a correct 32 byte parent hash");
	 }
}
