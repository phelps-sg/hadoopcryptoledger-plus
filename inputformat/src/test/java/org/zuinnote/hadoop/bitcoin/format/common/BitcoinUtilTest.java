/**
* Copyright 2016 ZuInnoTe (Jörn Franke) <zuinnote@gmail.com>
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
**/

package org.zuinnote.hadoop.bitcoin.format.common;


import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class BitcoinUtilTest {

  @Test
  public void convertSignedIntToUnsigned() {
    long unsignedint = BitcoinUtil.convertSignedIntToUnsigned(-1);
    assertEquals( 4294967295L,unsignedint,"-1 from signed int must be 4294967295L unsigned");
  }

  @Test
  public void convertIntToByteArray() {
    byte[] intByteArray = BitcoinUtil.convertIntToByteArray(1);
    byte[] comparatorArray = new byte[]{0x00,0x00,0x00,0x01};
    assertArrayEquals( comparatorArray,intByteArray,"1 in int must be equivalent to the array {0x00,0x00,0x00,0x01}");
  }

  @Test
  public void convertLongToByteArray() {
    byte[] longByteArray = BitcoinUtil.convertLongToByteArray(1L);
    byte[] comparatorArray = new byte[]{0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x01};
    assertArrayEquals( comparatorArray,longByteArray,"1 in int must be equivalent to the array {0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x01}");
  }

  @Test
  public void convertVarIntByteBufferToByteArray() {
    //note we will test here only one possible var int, because convertVarIntByteBufferToByteArray calls BitcoinUtil.getVarIntSize internally and thus we will do more tests when testing this function
    byte[] originVarInt = new byte[]{0x01};
    ByteBuffer testByteBuffer = ByteBuffer.allocate(1).put(originVarInt);
    testByteBuffer.flip();
    byte[] varIntByteArray = BitcoinUtil.convertVarIntByteBufferToByteArray(testByteBuffer);
    assertArrayEquals( originVarInt,varIntByteArray,"0x01 in ByteBuffer must be equivalent to the varint represented in array {0x01}");
  }

  @Test
  public void convertVarIntByteBufferToLong() {
    // note we will test here only one possible varint, because convertVarIntByteBufferToLong calls BitcoinUtil.getVarInt internally and thus we will do more tests when testing this function
   ByteBuffer testByteBuffer = ByteBuffer.allocate(1).put(new byte[]{0x01});
    testByteBuffer.flip();
    long convertedLong = BitcoinUtil.convertVarIntByteBufferToLong(testByteBuffer);
    assertEquals(1L, convertedLong,"0x01 in ByteBuffer must be 1 as a long");
  }



  @Test
  public void getVarIntByte() {
    byte[] originalVarInt = new byte[] {0x01};
    long convertedVarInt = BitcoinUtil.getVarInt(originalVarInt);
    assertEquals( 1L,convertedVarInt,"varInt {0x01} must be 1 as long");
  }

  @Test
  public void getVarIntWord() {
    byte[] originalVarInt = new byte[] {(byte)0xFD,0x01,0x00};
    long convertedVarInt = BitcoinUtil.getVarInt(originalVarInt);
    assertEquals( 1L, convertedVarInt,"varInt {0xFD,0x01,0x00} must be 1 as long");
  }

  @Test
  public void getVarIntDWord() {
    byte[] originalVarInt = new byte[] {(byte)0xFE,0x01,0x00,0x00,0x00};
    long convertedVarInt = BitcoinUtil.getVarInt(originalVarInt);
    assertEquals( 1L, convertedVarInt,"varInt {0xFE,0x01,0x00,0x00,0x00} must be 1 as long");
  }

  @Test
  public void getVarIntQWord() {
    byte[] originalVarInt = new byte[] {(byte)0xFF,0x01,0x00,0x00,0x00,0x00,0x00,0x00,0x00};
    long convertedVarInt = BitcoinUtil.getVarInt(originalVarInt);
    assertEquals( 1L, convertedVarInt,"varInt {0xFF,0x01,0x00,0x00,0x00,0x00,0x00,0x00,0x00} must be 1 as long");
  }


  @Test
  public void getVarIntSizeByte() {
    byte[] originalVarInt = new byte[] {0x02};
    byte varIntSize = BitcoinUtil.getVarIntSize(originalVarInt[0]);
    assertEquals( 1, varIntSize,"varInt {0x02} must be of size 1");
  }

  @Test
  public void getVarIntSizeWord() {
    byte[] originalVarInt = new byte[] {(byte)0xFD,0x01,0x00};
    byte varIntSize = BitcoinUtil.getVarIntSize(originalVarInt[0]);
    assertEquals( 3, varIntSize,"varInt {0xFD,0x01,0x00} must be of size 3");
  }


  @Test
  public void getVarIntSizeDWord() {
    byte[] originalVarInt = new byte[] {(byte)0xFE,0x01,0x00,0x00,0x00};
    byte varIntSize = BitcoinUtil.getVarIntSize(originalVarInt[0]);
    assertEquals( 5, varIntSize,"varInt {0xFE,0x01,0x00,0x00,0x00} must be of size 5");
  }


  @Test
  public void getVarIntSizeQWord() {
    byte[] originalVarInt = new byte[] {(byte)0xFF,0x01,0x00,0x00,0x00,0x00,0x00,0x00,0x00};
    byte varIntSize = BitcoinUtil.getVarIntSize(originalVarInt[0]);
    assertEquals( 9, varIntSize,"varInt {0xFF,0x01,0x00,0x00,0x00,0x00,0x00,0x00,0x00} must be of size 9");
  }


  @Test
  public void getSize() {
    byte[] blockSize = new byte[] {(byte)0x1D,0x01,0x00,0x00}; // this is the size of the genesis block
    long blockSizeLong = BitcoinUtil.getSize(blockSize);
    assertEquals( 285, blockSizeLong,"Size in Array {0x1D,0x01,0x00,0x00} must be 285");
  }


  @Test
  public void reverseByteArray() {
    byte[] originalByteArray = new byte[]{0x01,0x02,0x03,0x04,0x05,0x06,0x07,0x08};
    byte[] resultByteArray = BitcoinUtil.reverseByteArray(originalByteArray);
    byte[] reverseByteArray = new byte[]{0x08,0x07,0x06,0x05,0x04,0x03,0x02,0x01};
    assertArrayEquals( reverseByteArray, resultByteArray,"{0x01,0x02,0x03,0x04,0x05,0x06,0x07,0x08} is equivalent to {0x08,0x07,0x06,0x05,0x04,0x03,0x02,0x01} in reverse order");
  }


  @Test
  public void convertHexStringToByteArray() {
    String hexString = "01FF02";
    byte[] resultArray = BitcoinUtil.convertHexStringToByteArray(hexString);
    byte[] expectedByteArray = new byte[]{0x01,(byte)0xFF,0x02};
    assertArrayEquals( expectedByteArray, resultArray,"String \""+hexString+"\" is equivalent to byte array {0x01,0xFF,0x02}");
  }

  @Test
  public void convertByteArrayToHexString() {
    byte[] hexByteArray = new byte[]{0x01,(byte)0xFF,0x02};
    String resultString = BitcoinUtil.convertByteArrayToHexString(hexByteArray);
    String expectedString = "01FF02";
    assertEquals( expectedString, resultString,"Byte array {0x01,0xFF,0x02} is equivalent to Hex String \""+expectedString+"\"");
  }


  @Test
  public void convertIntToDate() {
    int genesisBlockTimeStamp=1231006505;
    Date genesisBlockDate = BitcoinUtil.convertIntToDate(genesisBlockTimeStamp);
    SimpleDateFormat simpleFormat = new SimpleDateFormat("yyyy-MM-dd");
    String genesisBlockDateString = simpleFormat.format(genesisBlockDate);
    String expectedDate="2009-01-03";
    assertEquals( expectedDate, genesisBlockDateString,"1231006505 is equivalent to the Date 2009-01-03");
  }


  @Test
  public void compareMagicsPos() {
    byte[] inputMagic1 = new byte[]{(byte)0xF9,(byte)0xBE,(byte)0xB4,(byte)0xD9};
    byte[] inputMagic2 = new byte[]{(byte)0xF9,(byte)0xBE,(byte)0xB4,(byte)0xD9};
    boolean isSame = BitcoinUtil.compareMagics(inputMagic1,inputMagic2);
    assertTrue( isSame,"Magic 1 {0xF9,0xBE,0xB4,0xD9} is equivalent to Magic 2 {0xF9,0xBE,0xB4,0xD9}");
  }


  @Test
  public void compareMagicsNeg() {
    byte[] inputMagic1 = new byte[]{(byte)0xF9,(byte)0xBE,(byte)0xB4,(byte)0xD9};
    byte[] inputMagic2 = new byte[]{(byte)0xFA,(byte)0xBF,(byte)0xB5,(byte)0xDA};
    boolean isSame = BitcoinUtil.compareMagics(inputMagic1,inputMagic2);
    assertFalse( isSame,"Magic 1 {0xF9,0xBE,0xB4,0xD9} is not equivalent to Magic 2 {0xFA,0xBF,0xB5,0xDA}");
  }

} 


