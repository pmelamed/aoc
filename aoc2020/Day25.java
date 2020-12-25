public class Day25 implements AocDay<Long, Long> {
    public static final long MODULUS = 20201227L;
    public static final long SUBJECT = 7L;

    private long pkCard;
    private long pkDoor;

    public static void main( String[] args ) {
        try {
            executeTasks( 5764801L, 17807724, 14897079L, null );
            executeTasks( 8987316L, 14681524, null, null );
        } catch ( Throwable e ) {
            e.printStackTrace();
        }
    }

    public static void executeTasks( long pkCard, long pkDoor, Long expected1, Long expected2 ) {
        Utils.executeDay( new Day25( pkCard, pkDoor ), expected1, expected2 );
    }

    public Day25( long pkCard, long pkDoor ) {
        this.pkCard = pkCard;
        this.pkDoor = pkDoor;
    }

    @Override
    public String sampleName() {
        return String.format( "CARD-PK=%d/DOOR-PK=%d", pkCard, pkDoor );
    }

    public Long task1() {
        long loopResult = 1;
        int loops = 0;
        while ( loopResult != pkCard ) {
            loopResult = loopResult * SUBJECT % MODULUS;
            ++loops;
        }
        loopResult = 1;
        for ( int loop = 0; loop < loops; ++loop ) {
            loopResult = loopResult * pkDoor % MODULUS;
        }
        return loopResult;
    }

    public Long task2() {
        return null;
    }
}
