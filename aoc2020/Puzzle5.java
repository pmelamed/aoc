import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

public class Puzzle5 {
    public static void main( String[] args ) {
        try {
            final char[] busy = new char[1024];

            Arrays.fill( busy, '0' );
            int maxId = Files.lines( Path.of( "c:\\tmp\\input05.dat" ) )
                             .map( s -> s.replace( 'B', '1' )
                                         .replace( 'F', '0' )
                                         .replace( 'R', '1' )
                                         .replace( 'L', '0' ) )
                             .map( s -> Integer.valueOf( s, 2 ) )
                             .peek( id -> busy[id] = '1' )
                             .max( Integer::compare )
                             .orElseThrow( RuntimeException::new );
            int myId = String.valueOf( busy ).indexOf( "101" ) + 1;
            System.out.println( "Max ID: " + maxId + " My ID: " + myId );
        } catch ( Throwable e ) {
            e.printStackTrace();
        }
    }
}
