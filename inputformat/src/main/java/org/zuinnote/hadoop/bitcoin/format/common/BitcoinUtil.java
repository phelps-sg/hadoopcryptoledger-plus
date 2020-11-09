/*
 * Copyright 2016 ZuInnoTe (Jörn Franke) <zuinnote@gmail.com>
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.zuinnote.hadoop.bitcoin.format.common;

import javax.xml.bind.DatatypeConverter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;


public class BitcoinUtil {

    public static final MessageDigest digestPrototype = BitcoinUtil.SHA256digest();

    private BitcoinUtil() {
    }

    /**
     * Converts a signed int to an unsigned (long)
     *
     * @param signedInt signed int that should be interpreted as unsigned
     * @return long corresponding to signed int
     */
    public static long convertSignedIntToUnsigned(int signedInt) {
        return signedInt & 0x00000000ffffffffL;
    }


    /**
     * Converts an int to a byte array
     *
     * @param intToConvert int that should be converted into a byte array
     * @return byte array corresponding to int
     **/
    public static byte[] convertIntToByteArray(int intToConvert) {
        return ByteBuffer.allocate(4).putInt(intToConvert).array();
    }

    /**
     * Converts a long to a byte array
     *
     * @param longToConvert long that should be converted into a byte array
     *
     * @return byte array corresponding to long
     *
     **/
    public static byte[] convertLongToByteArray(long longToConvert) {
        return ByteBuffer.allocate(8).putLong(longToConvert).array();
    }

    /**
     * Converts a Big Integer to a byte array
     *
     * @param bigIntegerToConvert BigInteger that should be converted into a byte array
     * @param exactArraySize exact size of array
     * @return byte array corresponding to BigInteger
     **/
    public static byte[] convertBigIntegerToByteArray(BigInteger bigIntegerToConvert, int exactArraySize) {
        if ((bigIntegerToConvert == null) || (bigIntegerToConvert.signum() == -1)) {// negative
            return null;
        }
        byte[] tempResult = bigIntegerToConvert.toByteArray();
        byte[] result = new byte[exactArraySize];
        int removeSign = 0;
        if ((tempResult.length > 1) && (tempResult[0] == 0)) { // remove sign
            removeSign = 1;
        }
        byte[] reverseTempResult = BitcoinUtil.reverseByteArray(tempResult);
        for (int i = 0; i < result.length; i++) {
            if (i < reverseTempResult.length - removeSign) {
                result[i] = reverseTempResult[i];
            }
        }
        return result;
    }

    /**
     * Converts a variable length integer (https://en.bitcoin.it/wiki/Protocol_documentation#Variable_length_integer) from a ByteBuffer to byte array
     *
     * @param byteBuffer Bytebuffer where to read from the variable length integer
     * @return byte[] of the variable length integer (including marker)
     */
    public static byte[] convertVarIntByteBufferToByteArray(ByteBuffer byteBuffer) {
        // get the size
        byte originalVarIntSize = byteBuffer.get();
        byte varIntSize = getVarIntSize(originalVarIntSize);
        // reserveBuffer
        byte[] varInt = new byte[varIntSize];
        varInt[0] = originalVarIntSize;
        byteBuffer.get(varInt, 1, varIntSize - 1);
        return varInt;
    }

    /**
     * Converts a variable length integer (https://en.bitcoin.it/wiki/Protocol_documentation#Variable_length_integer) from a ByteBuffer to long
     *
     * @param byteBuffer Bytebuffer where to read from the variable length integer
     * @return long corresponding to variable length integer. Please note that it is signed long and not unsigned long as int the Bitcoin specification. Should be in practice not relevant.
     */
    public static long convertVarIntByteBufferToLong(ByteBuffer byteBuffer) {
        byte[] varIntByteArray = convertVarIntByteBufferToByteArray(byteBuffer);
        return getVarInt(varIntByteArray);

    }

    /**
     * Converts a variable length integer (https://en.bitcoin.it/wiki/Protocol_documentation#Variable_length_integer) to BigInteger
     *
     * @param varInt byte array containing variable length integer
     * @return BigInteger corresponding to variable length integer
     */
    public static BigInteger getVarIntBI(byte[] varInt) {
        BigInteger result = BigInteger.ZERO;
        if (varInt.length == 0) {
            return result;
        }
        int unsignedByte = varInt[0] & 0xFF;
        if (unsignedByte < 0xFD) {
            return new BigInteger(new byte[]{(byte) unsignedByte});
        }
        int intSize = 0;
        if (unsignedByte == 0xFD) {
            intSize = 3;
        } else if (unsignedByte == 0xFE) {
            intSize = 5;
        } else {
            intSize = 9;
        }
        byte[] rawDataInt = reverseByteArray(Arrays.copyOfRange(varInt, 1, intSize));
        return new BigInteger(rawDataInt);
    }

    /**
     * Converts a variable length integer (https://en.bitcoin.it/wiki/Protocol_documentation#Variable_length_integer) to long
     *
     * @param varInt byte array containing variable length integer
     * @return long corresponding to variable length integer
     */
    public static long getVarInt(byte[] varInt) {
        long result = 0;
        if (varInt.length == 0) {
            return result;
        }
        int unsignedByte = varInt[0] & 0xFF;
        if (unsignedByte < 0xFD) {
            return unsignedByte;
        }
        int intSize = 0;
        if (unsignedByte == 0xFD) {
            intSize = 3;
        } else if (unsignedByte == 0xFE) {
            intSize = 5;
        } else {
            intSize = 9;
        }
        byte[] rawDataInt = reverseByteArray(Arrays.copyOfRange(varInt, 1, intSize));
        ByteBuffer byteBuffer = ByteBuffer.wrap(rawDataInt);
        if (intSize == 3) {
            result = byteBuffer.getShort();
        } else if (intSize == 5) {
            result = byteBuffer.getInt();
        } else {
            result = byteBuffer.getLong(); // Need to handle sign - available only in JDK8
        }
        return result;
    }

    /**
     * Determines size of a variable length integer (https://en.bitcoin.it/wiki/Protocol_documentation#Variable_length_integer)
     *
     * @param firstByteVarInt first byte of the variable integer
     * @return byte with the size of the variable int (either 2, 3, 5 or 9) - does include the marker!
     */
    public static byte getVarIntSize(byte firstByteVarInt) {
        int unsignedByte = firstByteVarInt & 0xFF;
        if (unsignedByte == 0xFD) {
            return 3;
        }
        if (unsignedByte == 0xFE) {
            return 5;
        }
        if (unsignedByte == 0xFF) {
            return 9;
        }
        return 1; //<0xFD
    }

    /**
     * Reads a size from a reversed byte order, such as block size in the block header
     *
     * @param byteSize byte array with a length of exactly 4
     * @return size, returns 0 in case of invalid block size
     */
    public static long getSize(byte[] byteSize) {
        if (byteSize.length != 4) {
            return 0;
        }
        ByteBuffer converterBuffer = ByteBuffer.wrap(byteSize);
        converterBuffer.order(ByteOrder.LITTLE_ENDIAN);
        return convertSignedIntToUnsigned(converterBuffer.getInt());
    }


    /**
     * Reverses the order of the byte array
     *
     * @param inputByteArray array to be reversed
     * @return inputByteArray in reversed order
     **/
    public static byte[] reverseByteArray(byte[] inputByteArray) {
        byte[] result = new byte[inputByteArray.length];
        for (int i = inputByteArray.length - 1; i >= 0; i--) {
            result[result.length - 1 - i] = inputByteArray[i];
        }
        return result;
    }

    /**
     * Converts a Hex String to Byte Array. Only used for configuration not for parsing.
     * Hex String is in format of xsd:hexBinary
     *
     * @param hexString String in Hex format.
     * @return byte array corresponding to String in Hex format
     */
    public static byte[] convertHexStringToByteArray(String hexString) {
        return DatatypeConverter.parseHexBinary(hexString);
    }


    /**
     * Converts a Byte Array to Hex String. Only used for configuration not for parsing.
     * Hex String is in format of xsd:hexBinary
     *
     * @param byteArray byte array to convert
     * @return String in Hex format corresponding to byteArray
     */
    public static String convertByteArrayToHexString(byte[] byteArray) {
        return DatatypeConverter.printHexBinary(byteArray);
    }

    /**
     * Converts an int to a date
     *
     * @param dateInt timestamp in integer format
     * @return Date corresponding to dateInt
     */
    public static Date convertIntToDate(int dateInt) {
        return new Date(dateInt * 1000L);
    }


    /**
     * Compares two Bitcoin magics
     *
     * @param magic1 first magic
     * @param magic2 second magics
     * @return false, if do not match, true if match
     */
    public static boolean compareMagics(byte[] magic1, byte[] magic2) {
        if (magic1.length != magic2.length) {
            return false;
        }
        for (int i = 0; i < magic1.length; i++) {
            if (magic1[i] != magic2[i]) {
                return false;
            }
        }
        return true;

    }

    /**
     * Calculates the double SHA256-Hash of a transaction in little endian format.
     * This could be used for certain analysis scenario where one want to investigate the referenced
     * transaction used as an input for a Transaction. Furthermore, it can be used as a unique identifier of the transaction
     * <p>
     * It corresponds to the Bitcoin specification of txid (https://bitcoincore.org/en/segwit_wallet_dev/)
     *
     * @param transaction The BitcoinTransaction of which we want to calculate the hash
     *
     * @return byte array containing the hash of the transaction. Note: This one can be compared to a prevTransactionHash. However, if you want to search for it in popular blockchain explorers then you need to apply the function BitcoinUtil.reverseByteArray to it!
     *
     * @deprecated Use {@link BitcoinTransaction#getTransactionHash()}
     */
    public static byte[] getTransactionHash(BitcoinTransaction transaction) {
        return transaction.getTransactionHash();
    }

    /**
     * Calculates the double SHA256-Hash of a transaction in little endian format. It serve as a unique identifier of a transaction, but cannot be used to link the outputs of other transactions as input
     * <p>
     * It corresponds to the Bitcoin specification of wtxid (https://bitcoincore.org/en/segwit_wallet_dev/)
     *
     * @param transaction The BitcoinTransaction of which we want to calculate the hash
     *
     * @return byte array containing the hash of the transaction.
     *          Note: This one can be compared to a prevTransactionHash. However, if you want to search for it in popular blockchain explorers then you need to apply the function BitcoinUtil.reverseByteArray to it!
     *
     * @deprecated Use {@link BitcoinTransaction#getTransactionHashSegwit()}
     */
    public static byte[] getTransactionHashSegwit(BitcoinTransaction transaction) {
        return transaction.getTransactionHashSegwit();
    }

    /**
     * Calculates the hash of hash on the given chunks of bytes.
     */
    public static byte[] hashTwice(byte[] input1, byte[] input2) {
        MessageDigest digest = newDigest();
        digest.update(input1);
        digest.update(input2);
        return digest.digest(digest.digest());
    }

    public static byte[] hashTwice(byte[] input) {
        MessageDigest digest = newDigest();
        digest.update(input);
        return digest.digest(digest.digest());
    }

    public static byte[] hash(byte[] input) {
        MessageDigest digest = newDigest();
        return digest.digest(input);
    }

    public static final MessageDigest newDigest() {
        try {
            return (MessageDigest) digestPrototype.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Returns a new SHA-256 MessageDigest instance.
     *
     * This is a convenience method which wraps the checked
     * exception that can never occur with a RuntimeException.
     *
     * @return a new SHA-256 MessageDigest instance
     */
    public static MessageDigest SHA256digest() {
        try {
            return MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);  // Can't happen.
        }
    }

    public static byte[] getBytes(List<Byteable> sources) {
        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            for (Byteable source : sources) {
                buffer.write(source.getBytes());
            }
            return buffer.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
