/* Generic definitions */




/* Assertions (useful to generate conditional code) */
/* Current type and class (and size, if applicable) */
/* Value methods */
/* Interfaces (keys) */
/* Interfaces (values) */
/* Abstract implementations (keys) */
/* Abstract implementations (values) */
/* Static containers (keys) */
/* Static containers (values) */
/* Implementations */
/* Synchronized wrappers */
/* Unmodifiable wrappers */
/* Other wrappers */
/* Methods (keys) */
/* Methods (values) */
/* Methods (keys/values) */
/* Methods that have special names depending on keys (but the special names depend on values) */
/* Equality */
/* Object/Reference-only definitions (keys) */
/* Primitive-type-only definitions (keys) */
/* Object/Reference-only definitions (values) */
/*		 
 * Copyright (C) 2002-2011 Sebastiano Vigna 
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 *
 *
 *
 * For the sorting and binary search code:
 *
 * Copyright (C) 1999 CERN - European Organization for Nuclear Research.
 *
 *   Permission to use, copy, modify, distribute and sell this software and
 *   its documentation for any purpose is hereby granted without fee,
 *   provided that the above copyright notice appear in all copies and that
 *   both that copyright notice and this permission notice appear in
 *   supporting documentation. CERN makes no representations about the
 *   suitability of this software for any purpose. It is provided "as is"
 *   without expressed or implied warranty. 
 */
package it.unimi.dsi.fastutil.booleans;
import it.unimi.dsi.fastutil.Arrays;
import it.unimi.dsi.fastutil.Hash;

import java.util.Random;
/** A class providing static methods and objects that do useful things with type-specific arrays.
 *
 * <p>In particular, the <code>ensureCapacity()</code>, <code>grow()</code>,
 * <code>trim()</code> and <code>setLength()</code> methods allow to handle
 * arrays much like array lists. This can be very useful when efficiency (or
 * syntactic simplicity) reasons make array lists unsuitable.
 *
 * <P>Note that {@link it.unimi.dsi.fastutil.io.BinIO} and {@link it.unimi.dsi.fastutil.io.TextIO}
 * contain several methods make it possible to load and save arrays of primitive types as sequences
 * of elements in {@link java.io.DataInput} format (i.e., not as objects) or as sequences of lines of text.
 *
 * @see java.util.Arrays
 */
public class BooleanArrays {





 /** The inverse of the golden ratio times 2<sup>16</sup>. */
 public static final long ONEOVERPHI = 106039;

 private BooleanArrays() {}

