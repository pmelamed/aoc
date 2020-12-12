import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Day12 {

    private static int[] VX = { 1, 0, -1, 0 };
    private static int[] VY = { 0, -1, 0, 1 };
    private int x;
    private int y;
    private int dx;
    private int dy;
    private int waypointX;
    private int waypointY;
    private String fileName;

    public static void main( String[] args ) {
        try {
            new Day12( "c:\\tmp\\sample12-1.dat" ).doTasks();
            new Day12( "c:\\tmp\\input12.dat" ).doTasks();
        } catch ( Throwable e ) {
            e.printStackTrace();
        }
    }

    public Day12( String file ) {
        this.fileName = file;
    }

    private void doTasks() throws IOException {
        long result1 = task1();
        System.out.println( "Task1 = " + result1 + " Task2 = " + task2() );
    }

    private long task1() throws IOException {
        x = 0;
        y = 0;
        dx = 1;
        dy = 0;
        Files.lines( Paths.get( fileName ) ).forEach( this::processInstruction );
        return Math.abs( x ) + Math.abs( y );
    }

    private long task2() throws IOException {
        x = 0;
        y = 0;
        waypointX = 10;
        waypointY = 1;
        Files.lines( Paths.get( fileName ) ).forEach( this::processNewInstruction );
        return Math.abs( x ) + Math.abs( y );
    }

    private void processInstruction( String instruction ) {
        char cmd = instruction.charAt( 0 );
        int arg = Integer.parseInt( instruction.substring( 1 ) );
        switch ( cmd ) {
            case 'R':
                turn( arg / 90 % 4 );
                break;
            case 'L':
                turn( 4 - arg / 90 % 4 );
                break;
            case 'F':
                move( dx, dy, arg );
                break;
            default:
                int dir = "ESWN".indexOf( cmd );
                move( VX[dir], VY[dir], arg );
        }
    }

    private void move( int dx, int dy, int arg ) {
        x += dx * arg;
        y += dy * arg;
    }

    private void turn( int steps ) {
        for ( int i = 0; i < steps; ++i ) {
            int nextY = -dx;
            dx = dy;
            dy = nextY;
        }
    }

    private void processNewInstruction( String instruction ) {
        char cmd = instruction.charAt( 0 );
        int arg = Integer.parseInt( instruction.substring( 1 ) );
        switch ( cmd ) {
            case 'R':
                turnWaypoint( arg / 90 % 4 );
                break;
            case 'L':
                turnWaypoint( 4 - arg / 90 % 4 );
                break;
            case 'F':
                moveTowardsWaypoint( arg );
                break;
            default:
                moveWaypoint( "ESWN".indexOf( cmd ), arg );
        }
    }

    private void turnWaypoint( int steps ) {
        for ( int i = 0; i < steps; ++i ) {
            int nextY = -waypointX;
            waypointX = waypointY;
            waypointY = nextY;
        }
    }

    private void moveTowardsWaypoint( int arg ) {
        x += arg * waypointX;
        y += arg * waypointY;
    }

    private void moveWaypoint( int dir, int arg ) {
        waypointX += VX[dir] * arg;
        waypointY += VY[dir] * arg;
    }
}
