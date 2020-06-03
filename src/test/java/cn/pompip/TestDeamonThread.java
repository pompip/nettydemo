package cn.pompip;

import org.junit.Test;

import java.util.Random;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;

public class TestDeamonThread {
    class SumTask extends RecursiveTask<Long>{
        long start;
        long end;
        public SumTask(long start,long end) {
            this.start = start;
            this.end = end;
        }

        @Override
        protected Long compute() {
            long sum = 0;
            if (end - start <= 1000000){
                for (long i = start;i<=end;i++){
                    sum +=i;
                }
            }else {
                long middle = (start+end)/2;
                SumTask t1 = new SumTask(start,middle);
                SumTask t2 = new SumTask(middle+1,end);
                invokeAll(t1,t2);
                long sum1 = t1.join();
                long sum2 = t2.join();
                sum = sum1+sum2;
            }
            return sum;
        }
    }

    @Test
    public void testH() throws InterruptedException {
        long s = 1;
        long e = 100000000L;
        long sum =0;
        long st1 = System.currentTimeMillis();
        for (long i =s;i<=e;i++){
            sum+=i;
        }
        long st2 = System.currentTimeMillis();
        System.out.println("sum1:"+sum+ " time:"+(st2-st1));

        long st3 = System.currentTimeMillis();
        ForkJoinTask<Long> task = new SumTask(s,e);
        Long result = ForkJoinPool.commonPool().invoke(task);

        long st4 = System.currentTimeMillis();
        System.out.println("sum2:" + result +" time:"+(st4-st3) );
    }
}
