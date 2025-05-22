# README.md

## Dual Value System

The Pi Store operates on a **Dual Value System**, which allows users to interact with product prices and transactions in two distinct currencies: **Pi** and **USD**. This system is designed to provide flexibility and transparency for users, enabling them to understand the value of their transactions in both internal and external contexts.

### Key Features of the Dual Value System:

1. **Internal Value**: 
   - This value represents the worth of Pi within the Pi ecosystem. It is primarily used for transactions that occur internally, such as purchases made directly within the Pi Store. The internal value is pegged to a specific rate, which is updated regularly to reflect market conditions.

2. **External Value**: 
   - The external value is derived from the average exchange rate of Pi against USD on various cryptocurrency exchanges. This value is crucial for users who wish to understand the market value of their Pi holdings and for those engaging in transactions that involve converting Pi to fiat currency.

3. **Real-Time Updates**: 
   - Both internal and external values are updated in real-time, ensuring that users have access to the most current pricing information. This feature enhances user experience by providing accurate and timely data for decision-making.

4. **Transaction Flexibility**: 
   - Users can choose their preferred currency when initiating transactions. Whether they want to transact based on the internal value (for internal purchases) or the external value (for exchanges), the system accommodates both options seamlessly.

5. **Transparency**: 
   - The Dual Value System promotes transparency by clearly displaying both values during transactions. Users can easily see how much they are spending in Pi and its equivalent in USD, fostering trust and confidence in the platform.

## Installation

To set up the frontend, follow these steps:

```bash
cd frontend
npm install
npm run serve
```

## Deploy with Kubernetes (optional)

1. Update `kubernetes/rate-service-deployment.yaml` with your API keys.
2. Apply the configurations:

```bash
kubectl apply -f kubernetes/
```

## Usage

### View Product Prices

- Open the Pi Store frontend (e.g., [http://localhost:8080](http://localhost:8080)).
- Product prices are displayed in Pi and USD (e.g., 0.01 Pi = $3,141.59 internal, ~$0.0081 external).

### Initiate Transactions

- Use the transaction interface (`SmartTransaction.vue`).
- Select Pi source:
  - "Penambangan" (mining) for internal value
  - "Bursa" (exchange) for external value
- Enter the amount and merchant ID; the USD equivalent updates automatically.

### Monitor Merchant Earnings

- Access the merchant dashboard (`MerchantDashboard.vue`).
- View transactions and total earnings in both internal and external values.

## API Endpoints

### Rate Service

- `GET /api/rate/pi?type=internal`: Returns internal rate (e.g., $314,159).
- `GET /api/rate/pi?type=external`: Returns average exchange rate (e.g., $0.8111).

### Transaction Service

- `POST /api/transactions/create-transaction`: Creates a transaction with Pi source.
- `POST /api/transactions/execute-transaction`: Completes a transaction.
- `GET /api/transactions/merchant?merchantId={id}`: Lists merchant transactions.

## Exchange Integration

The external value is calculated as the average PI/USDT price from:

- **OKX**: `/api/v5/market/ticker?instId=PI-USDT` (~$0.8111, May 2025).
- **Bitget**: `/api/v2/spot/market/ticker?symbol=PIUSDT` (~$0.7357).
- **Pionex**: `/api/v1/market/tickers?symbol=PI_USDT` (~$0.7801).
- **MEXC**: `/api/v3/ticker/price?symbol=PIUSDT` (~$0.7900).
- **Gate.io**: `/api/v4/spot/tickers?currency_pair=PI_USDT` (~$0.8200).

**Note**: API keys are required and stored securely in environment variables.

## Contributing

1. Fork the repository.
2. Create a feature branch:

```bash
git checkout -b feature/your-feature
```

3. Implement changes and test locally.
4. Commit and push your changes:

```bash
git add .
git commit -m "Add your feature description"
git push origin feature/your-feature
```

5. Create a pull request on GitHub, describing your changes.

## Issues and Future Improvements

- **Exchange Reliability**: Monitor OKX/Bitget withdrawal issues (reported post-mainnet).
- **Volatility Alerts**: Add notifications for significant price changes.
- **Additional Exchanges**: Support CoinW, DigiFinex, etc., as needed.
- **Mainnet Integration**: Integrate with Pi Network blockchain for transaction validation post-mainnet.

## License

This project is licensed under the MIT License. See `LICENSE` for details.

## Contact

For questions or suggestions, open an issue on GitHub or contact the Pi Store development team.

# Benefits

## Benefits of the Dual Value System, Especially with the Internal Value of 1 Pi = $314,159 and External Value from Exchanges

### 1. Clarity in Value Proposition

- **Defined Internal Value**: The establishment of an internal value of 1 Pi = $314,159 provides a clear and stable reference point for users, merchants, and stores. This clarity helps all parties understand the worth of Pi within the ecosystem, facilitating smoother transactions and reducing confusion.

- **Comparison with External Value**: By having a defined internal value, users can easily compare it with the external value derived from exchanges. This comparison allows users to gauge the market performance of Pi and make informed decisions about when to buy, sell, or use their Pi holdings.

### 2. Enhanced Trust and Credibility

- **Stability in Transactions**: The fixed internal value instills confidence among users and merchants, as they know the worth of Pi in a stable context. This stability can encourage more transactions within the ecosystem, as users feel secure in the value of their currency.

- **Market Validation**: The dual value system, with a clear internal value and fluctuating external values from exchanges, validates the Pi currency in the broader market. This can enhance the credibility of Pi as a legitimate currency, attracting more users and merchants to the platform.

### 3. Strategic Financial Management

- **Risk Mitigation**: Merchants and users can better manage their financial strategies by understanding the difference between internal and external values. For instance, if the external value is significantly lower than the internal value, users may choose to hold onto their Pi rather than convert it to USD, anticipating future appreciation.

- **Informed Decision-Making**: With a clear internal value, users can make more informed decisions regarding their investments and transactions. They can assess whether to transact in Pi or USD based on the current external market conditions, optimizing their financial outcomes.

### 4. Encouragement of Ecosystem Growth

- **Incentivizing Internal Transactions**: The defined internal value encourages users to transact within the Pi ecosystem, as they can see the tangible benefits of using Pi for purchases. This can lead to increased activity within the ecosystem, fostering growth and development.

- **Attracting New Users**: The clear internal value can attract new users who are looking for a stable cryptocurrency option. As more users join the ecosystem, the overall value and utility of Pi can increase, benefiting all participants.

### 5. Facilitating Exchange and Liquidity

- **Easier Conversion**: The dual value system allows for easier conversion between Pi and USD, as users can reference the internal value when making decisions about trading or spending their Pi. This can enhance liquidity in the market, making it easier for users to access their funds.

- **Market Dynamics Understanding**: Users can better understand market dynamics by observing how the external value fluctuates in relation to the internal value. This knowledge can empower users to engage more actively in trading and investment strategies.

### Conclusion

The Dual Value System, particularly with the internal value of 1 Pi = $314,159, offers significant benefits for users, merchants, and the overall ecosystem. By providing clarity, enhancing trust, and facilitating informed decision-making, this system not only strengthens the Pi currency but also promotes its adoption and growth in the broader market.
