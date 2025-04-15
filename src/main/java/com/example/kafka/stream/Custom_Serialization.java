package com.example.kafka.stream;

import org.apache.kafka.common.serialization.Serializer;

import java.nio.ByteBuffer;

public class Custom_Serialization implements Serializer<double[]> {
    @Override
    public byte[] serialize(String topic, double[] data) {
        if (data == null) {
            return null;
        }
        ByteBuffer buffer = ByteBuffer.allocate(Double.BYTES * data.length);
        for (double value : data) {
            buffer.putDouble(value);
        }
        return buffer.array();
    }

}
