package org.elastos.did.test;

import android.content.Context;
import android.support.test.InstrumentationRegistry;

import org.elastos.did.DIDDocument;
import org.elastos.did.DIDException;
import org.elastos.did.PublicKey;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

public class DIDDocumentTest{
	Context context;
	@Before
	public void init(){
		context = InstrumentationRegistry.getTargetContext();
	}

	@Test
	public void testParseDocument() throws DIDException, IOException {
		InputStream inputStream = context.getAssets().open("testdiddoc.json");
		Reader input = new InputStreamReader(inputStream);
		DIDDocument doc = DIDDocument.fromJson(input);

		assertEquals(4, doc.getPublicKeyCount());

		List<PublicKey> pks = doc.getPublicKeys();
		for (PublicKey pk : pks) {
			assertTrue(pk.getId().getFragment().equals("default")
					|| pk.getId().getFragment().equals("key2")
					|| pk.getId().getFragment().equals("keys3")
					|| pk.getId().getFragment().equals("recovery"));

			if (pk.getId().getFragment().equals("recovery"))
				assertNotEquals(doc.getSubject(), pk.getController());
			else
				assertEquals(doc.getSubject(), pk.getController());
		}

		assertEquals(3, doc.getAuthenticationKeyCount());
		assertEquals(1, doc.getAuthorizationKeyCount());


		assertEquals(2, doc.getCredentialCount());

		assertEquals(3, doc.getServiceCount());

		input.close();
		inputStream.close();
	}

	@Test
	public void testCompactJson() throws DIDException, IOException {
		InputStream inputStream = context.getAssets().open("testdiddoc.json") ;
		Reader input = new InputStreamReader(inputStream);
		DIDDocument doc = DIDDocument.fromJson(input);
		input.close();
		inputStream.close();

		String json = doc.toExternalForm(true);

		String cachePath = getAssetsCacheFile(context,"compact.json");
		File file = new File(cachePath);
		char[] chars = new char[(int)file.length()];
		input = new InputStreamReader(new FileInputStream(file));
		input.read(chars);
		input.close();

		String expected = new String(chars);

		assertEquals(expected, json);
		deleteFile(cachePath);
	}

	@Test
	public void testNormalizedJson() throws DIDException, IOException {
		InputStream inputStream = context.getAssets().open("testdiddoc.json") ;
		Reader input = new InputStreamReader(inputStream);
		DIDDocument doc = DIDDocument.fromJson(input);
		input.close();
		inputStream.close();
		String json = doc.toExternalForm(false);

		String cachePath = getAssetsCacheFile(context,"normalized.json");
		File file = new File(cachePath);
		char[] chars = new char[(int)file.length()];
		input = new InputStreamReader(new FileInputStream(file));
		input.read(chars);
		input.close();

		String expected = new String(chars);

		assertEquals(expected, json);
		deleteFile(cachePath);
	}


	private String getAssetsCacheFile(Context context,String fileName)   {
		File cacheFile = new File(context.getCacheDir(), fileName);
		try {
			InputStream inputStream = context.getAssets().open(fileName);
			try {
				FileOutputStream outputStream = new FileOutputStream(cacheFile);
				try {
					byte[] buf = new byte[1024];
					int len;
					while ((len = inputStream.read(buf)) > 0) {
						outputStream.write(buf, 0, len);
					}
				} finally {
					outputStream.close();
				}
			} finally {
				inputStream.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return cacheFile.getAbsolutePath();
	}

	private void deleteFile(String filePath){
		if (null!=filePath && !filePath.equals("")){
			File file = new File(filePath);
			if (file.exists()){
				file.delete();
			}
		}
	}
}
