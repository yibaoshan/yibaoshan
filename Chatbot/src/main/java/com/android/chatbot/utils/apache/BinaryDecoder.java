package com.android.chatbot.utils.apache;

public interface BinaryDecoder extends Decoder {
    byte[] decode(byte[] var1) throws DecoderException;
}