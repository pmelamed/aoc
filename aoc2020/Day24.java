import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;

public class Day24 implements AocDay<Integer, Integer> {

    private enum Dir {
        W( -2, 0 ), E( 2, 0 ), X( -1, -1 ), Y( 1, -1 ), U( -1, 1 ), V( 1, 1 );
        final int delta;

        Dir( int x, int y ) {
            delta = y * 10000 + x;
        }

        public int getDelta() {
            return delta;
        }
    }

    public static final int[] NEIGHBOURS = Arrays.stream( Dir.values() )
                                                 .mapToInt( Dir::getDelta )
                                                 .toArray();
    private String name;
    private Dir[][] lines;
    private Set<Integer> blackList = new TreeSet<>();

    public static void main( String[] args ) {
        try {
            executeTasks( "c:\\tmp\\sample24-1.dat", 10, 2208 );
            executeTasks( "c:\\tmp\\input24.dat", 277, null );
        } catch ( Throwable e ) {
            e.printStackTrace();
        }
    }

    public static void executeTasks( String fileName, Integer expected1, Integer expected2 ) {
        Utils.executeDay( new Day24( fileName ), expected1, expected2 );
    }

    public Day24( String file ) {
        name = file;
        lines = Utils.lines( file )
                     .map( this::lineToDirs )
                     .toArray( Dir[][]::new );
    }

    private Dir[] lineToDirs( String line ) {
        return Arrays.stream( line.replace( "nw", "x" )
                                  .replace( "ne", "y" )
                                  .replace( "sw", "u" )
                                  .replace( "se", "v" )
                                  .toUpperCase()
                                  .split( "" ) )
                     .map( Dir::valueOf ).toArray( Dir[]::new );
    }

    @Override
    public String sampleName() {
        return name;
    }

    public Integer task1() {
        blackList = new TreeSet<>();
        for ( Dir[] cellAddr : lines ) {
            Integer code = Arrays.stream( cellAddr )
                                 .reduce( 0, ( coord, dir ) -> coord += dir.delta, Integer::sum );
            flipCell( code );
        }
        return blackList.size();
    }

    public Integer task2() {
        for ( int move = 0; move < 100; ++move ) {
            TreeSet<Integer> checked = new TreeSet<>( blackList );
            blackList.stream()
                     .flatMapToInt( cell -> Arrays.stream( NEIGHBOURS ).map( n -> cell + n ) )
                     .boxed()
                     .forEach( checked::add );
            int[] flipped = checked.stream()
                                  .filter( this::needToFlip )
                                  .mapToInt( Integer::intValue )
                                  .toArray();
            Arrays.stream( flipped ).forEach( this::flipCell );
        }
        return blackList.size();
    }

    private void flipCell( int cell ) {
        if ( blackList.contains( cell ) ) {
            blackList.remove( cell );
        } else {
            blackList.add( cell );
        }
    }

    private boolean needToFlip( int cell ) {
        long neighboursCount = Arrays.stream( NEIGHBOURS )
                                     .map( n -> cell + n )
                                     .filter( blackList::contains )
                                     .count();
        return blackList.contains( cell )
                ? neighboursCount == 0 || neighboursCount > 2
                : neighboursCount == 2;
    }
}
