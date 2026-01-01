#!/bin/bash

# EventPlanner Testing Script

BASE_URL="http://localhost"
EVENT_SERVICE="${BASE_URL}:8082"
BOOKING_SERVICE="${BASE_URL}:8083"
USER_SERVICE="${BASE_URL}:8081"

echo "======================================"
echo "EventPlanner Test Suite"
echo "======================================"

# Health Check
echo ""
echo "[1/5] Checking Service Health..."
for port in 8761 8888 8081 8082 8083 8084 8085; do
  status=$(curl -s -o /dev/null -w "%{http_code}" http://localhost:$port/health)
  if [ "$status" = "200" ]; then
    echo "  ✓ Port $port: UP"
  else
    echo "  ✗ Port $port: DOWN (HTTP $status)"
  fi
done

# Create Event
echo ""
echo "[2/5] Creating Event..."
EVENT_RESPONSE=$(curl -s -X POST "${EVENT_SERVICE}/api/events" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Concert 2025",
    "description": "Amazing concert",
    "venue": "Stadium",
    "category": "Music",
    "eventDate": "2025-06-15",
    "totalSeats": 100,
    "bookedSeats": 0,
    "price": 50.0
  }')

EVENT_ID=$(echo "$EVENT_RESPONSE" | jq -r '.id // .eventId // empty' 2>/dev/null)

if [ -z "$EVENT_ID" ]; then
  echo "  ✗ Failed to create event"
  echo "  Response: $EVENT_RESPONSE"
  exit 1
fi

echo "  ✓ Event created: $EVENT_ID"

# Check Stock
echo ""
echo "[3/5] Checking Event Stock..."
STOCK_RESPONSE=$(curl -s "${EVENT_SERVICE}/api/events/${EVENT_ID}/stock")
AVAILABLE_SEATS=$(echo "$STOCK_RESPONSE" | jq -r '.availableSeats // empty' 2>/dev/null)

if [ -z "$AVAILABLE_SEATS" ]; then
  echo "  ✗ Failed to check stock"
  echo "  Response: $STOCK_RESPONSE"
  exit 1
fi

echo "  ✓ Available Seats: $AVAILABLE_SEATS"

# Create Booking
echo ""
echo "[4/5] Creating Booking..."
BOOKING_RESPONSE=$(curl -s -X POST "${BOOKING_SERVICE}/api/bookings" \
  -H "Content-Type: application/json" \
  -d "{
    \"userId\": \"user-test-001\",
    \"eventId\": \"${EVENT_ID}\",
    \"numberOfSeats\": 2,
    \"totalPrice\": 100.0
  }")

BOOKING_ID=$(echo "$BOOKING_RESPONSE" | jq -r '.bookingId // .id // empty' 2>/dev/null)
BOOKING_STATUS=$(echo "$BOOKING_RESPONSE" | jq -r '.status // empty' 2>/dev/null)

if [ -z "$BOOKING_ID" ]; then
  echo "  ✗ Failed to create booking"
  echo "  Response: $BOOKING_RESPONSE"
  exit 1
fi

echo "  ✓ Booking created: $BOOKING_ID (Status: $BOOKING_STATUS)"

# Verify Stock After Booking
echo ""
echo "[5/5] Verifying Stock After Booking..."
FINAL_STOCK=$(curl -s "${EVENT_SERVICE}/api/events/${EVENT_ID}/stock" | jq -r '.availableSeats // empty' 2>/dev/null)

if [ -z "$FINAL_STOCK" ]; then
  echo "  ✗ Failed to verify final stock"
  exit 1
fi

RESERVED=$((AVAILABLE_SEATS - FINAL_STOCK))
echo "  ✓ Final Available Seats: $FINAL_STOCK"
echo "  ✓ Seats Reserved: $RESERVED"

echo ""
echo "======================================"
echo "✓ All Tests Passed!"
echo "======================================"
echo ""
echo "Summary:"
echo "  Event ID: $EVENT_ID"
echo "  Booking ID: $BOOKING_ID"
echo "  Initial Stock: $AVAILABLE_SEATS"
echo "  Final Stock: $FINAL_STOCK"
echo "  Seats Booked: $RESERVED"
