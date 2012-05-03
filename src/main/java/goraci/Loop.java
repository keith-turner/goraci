/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package goraci;

import java.util.Arrays;
import java.util.UUID;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

/** 
 * Executes Generate and Verify in a loop. Data is not cleaned between runs, so each iteration
 * adds more data.
 */
public class Loop extends Configured implements Tool {

  private static final Log LOG = LogFactory.getLog(Loop.class); 
  
  protected void runGenerator(int numMappers, long numNodes) throws Exception {
    Generator generator = new Generator();
    generator.setConf(getConf());
    int retCode = generator.run(numMappers, numNodes);
    
    if (retCode > 0) {
      throw new RuntimeException("Generator failed with return code: " + retCode);
    }
  }
  
  protected void runVerify(String outputDir, int numReducers, long expectedNumNodes) throws Exception {
    Path outputPath = new Path(outputDir);
    UUID uuid = UUID.randomUUID(); //create a random UUID.
    Path iterationOutput = new Path(outputPath, uuid.toString());
    
    Verify verify = new Verify();
    verify.setConf(getConf());
    int retCode = verify.run(iterationOutput, numReducers);
    if (retCode > 0) {
      throw new RuntimeException("Verify.run failed with return code: " + retCode);
    }
    
    boolean verifySuccess = verify.verify(expectedNumNodes);
    if (!verifySuccess) {
      throw new RuntimeException("Verify.verify failed");
    }
  }

  @Override
  public int run(String[] args) throws Exception {
    if (args.length < 5) {
      System.err.println("Usage: Loop <num iterations> <num mappers> <num nodes per mapper> <output dir> <num reducers>");
      return 1;
    }
    
    LOG.info("Running Loop with args:" + Arrays.deepToString(args));
    
    int numIterations = Integer.parseInt(args[0]);
    int numMappers = Integer.parseInt(args[1]);
    long numNodes = Long.parseLong(args[2]);
    String outputDir = args[3];
    int numReducers = Integer.parseInt(args[4]);
    
    long expectedNumNodes = 0;
    
    if (numIterations < 0) {
      numIterations = Integer.MAX_VALUE; //run indefinitely (kind of)
    }
    
    for (int i=0; i < numIterations; i++) {
      LOG.info("Starting iteration = " + i);
      runGenerator(numMappers, numNodes);
      expectedNumNodes += numMappers * numNodes;
      
      runVerify(outputDir, numReducers, expectedNumNodes);
    }
    
    return 0;
  }
  
  public static void main(String[] args) throws Exception {
    int ret = ToolRunner.run(new Loop(), args);
    System.exit(ret);
  }
  
}
