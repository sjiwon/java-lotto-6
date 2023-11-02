package lotto.domain;

import camp.nextstep.edu.missionutils.Randoms;

import java.util.ArrayList;
import java.util.List;

public class DefaultLottoGenerator implements LottoGenerator {
    @Override
    public List<Integer> get() {
        return new ArrayList<>(Randoms.pickUniqueNumbersInRange(Lotto.LOWER_BOUND, Lotto.UPPER_BOUND, Lotto.TOTAL_SIZE));
    }
}
