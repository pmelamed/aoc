import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day6 {

    public static void main( String[] args ) {
        try {
            final Day6 day6 = new Day6();
            System.out.println( "Task1 = " + day6.task( ( s1, s2 ) -> day6.setOp( s1, s2, Set::addAll ) ) );
            System.out.println( "Task2 = " + day6.task( ( s1, s2 ) -> day6.setOp( s1, s2, Set::retainAll ) ) );
        } catch ( Throwable e ) {
            e.printStackTrace();
        }
    }

    private int task( BinaryOperator<Set<Character>> reducer ) throws IOException {
        return multilines( "c:\\tmp\\input06.dat" ).map( ls -> processSet( ls, reducer ) )
                                                   .reduce( Integer::sum )
                                                   .orElseThrow( () -> new RuntimeException( "No solution!" ) );
    }

    private int processSet( Stream<String> linesSet, BinaryOperator<Set<Character>> reducer ) {
        return linesSet.map( this::lineChars )
                       .reduce( reducer )
                       .map( Set::size )
                       .orElseThrow( () -> new RuntimeException( "No solution!" ) );
    }

    private Set<Character> lineChars( String line ) {
        return line.chars().mapToObj( c -> (char) c ).collect( Collectors.toSet() );
    }

    private <T> Set<T> setOp( Set<T> s1, Set<T> s2, BiConsumer<Set<T>, Set<T>> op ) {
        if ( s1 == null ) {
            return s2;
        }
        if ( s2 == null ) {
            return s1;
        }
        op.accept( s1, s2 );
        return s1;
    }

    private Stream<Stream<String>> multilines( String file ) throws IOException {
        return jointMultiLines( file ).map( jointLine -> Arrays.stream( jointLine.split( "\n" ) ) );
    }

    private Stream<String> jointMultiLines( String file ) throws IOException {
        return Arrays.stream(
                Files.lines( Paths.get( file ) )
                     .collect( Collectors.joining( "\n" ) ).split( "\n\n" )
        );
    }
}
