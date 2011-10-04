/*
 * Copyright 2011 VMware, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.nodex.tests.core;

import org.nodex.java.core.Nodex;
import org.nodex.java.core.http.HttpServer;
import org.nodex.java.core.logging.Logger;
import org.nodex.java.core.net.NetServer;
import org.nodex.tests.AwaitDone;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author <a href="http://tfox.org">Tim Fox</a>
 */
public class TestBase {

  private static final Logger log = Logger.getLogger(TestBase.class);

  @BeforeClass
  public void setUp() {
    log.debug("*** STARTING TEST " + this.getClass().getName());
  }

  @AfterClass
  public void tearDown() {
    log.debug("*** STOPPING TEST " + this.getClass().getName());
  }


  /*
  We need to gather assertions and throw them at the endHandler, this is because testng lets a test pass if an assertion
  is thrown from a different thread to that which ran the test
   */
  private static Queue<AssertionError> errors = new ConcurrentLinkedQueue<AssertionError>();

  public static void azzert(boolean exprValue) {
    azzert(exprValue, null);
  }

  public static void azzert(boolean exprValue, String msg) {
    if (!exprValue) {
      AssertionError err = new AssertionError(msg);
      err.fillInStackTrace();
      errors.add(err);
      throw err;
    }
  }

  protected void throwAssertions() {
    AssertionError err;
    while ((err = errors.poll()) != null) {
      throw err;
    }
  }

  protected void awaitClose(NetServer server) {
    AwaitDone await = new AwaitDone();
    server.close(await);
    azzert(await.awaitDone(5));
  }

  protected void awaitClose(HttpServer server) {
    AwaitDone await = new AwaitDone();
    server.close(await);
    azzert(await.awaitDone(5));
  }

  protected static class ContextChecker {
    final Thread th;
    final long contextID;

    public ContextChecker() {
      this.th = Thread.currentThread();
      this.contextID = Nodex.instance.getContextID();
    }

    public void check() {
      azzert(th == Thread.currentThread(), "Expected:" + th + " Actual:" + Thread.currentThread());
      azzert(contextID == Nodex.instance.getContextID(), "Expected:" + contextID + " Actual:" + Nodex.instance
          .getContextID());
    }
  }

  protected File setupFile(String fileName, String content) throws Exception {
    fileName = "./" + fileName;
    File file = new File(fileName);
    if (file.exists()) {
      file.delete();
    }
    BufferedWriter out = new BufferedWriter(new FileWriter(file));
    out.write(content);
    out.close();
    return file;
  }

}
