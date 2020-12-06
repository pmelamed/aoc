import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Puzzle6 {
    private static final Set<Character> FULL_SET = IntStream.range( 'a', 'z' + 1 )
                                                            .mapToObj( b -> (char) b )
                                                            .collect( Collectors.toSet() );

    public static void main( String[] args ) {
        part2();
    }

    private static void part1() {
        try {
            final Set<Character> answers = new HashSet<>();
            int sum = Files.lines( Path.of( "c:\\tmp\\input06.dat" ) )
                           .map( s -> {
                               if ( s.isEmpty() ) {
                                   final int size = answers.size();
                                   answers.clear();
                                   return size;
                               } else {
                                   answers.addAll(
                                           s.chars()
                                            .mapToObj( b -> (char) b )
                                            .collect( Collectors.toSet() )
                                   );
                                   return 0;
                               }
                           } )
                           .reduce( Integer::sum )
                           .orElseThrow( RuntimeException::new );
            System.out.println( "Sum: " + ( sum + answers.size() ) );
        } catch ( Throwable e ) {
            e.printStackTrace();
        }
    }

    private static void part2() {
        try {
            final Set<Character> answers = new HashSet<>( FULL_SET );
            int sum = Files.lines( Path.of( "c:\\tmp\\input06.dat" ) )
                           .map( s -> {
                               if ( s.isEmpty() ) {
                                   final int size = answers.size();
                                   answers.addAll( FULL_SET );
                                   return size;
                               } else {
                                   answers.retainAll(
                                           s.chars().mapToObj( b -> (char) b ).collect( Collectors.toSet() )
                                   );
                                   return 0;
                               }
                           } )
                           .reduce( Integer::sum )
                           .orElseThrow( RuntimeException::new );
            System.out.println( "Sum: " + ( sum + answers.size() ) );
        } catch ( Throwable e ) {
            e.printStackTrace();
        }
    }
}
