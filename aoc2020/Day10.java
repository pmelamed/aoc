import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

public class Day10 {

    private int[] adapters;
    private long[] ways;

    public static void main( String[] args ) {
        try {
            new Day10( "c:\\tmp\\sample10-1.dat" ).doTasks();
            new Day10( "c:\\tmp\\sample10-2.dat" ).doTasks();
            new Day10( "c:\\tmp\\input10.dat" ).doTasks();
        } catch ( Throwable e ) {
            e.printStackTrace();
        }
    }

    public Day10( String file ) throws IOException {
        adapters = Files.lines( Paths.get( file ) )
                        .mapToInt( Integer::parseInt )
                        .toArray();
        adapters = Arrays.copyOf( adapters, adapters.length + 1 );
        Arrays.sort( adapters );
        ways = new long[adapters.length];
    }

    private void doTasks() {
        long result1 = task1();
        System.out.println( "Task1 = " + result1 + " Task2 = " + task2() );
    }

    private long task1() {
        int count1 = 0;
        int count3 = 0;
        int prev = 0;
        for ( int joltage : adapters ) {
            switch ( joltage - prev ) {
                case 1:
                    ++count1;
                    break;
                case 3:
                    ++count3;
                    break;
            }
            prev = joltage;
        }
        return count1 * ( count3 + 1 );
    }

    private long task2() {
        ways[adapters.length - 1] = 1;
        for ( int i = adapters.length - 2; i >= 0; --i ) {
            ways[i] = ways( i );
        }
        return ways[0];
    }

    private long ways( int index ) {
        long result = 0;
        if ( index < adapters.length - 1 && adapters[index + 1] - adapters[index] <= 3 ) {
            result += ways[index + 1];
        }
        if ( index < adapters.length - 2 && adapters[index + 2] - adapters[index] <= 3 ) {
            result += ways[index + 2];
        }
        if ( index < adapters.length - 3 && adapters[index + 3] - adapters[index] <= 3 ) {
            result += ways[index + 3];
        }
        return result;
    }
    
    private void print( String msg ) {
        // System.out.println( msg );
    }
}
