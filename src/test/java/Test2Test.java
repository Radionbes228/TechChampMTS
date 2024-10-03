import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class Test2Test {

    private static final Test2 test2  = new Test2();

    private static Stream<Arguments> exceptionParseTestCases() {
        return Stream.of(
                Arguments.of("aBcDeFgHiMjKlMnOpQrSTuVwXyZMTS", 1),
                Arguments.of("tHeMtSsToRyTMS", 1),
                Arguments.of("MSTabc", 0),
                Arguments.of("HelloMTSWorld", 1),
                Arguments.of("mTsIsFunSTM", 1),
                Arguments.of("TmSsMtSMT", 1),
                Arguments.of("aBcDeFgHiJkLmNoPqRsTuVwXyZmTsTSM", 1),
                Arguments.of("MTSMTSMTS", 1),
                Arguments.of("tESTmTsMST", 1),
                Arguments.of("MtsAreCoolSMT", 1),
                Arguments.of("HeLlOwOrLdMTS", 1),
                Arguments.of("abcdefghijklmMnOpqrstuvwxyZMTS", 1),
                Arguments.of("MTSisTheBestTMS", 1),
                Arguments.of("tHisIsAtESTSTM", 0),
                Arguments.of("MTS123MTS", 1),
                Arguments.of("aBCdEfGhIjKlMnOpQrStUvWxYzTSM", 1),
                Arguments.of("MTSabcdMTS", 1),
                Arguments.of("tHeMtSsToRyMST", 1),
                Arguments.of("MTSisFunToPlaySTM", 1),
                Arguments.of("aBcDeFgHiJkLmNoPqRsTuVwXyZmTsTMS", 1)
        );
    }


    @ParameterizedTest
    @MethodSource("exceptionParseTestCases")
    void test2YY(String input, int expected) {
        assertEquals(expected, test2.test2(input));
    }
}