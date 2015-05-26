package com.xeiam.xchange.coinbaseex;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.xeiam.xchange.coinbaseex.dto.account.CoinbaseExAccount;
import com.xeiam.xchange.coinbaseex.dto.marketdata.CoinbaseExProductBook;
import com.xeiam.xchange.coinbaseex.dto.marketdata.CoinbaseExProductBookEntry;
import com.xeiam.xchange.coinbaseex.dto.marketdata.CoinbaseExProductStats;
import com.xeiam.xchange.coinbaseex.dto.marketdata.CoinbaseExProductTicker;
import com.xeiam.xchange.coinbaseex.dto.trade.CoinbaseExOrder;
import com.xeiam.xchange.currency.CurrencyPair;
import com.xeiam.xchange.dto.Order.OrderType;
import com.xeiam.xchange.dto.account.AccountInfo;
import com.xeiam.xchange.dto.marketdata.OrderBook;
import com.xeiam.xchange.dto.marketdata.Ticker;
import com.xeiam.xchange.dto.trade.LimitOrder;
import com.xeiam.xchange.dto.trade.OpenOrders;
import com.xeiam.xchange.dto.trade.Wallet;


public class CoinbaseExAdapters {
	// TODO: timezone might be off, needs to be converted
	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSSSS+00");
	
	public static Ticker adaptTicker(CoinbaseExProductTicker ticker, CoinbaseExProductStats stats, CoinbaseExProductBook book, CurrencyPair currencyPair) {

		BigDecimal last = ticker != null ? ticker.getPrice() : null;
		BigDecimal high = stats != null ? stats.getHigh() : null;
		BigDecimal low = stats != null ? stats.getLow() : null;
		BigDecimal buy = book != null ? book.getBestBid().getPrice() : null;
		BigDecimal sell = book != null ? book.getBestAsk().getPrice() : null;
		BigDecimal volume = stats != null ? stats.getVolume() : null;
		Date date = ticker != null ? ticker.getTime() : new Date();

		return new Ticker.Builder().currencyPair(currencyPair).last(last).high(high).low(low).bid(buy).ask(sell).volume(volume).timestamp(date).build();
	}

	public static OrderBook adaptOrderBook(CoinbaseExProductBook book, CurrencyPair currencyPair) {
		List<LimitOrder> asks = toLimitOrderList(book.getAsks(), OrderType.ASK, currencyPair);
		List<LimitOrder> bids = toLimitOrderList(book.getBids(), OrderType.BID, currencyPair);

		return new OrderBook(null, asks, bids);
	}

	private static List<LimitOrder> toLimitOrderList(
			CoinbaseExProductBookEntry[] levels, OrderType orderType, CurrencyPair currencyPair) {

		List<LimitOrder> allLevels = new ArrayList<LimitOrder>(levels.length);
		for(int i = 0; i < levels.length; i++) {
			CoinbaseExProductBookEntry ask = levels[i];

			allLevels.add(new LimitOrder(orderType, ask.getVolume(), currencyPair, "0", null, ask.getPrice()));
		}

		return allLevels;

	}

	public static AccountInfo adaptAccountInfo(CoinbaseExAccount[] coinbaseExAccountInfo) {
		List<Wallet> wallets = new ArrayList<Wallet>(coinbaseExAccountInfo.length);

		for(int i = 0; i < coinbaseExAccountInfo.length; i++) {
			CoinbaseExAccount account = coinbaseExAccountInfo[i];

			wallets.add(new Wallet(account.getCurrency(), account.getBalance(), account.getAvailable(), account.getHold()));	
		}

		return new AccountInfo(coinbaseExAccountInfo[0].getProfile_id(), wallets);
	}

	public static OpenOrders adaptOpenOrders(CoinbaseExOrder[] coinbaseExOpenOrders) {
		List<LimitOrder> orders = new ArrayList<LimitOrder>(coinbaseExOpenOrders.length);

		for(int i = 0; i < coinbaseExOpenOrders.length; i++) {
			CoinbaseExOrder order = coinbaseExOpenOrders[i];

			OrderType type = order.getSide().equals("buy") ? OrderType.BID : OrderType.ASK;
			CurrencyPair currencyPair = new CurrencyPair(order.getProductId().replace("-", "/"));

			Date createdAt;
			try {
				createdAt = dateFormat.parse(order.getCreatedAt());
			} catch (ParseException e) {
				// TODO error?
				continue;
			}

			orders.add(new LimitOrder(type, order.getSize(), currencyPair, order.getId(), createdAt, order.getPrice()));

		}

		return new OpenOrders(orders);
	}
}