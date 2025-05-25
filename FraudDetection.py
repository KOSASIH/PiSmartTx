   import pandas as pd
   from sklearn.ensemble import IsolationForest

   # Load transaction data
   data = pd.read_csv('transactions.csv')
   features = data[['amount', 'timestamp', 'user_id']]  # Features for fraud detection

   # Train Isolation Forest model
   model = IsolationForest(contamination=0.01)
   model.fit(features)

   # Predict anomalies
   data['is_fraud'] = model.predict(features)
   fraudulent_transactions = data[data['is_fraud'] == -1]
   
