package coyote.commons;

import java.util.Arrays;

final class Rfc3986Util {

  private static final char[] SUB_DELIMITERS = {'!', '$', '&', '\'', '(', ')', '*', '+', ',', ';', '='};

  static {
    Arrays.sort(SUB_DELIMITERS);
  }

  static boolean isFragmentSafe(final char c) {
    return isPChar(c) || c == '/' || c == '?';
  }

  static boolean isPChar(final char c) {
    // Excludes % used in %XX chars
    return isUnreserved(c) || isSubDelimeter(c) || c == ':' || c == '@';
  }

  static boolean isUnreserved(final char c) {
    return ('a' <= c && c <= 'z') || ('A' <= c && c <= 'Z') || ('0' <= c && c <= '9') || (c == '-' || c == '.' || c == '_' || c == '~');
  }

  private static boolean isSubDelimeter(final char c) {
    return Arrays.binarySearch(SUB_DELIMITERS, c) >= 0;
  }

}