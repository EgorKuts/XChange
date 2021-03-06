package org.knowm.xchange.kucoin.dto;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import org.knowm.xchange.currency.Currency;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.Order.OrderStatus;
import org.knowm.xchange.dto.Order.OrderType;
import org.knowm.xchange.dto.account.AccountInfo;
import org.knowm.xchange.dto.account.Balance;
import org.knowm.xchange.dto.account.FundingRecord;
import org.knowm.xchange.dto.account.Wallet;
import org.knowm.xchange.dto.marketdata.OrderBook;
import org.knowm.xchange.dto.marketdata.Ticker;
import org.knowm.xchange.dto.marketdata.Trade;
import org.knowm.xchange.dto.marketdata.Trades.TradeSortType;
import org.knowm.xchange.dto.meta.CurrencyMetaData;
import org.knowm.xchange.dto.meta.CurrencyPairMetaData;
import org.knowm.xchange.dto.meta.ExchangeMetaData;
import org.knowm.xchange.dto.trade.LimitOrder;
import org.knowm.xchange.dto.trade.OpenOrders;
import org.knowm.xchange.dto.trade.UserTrade;
import org.knowm.xchange.dto.trade.UserTrades;
import org.knowm.xchange.kucoin.dto.account.KucoinCoinBalance;
import org.knowm.xchange.kucoin.dto.account.KucoinWalletRecord;
import org.knowm.xchange.kucoin.dto.marketdata.KucoinCoin;
import org.knowm.xchange.kucoin.dto.marketdata.KucoinDealOrder;
import org.knowm.xchange.kucoin.dto.marketdata.KucoinOrderBook;
import org.knowm.xchange.kucoin.dto.marketdata.KucoinTicker;
import org.knowm.xchange.kucoin.dto.trading.*;

public class KucoinAdapters {

    public static String adaptCurrencyPair(CurrencyPair pair) {

        return pair.base.getCurrencyCode() + "-" + pair.counter.getCurrencyCode();
    }

    public static Ticker adaptTicker(KucoinTicker ticker) {

        return new Ticker.Builder()
                .currencyPair(
                        new CurrencyPair(
                                Currency.getInstance(ticker.getCoinType()),
                                Currency.getInstance(ticker.getCoinTypePair())))
                .bid(ticker.getBuy())
                .ask(ticker.getSell())
                .high(ticker.getHigh())
                .low(ticker.getLow())
                .last(ticker.getLastDealPrice())
                .volume(ticker.getVol())
                .quoteVolume(ticker.getVolValue())
                .timestamp(new Date(ticker.getDatetime()))
                .build();
    }

    public static OrderBook adaptOrderBook(
            KucoinResponse<KucoinOrderBook> response, CurrencyPair currencyPair) {

        KucoinOrderBook kcOrders = response.getData();
        Date timestamp = new Date(response.getTimestamp());
        List<LimitOrder> asks = new LinkedList<>();
        kcOrders
                .getSell()
                .stream()
                .forEach(s -> asks.add(adaptLimitOrder(currencyPair, OrderType.ASK, s, timestamp)));
        List<LimitOrder> bids = new LinkedList<>();
        kcOrders
                .getBuy()
                .stream()
                .forEach(s -> bids.add(adaptLimitOrder(currencyPair, OrderType.BID, s, timestamp)));
        return new OrderBook(timestamp, asks, bids);
    }

    public static Trade adaptTrade(KucoinDealOrder kucoinTrade, CurrencyPair currencyPair) {

        return new Trade(
                kucoinTrade.getOrderType().getOrderType(),
                kucoinTrade.getAmount(),
                currencyPair,
                kucoinTrade.getPrice(),
                new Date(kucoinTrade.getTimestamp()),
                null);
    }

    private static LimitOrder adaptLimitOrder(
            CurrencyPair currencyPair,
            OrderType orderType,
            List<BigDecimal> kucoinLimitOrder,
            Date timestamp) {

        return new LimitOrder(
                orderType, kucoinLimitOrder.get(1), currencyPair, null, timestamp, kucoinLimitOrder.get(0));
    }

    public static OpenOrders adaptActiveOrders(CurrencyPair currencyPair, KucoinActiveOrders data) {

        List<LimitOrder> openOrders = new LinkedList<>();
        data.getBuy().stream().forEach(order -> openOrders.add(adaptActiveOrder(currencyPair, order)));
        data.getSell().stream().forEach(order -> openOrders.add(adaptActiveOrder(currencyPair, order)));
        return new OpenOrders(openOrders);
    }

    public static OpenOrders adaptActiveOrders(KucoinActiveOrdersInKv ordersList) {

        Objects.requireNonNull(ordersList);

        List<LimitOrder> openOrders = new LinkedList<>();
        ordersList.getBUY().stream().map(d -> adaptActiveOrder(d, OrderType.BID))
                .forEach(openOrders::add);
        ordersList.getSELL().stream().map(d -> adaptActiveOrder(d, OrderType.ASK))
                .forEach(openOrders::add);
        return new OpenOrders(openOrders);
    }

    private static LimitOrder adaptActiveOrder(KucoinOrderDetails orderDetails, OrderType orderType) {

        return new LimitOrder.Builder(orderType,
                    new CurrencyPair(orderDetails.getCoinType(),
                            orderDetails.getCoinTypePair()))
                .timestamp(orderDetails.getCreatedAt())
                .id(orderDetails.getOid())
                .limitPrice(orderDetails.getPrice())
                .originalAmount(orderDetails.getPendingAmount())
                .cumulativeAmount(orderDetails.getDealAmount())
                .orderStatus(getOrderStatus(orderDetails))
                .build();
    }

