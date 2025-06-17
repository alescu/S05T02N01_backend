const http = require('http'); // Importa el mòdul HTTP per crear el servidor
const fs = require('fs');     // Importa el mòdul File System per llegir fitxers
const path = require('path');   // Importa el mòdul Path per gestionar rutes de fitxers

const hostname = '127.0.0.1'; // O 'localhost'. És l'adreça IP on el servidor escoltarà.
const port = 5500;            // El port on el servidor escoltarà. Pots triar-ne un altre (ex: 3000, 8080).
const pagePath = path.join(__dirname, 'loginPortal.html'); // Ruta completa al teu fitxer HTML

// Crea el servidor
const server = http.createServer((req, res) => {
    // Comprovem si la petició és per la pàgina arrel '/'
    if (req.url === '/' || req.url === '/loginPortal.html') {
        fs.readFile(pagePath, (err, data) => {
            if (err) {
                // Si hi ha un error en llegir el fitxer (ex: no existeix)
                res.statusCode = 500; // Internal Server Error
                res.setHeader('Content-Type', 'text/plain; charset=utf-8');
                res.end('Error intern del servidor: no es pot carregar la pàgina.\n');
                console.error(`Error llegint el fitxer ${pagePath}:`, err);
                return;
            }

            // Si el fitxer es llegeix correctament
            res.statusCode = 200; // OK
            res.setHeader('Content-Type', 'text/html; charset=utf-8'); // Especifica que la resposta és HTML
            res.end(data); // Envia el contingut del fitxer HTML com a resposta
        });
    } else {
        // Si la petició no és per la pàgina arrel
        res.statusCode = 404; // Not Found
        res.setHeader('Content-Type', 'text/plain; charset=utf-8');
        res.end('404 Not Found: La pàgina sol·licitada no existeix.\n');
    }
});

// Fes que el servidor escolti en el port i hostname especificats
server.listen(port, hostname, () => {
    console.log(`Servidor Node.js funcionant a http://${hostname}:${port}/`);
    console.log(`Obre el teu navegador a: http://localhost:${port}/`);
});