import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.LongStream;

public class Day14 {

    private String fileName;
    private Map<Long, Long> memory;
    private long maskAnd;
    private long maskOr;
    private long[] floatingBits = new long[36];
    private int floatingCount;

    public static void main( String[] args ) {
        try {
            // new Day14( "c:\\tmp\\sample14-3.dat" ).doTasks();
            new Day14( "c:\\tmp\\input14.dat" ).doTasks();
        } catch ( Throwable e ) {
            e.printStackTrace();
        }
    }

    public Day14( String file ) {
        this.fileName = file;
    }

    private void doTasks() throws IOException {
        long result1 = doTask(
                this::applyMask,
                this::applyMem
        );
        long result2 = doTask(
                this::parseFloatingMask,
                this::applyFloatingMem
        );
        System.out.println( "Task1 = " + result1 + " Task2 = " + result2 );
    }

    private long doTask(
            Consumer<String> maskProcessor,
            BiConsumer<Long, Long> memProcessor
    ) throws IOException {
        memory = new HashMap<>();
        Files.lines( Paths.get( fileName ) )
             .forEach( line -> applyLine( line, maskProcessor, memProcessor ) );
        return memory.values().stream().reduce( Long::sum ).orElse( -1L );
    }

    private void applyLine(
            String line,
            Consumer<String> maskProcessor,
            BiConsumer<Long, Long> memProcessor
    ) {
        try {
            if ( line.startsWith( "mask" ) ) {
                maskProcessor.accept( line.substring( 7 ) );
            } else {
                memProcessor.accept(
                        Long.parseLong( line.substring( line.indexOf( '[' ) + 1, line.indexOf( ']' ) ) ),
                        Long.parseLong( line.substring( line.indexOf( '=' ) + 2 ) )
                );
            }
        } catch ( Exception e ) {
            debug( "Exception in applyLine: %s", line );
            e.printStackTrace();
        }
    }

    private void applyMask( String mask ) {
        maskAnd = Long.parseLong( mask.replace( 'X', '1' ), 2 );
        maskOr = Long.parseLong( mask.replace( 'X', '0' ), 2 );
    }

    private void applyMem( long addr, long value ) {
        memory.put( addr, value & maskAnd | maskOr );
    }

    private void parseFloatingMask( String mask ) {
        maskOr = Long.parseLong( mask.replace( 'X', '0' ), 2 );
        floatingCount = 0;
        long bit = 1L;
        char[] maskChars = mask.toCharArray();
        for ( int index = 35; index >= 0; --index ) {
            if ( maskChars[index] == 'X' ) {
                floatingBits[floatingCount++] = bit;
            }
            bit <<= 1;
        }
    }

    private void applyFloatingMem( long addr, long value ) {
        LongStream.range( 0, 1L << floatingCount )
                  .map( perm -> applyAddressPermutation(addr, perm ) )
                  .forEach( a -> {
                      memory.put( a | maskOr, value );
                  });
    }

    private long applyAddressPermutation( long addr, long permutation ) {
        for (int index = 0; index < floatingCount; ++index ) {
            if( (permutation & 1L) == 0) {
                addr &= ~floatingBits[index];
            } else {
                addr |= floatingBits[index];
            }
            permutation >>= 1;
        }
        return addr;
    }

    private static void debug( String msg, Object... args ) {
        System.out.println( String.format( msg, args ) );
    }
}
