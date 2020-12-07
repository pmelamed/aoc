import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class Day7 {
    private static class Rule {
        private String outer;
        private String inner;
        private int quantity;

        public Rule( String outer, String inner, int quantity ) {
            this.outer = outer;
            this.inner = inner;
            this.quantity = quantity;
        }

        @Override
        public boolean equals( Object o ) {
            if ( this == o ) {
                return true;
            }
            if ( o == null || getClass() != o.getClass() ) {
                return false;
            }
            Rule rule = (Rule) o;
            return outer.equals( rule.outer ) &&
                    inner.equals( rule.inner );
        }

        @Override
        public int hashCode() {
            return Objects.hash( outer, inner );
        }
    }

    private Map<String, List<Rule>> innerMap = new HashMap<>();
    private Map<String, List<Rule>> outerMap = new HashMap<>();

    public static void main( String[] args ) {
        try {
            new Day7( "c:\\tmp\\sample07.dat" ).doTasks();
            new Day7( "c:\\tmp\\sample07-2.dat" ).doTasks();
            new Day7( "c:\\tmp\\input07.dat" ).doTasks();
        } catch ( Throwable e ) {
            e.printStackTrace();
        }
    }

    public Day7( String file ) throws IOException {
        Files.lines( Paths.get( file ) ).forEach( this::processLine );
    }

    private void doTasks() throws IOException {
        System.out.println( "Task1 = " + task1() + " Task2 = " + task2() );
    }

    private int task1() {
        Set<String> parents = new HashSet<>();
        addOuters( parents, "shiny gold" );
        return parents.size();
    }

    private int task2() {
        return getInners( "shiny gold" ) - 1;
    }

    private void processLine( String line ) {
        final String[] outerColorRule = line.split( " ", 5 );
        String outerColor = outerColorRule[0] + " " + outerColorRule[1];
        final String[] innerColorRules = outerColorRule[4].split( "," );
        for ( String rule : innerColorRules ) {
            processInnerColor( outerColor, rule );
        }
    }

    private void processInnerColor( String outerColor, String rule ) {
        if ( rule.startsWith( "no " ) ) {
            return;
        }
        final String[] ruleParts = rule.trim().split( " ", 4 );
        register( new Rule(
                outerColor,
                ruleParts[1] + " " + ruleParts[2],
                Integer.parseInt( ruleParts[0] )
        ) );
    }

    private void register( Rule rule ) {
        innerMap.computeIfAbsent( rule.inner, key -> new ArrayList<>() ).add( rule );
        outerMap.computeIfAbsent( rule.outer, key -> new ArrayList<>() ).add( rule );
    }

    private int getInners( String color ) {
        final List<Rule> colorRules = outerMap.get( color );
        if ( colorRules == null ) {
            return 1;
        }
        int count = 1;
        for ( Rule rule : colorRules ) {
            count += rule.quantity * getInners( rule.inner );
        }
        return count;
    }

    private void addOuters( Set<String> parents, String color ) {
        final List<Rule> colorRules = innerMap.get( color );
        if ( colorRules == null ) {
            return;
        }
        for ( Rule rule : colorRules ) {
            if ( !parents.contains( rule.outer ) ) {
                parents.add( rule.outer );
                addOuters( parents, rule.outer );
            }
        }
    }
}
