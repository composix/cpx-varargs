/**
 * interface Args
 *
 * This interface serves as the foundation for type-safe interfaces such as ArgsI,
 * ArgsII, etc., which manage tabular data organized into rows and columns.
 * It conforms to the familiar APIs from the Java Collection Framework and Java Streams,
 * but empowers them to deal with tabular data.
 *
 * Key functionalities include:
 * - Sorting rows based on a specified column, with support for custom sorting logic via
 *   an accessor function.
 * - Grouping and joining data (in conjunction with the Keys interface), facilitating
 *   more advanced data manipulation.
 * - Performing SQL-like operations such as filtering, grouping, and joining, akin to
 *   operations found in SQL queries.
 * - Splitting columns (e.g., parsing CSV lines) and merging multiple columns into a
 *   single column, which is often represented as a DTO (Data Transfer Object) for data
 *   transfer purposes.
 *
 * Example usage:
 * - Sorting rows by a column:
 *     args.column("age").sort(Comparator.naturalOrder());
 * - Grouping by column:
 *     orders.groupBy(Order::petId)
 *       .collect(Order::amount, Long::sum);
 * - Joining two tables:
 *     pets.on("petId")
 *       .joinMany(orders.on("petId"));
 * - Splitting cvs lines and combining into DTO:
 *     args.split(ArgsOrdinal.PATTERN)
 *       .combine(Pet.DEFAULTS, 1);
 *
 * Author: dr. ir. J. M. Valk
 * Date: April 2025
 */

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

import io.github.composix.models.Defaults;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.function.ToLongFunction;
import java.util.regex.Pattern;
import java.util.stream.LongStream;
import java.util.stream.Stream;

public interface Args extends Cols, MutableOrder {
  /**
   * Retrieve the text-based column with a given header. For example,
   * column("name") returns the column with the header "name".
   *
   * @param header - the header of the column
   * @return a list of values in the column
   * @throws NoSuchFieldException - if no column with the given header exists
   */
  List<CharSequence> column(CharSequence header) throws NoSuchFieldException;

  /**
   * Retrieve the column with a given header, but only if this column is
   * of a certain type; otherwise a NoSuchFieldException will be thrown
   *
   * @param header - the header of the column
   * @return a list of values in the column
   * @throws NoSuchFieldException - if no column with the given header exists
   * or it is not of the given type
   */
  <T> List<T> column(CharSequence header, Ordinal type)
    throws NoSuchFieldException;

  /**
   * Retrieve the column with a given header, but only if this column is
   * of a certain type; otherwise a NoSuchFieldException will be thrown
   *
   * @param header - the header of the column
   * @return a list of values in the column
   * @throws NoSuchFieldException - if no column with the given header exists
   * or it is not of the given type
   */
  <T> List<T> column(CharSequence header, Class<T> type)
    throws NoSuchFieldException;

  /**
   * Retrieve the pos-th primitive long stream of a column. For example,
   * longStream(1) returns the long stream of the first column of type long[],
   * longStream(2) returns the second column of type long[], and so on.
   *
   * @param pos - the position within the long columns
   * @return a LongStream of values in the column
   * @throws IndexOutOfBoundsException - if pos is out of bounds
   */
  LongStream longStream(int pos);

  /**
   * Retrieve the primitive long stream of a column with a given header.
   * For example, longStream("name") returns a long stream streaming the values
   * in the column with the header "name".
   *
   * @param header - the header of the column
   * @return a long stream of values in the column
   * @throws NoSuchFieldException - if no column with the given header exists
   * or this column is not of type long[]
   */
  LongStream longStream(CharSequence header);

  /**
   * Prepare for joining on a given column; a subsequence join call on the
   * Keys interface will join on the selected column.
   *
   * @param tpos - the type position of the column
   * @param pos - the position within columns of same type
   * @return a Keys object for joining
   * @throws IndexOutOfBoundsException - if pos is out of bounds
   */

  <T extends Defaults<T>> Args primaryKey(Ordinal tpos, ToLongFunction<T> accessor);

