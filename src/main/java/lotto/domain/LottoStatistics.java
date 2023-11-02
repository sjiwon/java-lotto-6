package lotto.domain;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

public class LottoStatistics {
    private final WinningLottoHolder winningLottoHolder;
    private final User user;
    private final Map<WinningRank, Integer> winningResult = new EnumMap<>(WinningRank.class);

    private LottoStatistics(final WinningLottoHolder winningLottoHolder, final User user) {
        this.winningLottoHolder = winningLottoHolder;
        this.user = user;

        initWinningResult();
        calculateLottoWinningResult();
    }

    public static LottoStatistics checkLottoResult(final WinningLottoHolder winningLottoHolder, final User user) {
        return new LottoStatistics(winningLottoHolder, user);
    }

    private void initWinningResult() {
        for (final WinningRank winningRank : WinningRank.values()) {
            winningResult.put(winningRank, 0); // 모든 Rank에 대한 당첨 횟수 초기화
        }
    }

    private void calculateLottoWinningResult() {
        for (final Lotto userLotto : user.getLottos()) {
            final int matchCount = winningLottoHolder.calculateLottoMatchCount(userLotto);
            final boolean hasBonus = winningLottoHolder.isBonusNumberInclude(userLotto);

            final WinningRank winningRank = WinningRank.of(matchCount, hasBonus);
            updateWinningResult(winningRank);
        }
    }

    private void updateWinningResult(final WinningRank winningRank) {
        winningResult.put(winningRank, winningResult.get(winningRank) + 1);
    }

    public BigDecimal calculateEarningRate() {
        final BigDecimal lottoPurchaseMoney = user.calculateLottoPurchaseMoney();
        final BigDecimal totalWinningMoney = calculateTotalWinningMoney();

        return totalWinningMoney
                .multiply(BigDecimal.valueOf(100)) // 백분율 계산
                .divide(lottoPurchaseMoney, 1, RoundingMode.HALF_UP);
    }

    private BigDecimal calculateTotalWinningMoney() {
        BigDecimal amount = BigDecimal.ZERO;
        for (final WinningRank winningRank : winningResult.keySet()) {
            final BigDecimal addPrize = calculateAddPrize(winningRank);
            amount = amount.add(addPrize);
        }
        return amount;
    }

    private BigDecimal calculateAddPrize(final WinningRank winningRank) {
        final int reward = winningRank.getReward();
        final int count = winningResult.get(winningRank);

        return BigDecimal.valueOf((long) reward * count);
    }

    public Map<WinningRank, Integer> getWinningResult() {
        return Collections.unmodifiableMap(winningResult);
    }
}
