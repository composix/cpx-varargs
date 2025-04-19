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
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import com.fasterxml.jackson.core.JsonPointer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.github.composix.math.Column;
import io.github.composix.varargs.AttrI;
import io.github.composix.varargs.Chars;

public final class Api {

  static class Json {

    private final JsonPointer pointer;

    static Json compile(String expr) {
      return new Json(JsonPointer.compile(expr));
    }

    private Json(JsonPointer pointer) {
      this.pointer = pointer;
    }

    public String text(JsonNode json) {
      return json.at(pointer).asText();
    }

    public Stream<String> properties(JsonNode json) {
      return StreamSupport.stream(
        Spliterators.spliteratorUnknownSize(
          json.at(pointer).fieldNames(),
          Spliterator.ORDERED
        ),
        false
      );
    }
  }

  private final Json TITLE = Json.compile("/info/title");
  private final Json PATHS = Json.compile("/paths");

  public static final ObjectMapper MAPPER = new ObjectMapper();

  public static final Api select(Chars args) {
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

  public static JsonNode readTree(URI uri) {
    try {
      return MAPPER.readTree(uri.toURL());
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  private final Column<ApiResource> resources;

  private Api(Chars args) throws NoSuchFieldException {
    // Retrieve the Swaggers...
    final AttrI<JsonNode> urisSwaggersTitles = args
      .attrURI("url:") // get the URLs
      .attrX(JsonNode.class) // custom attribute type X = JsonNode
      .mapUX(1, Api::readTree)
      // read swagggers from URLs
      .mapXS(1, TITLE::text); // get the titles

    // ...and retain only selected titles
    urisSwaggersTitles
      .stringColumn(1)
      .retainAll(args.attrString("title:").attr().stringColumn(-1));

    // create the Api resources...
    resources = urisSwaggersTitles
      // ...add the resource paths
      .flatMapXS(1, PATHS::properties)
      .attrY(ApiResource.class)
      .mapUSY(1, 2, ApiResource::new)
      .columnY(1);
    // TODO: ...assure unique resource path by mapping paths to URIs
  }

  public final <T> ApiResource<T> resource(CharSequence path, Class<T> dto) {
    final int index = resources.binarySearch(path);
    if (index < 0) {
      throw new IllegalArgumentException("resource path not found");
    }
    @SuppressWarnings("unchecked")
    final ApiResource<T> resource = (ApiResource<T>) resources.get(index);
    resource.dtoClass = dto;
    return resource;
  }
}
