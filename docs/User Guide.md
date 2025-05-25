# User Guide for PiSmartTx

## Introduction
PiSmartTx is a decentralized transaction system designed for the Pi Network. It aims to facilitate secure and efficient transactions while providing users with valuable features such as price alerts, transaction history, and smart contract integration.

## Table of Contents
1. [Features](#features)
2. [System Requirements](#system-requirements)
3. [Setup Instructions](#setup-instructions)
4. [Using PiSmartTx](#using-pismartx)
5. [Troubleshooting](#troubleshooting)
6. [Contributing](#contributing)
7. [License](#license)

## Features
- **Dual Value System:** 
  - Internal Value: $314,159/Pi
  - External Value: ~$0.8111/Pi
- **Pi Purity Badge:** 
  - A badge for verified mined Pi.
- **Volatility Alerts:** 
  - Alerts users of price changes greater than 5%.
- **Auto-Conversion for Merchants:** 
  - Facilitates automatic conversion for transactions.
- **Transaction History:** 
  - View and filter your transaction history.
- **Smart Contract Integration:** 
  - Secure and transparent transactions through smart contracts.

## System Requirements
- Node.js (version 14 or higher)
- npm (Node Package Manager)
- Docker (for containerized deployment)
- Kubernetes (for orchestration, optional)

## Setup Instructions

### 1. Clone the Repository
Open your terminal and run the following command:
```bash
git clone https://github.com/KOSASIH/PiSmartTx.git
```

### 2. Install Frontend Dependencies
Navigate to the frontend directory and install the required packages:
```bash
cd PiSmartTx/frontend
npm install
```

### 3. Configure Environment Variables
Create a `.env` file in the root directory based on the provided `.env.example` file. Update the variables as needed.

### 4. Run the Frontend
Start the frontend application:
```bash
npm run serve
```

### 5. Deploy Backend Services
If you are using Kubernetes, deploy the backend services (`rate-service` and `smartcontract-service`) as per the provided Kubernetes configuration files.

## Using PiSmartTx

### Creating a Transaction
1. Navigate to the transaction page in the application.
2. Enter the recipient's address and the amount to send.
3. Click on the "Send" button to initiate the transaction.

### Viewing Transaction History
1. Go to the "Transaction History" section.
2. You can filter transactions by date, amount, or status.

### Setting Up Volatility Alerts
1. Access the settings page.
2. Enable alerts for price changes and set your preferred threshold.

## Troubleshooting
- **Issue:** Application fails to start.
  - **Solution:** Ensure that all dependencies are installed and that the environment variables are correctly configured.
  
- **Issue:** Transactions are not processing.
  - **Solution:** Check the smart contract deployment and ensure that the backend services are running.

- **Issue:** Alerts are not being received.
  - **Solution:** Verify that the alert settings are enabled and that your notification preferences are correctly set.

## Contributing
We welcome contributions to PiSmartTx! If you would like to contribute, please follow these steps:
1. Fork the repository.
2. Create a new branch for your feature or bug fix.
3. Make your changes and commit them.
4. Push your branch and create a pull request.

## License
This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.

---

Thank you for using PiSmartTx! We hope you enjoy the features and functionalities of our decentralized transaction system.
```
