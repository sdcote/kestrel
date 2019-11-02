package coyote.commons;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.net.*;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class UrlBuilderTest {

  @Test
  void simpleUseCases() {
    // Build the URL over successive calls
    UrlBuilder builder = UrlBuilder.empty();
    builder.setHost("coyote.systems");
    builder.setScheme("https");
    builder.setPath("/docs/java");
    URL myUrl = builder.toUrl();
    assertNotNull(myUrl);
    assertEquals("coyote.systems", myUrl.getHost());

    // Common fluent API call - i.e. method chaining
    String text = UrlBuilder.empty()
            .setHost("coyote.systems")
            .setScheme("https")
            .setPath("/docs/java")
            .toString();

    text = UrlBuilder.empty()
            .setHost("coyote.systems")
            .setUserInfo("biff:passwd")
            .setScheme("https")
            .setPath("/docs/java")
            .toString();
    System.out.println(text);

    text = UrlBuilder.empty()
            .setHost("coyote.systems")
            .setUserInfo("biff")
            .setScheme("https")
            .setPath("/docs/java")
            .toString();
    System.out.println(text);

    text = UrlBuilder.empty()
            .setHost("coyote.systems")
            .setUserInfo("biff")
            .toString();
    System.out.println(text);

  }


  @Test
  public void userInfoTest() throws Exception {
    final String userInfo = "username:password";
    final String model = "http://" + userInfo + "@server/path?a=b#fragment";
    final UrlBuilder ub1 = UrlBuilder.fromString(model);
    assertEquals(ub1.userInfo, userInfo);
    assertEquals(ub1.toString(), model);
    final URL url1 = ub1.toUrl();
    assertEquals(url1.getUserInfo(), userInfo);
    assertEquals(url1.toString(), model);
    final UrlBuilder ub2 = UrlBuilder.fromUrl(new URL(model));
    assertEquals(ub2.userInfo, userInfo);
  }

  @Test
  public void incompleteUserInfoTest() throws Exception {
    final String userInfo = "username:password";
    final UrlBuilder ub1 = UrlBuilder.empty().setScheme("http").setUserInfo(userInfo);
    assertEquals(ub1.userInfo, userInfo);
    assertEquals(ub1.toString(), "http:");
    final URL url1 = ub1.toUrl();
    assertEquals(url1.toString(), "http:");
    assertNull(url1.getUserInfo());
    final UrlBuilder ub2 = UrlBuilder.fromString("http://username:password@");
    assertEquals(ub2.userInfo, userInfo);
    assertEquals(ub2.hostName, "");
    final UrlBuilder ub3 = UrlBuilder.fromString("http://username:password@/");
    assertEquals(ub3.userInfo, userInfo);
    assertEquals(ub3.hostName, "");
  }

  @Test()
  public void brokenUrlEncodingTest() throws Exception {
    Assertions.assertThrows(NumberFormatException.class, () -> {
      UrlBuilder.fromString("http://localhost/%ax");
    });
  }

  @Test
  public void utf8Test() throws Exception {
    assertEquals(UrlBuilder.fromString("http://foo/h%F6pl%E4", "ISO-8859-1").encodeAs("UTF-8").toString(),
            "http://foo/h%C3%B6pl%C3%A4");
  }

  @Test
  public void parameterTest() {
    final UrlBuilder ub1 = UrlBuilder.fromString("?a=b&a=c&b=c");
    assertTrue(ub1.queryParameters.containsKey("a"));
    assertTrue(ub1.queryParameters.containsKey("b"));
    assertEquals(ub1.queryParameters.get("a"), Arrays.asList("b", "c"));
  }

  @Test
  public void brokenparameterTest() {
    final UrlBuilder ub1 = UrlBuilder.fromString("?=b");
    assertEquals(ub1.queryParameters.get("").get(0), "b");
    final UrlBuilder ub2 = UrlBuilder.fromString("?==b");
    assertEquals(ub2.queryParameters.get("").get(0), "=b");
    assertEquals(ub2.toString(), "?=%3Db");
  }

  @Test
  public void simpleTest() throws Exception {
    final UrlBuilder ub1 = UrlBuilder.empty()
            .setScheme("http")
            .setHost("www.example.com")
            .setPath("/")
            .addParameter("foo", "bar");
    final String urlString1 = ub1.toString();

    final UrlBuilder ub2 = UrlBuilder.fromString("http://www.example.com/?foo=bar");
    final String urlString2 = ub2.toString();

    assertEquals(urlString1, "http://www.example.com/?foo=bar");
    assertEquals(urlString2, "http://www.example.com/?foo=bar");

    final String portUrl = "http://www.example.com:1234/?foo=bar";
    assertEquals(UrlBuilder.fromString(portUrl).toString(), portUrl);
  }

  @Test
  public void percentEndOfLineTest() throws Exception {
    final UrlBuilder ub1 = UrlBuilder.fromString("http://www.example.com/?q=Science%2");
    final UrlBuilder ub2 = UrlBuilder.fromString("http://www.example.com/?q=Science%25");
    final UrlBuilder ub3 = UrlBuilder.fromString("http://www.example.com/?q=Science%");
    final UrlBuilder ub4 = UrlBuilder.fromString("http://www.example.com/?q=Science%255");

    assertEquals(ub1.toString(), "http://www.example.com/?q=Science%252");
    assertEquals(ub2.toString(), "http://www.example.com/?q=Science%25");
    assertEquals(ub3.toString(), "http://www.example.com/?q=Science%25");
    assertEquals(ub4.toString(), "http://www.example.com/?q=Science%255");
  }

  @Test
  public void urlExceptionTest() throws Exception {
    UrlBuilder.fromString("https://www:1234/").toUriWithException();

    final String charset = "ISO-8859-1";
    final String foo = URLEncoder.encode("ööäöäöäö", charset);
    final String bar = URLDecoder.decode(foo, charset);
    final String url1 = "https://www:1234/foo?foo=" + foo;

    assertEquals(UrlBuilder.empty().encodeAs("ISO-8859-1")
                    .setHost("test")
                    .setPath("/foo")
                    .addParameter("foo", "öäöäöä")
                    .toString(),
            "//test/foo?foo=%F6%E4%F6%E4%F6%E4");

    assertEquals(UrlBuilder.fromString(url1, charset).encodeAs(charset).toString(), url1);

    assertEquals(UrlBuilder.fromString("?foo=%E4%F6%E4%F6%E4%F6%E4%F6%E4%F6", "ISO-8859-1").toString(),
            "?foo=%C3%A4%C3%B6%C3%A4%C3%B6%C3%A4%C3%B6%C3%A4%C3%B6%C3%A4%C3%B6");

    assertEquals(UrlBuilder.fromString("?foo=%E4%F6%E4%F6%E4%F6%E4%F6%E4%F6", "ISO-8859-1").encodeAs("ISO-8859-1").toString(),
            "?foo=%E4%F6%E4%F6%E4%F6%E4%F6%E4%F6");

    assertEquals(UrlBuilder.fromString("http://foo/h%E4pl%F6", "ISO-8859-1").encodeAs("UTF-8").toString(),
            "http://foo/h%C3%A4pl%C3%B6");
  }

  @Test
  public void testEmptyParameterNameAfterAmpersand() {
    assertEquals("foo", UrlBuilder.fromString("http://www.google.com/?q=foo&").queryParameters.get("q").get(0));
  }

  @Test
  public void testArrayParameterOrderStability() {
    final String qp1 = "?a=1&b=2&a=3&b=4";
    assertEquals(UrlBuilder.fromString(qp1).toString(), qp1);
  }

  @Test
  public void testNonArrayParameterOrderStability() {
    final String qp1 = "?a=1&b=2&c=3&d=4";
    assertEquals(UrlBuilder.fromString(qp1).toString(), qp1);
    final String qp2 = "?d=1&c=2&b=3&a=4";
    assertEquals(UrlBuilder.fromString(qp2).toString(), qp2);
  }

  @Test
  public void testPortParsing() {
    assertUrlBuilderEquals(null, "localhost", 8080, "/thing", UrlBuilder.fromString("http://localhost:8080/thing"));
    assertUrlBuilderEquals(null, "localhost", null, "/thing", UrlBuilder.fromString("http://localhost/thing"));
    assertUrlBuilderEquals("wonderdog", "localhost", null, "/thing", UrlBuilder.fromString("http://wonderdog@localhost/thing"));
    assertUrlBuilderEquals("wonderdog", "localhost", 808, "/thing", UrlBuilder.fromString("http://wonderdog@localhost:808/thing"));
    assertUrlBuilderEquals(null, "github.com", null, "", UrlBuilder.fromString("https://github.com"));
    assertUrlBuilderEquals(null, "github.com", 443, "", UrlBuilder.fromString("https://github.com:443.com"));
  }

  private static void assertUrlBuilderEquals(String expectedUserInfo, String expectedHostName, Integer expectedPort, String expectedPath, UrlBuilder b) {
    assertEquals(b.userInfo, expectedUserInfo);
    assertEquals(b.hostName, expectedHostName);
    assertEquals(b.port, expectedPort);
    assertEquals(b.path, expectedPath);
  }

  @Test
  public void parsePortAndHostname() {
    UrlBuilder b = UrlBuilder.fromString("http://foo:8080/foo");
    assertEquals(b.port, Integer.valueOf(8080));
    assertEquals(b.hostName, "foo");
  }

  @Test
  public void encodedPathFromURI() throws URISyntaxException {
    assertEquals(UrlBuilder.fromUri(new URI("http://foo/a%20b")).toString(), "http://foo/a%20b");
    assertEquals(UrlBuilder.fromUri(new URI("http://foo/a%7Bb")).toString(), "http://foo/a%7Bb");
  }

  @Test
  public void containsParameter() {
    final UrlBuilder b = UrlBuilder.fromString("/?a=1");
    assertTrue(b.queryParameters.containsKey("a"), "builder contains parameter");
    assertFalse(b.queryParameters.containsKey("b"), "builder doesn't contain parameter");
  }

  @Test
  public void testSetParameterShouldReplaceExistingParameter() {
    UrlBuilder builder = UrlBuilder.fromString("http://somehost.com/page?parameter1=value1");
    assertEquals(builder.setParameter("parameter1", "value2").toString(), "http://somehost.com/page?parameter1=value2");
  }

  @Test
  public void testAddParameterShouldAppendOneNewParameter() {
    UrlBuilder b = UrlBuilder.fromString("http://somehost.com/page?parameter1=value1");
    assertEquals(b.addParameter("parameter1", "value2").toString(), "http://somehost.com/page?parameter1=value1&parameter1=value2");
  }

  @Test
  public void testWithFragmentShouldAppendAnchor() {
    UrlBuilder b = UrlBuilder.fromString("http://somehost.com/page");
    assertEquals(b.setFragment("anchor").toString(), "http://somehost.com/page#anchor");
  }

  @Test
  public void testWithAllowedPlusSignInPath() {
    UrlBuilder b = UrlBuilder.fromString("http://somehost.com/page/++++");
    assertEquals(b.toString(), "http://somehost.com/page/++++");
  }


  @Test()
  public void runtimeUriSyntaxExceptionTest() {
    Assertions.assertThrows(RuntimeURISyntaxException.class, () -> {
      UrlBuilder.empty().setHost("%2").toUri();
    });
  }

  @Test()
  public void uriSyntaxExceptionTest() throws URISyntaxException {
    Assertions.assertThrows(URISyntaxException.class, () -> {
      UrlBuilder.empty().setHost("%2").toUriWithException();
    });
  }

  @Test()
  public void runtimeMalformedURLExceptionTest() {
    Assertions.assertThrows(RuntimeMalformedURLException.class, () -> {
      UrlBuilder.empty().toUrl();
    });
  }

  @Test()
  public void malformedURLExceptionTest() throws MalformedURLException {
    Assertions.assertThrows(MalformedURLException.class, () -> {
      UrlBuilder.empty().toUrlWithException();
    });
  }


//  @Test
//  public void testRemoveParameter() {
//    UrlBuilder b = UrlBuilder.fromString("http://somehost.com/page?parameter1=value1");
//    assertFalse(b.removeParameters("parameter1").queryParameters.containsKey("parameter1"));
//    assertEquals(b.removeParameter("parameter1", "value1").toString(), "http://somehost.com/page");
//  }
//
//  @Test
//  public void testRemoveOneParameterByKey() {
//    UrlBuilder b = UrlBuilder.fromString("http://somehost.com/page?parameter1=value1");
//    assertFalse(b.removeParameters("parameter1").queryParameters.containsKey("parameter1"));
//    assertEquals(b.removeParameters("parameter1").toString(), "http://somehost.com/page");
//  }
//
//  @Test
//  public void testRemoveTwoParametersByKey() {
//    UrlBuilder b = UrlBuilder.fromString("http://somehost.com/page?parameter1=value1&parameter1=value2");
//    assertFalse(b.removeParameters("parameter1").queryParameters.containsKey("parameter1"));
//    assertEquals(b.removeParameters("parameter1").toString(), "http://somehost.com/page");
//  }
//
//  @Test
//  public void testRemoveThreeParametersByKey() {
//    UrlBuilder b = UrlBuilder.fromString("http://somehost.com/page?parameter1=value1&parameter1=value2&parameter1=value3");
//    assertFalse(b.removeParameters("parameter1").queryParameters.containsKey("parameter1"));
//    assertEquals(b.removeParameters("parameter1").toString(), "http://somehost.com/page");
//  }

}