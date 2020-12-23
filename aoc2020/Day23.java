import java.util.Arrays;
import java.util.Optional;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Day23 implements AocDay<String, Long> {

    private String initial;

    public static void main( String[] args ) {
        try {
            executeTasks( "389125467", "67384529", 149245887792L );
            executeTasks( "523764819", null, null );
        } catch ( Throwable e ) {
            e.printStackTrace();
        }
    }

    public static void executeTasks( String fileName, String expected1, Long expected2 ) {
        Utils.executeDay( new Day23( fileName ), expected1, expected2 );
    }

    public Day23( String initial ) {
        this.initial = initial;
    }

    @Override
    public String sampleName() {
        return initial;
    }

    public String task1() {
        Cup[] cupsMap = IntStream.range( 0, 10 )
                                 .mapToObj( Cup::new )
                                 .toArray( Cup[]::new );
        TreeSet<Integer> cupsInCircle = buildInitialSet( 9 );
        Cup current = buildInitialCircle( cupsMap, initial, 0 );

        for ( int move = 0; move < 100; ++move ) {
            makeMove( current, cupsMap, cupsInCircle);
            current = current.next;
        }

        return renderCircle( cupsMap[1].next, 8 );
    }

    public Long task2() {
        Cup[] cupsMap = IntStream.range( 0, 1_000_001 )
                                 .mapToObj( Cup::new )
                                 .toArray( Cup[]::new );
        TreeSet<Integer> cupsInCircle = buildInitialSet( 1_000_000 );
        Cup current = buildInitialCircle( cupsMap, initial, 1_000_000 );

        for ( int move = 0; move < 10_000_000; ++move ) {
            makeMove( current, cupsMap, cupsInCircle);
            current = current.next;
        }


        long value1 = cupsMap[1].next.label;
        long value2 = cupsMap[1].next.next.label;
        return value1 * value2;
    }

    private void makeMove( Cup current, Cup[] cupsMap, TreeSet<Integer> cupsInCircle ) {
        // Pick up cups
        Cup cup = current.next;
        Cup firstPickedUp = cup;
        Cup lastPickedUp = cup;
        int[] skipped = new int[3];
        for ( int skip = 0; skip < 3; ++skip, cup = cup.next ) {
            skipped[skip] = cup.label;
            cupsInCircle.remove( cup.label );
            lastPickedUp = cup;
        }
        current.next = cup;
//            Utils.debug(
//                    "Removed <%s> Last removed: <%d> Rest of circle: <%s>",
//                    renderCircle( firstPickedUp, 3 ),
//                    lastPickedUp.label,
//                    renderCircle( current, 7 )
//            );

        // Find destination
        Cup destination = cupsMap[
                Optional.ofNullable( cupsInCircle.lower( current.label ) )
                        .orElse( cupsInCircle.last() )
                ];
//            Utils.debug(
//                    "Destination <%d>",
//                    destination.label
//            );
        Cup next = destination.next;
        destination.next = firstPickedUp;
        lastPickedUp.next = next;

        // Restore set
        Arrays.stream( skipped ).forEach( cupsInCircle::add );
//            Utils.debug( "%s", renderCircle( current, 9 ) );
    }

    private Cup buildInitialCircle( Cup[] cups, String config, int max ) {
        Cup cup;
        Cup prevCup = null;
        Cup firstCup = null;
        for ( char label : config.toCharArray() ) {
            cup = cups[label - '0'];
            if ( prevCup != null ) {
                prevCup.next = cup;
            } else {
                firstCup = cup;
            }
            prevCup = cup;
        }
        for ( int label = 10; label <= max; ++label ) {
            prevCup.next = cups[label];
            prevCup = prevCup.next;
        }
        prevCup.next = firstCup;
        return firstCup;
    }

    private TreeSet<Integer> buildInitialSet( int max ) {
        return IntStream.range( 1, max + 1 )
                        .boxed()
                        .collect( Collectors.toCollection( TreeSet::new ) );
    }

    private String renderCircle( Cup first, int length ) {
        StringBuilder result = new StringBuilder();
        Stream.iterate( first, Cup::getNext )
              .limit( length )
              .mapToInt( Cup::getLabel )
              .forEach( result::append );
        return result.toString();
    }

    private static class Cup {
        private int label;
        private Cup next;

        public Cup( int label ) {
            this.label = label;
        }

        public int getLabel() {
            return label;
        }

        public Cup getNext() {
            return next;
        }

        public Cup skip( int cups ) {
            Cup result = this.next;
            while ( cups > 0 ) {
                result = result.next;
                --cups;
            }
            return result;
        }
    }
}
