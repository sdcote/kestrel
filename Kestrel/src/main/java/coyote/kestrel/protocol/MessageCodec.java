package coyote.kestrel.protocol;

import coyote.commons.StringUtil;
import coyote.dataframe.DataFrame;
import coyote.dataframe.DecodeException;
import coyote.dataframe.marshal.JSONMarshaler;
import coyote.dataframe.marshal.json.JsonFrameParser;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Utility class for encoding and decoding messages to and from data frames.
 *
 * <p>The DataFrame is the primary data transfer object in the Kestrel
 * framework. Only data frames are expected on the transport. This will try to
 * parse JSON data into data frames and if that fails, will just assume the
 * data is a string.</p>
 *
 * <p>This will also try to serialize a message into JSON if it contains an
 * encoding field with a value of JSON or STR. The resulting strings will be
 * ISO-5589-1 encoded.</p>
 */
public class MessageCodec {

  private static final byte[] EMPTY_BYTE_ARRAY = new byte[0];

  /**
   * Decode a series of bytes into a Data Frame.
   *
   * <p>If the bytes are an encoded data frame, standard decoding is performed.
   * If an error is encountered parsing the bytes into a data frame, then the
   * data is turned into a string using ISO-8859-1 character set and placed in
   * a data frame with a generic field name. If that fails (unlikely) then it
   * is placed in a data frame as a byte array with a generic field name.</p>
   *
   * @param body the data representing the body of the message.
   * @return a data frame suitable for use as a payload.
   */
  public static DataFrame decode(byte[] body) {
    DataFrame retval = null;
    try {
      retval = new DataFrame(body);
      retval.set(KestrelProtocol.ENCODING_FIELD, KestrelProtocol.FRAME_ENCODING);
    } catch (DecodeException e) {
      String data = StandardCharsets.ISO_8859_1.decode(ByteBuffer.wrap(body)).toString();
      try {
        List<DataFrame> frames = new JsonFrameParser(data).parse();
        if (frames.size() > 0) {
          if (frames.size() == 1) {
            retval = frames.get(0);
            retval.set(KestrelProtocol.ENCODING_FIELD, KestrelProtocol.JSON_ENCODING);
          } else {
            retval = new DataFrame();
            for (DataFrame frame : frames) {
              retval.add(frame);
            }
          }
        } else {
          retval = new DataFrame().set(KestrelProtocol.GENERIC_DATA_FIELD, data);
        }
      } catch (Throwable ball) {
        retval = new DataFrame().set(KestrelProtocol.GENERIC_DATA_FIELD, data); // unknown string
        retval.set(KestrelProtocol.ENCODING_FIELD, KestrelProtocol.STRING_ENCODING);
      }
    } catch (Throwable ball) {
      retval = new DataFrame().set(KestrelProtocol.GENERIC_DATA_FIELD, body); // unknown binary
      retval.set(KestrelProtocol.ENCODING_FIELD, KestrelProtocol.UNKNOWN_ENCODING);
    }
    return retval;
  }

  /**
   * Encode the given frame into an array of bytes suitable for use as the
   * body of a message.
   *
   * @param frame the DataFrame to encode
   * @return an array of bytes representing the data frame or an empty array
   * if the data frame was null.
   */
  public static byte[] encode(DataFrame frame) {
    byte[] retval = EMPTY_BYTE_ARRAY;
    if (frame != null) {
      if (frame.contains(KestrelProtocol.ENCODING_FIELD) && (KestrelProtocol.JSON_ENCODING.equalsIgnoreCase(frame.getAsString(KestrelProtocol.ENCODING_FIELD)) || KestrelProtocol.STRING_ENCODING.equalsIgnoreCase(frame.getAsString(KestrelProtocol.ENCODING_FIELD)))) {
        retval = StringUtil.getBytes(JSONMarshaler.marshal(frame));
      } else {
        retval = frame.getBytes();
      }
    }
    return retval;
  }

}
