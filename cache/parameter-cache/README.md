# Parameter Cache

## Motivation

This java project shows how to implement a cache functionality using plain java.

## Parameter table structure

| Column | Type | Description |
| ------ | ---- | ----------- |
| type | varchar | Key |
| name | varchar | Key |
| data0 | varchar | |
| data1 | varchar | |
| data2 | varchar | |
| data3 | varchar | |

## API

The cache implementation allows for list access to all parameters of a given type or single parameter access.

Cache timeouts can be set on list as well as on single key level.

## Implementation details

The cache values are read from the database using java futures with a configurable timeout (default: 100 milliseconds for parameter list and 20 milliseconds for single parameter).

## Open issues

* Introduce a cache result meta object to be able to return some information back to the caller.  
  Like if he is receiving possible stale data because the db call didn't return in time (callable idled out).
* For tests write a h2 database user defined sleep function.  
  Parameterize the sql statements in the cache code in order to be able to use this function in the where clause.
* And of course: Write/complete unit tests.
* Fix name bug: rename last accessed to last refreshed in cache entry and cache map class. 