 /** A static, final, empty array. */
 public final static boolean[] EMPTY_ARRAY = {};
 /** Ensures that an array can contain the given number of entries.
	 *
	 * <P>If you cannot foresee whether this array will need again to be
	 * enlarged, you should probably use <code>grow()</code> instead.
	 *
	 * @param array an array.
	 * @param length the new minimum length for this array.
	 * @return <code>array</code>, if it contains <code>length</code> entries or more; otherwise,
	 * an array with <code>length</code> entries whose first <code>array.length</code>
	 * entries are the same as those of <code>array</code>.
	 */
 public static boolean[] ensureCapacity( final boolean[] array, final int length ) {
  if ( length > array.length ) {
   final boolean t[] =
    new boolean[ length ];
   System.arraycopy( array, 0, t, 0, array.length );
   return t;
  }
  return array;
 }
 /** Ensures that an array can contain the given number of entries, preserving just a part of the array.
	 *
	 * @param array an array.
	 * @param length the new minimum length for this array.
	 * @param preserve the number of elements of the array that must be preserved in case a new allocation is necessary.
	 * @return <code>array</code>, if it can contain <code>length</code> entries or more; otherwise,
	 * an array with <code>length</code> entries whose first <code>preserve</code>
	 * entries are the same as those of <code>array</code>.
	 */
 public static boolean[] ensureCapacity( final boolean[] array, final int length, final int preserve ) {
  if ( length > array.length ) {
   final boolean t[] =
    new boolean[ length ];
   System.arraycopy( array, 0, t, 0, preserve );
   return t;
  }
  return array;
 }
 /** Grows the given array to the maximum between the given length and
	 * the current length divided by the golden ratio, provided that the given
	 * length is larger than the current length.
	 *
	 * <P> Dividing by the golden ratio (&phi;) approximately increases the array
	 * length by 1.618. If you want complete control on the array growth, you
	 * should probably use <code>ensureCapacity()</code> instead.
	 *
	 * @param array an array.
	 * @param length the new minimum length for this array.
	 * @return <code>array</code>, if it can contain <code>length</code>
	 * entries; otherwise, an array with
	 * max(<code>length</code>,<code>array.length</code>/&phi;) entries whose first
	 * <code>array.length</code> entries are the same as those of <code>array</code>.
	 * */
 public static boolean[] grow( final boolean[] array, final int length ) {
  if ( length > array.length ) {
   final int newLength = (int)Math.min( Math.max( ( ONEOVERPHI * array.length ) >>> 16, length ), Integer.MAX_VALUE );
   final boolean t[] =
    new boolean[ newLength ];
   System.arraycopy( array, 0, t, 0, array.length );
   return t;
  }
  return array;
 }
 /** Grows the given array to the maximum between the given length and
	 * the current length divided by the golden ratio, provided that the given
	 * length is larger than the current length, preserving just a part of the array.
	 *
	 * <P> Dividing by the golden ratio (&phi;) approximately increases the array
	 * length by 1.618. If you want complete control on the array growth, you
	 * should probably use <code>ensureCapacity()</code> instead.
	 *
	 * @param array an array.
	 * @param length the new minimum length for this array.
	 * @param preserve the number of elements of the array that must be preserved in case a new allocation is necessary.
	 * @return <code>array</code>, if it can contain <code>length</code>
	 * entries; otherwise, an array with
	 * max(<code>length</code>,<code>array.length</code>/&phi;) entries whose first
	 * <code>preserve</code> entries are the same as those of <code>array</code>.
	 * */
 public static boolean[] grow( final boolean[] array, final int length, final int preserve ) {
  if ( length > array.length ) {
   final int newLength = (int)Math.min( Math.max( ( ONEOVERPHI * array.length ) >>> 16, length ), Integer.MAX_VALUE );
   final boolean t[] =
    new boolean[ newLength ];
   System.arraycopy( array, 0, t, 0, preserve );
   return t;
  }
  return array;
 }
 /** Trims the given array to the given length.
	 *
	 * @param array an array.
	 * @param length the new maximum length for the array.
	 * @return <code>array</code>, if it contains <code>length</code>
	 * entries or less; otherwise, an array with
	 * <code>length</code> entries whose entries are the same as
	 * the first <code>length</code> entries of <code>array</code>.
	 * 
	 */
 public static boolean[] trim( final boolean[] array, final int length ) {
  if ( length >= array.length ) return array;
  final boolean t[] =
   length == 0 ? EMPTY_ARRAY : new boolean[ length ];
  System.arraycopy( array, 0, t, 0, length );
  return t;
 }
 /** Sets the length of the given array.
	 *
	 * @param array an array.
	 * @param length the new length for the array.
	 * @return <code>array</code>, if it contains exactly <code>length</code>
	 * entries; otherwise, if it contains <em>more</em> than
	 * <code>length</code> entries, an array with <code>length</code> entries
	 * whose entries are the same as the first <code>length</code> entries of
	 * <code>array</code>; otherwise, an array with <code>length</code> entries
	 * whose first <code>array.length</code> entries are the same as those of
	 * <code>array</code>.
	 * 
	 */
 public static boolean[] setLength( final boolean[] array, final int length ) {
  if ( length == array.length ) return array;
  if ( length < array.length ) return trim( array, length );
  return ensureCapacity( array, length );
 }
 /** Returns a copy of a portion of an array.
	 *
	 * @param array an array.
	 * @param offset the first element to copy.
	 * @param length the number of elements to copy.
	 * @return a new array containing <code>length</code> elements of <code>array</code> starting at <code>offset</code>.
	 */
 public static boolean[] copy( final boolean[] array, final int offset, final int length ) {
  ensureOffsetLength( array, offset, length );
  final boolean[] a =
   length == 0 ? EMPTY_ARRAY : new boolean[ length ];
  System.arraycopy( array, offset, a, 0, length );
  return a;
 }
 /** Returns a copy of an array.
	 *
	 * @param array an array.
	 * @return a copy of <code>array</code>.
	 */
 public static boolean[] copy( final boolean[] array ) {
  return array.clone();
 }
 /** Fills the given array with the given value.
	 *
	 * <P>This method uses a backward loop. It is significantly faster than the corresponding
	 * method in {@link java.util.Arrays}.
	 *
	 * @param array an array.
	 * @param value the new value for all elements of the array.
	 */
 public static void fill( final boolean[] array, final boolean value ) {
  int i = array.length;
  while( i-- != 0 ) array[ i ] = value;
 }
 /** Fills a portion of the given array with the given value.
	 *
	 * <P>If possible (i.e., <code>from</code> is 0) this method uses a
	 * backward loop. In this case, it is significantly faster than the
	 * corresponding method in {@link java.util.Arrays}.
	 *
	 * @param array an array.
	 * @param from the starting index of the portion to fill.
	 * @param to the end index of the portion to fill.
	 * @param value the new value for all elements of the specified portion of the array.
	 */
 public static void fill( final boolean[] array, final int from, int to, final boolean value ) {
  ensureFromTo( array, from, to );
  if ( from == 0 ) while( to-- != 0 ) array[ to ] = value;
  else for( int i = from; i < to; i++ ) array[ i ] = value;
 }
 /** Returns true if the two arrays are elementwise equal.
	 *
	 * <P>This method uses a backward loop. It is significantly faster than the corresponding
	 * method in {@link java.util.Arrays}.
	 *
	 * @param a1 an array.
	 * @param a2 another array.
	 * @return true if the two arrays are of the same length, and their elements are equal.
	 */
 public static boolean equals( final boolean[] a1, final boolean a2[] ) {
  int i = a1.length;
  if ( i != a2.length ) return false;
  while( i-- != 0 ) if (! ( (a1[ i ]) == (a2[ i ]) ) ) return false;
  return true;
 }
 /** Ensures that a range given by its first (inclusive) and last (exclusive) elements fits an array.
	 *
	 * <P>This method may be used whenever an array range check is needed.
	 *
	 * @param a an array.
	 * @param from a start index (inclusive).
	 * @param to an end index (inclusive).
	 * @throws IllegalArgumentException if <code>from</code> is greater than <code>to</code>.
	 * @throws ArrayIndexOutOfBoundsException if <code>from</code> or <code>to</code> are greater than the array length or negative.
	 */
 public static void ensureFromTo( final boolean[] a, final int from, final int to ) {
  Arrays.ensureFromTo( a.length, from, to );
 }
 /** Ensures that a range given by an offset and a length fits an array.
	 *
	 * <P>This method may be used whenever an array range check is needed.
	 *
	 * @param a an array.
	 * @param offset a start index.
	 * @param length a length (the number of elements in the range).
	 * @throws IllegalArgumentException if <code>length</code> is negative.
	 * @throws ArrayIndexOutOfBoundsException if <code>offset</code> is negative or <code>offset</code>+<code>length</code> is greater than the array length.
	 */
 public static void ensureOffsetLength( final boolean[] a, final int offset, final int length ) {
  Arrays.ensureOffsetLength( a.length, offset, length );
 }
 private static final int SMALL = 7;
 private static final int MEDIUM = 50;
 private static void swap( final boolean x[], final int a, final int b ) {
  final boolean t = x[ a ];
  x[ a ] = x[ b ];
  x[ b ] = t;
 }
 private static void vecSwap( final boolean[] x, int a, int b, final int n ) {
  for( int i = 0; i < n; i++, a++, b++ ) swap( x, a, b );
 }
 private static int med3( final boolean x[], final int a, final int b, final int c, BooleanComparator comp ) {
  int ab = comp.compare( x[ a ], x[ b ] );
  int ac = comp.compare( x[ a ], x[ c ] );
  int bc = comp.compare( x[ b ], x[ c ] );
  return ( ab < 0 ?
   ( bc < 0 ? b : ac < 0 ? c : a ) :
   ( bc > 0 ? b : ac > 0 ? c : a ) );
 }
 private static void selectionSort( final boolean[] a, final int from, final int to, final BooleanComparator comp ) {
  for( int i = from; i < to - 1; i++ ) {
   int m = i;
   for( int j = i + 1; j < to; j++ ) if ( comp.compare( a[ j ], a[ m ] ) < 0 ) m = j;
   if ( m != i ) {
    final boolean u = a[ i ];
    a[ i ] = a[ m ];
    a[ m ] = u;
   }
  }
 }
 private static void insertionSort( final boolean[] a, final int from, final int to, final BooleanComparator comp ) {
  for ( int i = from; ++i < to; ) {
   boolean t = a[ i ];
   int j = i;
   for ( boolean u = a[ j - 1 ]; comp.compare( t, u ) < 0; u = a[ --j - 1 ] ) {
    a[ j ] = u;
    if ( from == j - 1 ) {
     --j;
     break;
    }
   }
   a[ j ] = t;
  }
 }
 @SuppressWarnings("unchecked")
 private static void selectionSort( final boolean[] a, final int from, final int to ) {
  for( int i = from; i < to - 1; i++ ) {
   int m = i;
   for( int j = i + 1; j < to; j++ ) if ( ( !(a[ j ]) && (a[ m ]) ) ) m = j;
   if ( m != i ) {
    final boolean u = a[ i ];
    a[ i ] = a[ m ];
    a[ m ] = u;
   }
  }
 }
 @SuppressWarnings("unchecked")
 private static void insertionSort( final boolean[] a, final int from, final int to ) {
  for ( int i = from; ++i < to; ) {
   boolean t = a[ i ];
   int j = i;
   for ( boolean u = a[ j - 1 ]; ( !(t) && (u) ); u = a[ --j - 1 ] ) {
    a[ j ] = u;
    if ( from == j - 1 ) {
     --j;
     break;
    }
   }
   a[ j ] = t;
  }
 }
 /** Sorts the specified range of elements according to the order induced by the specified
	 * comparator using quicksort. 
	 * 
	 * <p>The sorting algorithm is a tuned quicksort adapted from Jon L. Bentley and M. Douglas
	 * McIlroy, &ldquo;Engineering a Sort Function&rdquo;, <i>Software: Practice and Experience</i>, 23(11), pages
	 * 1249&minus;1265, 1993.
	 * 
	 * @param x the array to be sorted.
	 * @param from the index of the first element (inclusive) to be sorted.
	 * @param to the index of the last element (exclusive) to be sorted.
	 * @param comp the comparator to determine the sorting order.
	 * 
	 */
 public static void quickSort( final boolean[] x, final int from, final int to, final BooleanComparator comp ) {
  final int len = to - from;
  // Selection sort on smallest arrays
  if ( len < SMALL ) {
   selectionSort( x, from, to, comp );
   return;
  }
  // Choose a partition element, v
  int m = from + len / 2; // Small arrays, middle element
  if ( len > SMALL ) {
   int l = from;
   int n = to - 1;
   if ( len > MEDIUM ) { // Big arrays, pseudomedian of 9
    int s = len / 8;
    l = med3( x, l, l + s, l + 2 * s, comp );
    m = med3( x, m - s, m, m + s, comp );
    n = med3( x, n - 2 * s, n - s, n, comp );
   }
   m = med3( x, l, m, n, comp ); // Mid-size, med of 3
  }
  final boolean v = x[ m ];
  // Establish Invariant: v* (<v)* (>v)* v*
  int a = from, b = a, c = to - 1, d = c;
  while(true) {
   int comparison;
   while ( b <= c && ( comparison = comp.compare( x[ b ], v ) ) <= 0 ) {
    if ( comparison == 0 ) swap( x, a++, b );
    b++;
   }
   while (c >= b && ( comparison = comp.compare( x[ c ], v ) ) >=0 ) {
    if ( comparison == 0 ) swap( x, c, d-- );
    c--;
   }
   if ( b > c ) break;
   swap( x, b++, c-- );
  }
  // Swap partition elements back to middle
  int s, n = to;
  s = Math.min( a - from, b - a );
  vecSwap( x, from, b - s, s );
  s = Math.min( d - c, n - d - 1 );
  vecSwap( x, b, n - s, s );
  // Recursively sort non-partition-elements
  if ( ( s = b - a ) > 1 ) quickSort( x, from, from + s, comp );
  if ( ( s = d - c ) > 1 ) quickSort( x, n - s, n, comp );
 }
 /** Sorts an array according to the order induced by the specified
	 * comparator using quicksort. 
	 * 
	 * <p>The sorting algorithm is a tuned quicksort adapted from Jon L. Bentley and M. Douglas
	 * McIlroy, &ldquo;Engineering a Sort Function&rdquo;, <i>Software: Practice and Experience</i>, 23(11), pages
	 * 1249&minus;1265, 1993.
	 * 
	 * @param x the array to be sorted.
	 * @param comp the comparator to determine the sorting order.
	 * 
	 */
 public static void quickSort( final boolean[] x, final BooleanComparator comp ) {
  quickSort( x, 0, x.length, comp );
 }
 @SuppressWarnings("unchecked")
 private static int med3( final boolean x[], final int a, final int b, final int c ) {
  int ab = ( !(x[ a ]) && (x[ b ]) ? -1 : ( (x[ a ]) == (x[ b ]) ? 0 : 1 ) );
  int ac = ( !(x[ a ]) && (x[ c ]) ? -1 : ( (x[ a ]) == (x[ c ]) ? 0 : 1 ) );
  int bc = ( !(x[ b ]) && (x[ c ]) ? -1 : ( (x[ b ]) == (x[ c ]) ? 0 : 1 ) );
  return ( ab < 0 ?
   ( bc < 0 ? b : ac < 0 ? c : a ) :
   ( bc > 0 ? b : ac > 0 ? c : a ) );
 }
 /** Sorts the specified range of elements according to the natural ascending order using quicksort.
	 * 
	 * <p>The sorting algorithm is a tuned quicksort adapted from Jon L. Bentley and M. Douglas
	 * McIlroy, &ldquo;Engineering a Sort Function&rdquo;, <i>Software: Practice and Experience</i>, 23(11), pages
	 * 1249&minus;1265, 1993.
	 * 
	 * @param x the array to be sorted.
	 * @param from the index of the first element (inclusive) to be sorted.
	 * @param to the index of the last element (exclusive) to be sorted.
	 * 
	 */
 @SuppressWarnings("unchecked")
 public static void quickSort( final boolean[] x, final int from, final int to ) {
  final int len = to - from;
  // Selection sort on smallest arrays
  if ( len < SMALL ) {
   selectionSort( x, from, to );
   return;
  }
  // Choose a partition element, v
  int m = from + len / 2; // Small arrays, middle element
  if ( len > SMALL ) {
   int l = from;
   int n = to - 1;
   if ( len > MEDIUM ) { // Big arrays, pseudomedian of 9
    int s = len / 8;
    l = med3( x, l, l + s, l + 2 * s );
    m = med3( x, m - s, m, m + s );
    n = med3( x, n - 2 * s, n - s, n );
   }
   m = med3( x, l, m, n ); // Mid-size, med of 3
  }
  final boolean v = x[ m ];
  // Establish Invariant: v* (<v)* (>v)* v*
  int a = from, b = a, c = to - 1, d = c;
  while(true) {
   int comparison;
   while ( b <= c && ( comparison = ( !(x[ b ]) && (v) ? -1 : ( (x[ b ]) == (v) ? 0 : 1 ) ) ) <= 0 ) {
    if ( comparison == 0 ) swap( x, a++, b );
    b++;
   }
   while (c >= b && ( comparison = ( !(x[ c ]) && (v) ? -1 : ( (x[ c ]) == (v) ? 0 : 1 ) ) ) >=0 ) {
    if ( comparison == 0 ) swap( x, c, d-- );
    c--;
   }
   if ( b > c ) break;
   swap( x, b++, c-- );
  }
  // Swap partition elements back to middle
  int s, n = to;
  s = Math.min( a - from, b - a );
  vecSwap( x, from, b - s, s );
  s = Math.min( d - c, n - d - 1 );
  vecSwap( x, b, n - s, s );
  // Recursively sort non-partition-elements
  if ( ( s = b - a ) > 1 ) quickSort( x, from, from + s );
  if ( ( s = d - c ) > 1 ) quickSort( x, n - s, n );
 }
 /** Sorts an array according to the natural ascending order using quicksort.
	 * 
	 * <p>The sorting algorithm is a tuned quicksort adapted from Jon L. Bentley and M. Douglas
	 * McIlroy, &ldquo;Engineering a Sort Function&rdquo;, <i>Software: Practice and Experience</i>, 23(11), pages
	 * 1249&minus;1265, 1993.
	 * 
	 * @param x the array to be sorted.
	 * 
	 */
 public static void quickSort( final boolean[] x ) {
  quickSort( x, 0, x.length );
 }
 /** Sorts the specified range of elements according to the natural ascending order using mergesort, using a given support array.
	 * 
	 * <p>This sort is guaranteed to be <i>stable</i>: equal elements will not be reordered as a result
	 * of the sort. Moreover, no support arrays will be allocated. 
	 
	 * @param a the array to be sorted.
	 * @param from the index of the first element (inclusive) to be sorted.
	 * @param to the index of the last element (exclusive) to be sorted.
	 * @param supp a support array, at least as large as <code>a</code>.
	 */
 @SuppressWarnings("unchecked")
 public static void mergeSort( final boolean a[], final int from, final int to, final boolean supp[] ) {
  int len = to - from;
  // Insertion sort on smallest arrays
  if ( len < SMALL ) {
   insertionSort( a, from, to );
   return;
  }
  // Recursively sort halves of a into supp
  final int mid = ( from + to ) >>> 1;
  mergeSort( supp, from, mid, a );
  mergeSort( supp, mid, to, a );
  // If list is already sorted, just copy from supp to a.  This is an
  // optimization that results in faster sorts for nearly ordered lists.
  if ( ( !(supp[ mid - 1 ]) || (supp[ mid ]) ) ) {
   System.arraycopy( supp, from, a, from, len );
   return;
  }
  // Merge sorted halves (now in supp) into a
  for( int i = from, p = from, q = mid; i < to; i++ ) {
   if ( q >= to || p < mid && ( !(supp[ p ]) || (supp[ q ]) ) ) a[ i ] = supp[ p++ ];
   else a[ i ] = supp[ q++ ];
  }
 }
 /** Sorts the specified range of elements according to the natural ascending order using mergesort.
	 * 
	 * <p>This sort is guaranteed to be <i>stable</i>: equal elements will not be reordered as a result
	 * of the sort. An array as large as <code>a</code> will be allocated by this method.
	 
	 * @param a the array to be sorted.
	 * @param from the index of the first element (inclusive) to be sorted.
	 * @param to the index of the last element (exclusive) to be sorted.
	 */
 public static void mergeSort( final boolean a[], final int from, final int to ) {
  mergeSort( a, from, to, a.clone() );
 }
 /**	Sorts an array according to the natural ascending order using mergesort.
	 * 
	 * <p>This sort is guaranteed to be <i>stable</i>: equal elements will not be reordered as a result
	 * of the sort. An array as large as <code>a</code> will be allocated by this method.
	 
	 * @param a the array to be sorted.
	 */
 public static void mergeSort( final boolean a[] ) {
  mergeSort( a, 0, a.length );
 }
 /** Sorts the specified range of elements according to the order induced by the specified
	 * comparator using mergesort, using a given support array.
	 * 
	 * <p>This sort is guaranteed to be <i>stable</i>: equal elements will not be reordered as a result
	 * of the sort. Moreover, no support arrays will be allocated.
	 
	 * @param a the array to be sorted.
	 * @param from the index of the first element (inclusive) to be sorted.
	 * @param to the index of the last element (exclusive) to be sorted.
	 * @param comp the comparator to determine the sorting order.
	 * @param supp a support array, at least as large as <code>a</code>.
	 */
 @SuppressWarnings("unchecked")
 public static void mergeSort( final boolean a[], final int from, final int to, BooleanComparator comp, final boolean supp[] ) {
  int len = to - from;
  // Insertion sort on smallest arrays
  if ( len < SMALL ) {
   insertionSort( a, from, to, comp );
   return;
     }
  // Recursively sort halves of a into supp
  final int mid = ( from + to ) >>> 1;
  mergeSort( supp, from, mid, comp, a );
  mergeSort( supp, mid, to, comp, a );
  // If list is already sorted, just copy from supp to a.  This is an
  // optimization that results in faster sorts for nearly ordered lists.
  if ( comp.compare( supp[ mid - 1 ], supp[ mid ] ) <= 0 ) {
   System.arraycopy( supp, from, a, from, len );
   return;
  }
  // Merge sorted halves (now in supp) into a
  for( int i = from, p = from, q = mid; i < to; i++ ) {
   if ( q >= to || p < mid && comp.compare( supp[ p ], supp[ q ] ) <= 0 ) a[ i ] = supp[ p++ ];
   else a[ i ] = supp[ q++ ];
  }
 }
 /** Sorts the specified range of elements according to the order induced by the specified
	 * comparator using mergesort.
	 * 
	 * <p>This sort is guaranteed to be <i>stable</i>: equal elements will not be reordered as a result
	 * of the sort. An array as large as <code>a</code> will be allocated by this method.
	 *
	 * @param a the array to be sorted.
	 * @param from the index of the first element (inclusive) to be sorted.
	 * @param to the index of the last element (exclusive) to be sorted.
	 * @param comp the comparator to determine the sorting order.
	 */
 public static void mergeSort( final boolean a[], final int from, final int to, BooleanComparator comp ) {
  mergeSort( a, from, to, comp, a.clone() );
 }
 /** Sorts an array according to the order induced by the specified
	 * comparator using mergesort.
	 * 
	 * <p>This sort is guaranteed to be <i>stable</i>: equal elements will not be reordered as a result
	 * of the sort.  An array as large as <code>a</code> will be allocated by this method.
	 
	 * @param a the array to be sorted.
	 * @param comp the comparator to determine the sorting order.
	 */
 public static void mergeSort( final boolean a[], BooleanComparator comp ) {
  mergeSort( a, 0, a.length, comp );
 }
 /** Shuffles the specified array fragment using the specified pseudorandom number generator.
	 * 
	 * @param a the array to be shuffled.
	 * @param from the index of the first element (inclusive) to be shuffled.
	 * @param to the index of the last element (exclusive) to be shuffled.
	 * @param random a pseudorandom number generator (please use a <a href="http://dsiutils.dsi.unimi.it/docs/it/unimi/dsi/util/XorShiftStarRandom.html">XorShift*</a> generator).
	 * @return <code>a</code>.
	 */
 public static boolean[] shuffle( final boolean[] a, final int from, final int to, final Random random ) {
  for( int i = to - from; i-- != 0; ) {
   final int p = random.nextInt( i + 1 );
   final boolean t = a[ from + i ];
   a[ from + i ] = a[ from + p ];
   a[ from + p ] = t;
  }
  return a;
 }
 /** Shuffles the specified array using the specified pseudorandom number generator.
	 * 
	 * @param a the array to be shuffled.
	 * @param random a pseudorandom number generator (please use a <a href="http://dsiutils.dsi.unimi.it/docs/it/unimi/dsi/util/XorShiftStarRandom.html">XorShift*</a> generator).
	 * @return <code>a</code>.
	 */
 public static boolean[] shuffle( final boolean[] a, final Random random ) {
  for( int i = a.length; i-- != 0; ) {
   final int p = random.nextInt( i + 1 );
   final boolean t = a[ i ];
   a[ i ] = a[ p ];
   a[ p ] = t;
  }
  return a;
 }
 /** A type-specific content-based hash strategy for arrays. */
 private static final class ArrayHashStrategy implements Hash.Strategy<boolean[]>, java.io.Serializable {
  public static final long serialVersionUID = -7046029254386353129L;
  public int hashCode( final boolean[] o ) {
   return java.util.Arrays.hashCode( o );
  }
  public boolean equals( final boolean[] a, final boolean[] b ) {
   return BooleanArrays.equals( a, b );
  }
 }
 /** A type-specific content-based hash strategy for arrays.
	 *
	 * <P>This hash strategy may be used in custom hash collections whenever keys are
	 * arrays, and they must be considered equal by content. This strategy
	 * will handle <code>null</code> correctly, and it is serializable.
	 */
 @SuppressWarnings({"unchecked", "rawtypes"})
 public final static Hash.Strategy HASH_STRATEGY = new ArrayHashStrategy();
}
