# Badge-Based Access Control with Two-Factor Authentication (2FA)

## Overview
This project implements a simple badge-based access control system using **Java Card** and Two-Factor Authentication (2FA).

User authentication requires:
- A PIN code
- A private key stored in encrypted form on a smart card

Access is granted only when both factors are successfully verified.

## Core Modules
- PIN management and verification  
- Private key encryption using AES-128-CBC  
- User authentication logic  
- Session management with timeout handling  
- Access traceability (date and access duration)

## Security
- Private keys are stored encrypted on the smart card  
- Two independent factors are required for authentication  

## Purpose
Educational project demonstrating secure access control concepts using Java Card technology.
