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

import org.elastos.did.util.Aes256cbc;
import org.elastos.did.util.Base64;
import org.junit.Test;

import java.security.GeneralSecurityException;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class Aes256cbcTest {
	private static final String passwd = "secret";
	private static final String plain = "The quick brown fox jumps over the lazy dog.";
	private static final String cipherBase64 = "TBimuq42IyD6FsoZK0AoCOt75uiL/gEepZTpgu59RYSV+NR+fqxsYfx0cyyzGacX";

	@Test
	public void testEncrypt() throws GeneralSecurityException {
		byte[] cipher = Aes256cbc.encrypt(passwd, plain.getBytes());
		byte[] expected = Base64.decode(cipherBase64);

		assertArrayEquals(expected, cipher);
	}

	@Test
	public void testDecrypt() throws GeneralSecurityException {
		byte[] cipher = Base64.decode(cipherBase64);
		byte[] plainBytes = Aes256cbc.decrypt(passwd, cipher);
		byte[] expected = plain.getBytes();

		assertArrayEquals(expected, plainBytes);
	}

	@Test
	public void testEncryptToBase64() throws GeneralSecurityException {
		String cipher = Aes256cbc.encryptToBase64(passwd, plain.getBytes());

		assertEquals(cipherBase64, cipher);
	}

	@Test
	public void testDecryptFromBase64() throws GeneralSecurityException {
		byte[] plainBytes = Aes256cbc.decryptFromBase64(passwd, cipherBase64);
		byte[] expected = plain.getBytes();

		assertArrayEquals(expected, plainBytes);
	}
}
