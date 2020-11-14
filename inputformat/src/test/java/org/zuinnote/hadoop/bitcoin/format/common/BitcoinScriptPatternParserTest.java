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


import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;


public class BitcoinScriptPatternParserTest {

@Test
  public void testPaymentPubKeyGenesis() {
	byte[] txOutScriptGenesis= new byte[]{(byte)0x41,(byte)0x04,(byte)0x67,(byte)0x8A,(byte)0xFD,(byte)0xB0,(byte)0xFE,(byte)0x55,(byte)0x48,(byte)0x27,(byte)0x19,(byte)0x67,(byte)0xF1,(byte)0xA6,(byte)0x71,(byte)0x30,(byte)0xB7,(byte)0x10,(byte)0x5C,(byte)0xD6,(byte)0xA8,(byte)0x28,(byte)0xE0,(byte)0x39,(byte)0x09,(byte)0xA6,(byte)0x79,(byte)0x62,(byte)0xE0,(byte)0xEA,(byte)0x1F,(byte)0x61,(byte)0xDE,(byte)0xB6,(byte)0x49,(byte)0xF6,(byte)0xBC,(byte)0x3F,(byte)0x4C,(byte)0xEF,(byte)0x38,(byte)0xC4,(byte)0xF3,(byte)0x55,(byte)0x04,(byte)0xE5,(byte)0x1E,(byte)0xC1,(byte)0x12,(byte)0xDE,(byte)0x5C,(byte)0x38,(byte)0x4D,(byte)0xF7,(byte)0xBA,(byte)0x0B,(byte)0x8D,(byte)0x57,(byte)0x8A,(byte)0x4C,(byte)0x70,(byte)0x2B,(byte)0x6B,(byte)0xF1,(byte)0x1D,(byte)0x5F,(byte)0xAC};
       String result = BitcoinScriptPatternParser.getPaymentDestination(txOutScriptGenesis);
	String comparatorText = "bitcoinpubkey_4104678AFDB0FE5548271967F1A67130B7105CD6A828E03909A67962E0EA1F61DEB649F6BC3F4CEF38C4F35504E51EC112DE5C384DF7BA0B8D578A4C702B6BF11D5F";
	assertEquals( comparatorText,result,"TxOutScript from Genesis should be payment to a pubkey address");
  }


@Test
  public void testPaymentNull() {
	String result =  BitcoinScriptPatternParser.getPaymentDestination(null);
	assertNull( result,"Null as script returns null");
  }


@Test
  public void testPaymentAnyone() {
	String result =  BitcoinScriptPatternParser.getPaymentDestination(new byte[0]);
	assertEquals( "anyone",result,"Empty script means anyone can spend");
  }


@Test
  public void testPaymentUnspendable() {
	String result =  BitcoinScriptPatternParser.getPaymentDestination(new byte[]{0x6a});
	assertEquals( "unspendable",result,"Unspendable script");
  }


@Test
  public void testPaymentInvalid() {
	String result =  BitcoinScriptPatternParser.getPaymentDestination(new byte[]{0x00});
	assertNull( result,"Invalid script returns null");
  }



@Test
  public void testPaymentP2Hash() {
        byte[] txOutScriptP2Hash= new byte[]{(byte)0x76,(byte)0xa9,(byte)0x14,(byte)0xfd,(byte)0x92,(byte)0xaa,(byte)0xfe,(byte)0x55,(byte)0x5c,(byte)0x07,(byte)0xe8,(byte)0x90,(byte)0xe8,(byte)0x07,(byte)0x5e,(byte)0xd6,(byte)0x1f,(byte)0x39,(byte)0xca,(byte)0x90,(byte)0x52,(byte)0x2b,(byte)0x8f,(byte)0x88,(byte)0xAC};
	String result =  BitcoinScriptPatternParser.getPaymentDestination(txOutScriptP2Hash);
	String comparatorText = "bitcoinaddress_FD92AAFE555C07E890E8075ED61F39CA90522B8F";
	assertEquals( comparatorText, result,"Payment destination of script should be p2hash");
  }

@Test
  public void testPaymentPuzzle() {
        byte[] txOutScriptPuzzle= new byte[]{(byte)0xAA,(byte)0x20,(byte)0x6f,(byte)0xe2,(byte)0x8c,(byte)0x0a,(byte)0xb6,(byte)0xf1,(byte)0xb3,(byte)0x72,(byte)0xc1,(byte)0xa6,(byte)0xa2,(byte)0x46,(byte)0xae,(byte)0x63,(byte)0xf7,(byte)0x4f,(byte)0x93,(byte)0x1e,(byte)0x83,(byte)0x65,(byte)0xe1,(byte)0x5a,(byte)0x08,(byte)0x9c,(byte)0x68,(byte)0xd6,(byte)0x19,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x87};
	String result =  BitcoinScriptPatternParser.getPaymentDestination(txOutScriptPuzzle);
	String comparatorText = "puzzle_206FE28C0AB6F1B372C1A6A246AE63F74F931E8365E15A089C68D61900000000";
	assertEquals( comparatorText, result,"Payment destination of script should be puzzle");
  }


@Test
public void testPaymentSegwitP2HashSynthetic() {
	// synthetic because it is not based on real Bitcoin blockchain data
      byte[] txOutScriptP2Hash= new byte[]{(byte)0x00,(byte)0x14,(byte)0x00,(byte)0x01,(byte)0x02,(byte)0x03,(byte)0x04,(byte)0x05,(byte)0x06,(byte)0x07,(byte)0x08,(byte)0x09,(byte)0x0A,(byte)0x0B,(byte)0x0C,(byte)0x0D,(byte)0x0E,(byte)0x0F,(byte)0x10,(byte)0x11,(byte)0x12,(byte)0x13};
	
     String result =  BitcoinScriptPatternParser.getPaymentDestination(txOutScriptP2Hash);
	String comparatorText = "P2WPKH_000102030405060708090A0B0C0D0E0F10111213";
	assertEquals( comparatorText, result,"Payment destination of script should be P2WPKH");
}


@Test
public void testPaymentSegwitP2WSHSynthetic() {
	// synthetic because it is not based on real Bitcoin blockchain data
      byte[] txOutScriptP2Hash= new byte[]{(byte)0x00,(byte)0x20,(byte)0x00,(byte)0x01,(byte)0x02,(byte)0x03,(byte)0x04,(byte)0x05,(byte)0x06,(byte)0x07,(byte)0x08,(byte)0x09,(byte)0x0A,(byte)0x0B,(byte)0x0C,(byte)0x0D,(byte)0x0E,(byte)0x0F,(byte)0x10,(byte)0x11,(byte)0x12,(byte)0x13,(byte)0x14,(byte)0x15,(byte)0x16,(byte)0x17,(byte)0x18,(byte)0x19,(byte)0x1A,(byte)0x1B,(byte)0x1C,(byte)0x1D,(byte)0x1E,(byte)0x1F};

	String result =  BitcoinScriptPatternParser.getPaymentDestination(txOutScriptP2Hash);
	String comparatorText = "P2WSH_000102030405060708090A0B0C0D0E0F101112131415161718191A1B1C1D1E1F";
	assertEquals( comparatorText, result,"Payment destination of script should be P2WSH");
}



@Test
public void testPaymentSegwitPubKeySynthetic() {
	// synthetic because it is not based on real Bitcoin blockchain data
	byte[] txOutScriptP2WPKHP2SH = new byte[]{(byte)0xA9,(byte)0x14,(byte)0x00,(byte)0x01,(byte)0x02,(byte)0x03,(byte)0x04,(byte)0x05,(byte)0x06,(byte)0x07,(byte)0x08,(byte)0x09,(byte)0x0A,(byte)0x0B,(byte)0x0C,(byte)0x0D,(byte)0x0E,(byte)0x0F,(byte)0x10,(byte)0x11,(byte)0x12,(byte)0x13,(byte)0x87};
	  String result = BitcoinScriptPatternParser.getPaymentDestination(txOutScriptP2WPKHP2SH);
	String comparatorText = "P2WPKHP2SH_000102030405060708090A0B0C0D0E0F10111213";
	assertEquals( comparatorText,result,"Payment destination of script should be P2WPKHP2SH");
}


}

