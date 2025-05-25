   import pandas as pd
   from sklearn.cluster import KMeans
   import matplotlib.pyplot as plt

   # Load user transaction data
   data = pd.read_csv('user_transactions.csv')
   features = data[['transaction_amount', 'transaction_frequency']]

   # Apply KMeans clustering
   kmeans = KMeans(n_clusters=3)
   data['cluster'] = kmeans.fit_predict(features)

   # Visualize clusters
   plt.scatter(data['transaction_amount'], data['transaction_frequency'], c=data['cluster'])
   plt.xlabel('Transaction Amount')
   plt.ylabel('Transaction Frequency')
   plt.title('User Behavior Clustering')
   plt.show()
   
