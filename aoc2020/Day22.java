import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.stream.Collectors;

public class Day22 implements AocDay<Long, Long> {

    private String name;
    private LinkedList<Integer> initialDeck1;
    private LinkedList<Integer> initialDeck2;

    public static void main( String[] args ) {
        try {
            executeTasks( "c:\\tmp\\sample22-1.dat", 306L, 291L );
            executeTasks( "c:\\tmp\\input22.dat", null, null );
        } catch ( Throwable e ) {
            e.printStackTrace();
        }
    }

    public static void executeTasks( String fileName, Long expected1, Long expected2 ) {
        Utils.executeDay( new Day22( fileName ), expected1, expected2 );
    }

    public Day22( String file ) {
        name = file;
        Iterator<String> lines = Utils.readLines( file ).iterator();
        lines.next();
        initialDeck1 = new LinkedList<>();
        String line = lines.next();
        while ( !line.isEmpty() ) {
            initialDeck1.add( Integer.parseInt( line ) );
            line = lines.next();
        }

        initialDeck2 = new LinkedList<>();
        lines.next();
        while ( lines.hasNext() ) {
            initialDeck2.add( Integer.parseInt( lines.next() ) );
        }
    }

    @Override
    public String sampleName() {
        return name;
    }

    public Long task1() {
        LinkedList<Integer> deck1 = new LinkedList<>( initialDeck1 );
        LinkedList<Integer> deck2 = new LinkedList<>( initialDeck2 );
        while ( !( deck1.isEmpty() || deck2.isEmpty() ) ) {
            int card1 = deck1.pollFirst();
            int card2 = deck2.pollFirst();
            if ( card1 > card2 ) {
                deck1.addLast( card1 );
                deck1.addLast( card2 );
            } else {
                deck2.addLast( card2 );
                deck2.addLast( card1 );
            }
        }
        return countDeckValue( deck1.isEmpty() ? deck2 : deck1 );
    }

    public Long task2() {
        LinkedList<Integer> deck1 = new LinkedList<>( initialDeck1 );
        LinkedList<Integer> deck2 = new LinkedList<>( initialDeck2 );
        int winner = playGame( deck1, deck2 );
        return countDeckValue( winner == 1 ? deck1 : deck2 );
    }

    private Long countDeckValue( LinkedList<Integer> deck ) {
        int value = deck.size();
        long result = 0;
        for ( int card : deck ) {
            result += value * card;
            --value;
        }
        return result;
    }

    private int playGame( LinkedList<Integer> deck1, LinkedList<Integer> deck2 ) {
        LinkedList<RoundConfig> prevRounds = new LinkedList<>();
        while ( !( deck1.isEmpty() || deck2.isEmpty() ) ) {
            RoundConfig round = new RoundConfig( deck1, deck2 );
            if ( findRound( round, prevRounds ) ) {
                return 1;
            }
            prevRounds.addLast( round );
            int card1 = deck1.pollFirst();
            int card2 = deck2.pollFirst();
            if ( card1 <= deck1.size() && card2 <= deck2.size() ) {
                if ( playGame( copyDeck( deck1, card1 ), copyDeck( deck2, card2 ) ) == 1 ) {
                    deck1.addLast( card1 );
                    deck1.addLast( card2 );
                } else {
                    deck2.addLast( card2 );
                    deck2.addLast( card1 );
                }
            } else {
                if ( card1 > card2 ) {
                    deck1.addLast( card1 );
                    deck1.addLast( card2 );
                } else {
                    deck2.addLast( card2 );
                    deck2.addLast( card1 );
                }
            }
        }
        return deck1.isEmpty() ? 2 : 1;
    }

    private LinkedList<Integer> copyDeck( LinkedList<Integer> deck, int depth ) {
        return deck.stream()
                   .limit( depth )
                   .collect( Collectors.toCollection( LinkedList::new ) );
    }

    private boolean findRound( RoundConfig round, Collection<RoundConfig> prevRounds ) {
        return prevRounds.stream().anyMatch( round::isEqual );
    }

    private static class RoundConfig {
        private final int[] deck1;
        private final int[] deck2;

        public RoundConfig( Collection<Integer> deck1, Collection<Integer> deck2 ) {
            this.deck1 = deck1.stream().mapToInt( Integer::intValue ).toArray();
            this.deck2 = deck2.stream().mapToInt( Integer::intValue ).toArray();
        }

        private boolean isEqual( RoundConfig config ) {
            return config.deck1.length == deck1.length &&
                    ( Arrays.compare( config.deck1, deck1 ) == 0 || Arrays.compare( config.deck2, deck2 ) == 0 );
        }
    }
}
