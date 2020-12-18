import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

public class Day18 {

    private String rest;

    public static void main( String[] args ) {
        try {
            // executeTasks( "c:\\tmp\\sample17-1.dat", 112L, 848L );
            executeTasks( "c:\\tmp\\input18.dat", null, null );
        } catch ( Throwable e ) {
            e.printStackTrace();
        }
    }

    public static void executeTasks( String fileName, Long assert1, Long assert2 ) throws IOException {
        Day18 day = new Day18();
        long result1 = day.task1( fileName );
        long result2 = day.task2( fileName );
        debug( "%s: %d %d", fileName, result1, result2 );
    }

    public Day18() {
    }

    private long task1( String filename ) throws IOException {
        assertResult(
                "Sample 1.1",
                calculateExpr( "2 * 3 + (4 * 5)", this::calculateRaw ),
                26
        );
        assertResult(
                "Sample 1.2",
                calculateExpr( "5 + (8 * 3 + 9 + 3 * 4 * 3)", this::calculateRaw ),
                437
        );
        assertResult(
                "Sample 1.3",
                calculateExpr( "5 * 9 * (7 * 3 * 3 + 9 * 3 + (8 + 6 * 4))", this::calculateRaw ),
                12240
        );
        assertResult(
                "Sample 1.4",
                calculateExpr( "((2 + 4 * 9) * (6 + 9 * 8 + 6) + 6) + 2 + 4 * 2", this::calculateRaw ),
                13632
        );
        return Files.lines( Path.of( filename ) )
                    .mapToLong( expr -> calculateExpr( expr, this::calculateRaw ) )
                    .sum();
    }

    private long task2( String filename ) throws IOException {
        assertResult(
                "Sample 2.1",
                calculateExpr( "1 + 2 * 3 + 4 * 5 + 6", this::calculateWithPrecedence ),
                231
        );
        assertResult(
                "Sample 2.2",
                calculateExpr( "1 + (2 * 3) + (4 * (5 + 6))", this::calculateWithPrecedence ),
                51
        );
        assertResult(
                "Sample 2.3",
                calculateExpr( "2 * 3 + (4 * 5)", this::calculateWithPrecedence ),
                46
        );
        assertResult(
                "Sample 2.4",
                calculateExpr( "5 + (8 * 3 + 9 + 3 * 4 * 3)", this::calculateWithPrecedence ),
                1445
        );
        assertResult(
                "Sample 2.5",
                calculateExpr( "5 * 9 * (7 * 3 * 3 + 9 * 3 + (8 + 6 * 4))", this::calculateWithPrecedence ),
                669060
        );
        assertResult(
                "Sample 2.6",
                calculateExpr( "((2 + 4 * 9) * (6 + 9 * 8 + 6) + 6) + 2 + 4 * 2", this::calculateWithPrecedence ),
                23340
        );
        return Files.lines( Path.of( filename ) )
                    .mapToLong( expr -> calculateExpr( expr, this::calculateWithPrecedence ) )
                    .sum();
    }

    private long calculateExpr( String expression, BiFunction<List<Long>, List<Character>, Long> calculator ) {
        rest = expression;
        return parseExpr( calculator );
    }

    private long parseExpr( BiFunction<List<Long>, List<Character>, Long> calculator ) {
        List<Long> values = new ArrayList<>();
        values.add( parseSubExpr( calculator ) );
        List<Character> ops = new ArrayList<>();
        ops.add( ' ' );
        while ( !rest.isEmpty() && !rest.startsWith( ")" ) ) {
            ops.add( rest.charAt( 1 ) );
            rest = rest.substring( 3 );
            values.add( parseSubExpr( calculator ) );
        }
        return calculator.apply( values, ops );
    }

    private long parseSubExpr( BiFunction<List<Long>, List<Character>, Long> calculator ) {
        if ( rest.startsWith( "(" ) ) {
            rest = rest.substring( 1 );
            long result = parseExpr( calculator );
            rest = rest.substring( 1 );
            return result;
        }
        int spaceIndex = rest.indexOf( ' ' );
        int parIndex = rest.indexOf( ')' );
        String value;
        if ( spaceIndex == -1 && parIndex == -1 ) {
            value = rest;
            rest = "";
        } else {
            spaceIndex = spaceIndex == -1 ? Integer.MAX_VALUE : spaceIndex;
            parIndex = parIndex == -1 ? Integer.MAX_VALUE : parIndex;
            int index = Math.min( parIndex, spaceIndex );
            value = rest.substring( 0, index );
            rest = rest.substring( index );
        }
        return Long.parseLong( value );
    }

    private long calculateRaw( List<Long> values, List<Character> ops ) {
        long sum = values.get( 0 );
        for ( int index = 1; index < values.size(); ++index ) {
            switch ( ops.get( index ) ) {
                case '+':
                    sum += values.get( index );
                    break;
                case '*':
                    sum *= values.get( index );
                    break;
            }
        }
        return sum;
    }

    private long calculateWithPrecedence( List<Long> values, List<Character> ops ) {
        for ( int index = 1; index < values.size(); ) {
            if ( ops.get( index ) == '+' ) {
                values.set( index - 1, values.get( index - 1 ) + values.get( index ) );
                values.remove( index );
                ops.remove( index );
            } else {
                ++index;
            }
        }
        return values.stream().mapToLong( Long::valueOf ).reduce( 1L, ( l, r ) -> r * l );
    }

    private static void debug( String msg, Object... args ) {
        System.out.println( String.format( msg, args ) );
    }

    private static void assertResult( String test, long result, long expected ) {
        if ( result != expected ) {
            debug( "Failed: expr = %s, expected = %d, actual = %d", test, expected, result );
        } else {
            debug( "OK: expr = %s, expected = %d, actual = %d", test, expected, result );
        }
    }
}
