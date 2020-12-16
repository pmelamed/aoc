import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day16 {

    private Map<Long, Set<String>> fieldRanges = new TreeMap<>();
    private Set<String> fieldNames = new HashSet<>();
    private long[] myTicket;
    private List<long[]> otherTickets = new ArrayList<>();

    public static void main( String[] args ) {
        try {
            // executeTasks( "c:\\tmp\\sample16-1.dat", 71L, null );
            executeTasks( "c:\\tmp\\input16.dat", null, null );
        } catch ( Throwable e ) {
            e.printStackTrace();
        }
    }

    public static void executeTasks( String fileName, Long assert1, Long assert2 ) throws IOException {
        Day16 day = new Day16( fileName );
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

    public Day16( String file ) throws IOException {
        List<String> allLines = Files.readAllLines( Path.of( file ) );
        int lineIndex = 0;
        while ( !allLines.get( lineIndex ).isEmpty() ) {
            processRule( allLines.get( lineIndex++ ) );
        }
        myTicket = processTicket( allLines.get( lineIndex + 2 ) );
        lineIndex += 5;
        while ( lineIndex < allLines.size() ) {
            otherTickets.add( processTicket( allLines.get( lineIndex++ ) ) );
        }
    }

    private long task1() throws IOException {
        return otherTickets.stream().mapToLong( this::calcWrong ).sum();
    }

    private long task2() {
        List<Set<String>> fieldPositions = Stream.generate( () -> new HashSet<>( fieldNames ) )
                                                 .limit( myTicket.length )
                                                 .collect( Collectors.toList() );
        otherTickets.stream()
                    .filter( this::isCorrectTicket )
                    .forEach( ticket -> narrowFieldsSet( ticket, fieldPositions ) );

        boolean flag;
        do {
            flag = false;
            for ( int index = 0; index < fieldPositions.size(); index++ ) {
                Set<String> available = fieldPositions.get( index );
                if ( available.size() == 1 ) {
                    String field = available.iterator().next();
                    for ( Set<String> s : fieldPositions ) {
                        if ( s != available ) {
                            flag |= s.remove( field );
                        }
                    }
                }
            }
        } while ( flag );

        long prod = 1;
        for ( int index = 0; index < fieldPositions.size(); index++ ) {
            Set<String> fields = fieldPositions.get( index );
            if ( fields.size() != 1 ) {
                throw new IllegalStateException(
                        "Unknown field found at position " + index + ", possible fields: " +
                                fields.toString()
                );
            }
            if ( fields.iterator().next().startsWith( "departure" ) ) {
                prod *= myTicket[index];
            }
        }
        return prod;
    }

    private long calcWrong( long[] ticket ) {
        return Arrays.stream( ticket ).filter( val -> !fieldRanges.containsKey( val ) ).sum();
    }

    private boolean isCorrectTicket( long[] ticket ) {
        return Arrays.stream( ticket )
                     .filter( val -> !fieldRanges.containsKey( val ) )
                     .findAny()
                     .isEmpty();
    }

    private void processRule( String rule ) {
        Pattern regex = Pattern.compile( "^([^:]+): ([^-]+)-([^ ]+) or ([^-]+)-(.+)$" );
        Matcher matcher = regex.matcher( rule );
        if ( !matcher.find() ) {
            throw new IllegalStateException( "Bad rule :" + rule );
        }
        String field = matcher.group( 1 );
        fieldNames.add( field );
        long from1 = Integer.parseInt( matcher.group( 2 ) );
        long to1 = Integer.parseInt( matcher.group( 3 ) );
        long from2 = Integer.parseInt( matcher.group( 4 ) );
        long to2 = Integer.parseInt( matcher.group( 5 ) );
        while ( from1 <= to1 ) {
            registerValue( from1++, field );
        }
        while ( from2 <= to2 ) {
            registerValue( from2++, field );
        }
    }

    private void registerValue( long value, String field ) {
        fieldRanges.computeIfAbsent( value, f -> new HashSet<>() ).add( field );
    }

    private long[] processTicket( String ticketStr ) {
        return Arrays.stream( ticketStr.split( "," ) )
                     .mapToLong( Long::parseLong )
                     .toArray();
    }

    private void narrowFieldsSet( long[] ticket, List<Set<String>> fieldPositions ) {
        for ( int index = 0; index < ticket.length; index++ ) {
            fieldPositions.get( index ).retainAll( fieldRanges.get( ticket[index] ) );
        }
    }


    private static void debug( String msg, Object... args ) {
        System.out.println( String.format( msg, args ) );
    }
}
