import org.w3c.dom.css.Counter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.ToIntBiFunction;

public class Day11 {

    private char[][] initial;
    private char[][] seats;
    private char[][] nextState;
    private int cols;
    private int rows;

    public static void main( String[] args ) {
        try {
            new Day11( "c:\\tmp\\sample11-1.dat" ).doTasks();
//            new Day10( "c:\\tmp\\sample10-2.dat" ).doTasks();
            new Day11( "c:\\tmp\\input11.dat" ).doTasks();
        } catch ( Throwable e ) {
            e.printStackTrace();
        }
    }

    public Day11( String file ) throws IOException {
        List<char[]> full = new ArrayList<>();
        Files.lines( Paths.get( file ) ).map( s -> "." + s + "." )
             .map( String::toCharArray )
             .forEach( full::add );
        cols = full.get( 0 ).length - 2;
        rows = full.size();
        char[] padding = new char[cols + 2];
        Arrays.fill( padding, '.' );
        full.add( 0, padding );
        full.add( padding );
        initial = full.toArray( char[][]::new );
        seats = new char[rows + 2][cols + 2];
        nextState = new char[rows + 2][cols + 2];
    }

    private void doTasks() {
        long result1 = task1();
        System.out.println( "Task1 = " + result1 + " Task2 = " + task2() );
    }

    private long task1() {
        return task( this::countAdjacent, 4 );
    }

    private long task2() {
        return task( this::countVisible, 5 );
    }

    private long task( Counter counter, int tolerance ) {
        copyState( initial, seats );
        copyState( initial, nextState );
        while ( iterate( counter, tolerance ) ) {
        }
        return countBusy();
    }
    private boolean iterate( Counter counter, int tolerance ) {
        boolean changed = false;
        for ( int row = 1; row <= rows; ++row ) {
            for ( int col = 1; col <= cols; ++col ) {
                if ( seats[row][col] == '.' ) {
                    continue;
                }
                int count = counter.count( row, col );
                if ( count >= tolerance && seats[row][col] == '#' ) {
                    changed = true;
                    nextState[row][col] = 'L';
                } else if ( count == 0 && seats[row][col] == 'L' ) {
                    changed = true;
                    nextState[row][col] = '#';
                }
            }
        }
        copyState( nextState, seats );
        return changed;
    }

    private void copyState( char[][] src, char[][] dst ) {
        for ( int row = 0; row < src.length; ++row ) {
            for ( int col = 0; col < src[0].length; ++col ) {
                dst[row][col] = src[row][col];
            }
        }
    }

    private int countBusy() {
        int result = 0;
        for ( int row = 1; row <= rows; ++row ) {
            for ( int col = 1; col <= cols; ++col ) {
                if ( seats[row][col] == '#' ) {
                    ++result;
                }
            }
        }
        return result;
    }

    private int countAdjacent( int row, int col ) {
        int count = 0;
        for ( int dr = -1; dr <= 1; ++dr ) {
            for ( int dc = -1; dc <= 1; ++dc ) {
                if ( ( dr != 0 || dc != 0 ) && seats[row + dr][col + dc] == '#' ) {
                    ++count;
                }
            }
        }
        return count;
    }

    private int countVisible( int row, int col ) {
        return scanLine( row, col, -1, -1, 0, 0 )
                + scanLine( row, col, -1, 0, 0, 0 )
                + scanLine( row, col, -1, 1, 0, cols + 1 )
                + scanLine( row, col, 0, 1, 0, cols + 1 )
                + scanLine( row, col, 1, 1, rows + 1, cols + 1 )
                + scanLine( row, col, 1, 0, rows + 1, 0 )
                + scanLine( row, col, 1, -1, rows + 1, 0 )
                + scanLine( row, col, 0, -1, 0, 0 );
    }

    private int scanLine( int row, int col, int dr, int dc, int rlimit, int climit ) {
        do {
            row += dr;
            col += dc;
            if ( seats[row][col] != '.' ) {
                return seats[row][col] == '#' ? 1 : 0;
            }
        } while ( row != rlimit && col != climit );
        return 0;
    }

    private void debug( String msg, Object... args ) {
        // System.out.print( String.format( msg, args ) );
    }

    interface Counter {
        int count(int row, int col);
    }
}
