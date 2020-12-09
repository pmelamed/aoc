import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Day9 {

    private int preambleSize;
    private long[] queue;

    public static void main( String[] args ) {
        try {
            new Day9( "c:\\tmp\\sample09-1.dat", 5 ).doTasks();
            new Day9( "c:\\tmp\\input09.dat", 25 ).doTasks();
        } catch ( Throwable e ) {
            e.printStackTrace();
        }
    }

    public Day9( String file, int preambleSize ) throws IOException {
        this.preambleSize = preambleSize;
        queue = Files.lines( Paths.get( file ) )
                     .map( Long::parseLong )
                     .mapToLong( Long::longValue )
                     .toArray();
    }

    private void doTasks() {
        long result1 = task1();
        System.out.println( "Task1 = " + result1 + " Task2 = " + task2( result1 ) );
    }

    private long task1() {
        for ( int index = preambleSize; index < queue.length; ++index ) {
            if ( !checkNumber( index ) ) {
                return queue[index];
            }
        }
        return -1;
    }

    private boolean checkNumber( int index ) {
        long target = queue[index];
        for ( int lower = index - preambleSize; lower < index - 1; ++lower ) {
            for ( int higher = lower + 1; higher < index; ++higher ) {
                if ( target == queue[lower] + queue[higher] ) {
                    return true;
                }
            }
        }
        return false;
    }

    private long task2( long target ) {
        for ( int start = 0; start < queue.length - 1; ++start ) {
            long sum = queue[start];
            long min = sum;
            long max = sum;
            for ( int end = start + 1; sum < target && end < queue.length; ++end ) {
                long current = queue[end];
                sum += current;
                if ( current < min ) {
                    min = current;
                }
                if ( current > max ) {
                    max = current;
                }
                if ( sum == target ) {
                    return min + max;
                }
            }
        }
        return -1;
    }
}
