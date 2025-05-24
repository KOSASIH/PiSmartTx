# PiSmartTx
A decentralized transaction system on Pi Network with an Autonomous Transaction System, featuring:
- Dual Value System (Internal: $314,159/Pi, External: ~$0.8111/Pi)
- Pi Purity Badge for verified mined Pi
- Volatility Alert for price changes >5%
- Auto-Conversion for merchants

Originally forked from pi-apps/store-on-pi and tt_car_app.

## Setup
1. Clone: `git clone https://github.com/KOSASIH/PiSmartTx.git`
2. Install frontend: `cd frontend && npm install`
3. Configure `.env` (see `.env.example`)
4. Run: `npm run serve`
5. Deploy backend services (rate-service, smartcontract-service) using Kubernetes.

## License
MIT
