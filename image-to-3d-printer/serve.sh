#!/bin/bash

echo "Starting Image to 3D Printer Web Server..."
echo "Server will be available at: http://localhost:8000"
echo "Press Ctrl+C to stop the server"
echo ""

python3 -m http.server 8000