    /**
   * Prepare for joining on a given column; a subsequence join call on the
   * Keys interface will join on the selected column.
   *
   * @param tpos - the type position of the column
   * @param pos - the position within columns of same type
   * @return a Keys object for joining
   * @throws IndexOutOfBoundsException - if pos is out of bounds
   */

  <T extends Defaults<T>> Args foreignKey(Ordinal tpos, ToLongFunction<T> accessor);

  Args pk(CharSequence name, Ordinal type) throws NoSuchFieldException;

  Args fk(CharSequence name, Ordinal type) throws NoSuchFieldException;

  Args attr(CharSequence name, Ordinal type) throws NoSuchFieldException;

  Args joinOne(Args rhs);

  Keys joinMany(Args rhs);


  /**
   * Group a DTO column by a given accessor function. This method will
   * first sort column(tpos) using the given accessor function, and then
   * apply a grouping on column(tpos) that can be used for futher joining
   * and collecting using the method in the Keys interface.
   *
   * @param tpos - the type position of the column
   * @param accessor - the accessor function to group by
   * @return a Keys object prepared with the grouping
   */
  <T extends Defaults<T>, K extends Comparable<K>> Keys groupBy(
    Ordinal tpos,
    Function<T, K> accessor
  );

  /**
   * Group a DTO column by a given accessor function. This method will
   * first sort column(tpos) using the given accessor function, and then
   * apply a grouping on column(tpos) that can be used for futher joining
   * and collecting using the method in the Keys interface.
   *
   * @param tpos - the type position of the column
   * @param accessor - the accessor function to group by
   * @return a Keys object prepared with the grouping
   */
  <T extends Defaults<T>> Keys groupBy(Ordinal col, ToLongFunction<T> accessor);

  /**
   * Split a CharSequence column into multiple CharSequence columns based on a given pattern.
   * For example, split(ArgsOrdinal.PATTERN) will spread a line of csv data over multiple columns.
   *
   * Note that, as a side effect, this method reuses the original Args object. This means that the
   * original lines are no longer available after the call, and are therefore eligible for cleanup
   * by the garbage collector.
   *
   * @param pattern - the pattern to split the column
   * @return a new Args object with the split columns
   */
  Args split(Pattern pattern);

  /**
   * Combine multiple columns into a single column of a given DTO (Data Transfer Object) type.
   * This DTO must implement the Defaults interface, and can be provided by given an instance
   * of defaults values of the DTO.
   *
   * @param defaults - the defaults values of the DTO
   * @return new Args object with the combined columns
   * @throws IndexOutOfBoundsException - pos or repeat is out of bounds
   */
  <T extends Defaults<T>> Column<T> combine(T defaults);

  /**
   * Parse a single CharSequence column into a single primitive long column.
   *
   * @param pos - position of the text-based column where to start
   * @param repeat - the number of column to repeat the parsing on
   * @return a new Args object with the parsed long columns
   * @throws IndexOutOfBoundsException - pos or repeat is out of bounds
   */
  Args parse(Class<?> type, int pos, int repeat);

  void clear();
  
  @Override
  Args clone() throws CloneNotSupportedException;

  void export(Args target, byte position, int size);

  @Deprecated
  Args select(Order order);

  <T> T getValue(int index);

  default Args getArgsValue(int index) {
    return (Args) getValue(index);
  }

  long getLongValue(int index);

  Ordinal ordinalAt(Ordinal ordinal, Object value);

  @Deprecated
  Comparator<Ordinal> comparator(Ordinal ordinal);

  @Deprecated
  <T, K extends Comparable<K>> Comparator<Ordinal> comparator(
    Ordinal ordinal,
    Function<T, K> accessor
  );

  @Deprecated
  <T> Comparator<Ordinal> comparator(
    Ordinal ordinal,
    ToLongFunction<T> accessor
  );

  @Deprecated
  <T> Stream<T> stream(Ordinal col);

  @Deprecated
  <T> Stream<T> stream(Class<T> type, int pos) throws NoSuchFieldException;

  @Deprecated
  LongStream longStream(Ordinal col);

  @Deprecated
  default <T> Stream<T> stream(Class<T> type) throws NoSuchFieldException {
    return stream(type, 0);
  }
}
