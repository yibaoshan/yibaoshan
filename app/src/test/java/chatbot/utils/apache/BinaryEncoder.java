package chatbot.utils.apache;

public interface BinaryEncoder extends Encoder {
    byte[] encode(byte[] var1) throws EncoderException;
}