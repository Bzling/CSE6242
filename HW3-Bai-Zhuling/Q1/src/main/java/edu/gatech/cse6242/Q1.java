package edu.gatech.cse6242;

import org.apache.hadoop.fs.Path;
import java.io.IOException;
import java.lang.Object;
import java.util.StringTokenizer;

//import javax.xml.soap.Text;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.*;
import org.apache.hadoop.util.*;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class Q1 {
	public static class Map extends Mapper<Object, Text, IntWritable, IntWritable>{
		private IntWritable inboundEmail = new IntWritable();
		private IntWritable weight = new IntWritable();
		public void map(Object key, Text value, Context context) throws IOException, InterruptedException{
			StringTokenizer data = new StringTokenizer(value.toString(),"\n");
			while(data.hasMoreTokens()) {
				String line = data.nextToken();
				String tokens[] = line.split("\t");
				inboundEmail.set(Integer.parseInt(tokens[1]));
				weight.set(Integer.parseInt(tokens[2]));
				context.write(inboundEmail, weight);
			}
		}
	}
	
	public static class Reduce extends Reducer<IntWritable, IntWritable, IntWritable, IntWritable>{
		private IntWritable result = new IntWritable();
		public void reduce(IntWritable key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException{
			int min = Integer.MAX_VALUE;
			for(IntWritable val : values) {
				if(val.get()<min) min = val.get();
			}
			result.set(min);
			context.write(key, result);
		}
	}

	
  public static void main(String[] args) throws Exception {
    Configuration conf = new Configuration();
    Job job = Job.getInstance(conf, "Q1");

    job.setJarByClass(Q1.class);
    job.setMapperClass(Map.class);
    job.setCombinerClass(Reduce.class);
    job.setReducerClass(Reduce.class);
    job.setOutputKeyClass(IntWritable.class);
    job.setOutputValueClass(IntWritable.class);

    FileInputFormat.addInputPath(job, new Path(args[0]));
    FileOutputFormat.setOutputPath(job, new Path(args[1]));
    System.exit(job.waitForCompletion(true) ? 0 : 1);
  }
}
