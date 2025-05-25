   const request = require('supertest');
   const app = require('../app');

   describe('Transaction API', () => {
       it('should create a new transaction', async () => {
           const response = await request(app)
               .post('/api/transactions')
               .send({ from: 'address1', to: 'address2', amount: 100 });
           expect(response.statusCode).toBe(201);
           expect(response.body).toHaveProperty('transactionId');
       });
   });
   
