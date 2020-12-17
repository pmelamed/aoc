import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day17 {

    private Set<Coords> initialCells;
    private Set<Coords> activeCells;

    public static void main( String[] args ) {
        try {
            executeTasks( "c:\\tmp\\sample17-1.dat", 112L, 848L );
            executeTasks( "c:\\tmp\\input17.dat", null, null );
        } catch ( Throwable e ) {
            e.printStackTrace();
        }
    }

    public static void executeTasks( String fileName, Long assert1, Long assert2 ) throws IOException {
        Day17 day = new Day17( fileName );
        long result1 = day.task1();
        long result2 = day.task2();
        debug( "%s: %d %d", fileName, result1, result2 );
        if ( assert1 != null && result1 != assert1 ) {
            debug( "Failed on %s task #1: %d instead of %d", fileName, result1, assert1 );
        }
        if ( assert2 != null && result2 != assert2 ) {
            debug( "Failed on %s task #2: %d instead of %d", fileName, result2, assert2 );
        }
    }

    public Day17( String file ) throws IOException {
        initialCells = new TreeSet<>();
        List<String> lines = Files.readAllLines( Path.of( file ) );
        for ( int y = 0; y < lines.size(); y++ ) {
            char[] line = lines.get( y ).toCharArray();
            for ( int x = 0; x < line.length; x++ ) {
                if ( line[x] == '#' ) {
                    initialCells.add( new Coords( x, y, 0, 0 ) );
                }
            }
        }
    }

    private long task1() throws IOException {
        return doTask( this::getNeighbours3 );
    }

    private long task2() {
        return doTask( this::getNeighbours4 );
    }

    private long doTask( BiFunction<Coords, Boolean, Stream<Coords>> generator ) {
        activeCells = new TreeSet<>( initialCells );
        for ( int step = 0; step < 6; ++step ) {
            doStep( generator );
        }
        return activeCells.size();
    }

    private void doStep( BiFunction<Coords, Boolean, Stream<Coords>> neighboursGenerator ) {
        Set<Coords> checked = activeCells.stream()
                                         .flatMap( coords -> neighboursGenerator.apply( coords, true ) )
                                         .collect( Collectors.toCollection( TreeSet::new ) );
        activeCells = checked.stream()
                             .filter( cell -> isCellRemainActive( cell, neighboursGenerator ) )
                             .collect( Collectors.toCollection( TreeSet::new ) );
    }

    private boolean isCellRemainActive(
            Coords cell,
            BiFunction<Coords, Boolean, Stream<Coords>> neighboursGenerator
    ) {
        long count = neighboursGenerator.apply( cell, false )
                                        .filter( activeCells::contains )
                                        .count();
        boolean selfActive = activeCells.contains( cell );
        return selfActive && count == 2 || count == 3;
    }

    private Stream<Coords> getNeighbours3( Coords c, boolean includeSelf ) {
        Coords[] result = new Coords[includeSelf ? 27 : 26];
        int index = 0;
        for ( int dx = -1; dx <= 1; ++dx ) {
            for ( int dy = -1; dy <= 1; ++dy ) {
                for ( int dz = -1; dz <= 1; ++dz ) {
                    if ( includeSelf || dx != 0 || dy != 0 || dz != 0 ) {
                        result[index++] = new Coords( c.x + dx, c.y + dy, c.z + dz, 0 );
                    }
                }
            }
        }
        return Arrays.stream( result );
    }

    private Stream<Coords> getNeighbours4( Coords c, boolean includeSelf ) {
        Coords[] result = new Coords[includeSelf ? 81 : 80];
        int index = 0;
        for ( int dx = -1; dx <= 1; ++dx ) {
            for ( int dy = -1; dy <= 1; ++dy ) {
                for ( int dz = -1; dz <= 1; ++dz ) {
                    for ( int dw = -1; dw <= 1; ++dw ) {
                        if ( includeSelf || dx != 0 || dy != 0 || dz != 0 || dw != 0 ) {
                            result[index++] = new Coords( c.x + dx, c.y + dy, c.z + dz, c.w + dw );
                        }
                    }
                }
            }
        }
        return Arrays.stream( result );
    }

    private static void debug( String msg, Object... args ) {
        System.out.println( String.format( msg, args ) );
    }

    private static class Coords implements Comparable<Coords> {
        int x, y, z, w;

        public Coords( int x, int y, int z, int w ) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.w = w;
        }

        @Override
        public boolean equals( Object o ) {
            if ( this == o ) {
                return true;
            }
            Coords coords = (Coords) o;
            return x == coords.x && y == coords.y && z == coords.z && w == coords.w;
        }

        @Override
        public int hashCode() {
            int result = x;
            result = 31 * result + y;
            result = 31 * result + z;
            result = 31 * result + w;
            return result;
        }

        @Override
        public int compareTo( Coords o ) {
            if ( x != o.x ) {
                return x - o.x;
            }
            if ( y != o.y ) {
                return y - o.y;
            }
            if ( z != o.z ) {
                return z - o.z;
            }
            return w - o.w;
        }
    }
}
