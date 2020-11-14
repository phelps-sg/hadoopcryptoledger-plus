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
import java.util.Date;

/**
 * A date time stamp stored as a little-endian unsigned 32-bit integer epoch time.
 */
public class EpochDatetime extends UInt32 {

    public EpochDatetime(ByteBuffer rawByteBuffer) {
        super(rawByteBuffer);
    }

    public EpochDatetime(long value) {
        super(value);
    }

    public Date getDate() {
        return new Date(getValue() * 1000);
    }

}
