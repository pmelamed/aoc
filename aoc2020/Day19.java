import java.io.IOException;
import java.util.*;

public class Day19 {

    private Map<Integer, Rule> rules = new TreeMap<>();
    private List<String> messages = new ArrayList<>();

    public static void main( String[] args ) {
        try {
            // executeTasks( "c:\\tmp\\sample17-1.dat", 112L, 848L );
            executeTasks( "c:\\tmp\\input19.dat", null, null );
        } catch ( Throwable e ) {
            e.printStackTrace();
        }
    }

    public static void executeTasks( String fileName, Long assert1, Long assert2 ) throws IOException {
        Day19 day = new Day19( fileName );
        long result1 = day.task1();
        long result2 = day.task2();
        Utils.debug( "%s: %d %d", fileName, result1, result2 );
        if ( assert1 != null && result1 != assert1 ) {
            Utils.debug( "Failed on %s task #1: %d instead of %d", fileName, result1, assert1 );
        }
        if ( assert2 != null && result2 != assert2 ) {
            Utils.debug( "Failed on %s task #2: %d instead of %d", fileName, result2, assert2 );
        }
    }

    public Day19( String file ) {
        List<String> lines = Utils.readLines( file );
        Iterator<String> lineIterator = lines.iterator();
        String line = lineIterator.next();
        while ( !line.isEmpty() ) {
            String[] numberAndRule = line.split( ": " );
            Rule rule;
            if ( numberAndRule[1].charAt( 0 ) == '"' ) {
                rule = new SimpleRule( numberAndRule[1].charAt( 1 ) );
            } else {
                rule = new ComplexRule( numberAndRule[1] );
            }
            rules.put( Integer.parseInt( numberAndRule[0] ), rule );
            line = lineIterator.next();
        }
        while ( lineIterator.hasNext() ) {
            messages.add( lineIterator.next() );
        }
    }

    private long task1() {
        Set<String> match0 = rules.get( 0 ).getAll();
        return messages.stream().filter( match0::contains ).count();
    }

    private long task2() {
        Set<String> all42 = rules.get( 42 ).getAll();
        Set<String> all31 = rules.get( 31 ).getAll();
        Rule0 rule0 = new Rule0( all42, all31 );
        return messages.stream().filter( rule0::matches ).count();
    }

    interface Rule {
        Set<String> getAll();
    }

    private static class SimpleRule implements Rule {
        private Set<String> match;

        public SimpleRule( char match ) {
            this.match = Collections.singleton( new String( new char[]{ match } ) );
        }

        @Override
        public Set<String> getAll() {
            return match;
        }
    }

    private class ComplexRule implements Rule {
        private String rule;
        private Set<String> matches;

        public ComplexRule( String rule ) {
            this.rule = rule.trim();
        }

        @Override
        public Set<String> getAll() {
            if ( matches == null ) {
                String[] subRules = rule.split( "\\|" );
                matches = new HashSet<>( parseSubRule( subRules[0] ) );
                if ( subRules.length > 1 ) {
                    matches.addAll( parseSubRule( subRules[1] ) );
                }
            }
            return matches;
        }

        private Set<String> parseSubRule( String subRule ) {
            String[] parents = subRule.trim().split( " " );
            Set<String> first = rules.get( Integer.parseInt( parents[0] ) ).getAll();
            if ( parents.length == 1 ) {
                return first;
            }
            Set<String> second = rules.get( Integer.parseInt( parents[1] ) ).getAll();
            Set<String> result = new HashSet<>();
            for ( String firstStr : first ) {
                for ( String secondStr : second ) {
                    result.add( firstStr + secondStr );
                }
            }
            return result;
        }
    }

    private static class Rule0 implements Rule {
        private Set<String> all42;
        private Set<String> all31;

        public Rule0( Set<String> all42, Set<String> all31 ) {
            this.all42 = all42;
            this.all31 = all31;
        }

        public boolean matches( String message ) {
            for ( String str42 : all42 ) {
                if ( message.length() > str42.length() && message.startsWith( str42 ) ) {
                    String inner = message.substring( str42.length() );
                    if ( matches11( inner ) || matches( inner ) ) {
                        return true;
                    }
                }
            }
            return false;
        }

        private boolean matches11( String message ) {
            for ( String str42 : all42 ) {
                for ( String str31 : all31 ) {
                    int length42 = str42.length();
                    int length31 = str31.length();
                    if ( message.length() % ( length42 + length31 ) != 0 ) {
                        continue;
                    }
                    if ( message.startsWith( str42 ) && message.endsWith( str31 ) ) {
                        String inner = message.substring( length42, message.length() - length31 );
                        if ( inner.isEmpty() || matches11( inner ) ) {
                            return true;
                        }
                    }
                }
            }
            return false;
        }

        @Override
        public Set<String> getAll() {
            throw new IllegalStateException( "Rule 0 is cycled" );
        }
    }
}
