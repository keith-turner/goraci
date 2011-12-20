/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
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

import goraci.generated.CINode;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.apache.avro.util.Utf8;
import org.apache.gora.store.DataStore;
import org.apache.gora.store.DataStoreFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.mapreduce.InputFormat;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.JobContext;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.RecordReader;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.mapreduce.lib.output.NullOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

/**
 * 
 */
public class Generator extends Configured implements Tool {
  
  private static final int WIDTH = 1000000;
  private static final int WRAP = WIDTH * 25;

  static class GeneratorInputFormat extends InputFormat<LongWritable,NullWritable> {
    
    static class GeneratorInputSplit extends InputSplit implements Writable {
      
      @Override
      public long getLength() throws IOException, InterruptedException {
        return 1;
      }
      
      @Override
      public String[] getLocations() throws IOException, InterruptedException {
        return new String[0];
      }
      
      @Override
      public void readFields(DataInput arg0) throws IOException {
        // TODO Auto-generated method stub
        
      }
      
      @Override
      public void write(DataOutput arg0) throws IOException {
        // TODO Auto-generated method stub
        
      }
      

      
    }
    
    static class GeneratorRecordReader extends RecordReader<LongWritable,NullWritable> {
      
      private long numNodes;
      private boolean hasNext = true;
      
      @Override
      public void close() throws IOException {
        
      }
      
      @Override
      public LongWritable getCurrentKey() throws IOException, InterruptedException {
        return new LongWritable(numNodes);
      }
      
      @Override
      public NullWritable getCurrentValue() throws IOException, InterruptedException {
        return NullWritable.get();
      }
      
      @Override
      public float getProgress() throws IOException, InterruptedException {
        return 0;
      }
      
      @Override
      public void initialize(InputSplit arg0, TaskAttemptContext context) throws IOException, InterruptedException {
        numNodes = context.getConfiguration().getLong("goraci.generator.nodes", 1000000);
      }
      
      @Override
      public boolean nextKeyValue() throws IOException, InterruptedException {
        boolean hasnext = this.hasNext;
        this.hasNext = false;
        return hasnext;
      }
      
    }
    
    @Override
    public RecordReader<LongWritable,NullWritable> createRecordReader(InputSplit split, TaskAttemptContext context) throws IOException, InterruptedException {
      GeneratorRecordReader rr = new GeneratorRecordReader();
      rr.initialize(split, context);
      return rr;
    }
    
    @Override
    public List<InputSplit> getSplits(JobContext job) throws IOException, InterruptedException {
      int numMappers = job.getConfiguration().getInt("goraci.generator.mappers", 1);
      
      ArrayList<InputSplit> splits = new ArrayList<InputSplit>(numMappers);
      
      for (int i = 0; i < numMappers; i++) {
        splits.add(new GeneratorInputSplit());
      }
      
      return splits;
    }
    
  }

  static class GeneratorMapper extends Mapper<LongWritable,NullWritable,NullWritable,NullWritable> {
    
    @Override
    protected void map(LongWritable key, NullWritable value, Context output) throws IOException {
      long num = key.get();
      System.out.println("num" + num);
      
      Utf8 id = new Utf8(UUID.randomUUID().toString());
      
      DataStore<Long,CINode> store = DataStoreFactory.getDataStore(Long.class, CINode.class, new Configuration());
      
      store.createSchema();
      
      Random rand = new Random();
      
      long[] first = null;
      long[] prev = null;
      long[] current = new long[WIDTH];
      
      long count = 0;
      while (count < num) {
        for (int i = 0; i < current.length; i++)
          current[i] = Math.abs(rand.nextLong());
        
        persist(store, count, prev, current, id);
        
        if (first == null)
          first = current;
        prev = current;
        current = new long[WIDTH];
        
        count += current.length;
        output.setStatus("Count " + count);
        
        if (count % WRAP == 0) {
          // this block of code turns the 1 million linked list of length 25 into one giant circular linked list of 25 million
          
          circularLeftShift(first);
          
          updatePrev(store, first, prev);
          
          first = null;
          prev = null;
        }
        
      }
      
      store.close();
      
    }
    
    private static void circularLeftShift(long[] first) {
      long ez = first[0];
      for (int i = 0; i < first.length - 1; i++)
        first[i] = first[i + 1];
      first[first.length - 1] = ez;
    }
    
    private static void persist(DataStore<Long,CINode> store, long count, long[] prev, long[] current, Utf8 id) throws IOException {
      for (int i = 0; i < current.length; i++) {
        CINode node = store.newPersistent();
        node.setCount(count + i);
        if (prev != null)
          node.setPrev(prev[i]);
        else
          node.setPrev(-1);
        node.setClient(id);
        
        store.put(current[i], node);
      }
      
      store.flush();
    }
    
    private static void updatePrev(DataStore<Long,CINode> store, long[] first, long[] current) throws IOException {
      for (int i = 0; i < current.length; i++) {
        CINode node = store.newPersistent();
        node.setPrev(current[i]);
        store.put(first[i], node);
        System.out.printf("Set prev %016x %016x\n", first[i], current[i]);
      }
      
      store.flush();
    }
  }

  @Override
  public int run(String[] args) throws Exception {
    
    if (args.length == 0) {
      System.out.println("Usage : " + Generator.class.getSimpleName() + " <num mappers> <num nodes>");
      return 0;
    }

    int numMappers = Integer.parseInt(args[0]);
    long numNodes = Long.parseLong(args[1]);
    
    Job job = new Job(getConf());
    
    job.setJobName("Link Generator");
    job.setNumReduceTasks(0);
    job.setJarByClass(getClass());
    
    job.setInputFormatClass(GeneratorInputFormat.class);
    job.setOutputKeyClass(NullWritable.class);
    job.setOutputValueClass(NullWritable.class);
    
    job.getConfiguration().setInt("goraci.generator.mappers", numMappers);
    job.getConfiguration().setLong("goraci.generator.nodes", numNodes);
    
    job.setMapperClass(GeneratorMapper.class);
    
    job.setOutputFormatClass(NullOutputFormat.class);

    job.getConfiguration().setBoolean("mapred.map.tasks.speculative.execution", false);

    boolean success = job.waitForCompletion(true);
    
    return success ? 0 : 1;
  }
  
  public static void main(String[] args) throws Exception {
    int ret = ToolRunner.run(new Generator(), args);
    System.exit(ret);
  }

  
}
