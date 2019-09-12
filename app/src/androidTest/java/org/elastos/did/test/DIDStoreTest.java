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

package org.elastos.did.test;

import android.content.Context;
import android.support.test.InstrumentationRegistry;

import org.elastos.credential.Issuer;
import org.elastos.credential.VerifiableCredential;
import org.elastos.did.DID;
import org.elastos.did.DIDDocument;
import org.elastos.did.DIDException;
import org.elastos.did.DIDStore;
import org.elastos.did.DIDStore.Entry;
import org.elastos.did.DIDStoreException;
import org.elastos.did.DIDURL;
import org.elastos.did.MalformedDocumentException;
import org.elastos.did.backend.DIDBackend;
import org.elastos.did.util.Mnemonic;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class DIDStoreTest {
	private String testStoreName = "TestDIDStore";
	private String storeRootName = "DIDStore";
	private static String passphrase = "secret";
	private static DIDStore store;

	private static LinkedHashMap<DID, String> ids;

	private static DID primaryDid;
	private Context context ;
	private File tempStoreRoot , storeRoot;

	@BeforeClass
	public static void setup() {
		DIDBackend.initialize(new FakeConsoleAdaptor());
	}

	@Before
	public void init(){
		context = InstrumentationRegistry.getInstrumentation().getTargetContext();
		tempStoreRoot = new File(context.getCacheDir(),testStoreName);
		storeRoot = new File(context.getCacheDir(),storeRootName);
	}

	@Test
	public void test00CreateEmptyStore0() throws DIDStoreException {
    	deleteFile(tempStoreRoot);

    	DIDStore.initialize("filesystem", tempStoreRoot.getAbsolutePath(), passphrase);

    	DIDStore tempStore = DIDStore.getInstance();

    	assertFalse(tempStore.hasPrivateIdentity());

    	File file = tempStoreRoot;
    	assertTrue(file.exists());
    	assertTrue(file.isDirectory());

    	file = new File(tempStoreRoot + File.separator + ".DIDStore");
    	assertTrue(file.exists());
    	assertTrue(file.isFile());

    	assertFalse(tempStore.hasPrivateIdentity());
	}

	@Test(expected = DIDStoreException.class)
	public void test00CreateEmptyStore1() throws DIDStoreException {
    	DIDStore.initialize("filesystem", tempStoreRoot.getAbsolutePath(), passphrase);

    	DIDStore tempStore = DIDStore.getInstance();

    	tempStore.newDid(passphrase, "my first did");
	}

	@Test
	public void test01InitPrivateIdentity0() throws DIDStoreException {
    	deleteFile(tempStoreRoot);

    	DIDStore.initialize("filesystem", tempStoreRoot.getAbsolutePath(), passphrase);

    	DIDStore tempStore = DIDStore.getInstance();

    	assertFalse(tempStore.hasPrivateIdentity());

    	String mnemonic = Mnemonic.generate(Mnemonic.ENGLISH);
    	tempStore.initPrivateIdentity(mnemonic, passphrase, true);

    	File file = new File(tempStoreRoot + File.separator + "private"
    			+ File.separator + "key");
    	assertTrue(file.exists());
    	assertTrue(file.isFile());

    	file = new File(tempStoreRoot + File.separator + "private"
    			+ File.separator + "index");
    	assertTrue(file.exists());
    	assertTrue(file.isFile());

    	assertTrue(tempStore.hasPrivateIdentity());

    	DIDStore.initialize("filesystem", tempStoreRoot.getAbsolutePath(), passphrase);

    	tempStore = DIDStore.getInstance();

    	assertTrue(tempStore.hasPrivateIdentity());
	}

	// Can not decrypt root private key because wrong passphrase
	@Test(expected = DIDStoreException.class)
	public void test01InitPrivateIdentity1() throws DIDStoreException {
		DIDStore.initialize("filesystem", tempStoreRoot.getAbsolutePath(), "password");

    	DIDStore tempStore = DIDStore.getInstance();

    	assertTrue(tempStore.hasPrivateIdentity());
	}

    @Test
    public void test02Setup() throws DIDStoreException {
    	deleteFile(storeRoot);

    	DIDStore.initialize("filesystem", storeRoot.getAbsolutePath(), passphrase);


    	store = DIDStore.getInstance();

    	String mnemonic = Mnemonic.generate(Mnemonic.ENGLISH);
    	store.initPrivateIdentity(mnemonic, passphrase, true);

    	ids = new LinkedHashMap<DID, String>(128);
    }

	@Test
	public void test03CreateDID1() throws DIDStoreException {
		String hint = "my first did";

    	DIDDocument doc = store.newDid(passphrase, hint);
    	primaryDid = doc.getSubject();

    	File file = new File(storeRoot + File.separator + "ids"
    			+ File.separator + doc.getSubject().getMethodSpecificId()
    			+ File.separator + "document");
    	assertTrue(file.exists());
    	assertTrue(file.isFile());

    	file = new File(storeRoot + File.separator + "ids"
    			+ File.separator + "."
    			+ doc.getSubject().getMethodSpecificId() + ".meta");
    	assertTrue(file.exists());
    	assertTrue(file.isFile());

    	ids.put(doc.getSubject(), hint);
	}

	@Test
	public void test03CreateDID2() throws DIDStoreException {
    	DIDDocument doc = store.newDid(passphrase, null);

    	File file = new File(storeRoot + File.separator + "ids"
    			+ File.separator + doc.getSubject().getMethodSpecificId()
    			+ File.separator + "document");
    	assertTrue(file.exists());
    	assertTrue(file.isFile());

    	file = new File(storeRoot + File.separator + "ids"
    			+ File.separator + "."
    			+ doc.getSubject().getMethodSpecificId() + ".meta");
    	assertFalse(file.exists());

    	ids.put(doc.getSubject(), null);
	}

	@Test
	public void test03CreateDID3() throws DIDStoreException {
    	for (int i = 0; i < 100; i++) {
    		String hint = "my did " + i;
    		DIDDocument doc = store.newDid(passphrase, hint);

	    	File file = new File(storeRoot + File.separator + "ids"
	    			+ File.separator + doc.getSubject().getMethodSpecificId()
	    			+ File.separator + "document");
	    	assertTrue(file.exists());
	    	assertTrue(file.isFile());

	    	file = new File(storeRoot + File.separator + "ids"
	    			+ File.separator + "."
	    			+ doc.getSubject().getMethodSpecificId() + ".meta");
	    	assertTrue(file.exists());
	    	assertTrue(file.isFile());

	    	ids.put(doc.getSubject(), hint);
    	}
	}

	@Test
	public void test04DeleteDID1() throws DIDStoreException {
		Iterator<DID> dids = ids.keySet().iterator();
		int i = 0;

		while (dids.hasNext()) {
			DID did = dids.next();

			if (++i % 9 != 0 || did.equals(primaryDid))
				continue;

    		boolean deleted = store.deleteDid(did);
    		assertTrue(deleted);

	    	File file = new File(storeRoot + File.separator + "ids"
	    			+ File.separator + did.getMethodSpecificId());
	    	assertFalse(file.exists());

	    	file = new File(storeRoot + File.separator + "ids"
	    			+ File.separator + "."
	    			+ did.getMethodSpecificId() + ".meta");
	    	assertFalse(file.exists());

    		deleted = store.deleteDid(did);
    		assertFalse(deleted);

	    	dids.remove();
    	}
	}

	@Test
	public void test04PublishDID() throws DIDStoreException, MalformedDocumentException {
		Iterator<DID> dids = ids.keySet().iterator();
		int i = 0;

		while (dids.hasNext()) {
			DID did = dids.next();

			if (++i % 9 != 0)
				continue;

			DIDDocument doc = store.loadDid(did);
    		store.publishDid(doc, new DIDURL(did, "primary"), passphrase);
    	}
	}

	@Test
	public void test05IssueSelfClaimCredential1() throws DIDException {
		Issuer issuer = new Issuer(primaryDid);

		Map<String, String> props = new HashMap<String, String>();
		props.put("name", "Elastos");
		props.put("email", "contact@elastos.org");
		props.put("website", "https://www.elastos.org/");
		props.put("phone", "12345678900");

		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, cal.get(Calendar.YEAR) + 1);
		Date expire = cal.getTime();
		VerifiableCredential vc = issuer.issueFor(primaryDid)
				.id("cred-1")
				.type(new String[] {"SelfProclaimedCredential", "BasicProfileCredential" })
				.expirationDate(expire)
				.properties(props)
				.sign(passphrase);

		DIDDocument doc = store.resolveDid(primaryDid);
		doc.modify();
		doc.addCredential(vc);
		store.storeDid(doc);

		doc = store.resolveDid(primaryDid);
		DIDURL vcId = new DIDURL(primaryDid, "cred-1");
		vc = doc.getCredential(vcId);
		assertNotNull(vc);
		assertEquals(vcId, vc.getId());
		assertEquals(primaryDid, vc.getSubject().getId());
	}

	@Test
	public void test05IssueSelfClaimCredential2() throws DIDException {
		DID issuerDid = primaryDid;
		Issuer issuer = new Issuer(issuerDid);

		for (DID did : ids.keySet()) {
			Map<String, String> props = new HashMap<String, String>();
			props.put("name", "Elastos-" + did.getMethodSpecificId());
			props.put("email", "contact@elastos.org");
			props.put("website", "https://www.elastos.org/");
			props.put("phone", did.getMethodSpecificId());

			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.YEAR, cal.get(Calendar.YEAR) + 1);
			Date expire = cal.getTime();
			VerifiableCredential vc = issuer.issueFor(did)
					.id("cred-1")
					.type(new String[] { "BasicProfileCredential" })
					.expirationDate(expire)
					.properties(props)
					.sign(passphrase);

			store.storeCredential(vc, "default");

			props.clear();
			props.put("name", "CyberRepublic-" + did.getMethodSpecificId());
			props.put("email", "contact@CyberRepublic.org");
			props.put("website", "https://www.CyberRepublic.org/");
			props.put("phone", did.getMethodSpecificId());

			vc = issuer.issueFor(did)
				.id("cred-2")
				.type(new String[] { "BasicProfileCredential" })
				.expirationDate(expire)
				.properties(props)
				.sign(passphrase);

			store.storeCredential(vc);

	    	File file = new File(storeRoot + File.separator + "ids"
	    			+ File.separator + did.getMethodSpecificId() + File.separator
	    			+ "credentials" + File.separator + "cred-1");
	    	assertTrue(file.exists());
	    	assertTrue(file.isFile());
	    	assertTrue(file.length() > 0);

	    	file = new File(storeRoot + File.separator + "ids"
	    			+ File.separator + did.getMethodSpecificId() + File.separator
	    			+ "credentials" + File.separator + ".cred-1.meta");
	    	assertTrue(file.exists());
	    	assertTrue(file.isFile());
	    	assertTrue(file.length() > 0);

	    	file = new File(storeRoot + File.separator + "ids"
	    			+ File.separator + did.getMethodSpecificId() + File.separator
	    			+ "credentials" + File.separator + "cred-2");
	    	assertTrue(file.exists());
	    	assertTrue(file.isFile());
	    	assertTrue(file.length() > 0);

	    	file = new File(storeRoot + File.separator + "ids"
	    			+ File.separator + did.getMethodSpecificId() + File.separator
	    			+ "credentials" + File.separator + ".cred-2.meta");
	    	assertFalse(file.exists());
		}
	}

	@Test
	public void test06DeleteCredential1() throws DIDException {
		boolean deleted = store.deleteCredential(primaryDid, new DIDURL(primaryDid, "cred-1"));
		assertTrue(deleted);

		deleted = store.deleteCredential(primaryDid, new DIDURL(primaryDid, "cred-2"));
		assertTrue(deleted);

		deleted = store.deleteCredential(primaryDid, new DIDURL(primaryDid, "cred-3"));
		assertFalse(deleted);

    	File file = new File(storeRoot + File.separator + "ids"
    			+ File.separator + primaryDid.getMethodSpecificId() + File.separator
    			+ "credentials" + File.separator + "cred-1");
    	assertFalse(file.exists());

    	file = new File(storeRoot + File.separator + "ids"
    			+ File.separator + primaryDid.getMethodSpecificId() + File.separator
    			+ "credentials" + File.separator + ".cred-1.meta");
    	assertFalse(file.exists());

    	file = new File(storeRoot + File.separator + "ids"
    			+ File.separator + primaryDid.getMethodSpecificId() + File.separator
    			+ "credentials" + File.separator + "cred-2");
    	assertFalse(file.exists());

    	file = new File(storeRoot + File.separator + "ids"
    			+ File.separator + primaryDid.getMethodSpecificId() + File.separator
    			+ "credentials" + File.separator + ".cred-2.meta");
    	assertFalse(file.exists());

	}

	@Test
	public void test06ListCredential1() throws DIDException {
		for (DID did : ids.keySet()) {
			List<Entry<DIDURL, String>> creds = store.listCredentials(did);

			if (did.equals(primaryDid))
				assertEquals(0, creds.size());
			else
				assertEquals(2, creds.size());

			for (Entry<DIDURL, String> cred : creds) {
				if (cred.getKey().getFragment().equals("cred-1"))
					assertEquals("default", cred.getValue());
				else if (cred.getKey().getFragment().equals("cred-2"))
					assertNull(cred.getValue());
				else
					fail("Unexpected credential id '" + cred.getKey() + "'.");
			}
		}
	}

	@Test
	public void test07LoadCredential1() throws DIDException {
		for (DID did : ids.keySet()) {
			if (did.equals(primaryDid))
				continue;

			DIDURL id1 = new DIDURL(did, "cred-1");
			VerifiableCredential vc1 = store.loadCredential(did, id1);
			assertNotNull(vc1);

			DIDURL id2 = new DIDURL(did, "cred-2");
			VerifiableCredential vc2 = store.loadCredential(did, id2);
			assertNotNull(vc2);

			assertEquals(id1, vc1.getId());
			assertEquals(primaryDid, vc1.getIssuer());
			assertEquals(did, vc1.getSubject().getId());
			assertEquals("Elastos-" + did.getMethodSpecificId(), vc1.getSubject().getProperty("name"));

			assertEquals(id2, vc2.getId());
			assertEquals(primaryDid, vc2.getIssuer());
			assertEquals(did, vc2.getSubject().getId());
			assertEquals("CyberRepublic-" + did.getMethodSpecificId(), vc2.getSubject().getProperty("name"));
		}
	}

    @Test
    public void test99Check() throws DIDStoreException {
    	List<Entry<DID, String>> dids = store.listDids(DIDStore.DID_ALL);

    	assertEquals(ids.size(), dids.size());

    	for (Entry<DID, String> entry : dids) {
    		assertTrue(ids.containsKey(entry.getKey()));
    		assertEquals(ids.get(entry.getKey()), entry.getValue());
    	}
    }

	private static void deleteFile(File file) {
		if (file.isDirectory()) {
			File[] children = file.listFiles();
			for (File child : children)
				deleteFile(child);
		}

		file.delete();
	}
}
