/**
 * MIT License
 *
 * Copyright (c) 2025 ComPosiX
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package io.github.composix.apis;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.IntFunction;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import com.fasterxml.jackson.core.JsonPointer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.TextNode;

import io.github.composix.varargs.ArgsI;
import io.github.composix.varargs.ArgsIII;

public final class Api {

  static class Json {

    private final JsonPointer pointer;

    static Json compile(String expr) {
      return new Json(JsonPointer.compile(expr));
    }

    private Json(JsonPointer pointer) {
      this.pointer = pointer;
    }

    public CharSequence text(JsonNode json) {
      return json.at(pointer).asText();
    }

    public CharSequence[] properties(JsonNode json) {
      return StreamSupport.stream(
        Spliterators.spliteratorUnknownSize(
          json.at(pointer).fieldNames(),
          Spliterator.ORDERED
        ),
        false
      ).toArray(CharSequence[]::new);
    }
  }

  private final Json TITLE = Json.compile("/info/title");
  private final Json PATHS = Json.compile("/paths");

  public static final ObjectMapper MAPPER = new ObjectMapper();

  public static final Api select(ArgsI<CharSequence> args) {
    try {
      return new Api(args);
    } catch (NoSuchFieldException e) {
      throw new IllegalArgumentException(e);
    }
  }

  public static URI toUri(CharSequence url) {
    try {
      return new URI(url.toString());
    } catch (URISyntaxException e) {
      throw new UncheckedIOException(new IOException(e));
    }
  }

  public static URL toUrl(URI url) {
      try {
        return url.toURL();
      } catch (MalformedURLException e) {
        throw new UncheckedIOException(e);
      }
  }

  public static JsonNode readTree(URL url) {
    try {
      return MAPPER.readTree(url);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  private final ArgsIII<CharSequence, URI, JsonNode> titlesWithUrisAndSwaggers;
  private final List<ApiResource<?>> resources;

  private Api(ArgsI<CharSequence> args) throws NoSuchFieldException {
    // Retrieve the URIs
    URI[] uris = header(URI[]::new, URI.create("urls"), args.streamA("urls").map(Api::toUri));

    // Retrieve the Swaggers
    JsonNode[] swaggers = header(
      JsonNode[]::new,
      new TextNode("swagger"), 
      Stream.of(uris).map(Api::toUrl).map(Api::readTree)
    );

    // Get the titles from the swaggers
    CharSequence[] titles = header(
      CharSequence[]::new,
      "title",
      Stream.of(swaggers).skip(1).map(TITLE::text)
    );

    titlesWithUrisAndSwaggers = ArgsI.of(titles)
    .extendB(uris).extendC(swaggers);

    // retain only selected titles
    titlesWithUrisAndSwaggers.onA().retainAll(args.onA().toSet());

    // create the Api resources
    final Stream<Stream<ApiResource<?>>> resourcesBySwagger = titlesWithUrisAndSwaggers.onB().thenOnC().toMap().entrySet().stream().map(entry -> {
      return Stream.of(PATHS.properties(entry.getValue())).map(path -> new ApiResource<>(entry.getKey(), path));
    });

    resources = Arrays.asList(resourcesBySwagger.flatMap(x -> x).toArray(ApiResource[]::new));
    if (resources.stream().distinct().count() != resources.size()) {
      throw new IllegalArgumentException("duplicate api resource path");
    }

    Collections.sort(resources);
  }

  public final <T> ApiResource<T> resource(CharSequence path, Class<T> dto) {
    final int index = Collections.binarySearch(resources, path);
    if (index < 0) {
      throw new IllegalArgumentException("resource path not found");
    }
    @SuppressWarnings("unchecked")
    final ApiResource<T> resource = (ApiResource<T>) resources.get(index);
    resource.dtoClass = dto;
    return resource;
  }

  private static <T> T[] header(
    IntFunction<T[]> generator,
    T header,
    Stream<T> stream
  ) {
    return Stream.concat(Stream.of(header), stream).toArray(generator);
  }
}
