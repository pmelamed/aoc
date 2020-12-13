import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.function.IntBinaryOperator;

public class Day13 {

    private String fileName;
    private int minId;
    private int minWait;

    public static void main( String[] args ) {
        try {
            // new Day13( "c:\\tmp\\sample13-1.dat" ).doTasks();
            new Day13( "c:\\tmp\\input13.dat" ).doTasks();
        } catch ( Throwable e ) {
            e.printStackTrace();
        }
    }

    public Day13( String file ) {
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
        long[] solutions = Arrays.stream( fileLines )
                                 .skip( 1 )
                                 .mapToLong( ids -> solveTask2( 100000000000000L, ids ) )
                                 .toArray();
        return solutions[solutions.length - 1];
    }

    private void doAssert( String ids, long expected ) {
        long actual = solveTask2( 0, ids );
        if ( actual != expected ) {
            debug( "Failed on %s: %d instead of %d", ids, actual, expected );
        }
    }

    private long solveTask2( long min, String idsList ) {
        long[] ids = Arrays.stream( idsList.split( "," ) )
                           .mapToLong( id -> id.startsWith( "x" ) ? -1 : Long.parseLong( id ) )
                           .toArray();
        long step = ids[0];
        long moment = 0;
        for ( int idx = 1; idx < ids.length; ++idx ) {
            long current = ids[idx];
            if ( current == -1 ) {
                continue;
            }
            moment = findCollision( moment, current - idx, step, current );
            step = lcd( step, current );
        }
        return moment;
    }

    private boolean stopsAt( long moment, long id ) {
        return id == -1 || moment % id == 0;
    }

    private long gcd( long a, long b ) {
        // debug( "GCD: %-8d / %-8d", a, b );
        while ( a != b ) {
            if ( a < b ) {
                b -= a * ( b / a );
                if ( b == 0 ) {
                    return a;
                }
            } else {
                a -= b * ( a / b );
                if ( a == 0 ) {
                    return b;
                }
            }
        }
        return a;
    }

    private long lcd( long a, long b ) {
        long g = gcd( a, b );
        return a / g * b;
    }

    private long findCollision( long a, long b, long stepa, long stepb ) {
        // debug( "findCollision: a=%-8d b=%-8d sa=%-8d sb=%-8d", a, b, stepa, stepb );
        while ( a != b ) {
            if ( a < b ) {
                a += ( b - a ) / stepa * stepa;
                if ( a != b ) {
                    a += stepa;
                }
            } else {
                b += ( a - b ) / stepb * stepb;
                if ( a != b ) {
                    b += stepb;
                }
            }
        }
        // debug( "findCollision: r=%d", a );
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
