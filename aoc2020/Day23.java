import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Day23 implements AocDay<String, Long> {

    private String initial;

    public static void main( String[] args ) {
        try {
            executeTasks( "389125467", "67384529", 149245887792L );
            executeTasks( "523764819", "49576328", 511780369955L );
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
        Cup cup1 = playGame( 9, 100 );
        return renderCircle( cup1.next, 8 );
    }

    public Long task2() {
        Cup cup1 = playGame( 1_000_000, 10_000_000 );
        return ( (long) cup1.next.label ) * cup1.next.next.label;
    }

    private Cup playGame( int max, int moves ) {
        Cup[] cupsMap = IntStream.range( 0, max + 1 )
                                 .mapToObj( Cup::new )
                                 .toArray( Cup[]::new );
        Cup current = buildInitialCircle( cupsMap, initial, max );

        for ( int move = 0; move < moves; ++move ) {
            makeMove( current, cupsMap, max );
            current = current.next;
        }
        return cupsMap[1];
    }

    private void makeMove( Cup current, Cup[] cupsMap, int max ) {
        // Pick up cups
        Cup firstPickedUp = current.next;
        Cup lastPickedUp = current.next.next.next;
        current.next = lastPickedUp.next;

        // Find destination
        Cup destination = cupsMap[getDestination(
                current.label,
                max,
                firstPickedUp.label,
                firstPickedUp.next.label,
                lastPickedUp.label
        )];
        Cup next = destination.next;
        destination.next = firstPickedUp;
        lastPickedUp.next = next;
    }

    private int getDestination( int current, int max, int skipped1, int skipped2, int skipped3 ) {
        do {
            if ( current == 1 ) {
                current = max;
            } else {
                --current;
            }
        } while ( current == skipped1 || current == skipped2 || current == skipped3 );
        return current;
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
