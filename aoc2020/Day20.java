import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Day20 implements AocDay<Long, Long> {
    private static final int SIZE = 10;
    private static final int LAST = SIZE - 1;
    private static final int N = 0;
    private static final int E = 1;
    private static final int S = 2;
    private static final int W = 3;
    private static final char[][] MONSTER = {
            "                  # ".toCharArray(),
            "#    ##    ##    ###".toCharArray(),
            " #  #  #  #  #  #   ".toCharArray()
    };

    private Map<Integer, Set<Tile>> edges = new HashMap<>();
    private Map<Integer, Tile> tiles = new HashMap<>();
    private String fileName;
    private int fullImageSize;

    public static void main( String[] args ) {
        try {
            executeTasks( "c:\\tmp\\sample20-1.dat", 3, 20899048083289L, 273L );
            executeTasks( "c:\\tmp\\input20.dat", 12, null, null );
        } catch ( Throwable e ) {
            e.printStackTrace();
        }
    }

    public static void executeTasks( String fileName, int fullImageSize, Long expected1, Long expected2 ) {
        Day20 day = new Day20( fileName, fullImageSize );
        Utils.executeDay( day, expected1, expected2 );
    }

    public Day20( String file, int fullImageSize ) {
        this.fileName = file;
        this.fullImageSize = fullImageSize;
        List<String> lines = Utils.readLines( file );
        Iterator<String> linePtr = lines.iterator();
        while ( linePtr.hasNext() ) {
            int id = Integer.parseInt( linePtr.next().substring( 5, 9 ) );
            String[] image = new String[SIZE];
            for ( int index = 0; index < SIZE; ++index ) {
                image[index] = linePtr.next();
            }
            Tile tile = new Tile( id, image );
            tiles.put( id, tile );
            mapTile( tile );
            if ( linePtr.hasNext() ) {
                linePtr.next();
            }
        }
        if ( tiles.size() != fullImageSize * fullImageSize ) {
            throw new IllegalStateException(
                    "Bad number of tiles [" + tiles.size() + "] for image size " + fullImageSize
            );
        }
    }

    @Override
    public String sampleName() {
        return fileName;
    }

    public Long task1() {
        long prod = 1;
        for ( Tile tile : tiles.values() ) {
            int unmatchedCount = 0;
            for ( Edge edge : tile.edges ) {
                if ( edges.get( edge.normal ).size() == 1 ) {
                    unmatchedCount++;
                }
            }
            if ( unmatchedCount == 2 ) {
                prod *= tile.id;
            }
        }
        return prod;
    }

    public Long task2() {
        List<OrientedTile> orientedTiles = tiles.values()
                                                .stream()
                                                .flatMap( Tile::orientedTiles )
                                                .collect( Collectors.toList() );
        OrientedTile[][] fullImageTiles = new OrientedTile[fullImageSize][fullImageSize];
        // look for NW tile
        OrientedTile nwTile = findTile(
                orientedTiles,
                tile -> isUniqueEdge( tile.normal[N] ) && isUniqueEdge( tile.normal[W] )
        )
                .orElseThrow( () -> new IllegalStateException( "NW tile not found!" ) );
        purgeUsed( nwTile, orientedTiles );
        fullImageTiles[0][0] = nwTile;

        // Reconstruct N image side
        for ( int index = 1; index < fullImageSize; ++index ) {
            int lookForEdge = fullImageTiles[0][index - 1].normal[E];
            List<OrientedTile> found = findTiles( orientedTiles, t -> t.flipped[W] == lookForEdge );
            if ( found.isEmpty() ) {
                throw new IllegalStateException( "Can't find E tile for row 0 col " + index );
            }
            OrientedTile tile = found.get( 0 );
            fullImageTiles[0][index] = tile;
            purgeUsed( tile, orientedTiles );
        }

        // Reconstruct W image side
        for ( int index = 1; index < fullImageSize; ++index ) {
            int lookForEdge = fullImageTiles[index - 1][0].normal[S];
            List<OrientedTile> found = findTiles( orientedTiles, t -> t.flipped[N] == lookForEdge );
            if ( found.isEmpty() ) {
                throw new IllegalStateException( "Can't find N tile for row 0 col " + index );
            }
            OrientedTile tile = found.get( 0 );
            fullImageTiles[index][0] = tile;
            purgeUsed( tile, orientedTiles );
        }

        // Reconstruct rest of image
        for ( int row = 1; row < fullImageSize; ++row ) {
            for ( int col = 1; col < fullImageSize; ++col ) {
                int lookForEdgeN = fullImageTiles[row - 1][col].normal[S];
                int lookForEdgeW = fullImageTiles[row][col - 1].normal[E];
                List<OrientedTile> found = findTiles(
                        orientedTiles,
                        t -> t.flipped[N] == lookForEdgeN && t.flipped[W] == lookForEdgeW
                );
                if ( found.isEmpty() ) {
                    throw new IllegalStateException( "Can't find E tile for row " + row + " col " + col );
                }
                OrientedTile tile = found.get( 0 );
                fullImageTiles[row][col] = tile;
                purgeUsed( tile, orientedTiles );
            }
        }

        // Create full image
        int fullSideSize = fullImageSize * ( SIZE - 2 );
        char[][] fullImage = new char[fullSideSize][fullSideSize];
        for ( int row = 0; row < fullImageSize; ++row ) {
            for ( int col = 0; col < fullImageSize; ++col ) {
                putImage(
                        fullImage,
                        row * ( SIZE - 2 ),
                        col * ( SIZE - 2 ),
                        fullImageTiles[row][col]
                );
            }
        }

        // Apply monsters
        char[][] found = null;

        for ( int rotate = 0; rotate < 4; ++rotate ) {
            if ( applyMonster( fullImage, MONSTER ) ) {
                found = fullImage;
                break;
            }
            char[][] flippedH = flipImageH( fullImage );
            if ( applyMonster( flippedH, MONSTER ) ) {
                found = flippedH;
                break;
            }
            char[][] flippedV = flipImageV( fullImage );
            if ( applyMonster( flippedV, MONSTER ) ) {
                found = flippedV;
                break;
            }
            char[][] flippedHV = flipImageV( flippedH );
            if ( applyMonster( flippedHV, MONSTER ) ) {
                found = flippedHV;
                break;
            }
            fullImage = rotateImage( fullImage );
        }
        if ( found == null ) {
            throw new IllegalStateException( "Monsters not found" );
        }

        long count = 0;
        for ( char[] row : found ) {
            for ( char cell : row ) {
                if ( cell == '#' ) {
                    ++count;
                }
            }
        }
        return count;
    }

    private Optional<OrientedTile> findTile( List<OrientedTile> tiles, Predicate<OrientedTile> predicate ) {
        return tiles.stream().filter( predicate ).findAny();
    }

    private List<OrientedTile> findTiles( List<OrientedTile> tiles, Predicate<OrientedTile> predicate ) {
        return tiles.stream().filter( predicate ).collect( Collectors.toList() );
    }

    private void purgeUsed( OrientedTile used, List<OrientedTile> tiles ) {
        tiles.removeIf( tile -> tile.tileId == used.tileId );
    }

    private boolean isUniqueEdge( int edge ) {
        return edges.get( edge ).size() == 1;
    }

    private void mapTile( Tile tile ) {
        tile.edges()
            .forEach( edge -> edges.computeIfAbsent( edge, ignore -> new TreeSet<>() ).add( tile ) );
    }

    private static char[][] rotateImage( char[][] image ) {
        int rows = image.length;
        int cols = image[0].length;
        char[][] result = new char[cols][rows];
        for ( int row = 0; row < rows; ++row ) {
            for ( int col = 0; col < cols; ++col ) {
                result[col][rows - row - 1] = image[row][col];
            }
        }
        return result;
    }

    private static char[][] flipImageV( char[][] image ) {
        int rows = image.length;
        int cols = image[0].length;
        char[][] result = new char[rows][cols];
        for ( int row = 0; row < rows; ++row ) {
            for ( int col = 0; col < cols; ++col ) {
                result[rows - row - 1][col] = image[row][col];
            }
        }
        return result;
    }

    private static char[][] flipImageH( char[][] image ) {
        int rows = image.length;
        int cols = image[0].length;
        char[][] result = new char[rows][cols];
        for ( int row = 0; row < rows; ++row ) {
            for ( int col = 0; col < cols; ++col ) {
                result[row][cols - 1 - col] = image[row][col];
            }
        }
        return result;
    }

    private void putImage( char[][] fullImage, int row, int col, OrientedTile orientedTile ) {
        for ( int imageRow = 0; imageRow < SIZE - 2; ++imageRow ) {
            for ( int imageCol = 0; imageCol < SIZE - 2; ++imageCol ) {
                fullImage[row + imageRow][col + imageCol] = orientedTile.image[imageRow + 1][imageCol + 1];
            }
        }
    }

    private boolean applyMonster( char[][] fullImage, char[][] monster ) {
        boolean found = false;
        int scanRows = fullImage.length - monster.length;
        int scanCols = fullImage[0].length - monster[0].length;
        for ( int row = 0; row < scanRows; ++row ) {
            for ( int col = 0; col < scanCols; ++col ) {
                found |= applyMonsterAt( fullImage, row, col, monster );
            }
        }
        return found;
    }

    private boolean applyMonsterAt( char[][] fullImage, int row, int col, char[][] monster ) {
        int rows = monster.length;
        int cols = monster[0].length;
        for ( int monsterRow = 0; monsterRow < rows; ++monsterRow ) {
            for ( int monsterCol = 0; monsterCol < cols; ++monsterCol ) {
                if ( monster[monsterRow][monsterCol] == '#' && fullImage[row + monsterRow][col + monsterCol] != '#' ) {
                    return false;
                }
            }
        }
        for ( int monsterRow = 0; monsterRow < rows; ++monsterRow ) {
            for ( int monsterCol = 0; monsterCol < cols; ++monsterCol ) {
                if ( monster[monsterRow][monsterCol] == '#' ) {
                    fullImage[row + monsterRow][col + monsterCol] = 'O';
                }
            }
        }
        return true;
    }

    private static class Tile implements Comparable<Tile> {
        private final int id;
        private final char[][] image;
        private final Edge[] edges = new Edge[]{ new Edge(), new Edge(), new Edge(), new Edge() };

        private Tile( int id, String[] image ) {
            this.id = id;
            this.image = new char[SIZE][];
            for ( int row = 0; row < SIZE; ++row ) {
                this.image[row] = image[row].toCharArray();
            }
            for ( int index = 0; index < SIZE; ++index ) {
                edges[N].update( index, image[0].charAt( LAST - index ) );
                edges[E].update( index, image[LAST - index].charAt( LAST ) );
                edges[S].update( index, image[LAST].charAt( index ) );
                edges[W].update( index, image[index].charAt( 0 ) );
            }
        }

        @Override
        public int compareTo( Tile o ) {
            return Integer.compare( id, o.id );
        }

        @Override
        public boolean equals( Object obj ) {
            return id == ( (Tile) obj ).id;
        }

        private IntStream edges() {
            IntStream.Builder builder = IntStream.builder();
            for ( Edge edge : edges ) {
                builder.add( edge.normal ).add( edge.flipped );
            }
            return builder.build();
        }

        private Stream<OrientedTile> orientedTiles() {
            Stream.Builder<OrientedTile> builder = Stream.builder();
            char[][] tileImage = image;
            for ( int rotation = 0; rotation < 4; ++rotation ) {
                int[] normal = new int[4];
                int[] flipped = new int[4];
                for ( int edgeOff = 0; edgeOff < 4; ++edgeOff ) {
                    int edge = ( 4 - rotation + edgeOff ) % 4;
                    normal[edgeOff] = edges[edge].normal;
                    flipped[edgeOff] = edges[edge].flipped;
                }
                int[] mirrorV = new int[]{ flipped[S], flipped[E], flipped[N], flipped[W] };
                int[] mirrorVFlipped = new int[]{ normal[S], normal[E], normal[N], normal[W] };
                char[][] imgMirrorV = flipImageV( tileImage );
                int[] mirrorH = new int[]{ flipped[N], flipped[W], flipped[S], flipped[E] };
                int[] mirrorHFlipped = new int[]{ normal[N], normal[W], normal[S], normal[E] };
                char[][] imgMirrorH = flipImageH( tileImage );
                int[] mirrorHV = new int[]{ normal[S], normal[W], normal[N], normal[E] };
                int[] mirrorHVFlipped = new int[]{ flipped[S], flipped[W], flipped[N], flipped[E] };
                char[][] imgMirrorHV = flipImageV( imgMirrorH );
                builder.add( new OrientedTile( id, tileImage, normal, flipped ) )
                       .add( new OrientedTile( id, imgMirrorV, mirrorV, mirrorVFlipped ) )
                       .add( new OrientedTile( id, imgMirrorH, mirrorH, mirrorHFlipped ) )
                       .add( new OrientedTile( id, imgMirrorHV, mirrorHV, mirrorHVFlipped ) );
                tileImage = rotateImage( tileImage );
            }
            return builder.build();
        }
    }

    private static class Edge {
        private int normal;
        private int flipped;

        private void update( int bit, char pixel ) {
            if ( pixel == '#' ) {
                normal |= 1 << bit;
                flipped |= 1 << ( LAST - bit );
            }
        }
    }

    private static class OrientedTile {
        private final long tileId;
        private final int[] normal;
        private final int[] flipped;
        private final char[][] image;

        public OrientedTile(
                long tileId,
                char[][] image,
                int[] normal,
                int[] flipped
        ) {
            this.tileId = tileId;
            this.image = image;
            this.normal = normal;
            this.flipped = flipped;
        }

        @Override
        public String toString() {
            StringBuilder result = new StringBuilder(
                    String.format(
                            "id=%d N=%s E=%s S=%s W=%s\n",
                            tileId,
                            Utils.bin( normal[N], SIZE ),
                            Utils.bin( normal[E], SIZE ),
                            Utils.bin( normal[S], SIZE ),
                            Utils.bin( normal[W], SIZE )
                    )
            );
            for ( char[] chars : image ) {
                result.append( new String( chars ) ).append( '\n' );
            }
            return result.toString();
        }
    }
}
