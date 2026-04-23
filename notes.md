# Code Quality Fixes - UserController & WebClientInstance

## Overview

This document outlines critical antipatterns and recommended fixes for the Signal API project's core components.

---

## 1. userController.java Refactoring

### Current Issues (Line Numbers Refer to Original File)

| Issue | Severity | Description |
|-------|----------|-------------|
| Per-request WebClient instantiation | Critical | Creates new connection on every request, no pooling |
| Useless try-catch in reactive methods | High | Catch block never executes for async errors |
| Wrong logger class | Medium | Using `HttpConnectionLiveness` instead of SLF4J |
| Ignored return values | Medium | POST results not validated |
| Generic Error catching | Low | Should catch specific exceptions |
