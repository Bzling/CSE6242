package edu.gatech.cse6242;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.*;
import org.apache.hadoop.util.*;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import java.io.IOException;
import java.util.StringTokenizer;
import java.lang.Object;


//import javax.xml.soap.Text;

public class Q4 {
	
	public static class map1 extends Mapper<Object, Text, IntWritable, IntWritable>{
		private IntWritable node = new IntWritable();
		private final IntWritable nodeOut = new IntWritable(1);
		private final IntWritable nodeIn = new IntWritable(-1);
		public void map(Object key, Text value, Context context) throws IOException, InterruptedException{
			StringTokenizer data = new StringTokenizer(value.toString(), "\n");
			while(data.hasMoreTokens()) {
				String line = data.nextToken();
				String tokens[] = line.split("\t");
				node.set(Integer.parseInt(tokens[0]));
				context.write(node, nodeOut);
				node.set(Integer.parseInt(tokens[1]));
				context.write(node, nodeIn);
			}
		}
	}
	
	public static class reduce1 extends Reducer<IntWritable, IntWritable, IntWritable, IntWritable>{
		private IntWritable diff = new IntWritable();
		public void reduce(IntWritable key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException{
			int sum = 0;
			for(IntWritable val: values) {
				sum += val.get();
			}	
			diff.set(sum);
			context.write(key, diff);
		}
	}
	
	public static class map2 extends Mapper<Object, Text, IntWritable, IntWritable>{
		private final static IntWritable a = new IntWritable(1);
		private IntWritable differ = new IntWritable();
		public void map(Object key1, Text values1, Context context) 
		throws IOException, InterruptedException{
			StringTokenizer data1 = new StringTokenizer(values1.toString(), "\n");
			while(data1.hasMoreTokens()) {
				String line1 = data1.nextToken();
				String tokens1[] = line1.split("\t");
				differ.set(Integer.parseInt(tokens1[1]));
				context.write(differ, a);
			}
		}
	}
	
	public static class reduce2 extends Reducer<IntWritable, IntWritable, IntWritable, IntWritable>{
		private IntWritable count = new IntWritable();
		public void reduce(IntWritable key1, Iterable<IntWritable> values1, Context context) throws IOException, InterruptedException{
			int sum1 = 0;
			for(IntWritable val1: values1) {
				sum1 += val1.get();
			}	
			count.set(sum1);
			context.write(key1, count);
		}
	}
  public static void main(String[] args) throws Exception {
    Configuration conf = new Configuration();
    Job job = Job.getInstance(conf, "Q4");

    job.setJarByClass(Q4.class);
    job.setMapperClass(map1.class);
    job.setCombinerClass(reduce1.class);
    job.setReducerClass(reduce1.class);
    job.setOutputKeyClass(IntWritable.class);
    job.setOutputValueClass(IntWritable.class);


    FileInputFormat.addInputPath(job, new Path(args[0]));
    FileOutputFormat.setOutputPath(job, new Path("output"));
    job.waitForCompletion(true);

    Configuration conf2 = new Configuration();
    Job job2 = Job.getInstance(conf2, "Q4");

    job2.setJarByClass(Q4.class);
    job2.setMapperClass(map2.class);
    job2.setCombinerClass(reduce2.class);
    job2.setReducerClass(reduce2.class);
    job2.setOutputKeyClass(IntWritable.class);
    job2.setOutputValueClass(IntWritable.class);
    FileInputFormat.addInputPath(job2, new Path("output"));
    FileOutputFormat.setOutputPath(job2, new Path(args[1]));
    System.exit(job2.waitForCompletion(true) ? 0 : 1);
  }
}
