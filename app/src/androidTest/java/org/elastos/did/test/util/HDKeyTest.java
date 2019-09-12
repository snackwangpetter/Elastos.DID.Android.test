/*
 * Copyright (c) 2019 Elastos Foundation
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

package org.elastos.did.test.util;

import org.elastos.did.util.HDKey;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class HDKeyTest {
	/*
	private static void dumpHex(byte[] bytes) {
		for (byte b : bytes)
			System.out.print(String.format("%02x", b));
		System.out.println();
	}
	*/

	// Test HD key algorithm, keep compatible with SPV.

	@Test
	public void test0() {
		String expectedIDString = "iY4Ghz9tCuWvB5rNwvn4ngWvthZMNzEA7U";
		String mnemonic = "cloth always junk crash fun exist stumble shift over benefit fun toe";

		HDKey root = HDKey.fromMnemonic(mnemonic, "");
		HDKey.DerivedKey key = root.derive(0);

		assertEquals(expectedIDString, key.getAddress());
	}


	@Test
	public void testAbc() {
		String expectedIDString = "iW3HU8fTmwkENeVT9UCEvvg3ddUD5oCxYA";
		String mnemonic = "service illegal blossom voice three eagle grace agent service average knock round";

		HDKey root = HDKey.fromMnemonic(mnemonic, "");
		HDKey.DerivedKey key = root.derive(0);

		assertEquals(expectedIDString, key.getAddress());
	}

}