    private static OrderStatus getOrderStatus(KucoinOrderDetails orderDetails) {
        BigDecimal dealAmount = orderDetails.getDealAmount();

        if(Objects.isNull(dealAmount))
            return OrderStatus.UNKNOWN;

        if(dealAmount.compareTo(BigDecimal.ZERO) == 0)
            return OrderStatus.NEW;

        return OrderStatus.PARTIALLY_FILLED;
    }

    private static LimitOrder adaptActiveOrder(CurrencyPair currencyPair, KucoinActiveOrder order) {

        return new LimitOrder.Builder(order.getOrderType().getOrderType(), currencyPair)
                .timestamp(order.getTimestamp())
                .id(order.getOrderOid())
                .limitPrice(order.getPrice())
                .originalAmount(order.getAmount()) // this might be the remaining amount, not sure
                .cumulativeAmount(order.getDealAmount())
                .orderStatus(
                        order.getDealAmount().compareTo(BigDecimal.ZERO) == 0
                                ? OrderStatus.NEW
                                : OrderStatus.PARTIALLY_FILLED)
                .build();
    }

    public static UserTrades adaptUserTrades(List<KucoinDealtOrder> orders) {

        List<UserTrade> trades = new LinkedList<>();
        orders.stream().forEach(order -> trades.add(adaptUserTrade(order)));
        return new UserTrades(trades, TradeSortType.SortByTimestamp);
    }

    private static UserTrade adaptUserTrade(KucoinDealtOrder order) {

        return new UserTrade.Builder()
                .currencyPair(new CurrencyPair(order.getCoinType(), order.getCoinTypePair()))
                .id(order.getOid())
                .orderId(order.getOrderOid())
                .originalAmount(order.getAmount())
                .price(order.getDealPrice())
                .timestamp(new Date(order.getCreatedAt()))
                .type(order.getDirection().getOrderType())
                .feeAmount(order.getFee())
                .feeCurrency(
                        order.getDirection().equals(KucoinOrderType.BUY)
                                ? Currency.getInstance(order.getCoinType())
                                : Currency.getInstance(order.getCoinTypePair()))
                .build();
    }

    public static ExchangeMetaData adaptExchangeMetadata(
            List<KucoinTicker> tickers, List<KucoinCoin> coins) {

        Map<String, KucoinCoin> coinMap =
                coins.stream().collect(Collectors.toMap(c -> c.getCoin(), c -> c));
        Map<CurrencyPair, CurrencyPairMetaData> pairMeta = adaptCurrencyPairMap(tickers, coinMap);
        Map<Currency, CurrencyMetaData> coinMeta = adaptCurrencyMap(coins);
        return new ExchangeMetaData(pairMeta, coinMeta, null, null, null);
    }

    private static Map<Currency, CurrencyMetaData> adaptCurrencyMap(List<KucoinCoin> coins) {

        return coins
                .stream()
                .collect(
                        Collectors.toMap(
                                c -> Currency.getInstance(c.getCoin()), c -> adaptCurrencyMetadata(c)));
    }

    private static CurrencyMetaData adaptCurrencyMetadata(KucoinCoin coin) {
        return new CurrencyMetaData(coin.getTradePrecision(), coin.getWithdrawMinFee());
    }

    private static Map<CurrencyPair, CurrencyPairMetaData> adaptCurrencyPairMap(
            List<KucoinTicker> symbols, Map<String, KucoinCoin> coins) {

        return symbols
                .stream()
                .collect(
                        Collectors.toMap(
                                t -> new CurrencyPair(t.getCoinType(), t.getCoinTypePair()),
                                t -> adaptCurrencyPairMetadata(t, coins.get(t.getCoinTypePair()))));
    }

    private static CurrencyPairMetaData adaptCurrencyPairMetadata(
            KucoinTicker tick, KucoinCoin coin) {

        // trading scale is determined by the base currency's trade precision
        return new CurrencyPairMetaData(tick.getFeeRate(), null, null, coin.getTradePrecision());
    }

    public static AccountInfo adaptAccountInfo(List<KucoinCoinBalance> balances) {

        return new AccountInfo(
                new Wallet(
                        balances.stream().map(KucoinAdapters::adaptBalance).collect(Collectors.toList())));
    }

    private static Balance adaptBalance(KucoinCoinBalance balance) {

        BigDecimal avail = balance.getBalance();
        BigDecimal freezeBalance = balance.getFreezeBalance();
        BigDecimal total = BigDecimal.ZERO.add(avail).add(freezeBalance);
        return new Balance(Currency.getInstance(balance.getCoinType()), total, avail, freezeBalance);
    }

    public static List<FundingRecord> adaptFundingHistory(List<KucoinWalletRecord> records) {

        return records.stream().map(KucoinAdapters::adaptFundingRecord).collect(Collectors.toList());
    }

    private static FundingRecord adaptFundingRecord(KucoinWalletRecord record) {

        return new FundingRecord.Builder()
                .setAmount(record.getAmount())
                .setAddress(record.getAddress())
                .setCurrency(Currency.getInstance(record.getCoinType()))
                .setDate(new Date(record.getCreatedAt()))
                .setFee(record.getFee())
                .setStatus(record.getStatus().getFundingRecordStatus())
                .setBlockchainTransactionHash(record.getOuterWalletTxid())
                .setInternalId(record.getOid())
                .setDescription(record.getRemark())
                .setType(record.getType().getFundingRecordType())
                .build();
    }
}
