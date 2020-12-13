import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.function.IntBinaryOperator;

public class Day13LU {

    private String fileName;

    public static void main( String[] args ) {
        try {
            new Day13LU( "c:\\tmp\\input13.dat" ).doTasks();
        } catch ( Throwable e ) {
            e.printStackTrace();
        }
    }

    public Day13LU( String file ) {
        this.fileName = file;
    }

    private void doTasks() throws IOException {
        long result1 = task1();
        System.out.println( "Task1 = " + result1 + " Task2 = " + task2() );
    }

    private int task1() throws IOException {
        String[] fileLines = Files.lines( Paths.get( fileName ) )
                                  .toArray( String[]::new );
        return Arrays.stream( fileLines[1].split( "," ) )
                     .filter( id -> !id.equals( "x" ) )
                     .mapToInt( Integer::parseInt )
                     .reduce( new MinWaitReduce( Integer.parseInt( fileLines[0] ) ) )
                     .orElse( -1 );
    }

    private long task2() throws IOException {
        doAssert( "7,13,x,x,59,x,31,19", 1068781 );
        doAssert( "17,x,13,19", 3417 );
        doAssert( "67,7,59,61", 754018 );
        doAssert( "67,x,7,59,61", 779210 );
        doAssert( "67,7,x,59,61", 1261476 );
        doAssert( "1789,37,47,1889", 1202161486 );
        String[] fileLines = Files.lines( Paths.get( fileName ) )
                                  .toArray( String[]::new );
        return Arrays.stream( fileLines )
                     .skip( 1 )
                     .limit( 1 )
                     .mapToLong( this::solveTask2 )
                     .max()
                     .orElse( -1 );
    }

    private void doAssert( String ids, long expected ) {
        long actual = solveTask2( ids );
        if ( actual != expected ) {
            debug( "Failed on %s: %d instead of %d", ids, actual, expected );
        }
    }

    private long solveTask2( String idsList ) {
        long[] ids = Arrays.stream( idsList.split( "," ) )
                           .mapToLong( id -> id.startsWith( "x" ) ? -1 : Long.parseLong( id ) )
                           .toArray();
        long step = ids[0];
        long moment = step;
        for ( int idx = 1; idx < ids.length; ++idx ) {
            long current = ids[idx];
            if ( current != -1 ) {
                moment = findCollision( moment, current - idx, step, current );
                step *= current;
            }
        }
        return moment;
    }

    private long findCollision( long a, long b, long stepa, long stepb ) {
        while ( a != b ) {
            if ( a < b ) {
                a += ( b - a ) / stepa * stepa;
                if ( a == b ) {
                    return a;
                }
                a += stepa;
            } else {
                b += ( a - b ) / stepb * stepb;
                if ( a == b ) {
                    return b;
                }
                b += stepb;
            }
        }
        return a;
    }

    private static void debug( String msg, Object... args ) {
        System.out.println( String.format( msg, args ) );
    }

    private static class MinWaitReduce implements IntBinaryOperator {
        private int moment;
        private int minId = -1;
        private int minWait = Integer.MAX_VALUE;

        public MinWaitReduce( int moment ) {
            this.moment = moment;
        }

        @Override
        public int applyAsInt( int ignore, int id ) {
            int waitTime = ( moment + id ) / id * id - moment;
            if ( waitTime < minWait ) {
                minWait = waitTime;
                minId = id;
            }
            return minId == -1 ? -1 : minWait * minId;
        }
    }
}
