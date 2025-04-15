package com.example.kafka.stream;

import org.apache.kafka.common.serialization.Deserializer;

import java.nio.ByteBuffer;

public class Custom_Deserialization implements Deserializer<double[]> {

    @Override
    public double[] deserialize(String topic, byte[] data) {
        if (data == null) {
            return null;
        }
        ByteBuffer buffer = ByteBuffer.wrap(data);
        int length = data.length / Double.BYTES;
        double[] result = new double[length];
        for (int i = 0; i < length; i++) {
            result[i] = buffer.getDouble();
        }
        return result;
    }

}
