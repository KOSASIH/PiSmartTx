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
