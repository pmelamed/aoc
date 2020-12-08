import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.OptionalInt;
import java.util.Set;
import java.util.stream.Collectors;

public class Day8 {
    private static class Instruction {
        private String operation;
        private int operand;

        public Instruction( String code ) {
            operation = code.substring( 0, 3 );
            operand = Integer.parseInt( code.substring( 5 ) );
            if ( code.toCharArray()[4] == '-' ) {
                operand = -operand;
            }
        }

        private boolean swap() {
            switch ( operation ) {
                case "nop":
                    operation = "jmp";
                    return true;
                case "jmp":
                    operation = "nop";
                    return true;
            }
            return false;
        }
    }

    private List<Instruction> program;

    public static void main( String[] args ) {
        try {
            new Day8( "c:\\tmp\\sample08-1.dat" ).doTasks();
            new Day8( "c:\\tmp\\input08.dat" ).doTasks();
        } catch ( Throwable e ) {
            e.printStackTrace();
        }
    }

    public Day8( String file ) throws IOException {
        program = Files.lines( Paths.get( file ) )
                       .map( Instruction::new )
                       .collect( Collectors.toList() );
    }

    private void doTasks() {
        System.out.println( "Task1 = " + task1() + " Task2 = " + task2() );
    }

    private int task1() {
        return dryRun( true ).orElse( -1 );
    }

    private int task2() {
        for ( Instruction instruction : program ) {
            if ( instruction.swap() ) {
                OptionalInt result = dryRun( false );
                instruction.swap();
                if ( result.isPresent() ) {
                    return result.getAsInt();
                }
            }
        }
        return 0;
    }

    private OptionalInt dryRun( boolean retAccum ) {
        Set<Integer> visited = new HashSet<>();
        int accum = 0;
        int ip = 0;
        while ( ip < program.size() && !visited.contains( ip ) ) {
            visited.add( ip );
            final Instruction op = program.get( ip );
            switch ( op.operation ) {
                case "nop":
                    ++ip;
                    break;
                case "acc":
                    accum += op.operand;
                    ++ip;
                    break;
                case "jmp":
                    ip += op.operand;
                    break;
            }
        }
        return retAccum || ip >= program.size() ? OptionalInt.of( accum ) : OptionalInt.empty();
    }


}
