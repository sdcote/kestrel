package coyote.commons;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.Locale;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * Percent-encoding according to the URI and URL standards.
 */
public class Encoder {

  protected static final boolean IS_PATH = true;
  protected static final boolean IS_NOT_PATH = false;
  protected static final boolean IS_FRAGMENT = true;
  protected static final boolean IS_NOT_FRAGMENT = false;
  protected static final boolean IS_USERINFO = true;
  protected static final boolean IS_NOT_USERINFO = false;
  protected final Charset outputEncoding;

  public Encoder(final Charset outputEncoding) {
    this.outputEncoding = outputEncoding;
  }

  public String encodeUserInfo(String input) {
    if (null == input || input.isEmpty()) {
      return "";
    }
    return urlEncode(input, IS_NOT_PATH, IS_NOT_FRAGMENT, IS_USERINFO);
  }

  public String encodePath(final String input) {
    if (input == null || input.isEmpty()) {
      return "";
    }
    final StringBuilder sb = new StringBuilder();
    final StringTokenizer st = new StringTokenizer(input, "/", true);
    while (st.hasMoreElements()) {
      final String element = st.nextToken();
      if ("/".equals(element)) {
        sb.append(element);
      } else if (!element.isEmpty()) {
        sb.append(urlEncode(element, IS_PATH, IS_NOT_FRAGMENT, IS_NOT_USERINFO));
      }
    }
    return sb.toString();
  }

  public String encodeQueryParameters(final UrlParameterMultimap queryParameters) {
    if (queryParameters == null)
      throw new IllegalArgumentException("query parameters map is required to not be null.");
    final StringBuilder sb = new StringBuilder();
    for (final Map.Entry<String, String> e : queryParameters.flatEntryList()) {
      sb.append(encodeQueryElement(e.getKey()));
      if (e.getValue() != null) {
        sb.append('=');
        sb.append(encodeQueryElement(e.getValue()));
      }
      sb.append('&');
    }
    sb.deleteCharAt(sb.length() - 1);
    return sb.toString();
  }

  public String encodeQueryElement(final String input) {
    return urlEncode(input, IS_NOT_PATH, IS_NOT_FRAGMENT, IS_NOT_USERINFO);
  }

  public String encodeFragment(final String input) {
    if (input == null || input.isEmpty()) {
      return input;
    }
    return urlEncode(input, IS_NOT_PATH, IS_FRAGMENT, IS_NOT_USERINFO);
  }

  public String urlEncode(final String input, final boolean isPath,
                          final boolean isFragment, final boolean isUserInfo) {
    final StringBuilder sb = new StringBuilder();
    final char[] inputChars = input.toCharArray();
    for (int i = 0; i < Character.codePointCount(inputChars, 0, inputChars.length); i++) {
      final CharBuffer cb;
      final int codePoint = Character.codePointAt(inputChars, i);
      if (Character.isBmpCodePoint(codePoint)) {
        final char c = Character.toChars(codePoint)[0];
        if ((isPath && Rfc3986Util.isPChar(c)) || isFragment && Rfc3986Util.isFragmentSafe(c) || isUserInfo && c == ':' || Rfc3986Util.isUnreserved(c)) {
          sb.append(c);
          continue;
        } else {
          cb = CharBuffer.allocate(1);
          cb.append(c);
        }
      } else {
        cb = CharBuffer.allocate(2);
        cb.append(Character.highSurrogate(codePoint));
        cb.append(Character.lowSurrogate(codePoint));
      }
      cb.rewind();
      final ByteBuffer bb = outputEncoding.encode(cb);
      for (int j = 0; j < bb.limit(); j++) {
        // simplicity over performance
        sb.append('%');
        sb.append(String.format(Locale.US, "%1$02X", bb.get(j)));
      }
    }
    return sb.toString();
  }

}