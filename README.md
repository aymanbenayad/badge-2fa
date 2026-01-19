# Badge-Based Access Control with Two-Factor Authentication (2FA)

## Overview
This project implements a simple badge-based access control system using Java Card and Two-Factor Authentication (2FA).

User authentication requires:
- A PIN code
- An RSA Private Key (stored encrypted on-card via AES and decrypted using the PIN)

Access is granted only when both factors are successfully verified via a Challenge-Response mechanism.

## Core Modules
- PIN management and verification
- On-card Private Key decryption using AES-128-CBC (derived from PIN)
- RSA Challenge decryption using Private Key (Applet)
- Session management with timeout handling
- Access traceability (date and access duration)

## Security
- The RSA Private Key is stored encrypted (AES) and decrypted on-card (never leaves the device)
- The Applet proves identity using the RSA Private Key
- Two independent factors are required for authentication

## Sequence Diagrams

### 1. Conceptual View
High-level functional flow showing the dependency between PIN validation and the cryptographic challenge.

![Conceptual Sequence Diagram](./seq1.png)

### 2. Technical View
Detailed execution flow illustrating APDU commands (0x20, 0x30) and internal module interactions.

![Technical Sequence Diagram](./seq2.png)

## Purpose
Educational project demonstrating secure access control concepts using Java Card technology.
