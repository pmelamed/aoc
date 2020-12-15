import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

// 7:58 - 8:34 - 8:39
public class Day15 {

    public static void main( String[] args ) {
        try {
            new Day15().doTasks( "9,3,1,0,8,4" );
        } catch ( Throwable e ) {
            e.printStackTrace();
        }
    }

    private void doTasks( String input ) {
        assertTask1( "0,3,6", 436, 175594 );
//        assertTask1( "1,3,2", 1, 2578  );
//        assertTask1( "2,1,3", 10, 3544142  );
//        assertTask1( "1,2,3", 27, 261214  );
//        assertTask1( "2,3,1", 78, 6895259  );
//        assertTask1( "3,2,1", 438, 18  );
//        assertTask1( "3,1,2", 1836, 362  );
        System.out.println( "Task1 = " + task1( input, 2020 ) + " Task2 = " + task1( input, 30000000 ) );
    }

    private long task1( String input, int turn ) {
        long[] numbers = Arrays.stream( input.split( "," ) )
                               .mapToLong( Long::parseLong )
                               .toArray();
        Map<Long, Integer> lastAppearance = new HashMap<>();
        int idx = 0;
        for ( ; idx < numbers.length - 1; idx++ ) {
            lastAppearance.put( numbers[idx], idx );
        }
        long lastNumber = numbers[numbers.length - 1];
        for ( ; idx < turn - 1; ++idx ) {
            long nextNumber = idx - lastAppearance.getOrDefault( lastNumber, idx );
            lastAppearance.put( lastNumber, idx );
            lastNumber = nextNumber;
        }
        return lastNumber;
    }

    private void assertTask1( String numbers, long expected1, long expected2 ) {
        long actual1 = task1( numbers, 2020 );
        if ( actual1 != expected1 ) {
            debug( "Failed on %s: %d instead of %d", numbers, actual1, expected1 );
        }
        long actual2 = task1( numbers, 30000000 );
        if ( actual2 != expected2 ) {
            debug( "Failed on %s: %d instead of %d", numbers, actual2, expected2 );
        }
    }

    private static void debug( String msg, Object... args ) {
        System.out.println( String.format( msg, args ) );
    }

}
