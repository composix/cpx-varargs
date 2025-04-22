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
import java.net.URI;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.github.composix.models.Defaults;
import io.github.composix.varargs.ArgsI;

public final class ApiResource<T extends Defaults<T>> implements CharSequence, Comparable<CharSequence> {

  public static final ObjectMapper MAPPER = new ObjectMapper();

  static {
    // Configure Jackson to accept case-insensitive enums
    MAPPER.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS, true);
  }

  Class<T> dtoClass;
  private final URI uri;
  private final String path;

  public ApiResource(URI base, CharSequence path) {
    uri = base.resolve(path.toString().substring(1));
    this.path = path.toString().intern();
  }

  public ArgsI<T> get(CharSequence query) throws IOException {
    if (query.charAt(0) != '?') {
      throw new IllegalArgumentException("query must start with '?'");
    }
    T[] column = (T[]) MAPPER.readValue(uri.resolve(query.toString()).toURL(), dtoClass.arrayType());
    return (ArgsI<T>) ArgsI.of(column);
  }

  @Override
  public String toString() {
    return path;
  }

  @Override
  public int hashCode() {
    return path.hashCode();
  }

  @Override
  public boolean equals(Object rhs) {
    return compareTo((CharSequence) rhs) == 0;
  }

  @Override
  public int length() {
    return path.length();
  }

  @Override
  public char charAt(int index) {
    return path.charAt(index);
  }

  @Override
  public CharSequence subSequence(int start, int end) {
    return path.subSequence(start, end);
  }

  @Override
  public int compareTo(CharSequence o) {
    return path.compareTo(o.toString());
  }
}
