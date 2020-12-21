import java.util.*;
import java.util.stream.Collectors;

public class Day21 implements AocDay<Integer, String> {

    private String name;
    private Map<String, Set<String>> allergensMap;
    private Set<String> allIngredients;
    Map<String, Integer> ingredientCount;

    public static void main( String[] args ) {
        try {
            executeTasks( "c:\\tmp\\sample21-1.dat", 5, "mxmxvkd,sqjhc,fvjkl" );
            executeTasks( "c:\\tmp\\input21.dat", null, null );
        } catch ( Throwable e ) {
            e.printStackTrace();
        }
    }

    public static void executeTasks( String fileName, Integer expected1, String expected2 ) {
        Day21 day = new Day21( fileName );
        Utils.executeDay( day, expected1, expected2 );
    }

    public Day21( String file ) {
        name = file;
        allergensMap = new TreeMap<>();
        allIngredients = new TreeSet<>();
        ingredientCount = new TreeMap<>();
        Utils.lines( file ).forEach(
                line -> {
                    int allergensStart = line.indexOf( '(' );
                    Set<String> ingredients = Set.of( line.substring( 0, allergensStart - 1 ).split( " " ) );
                    ingredients.forEach( ingredient -> {
                        ingredientCount.putIfAbsent( ingredient, 0 );
                        ingredientCount.compute( ingredient, ( key, count ) -> count + 1 );
                    } );
                    allIngredients.addAll( ingredients );
                    String[] allergensList = line.substring( allergensStart + "(contains ".length(), line.length() - 1 )
                                                 .split( ", " );
                    for ( String allergen : allergensList ) {
                        if ( allergensMap.containsKey( allergen ) ) {
                            allergensMap.get( allergen ).retainAll( ingredients );
                        } else {
                            allergensMap.put( allergen, new TreeSet<>( ingredients ) );
                        }
                    }
                }
        );

        // Reduce set of ingredients for allergens
        List<Set<String>> allAllergens = new ArrayList<>( allergensMap.values() );
        // Can be > 1 but need to assert single ingredient state for the last
        while ( allAllergens.size() > 0 ) {
            Set<String> determined = allAllergens.stream()
                                                 .filter( set -> set.size() == 1 )
                                                 .findAny()
                                                 .orElseThrow( () -> new IllegalStateException(
                                                         "Set with 1 ingredient not found" ) );
            allAllergens.remove( determined );
            String ingredient = determined.iterator().next();
            allAllergens.forEach( set -> set.remove( ingredient ) );
        }
    }

    @Override
    public String sampleName() {
        return name;
    }

    public Integer task1() {
        TreeSet<String> clean = new TreeSet<>( allIngredients );
        allergensMap.values()
                    .stream()
                    .map( set -> set.iterator().next() )
                    .forEach( clean::remove );
        return ingredientCount.entrySet()
                              .stream()
                              .filter( entry -> clean.contains( entry.getKey() ) )
                              .mapToInt( Map.Entry::getValue )
                              .sum();
    }

    public String task2() {
        return allergensMap.values()
                    .stream()
                    .map( set -> set.iterator().next() )
                    .collect( Collectors.joining( "," ));
    }
}
