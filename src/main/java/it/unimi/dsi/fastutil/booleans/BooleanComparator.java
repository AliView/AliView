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
 */
package it.unimi.dsi.fastutil.booleans;
import java.util.Comparator;
/** A type-specific {@link Comparator}; provides methods to compare two primitive types both as objects
 * and as primitive types. 
 *
 * <P>Note that <code>fastutil</code> provides a corresponding abstract class that
 * can be used to implement this interface just by specifying the type-specific
 * comparator.
 *
 * @see Comparator
 */
public interface BooleanComparator extends Comparator<Boolean> {
 /** Compares the given primitive types.
	 *
	 * @see java.util.Comparator
	 * @return A positive integer, zero, or a negative integer if the first
	 * argument is greater than, equal to, or smaller than, respectively, the
	 * second one.
	 */
 public int compare( boolean k1, boolean k2 );
}
