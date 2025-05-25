   const WebSocket = require('ws');

   const wss = new WebSocket.Server({ port: 8080 });

   wss.on('connection', (ws) => {
       console.log('New client connected');

       ws.on('message', (message) => {
           console.log(`Received: ${message}`);
       });

       // Send notifications
       const sendNotification = (data) => {
           ws.send(JSON.stringify(data));
       };

       // Example: Notify user of a successful transaction
       sendNotification({ type: 'transaction', status: 'success', message: 'Transaction completed successfully!' });
   });
   
