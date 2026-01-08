# Badge-Based Access Control with Two-Factor Authentication (2FA)

## Overview
This project implements a simple badge-based access control system using **Java Card** and Two-Factor Authentication (2FA).

User authentication requires:
- A PIN code
- An **RSA Private Key** (stored encrypted via AES and decrypted using the PIN)

Access is granted only when both factors are successfully verified via a **Challenge-Response** mechanism.

## Core Modules
- PIN management and verification
- **Private Key decryption** using AES-128-CBC (derived from PIN)
- **RSA Challenge encryption** using Public Key (Applet)
- Session management with timeout handling
- Access traceability (date and access duration)

## Security
- The RSA Private Key is stored **encrypted** (AES) and reconstructed client-side
- The Applet verifies identity using the **RSA Public Key**
- Two independent factors are required for authentication

## Sequence Diagram

![sequence-diagram](./sequence_diagram.png)

## Purpose
Educational project demonstrating secure access control concepts using Java Card technology.
