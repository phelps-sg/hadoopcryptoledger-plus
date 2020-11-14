package org.zuinnote.hadoop.bitcoin.format.littleendian;

import java.nio.ByteBuffer;
import java.util.Date;

/**
 * A date time stamp stored as an unsigned 32-bit integer epoch time.
 */
public class EpochDatetime extends UInt32 {

    public EpochDatetime(ByteBuffer rawByteBuffer) {
        super(rawByteBuffer);
    }

    public EpochDatetime(long value) {
        super(value);
    }

    public Date getDate() {
        return new Date(getValue()*1000);
    }

}
