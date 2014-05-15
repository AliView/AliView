package it.unimi.dsi.fastutil;

/*		 
 * Copyright (C) 2010-2011 Sebastiano Vigna 
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

/** An object that can swap elements whose position is specified by integers 
 *
 * @see Arrays#quickSort(int, int, it.unimi.dsi.fastutil.ints.IntComparator, Swapper)
 */

public interface Swapper {
	/** Swaps the data at the given positions.
	 * 
	 * @param a the first position to swap.
	 * @param b the second position to swap.
	 */
	void swap( int a, int b );
}
