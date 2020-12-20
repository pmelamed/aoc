import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class Utils {
    static void debug( String msg, Object... args ) {
        System.out.println( String.format( msg, args ) );
    }

    static Stream<String> lines( String path ) {
        try {
            return Files.lines( Path.of( path ) );
        } catch ( IOException e ) {
            throw new RuntimeException( e.getMessage(), e );
        }
    }

    static List<String> readLines( String path ) {
        try {
            return Files.readAllLines( Path.of( path ) );
        } catch ( IOException e ) {
            throw new RuntimeException( e.getMessage(), e );
        }
    }

    public static <T1, T2> void executeDay( AocDay<T1, T2> day, T1 expected1, T2 expected2 ) {
        executeAndPrintAssert( day::task1, day.sampleName(), 1, expected1 );
        executeAndPrintAssert( day::task2, day.sampleName(), 2, expected2 );
    }

    private static <T> void executeAndPrintAssert( Supplier<T> task, String sampleName, int taskNo, T expected ) {
        System.out.print( sampleName + ": executing task #" + taskNo );
        long start = System.currentTimeMillis();
        T result = task.get();
        long time = System.currentTimeMillis() - start;
        System.out.print( " = " + result + " [" + time + "ms]" );
        if ( expected != null ) {
            if ( expected.equals( result ) ) {
                System.out.print( " == " + expected + " - OK!" );
            } else {
                System.out.print( " != " + expected + " - Failed!" );
            }
        }
        System.out.println();
    }

    public static String bin(long value, int digits) {
        return padLeft( Long.toString( value, 2 ), digits, '0' );
    }

    public static String padLeft( String str, int length, char ch ) {
        if ( str.length() >= length ) {
            return str;
        }
        StringBuilder result = new StringBuilder( length );
        for ( int i = str.length(); i < length; ++i ) {
            result.append( ch );
        }
        return result.append( str ).toString();
    }
}
