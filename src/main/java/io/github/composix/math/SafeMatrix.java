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

package io.github.composix.math;

public class SafeMatrix extends Matrix {

  protected SafeMatrix(int ordinal) {
    super(ordinal);
  }

  private static int MASK = 15;

  private Object[] argv = new Object[MASK + 1];

  @Override
  public ArgsI<A> clone() throws CloneNotSupportedException {
    final SafeMatrix result = (SafeMatrix) super.clone();
    result.argv = argv.clone();
    return result;
  }

  @Override
  public int hashCode() {
    return 0;
  }

  @Override
  protected Object[] argv() {
    return argv;
  }

  @Override
  protected int mask() {
    return MASK;
  }

  @Override
  protected int mask(int index) {
    return index & MASK;
  }
}
