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

package org.zuinnote.hadoop.bitcoin.format.littleendian;

import java.nio.ByteBuffer;

public class Magic extends UInt32 {

    public static final Magic DEFAULT =
            new Magic(new byte[] {(byte) 0xF9, (byte) 0xBE, (byte) 0xB4, (byte) 0xD9});

    public static final Magic TESTNET3 =
            new Magic(new byte[] {(byte) 0x0B, (byte) 0x11, (byte) 0x09, (byte) 0x07});

//    public static final Magic MULTINET =
//            new Magic(new byte[] {(byte) 0xF9, (byte) 0xBE, (byte) 0xB4, (byte) 0xD9}, {(byte) 0x0B, (byte) 0x11, (byte) 0x09, (byte) 0x07});

    public Magic(byte[] value) {
        super(value);
    }

    public Magic(long value) {
        super(value);
    }

    public Magic(ByteBuffer value) {
        super(value);
    }

    public Magic() {
        this(DEFAULT.getBytes());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Magic) {
            return this.getValue() == ((Magic) obj).getValue();
        } else {
            return super.equals(obj);
        }
    }
}
