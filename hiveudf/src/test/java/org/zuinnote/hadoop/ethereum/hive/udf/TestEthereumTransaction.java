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

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.math.BigInteger;

import org.apache.hadoop.io.Writable;
import org.zuinnote.hadoop.ethereum.format.common.EthereumTransaction;

/**
 *
 *
 */
public class TestEthereumTransaction implements Writable {

private byte[] nonce;
private BigInteger value;
private byte[] valueRaw;
private byte[] receiveAddress;
private BigInteger gasPrice;
private byte[] gasPriceRaw;
private BigInteger gasLimit;
private byte[] gasLimitRaw;

private byte[] data;
private byte[] sig_v;
private byte[] sig_r;
private byte[] sig_s;

public TestEthereumTransaction() {
	// please use setter to set the data
}

@Override
public void write(DataOutput out) throws IOException {
	   throw new UnsupportedOperationException("write unsupported");	
}

@Override
public void readFields(DataInput in) throws IOException {
	   throw new UnsupportedOperationException("readFields unsupported");
}

public byte[] getNonce() {
	return nonce;
}

public void setNonce(byte[] nonce) {
	this.nonce = nonce;
}

public BigInteger getValue() {
	return value;
}

public void setValue(BigInteger value) {
	this.value = value;
}

public byte[] getReceiveAddress() {
	return receiveAddress;
}

public void setReceiveAddress(byte[] receiveAddress) {
	this.receiveAddress = receiveAddress;
}


public BigInteger getGasPrice() {
	return gasPrice;
}

public void setGasPrice(BigInteger gasPrice) {
	this.gasPrice = gasPrice;
}

public BigInteger getGasLimit() {
	return gasLimit;
}

public void setGasLimit(BigInteger gasLimit) {
	this.gasLimit = gasLimit;
}

public byte[] getData() {
	return data;
}

public void setData(byte[] data) {
	this.data = data;
}



public void set(EthereumTransaction newTransaction) {
	this.nonce=newTransaction.getNonce();
	this.value=newTransaction.getValue();
	this.valueRaw=newTransaction.getValueRaw();
	this.receiveAddress=newTransaction.getReceiveAddress();
	this.gasPrice=newTransaction.getGasPrice();
	this.gasPriceRaw=newTransaction.getGasPriceRaw();
	this.gasLimit=newTransaction.getGasLimit();
	this.gasLimitRaw=newTransaction.getGasLimitRaw();
	this.data=newTransaction.getData();
	this.sig_v=newTransaction.getSig_v();
	this.sig_r=newTransaction.getSig_r();
	this.sig_s=newTransaction.getSig_s();
}

public byte[] getSig_v() {
	return sig_v;
}

public void setSig_v(byte[] sig_v) {
	this.sig_v = sig_v;
}

public byte[] getSig_r() {
	return sig_r;
}

public void setSig_r(byte[] sig_r) {
	this.sig_r = sig_r;
}

public byte[] getSig_s() {
	return sig_s;
}

public void setSig_s(byte[] sig_s) {
	this.sig_s = sig_s;
}

public byte[] getValueRaw() {
	return valueRaw;
}

public void setValueRaw(byte[] valueRaw) {
	this.valueRaw = valueRaw;
}



public byte[] getGasLimitRaw() {
	return gasLimitRaw;
}

public void setGasLimitRaw(byte[] gasLimitRaw) {
	this.gasLimitRaw = gasLimitRaw;
}

public byte[] getGasPriceRaw() {
	return gasPriceRaw;
}

public void setGasPriceRaw(byte[] gasPriceRaw) {
	this.gasPriceRaw = gasPriceRaw;
}


}
