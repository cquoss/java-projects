# Parameter Cache

## Motivation

This java project shows how to implement a cache functionality using plain java.

## Table structure

| Column | Type | Description |
| ------ | ---- | ----------- |
| type | varchar | Key |
| key | varchar | Key |
| data0 | varchar | |
| data1 | varchar | |
| data2 | varchar | |
| data3 | varchar | |

## API

The cache implementation allows for list access to all keys of a given type or single key access.

Cache timeouts can be set on list as well as on single key level.

## Implementation details

The cache values are read from the database using java futures with a configurable timeout (default: 20 milliseconds).

The underlying database statement runs, where applicable, with a query timeout of 1 second.
